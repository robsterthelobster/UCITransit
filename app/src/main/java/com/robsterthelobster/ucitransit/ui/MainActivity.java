package com.robsterthelobster.ucitransit.ui;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.robsterthelobster.ucitransit.R;
import com.robsterthelobster.ucitransit.UCITransitApp;
import com.robsterthelobster.ucitransit.data.ArrivalsAdapter;
import com.robsterthelobster.ucitransit.data.BusApiService;
import com.robsterthelobster.ucitransit.data.models.Arrivals;
import com.robsterthelobster.ucitransit.data.models.Route;
import com.robsterthelobster.ucitransit.data.models.Stop;
import com.robsterthelobster.ucitransit.utils.Constants;
import com.robsterthelobster.ucitransit.utils.Utils;
import com.tbruyelle.rxpermissions.RxPermissions;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.realm_recycler_view)
    RealmRecyclerView recyclerView;

    @Inject
    BusApiService apiService;
    @Inject
    Realm realm;

    RealmResults<Route> routeResults;
    ArrivalsAdapter arrivalsAdapter;

    Subscription fetchRouteSub;
    Subscription fetchArrivalsSub;
    Subscription permissionSub;
    Subscription locationSub;
    Observable<Location> locationUpdatesObservable;

    Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UCITransitApp.getComponent(this).inject(this);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        permissionSub = RxPermissions.getInstance(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        callLocationService();
                    } else {
                        Toast.makeText(this, "Location Permission denied.", Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        // predictions that are current
        RealmResults<Arrivals> arrivals = realm
                .where(Arrivals.class)
                .equalTo("isCurrent", true)
                .equalTo("isNearby", true)
                .findAllSorted("isFavorite");

        arrivalsAdapter = new ArrivalsAdapter(this, arrivals, true, false);
        recyclerView.setAdapter(arrivalsAdapter);
        recyclerView.setOnRefreshListener(() -> fetchArrivals());

        if (realm.isEmpty()) {
            fetchInitialRouteData();
        } else {
            routeResults = realm.where(Route.class).findAll();
            setUpNavigationView();
            fetchArrivals();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
        Utils.unsubscribe(fetchRouteSub);
        Utils.unsubscribe(fetchArrivalsSub);
        Utils.unsubscribe(permissionSub);
        Utils.unsubscribe(locationSub);
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationSubscription();
        drawer.closeDrawer(GravityCompat.START, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.unsubscribe(locationSub);
    }

    private void setUpNavigationView() {
        Observable.from(routeResults).subscribe(route -> {
            final Intent intent = new Intent(this, DetailActivity.class);
            String name = route.getName();
            intent.putExtra(Constants.ROUTE_ID_KEY, name);
            MenuItem item = navigationView.getMenu().add(name);
            item.setIntent(intent);
        });
    }

    private void callLocationService() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000 * 5); // number of seconds

        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(this);
        locationUpdatesObservable = locationProvider.getUpdatedLocation(locationRequest);
    }

    private void startLocationSubscription() {
        if (locationUpdatesObservable != null) {
            locationSub = locationUpdatesObservable
                    .subscribe(new Subscriber<Location>() {
                        final String TAG = "Location subscription";

                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, e.toString());
                        }

                        @Override
                        public void onNext(Location location) {
                            Log.d(TAG, location.toString());
                            mLocation = location;
                        }
                    });
        }
    }

    private void fetchInitialRouteData() {
        fetchRouteSub = apiService.getRoutes()
                .flatMap(Observable::from).map(route -> {
                    RealmList<Stop> stops = new RealmList<>();
                    apiService.getStops(route.getId())
                            .subscribe(stops::addAll);
                    route.setStops(stops);
                    return route;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Route>() {
                    @Override
                    public void onCompleted() {
                        Log.d("fetchRouteSub", "onCompleted");
                        routeResults = realm.where(Route.class).findAll();
                        setUpNavigationView();
                        fetchArrivals();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("fetchRouteSub", e.getMessage());
                    }

                    @Override
                    public void onNext(Route route) {
                        final Realm realm = Realm.getDefaultInstance();
                        try {
                            realm.executeTransaction(r -> r.copyToRealmOrUpdate(route));
                        } finally {
                            realm.close();
                        }
                    }
                });
    }

    private void fetchArrivals() {
        recyclerView.setRefreshing(true);
        fetchArrivalsSub = getArrivalsObservable()
                .subscribe(new Subscriber<Arrivals>() {
                    final String TAG = "ArrivalsSub";
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                        recyclerView.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(Arrivals arrivals) {
                        //Log.d(TAG, "onConNext: " + arrivals.getRouteName());
                    }
                });
    }

    private Observable<Arrivals> getArrivalsObservable() {
        return Observable.defer(() -> {
            final Realm threadRealm = Realm.getDefaultInstance();
            RealmResults<Route> routeRealmResults = threadRealm.where(Route.class).findAll();
            return Observable.from(routeRealmResults)
                    .doOnCompleted(threadRealm::close);
        })
                .flatMap(route -> Observable.from(route.getStops())
                        .filter(stop -> {
                            boolean isNearby = false;
                            if(mLocation == null) {
                                isNearby = false;
                            }else{
                                Location stopLocation = new Location("stop");
                                stopLocation.setLatitude(stop.getLatitude());
                                stopLocation.setLongitude(stop.getLongitude());
                                isNearby = stopLocation.distanceTo(mLocation) <= 500;
                            }

                            final boolean result = isNearby;
                            final Realm realm = Realm.getDefaultInstance();
                            try {
                                RealmResults<Arrivals> oldArrivals = realm.where(Arrivals.class).equalTo("stopId", stop.getId()).findAll();
                                for(int i = 0; i < oldArrivals.size(); i++){
                                    realm.executeTransaction(r -> {
                                        Arrivals arrivals = oldArrivals.get(0);
                                        arrivals.setNearby(result);
                                        r.copyToRealmOrUpdate(arrivals);
                                    });
                                }
                            } finally {
                                realm.close();
                            }

                            return result;
                        })
                        .flatMap(stop -> apiService.getArrivalTimes(route.getId(), stop.getId())
                                .map(arrivals -> {
                                    if (arrivals.getPredictions().size() > 0) {
                                        arrivals.setCurrent(true);
                                    } else {
                                        arrivals.setCurrent(false);
                                    }
                                    arrivals.setId(route.getId() + "" + stop.getId());
                                    arrivals.setRouteId(route.getId());
                                    arrivals.setStopId(stop.getId());
                                    arrivals.setRouteName(route.getName());
                                    arrivals.setStopName(stop.getName());
                                    arrivals.setRouteColor(route.getColor());
                                    final Realm realm = Realm.getDefaultInstance();
                                    try {
                                        Arrivals oldArrivals = realm.where(Arrivals.class).equalTo("id", arrivals.getId()).findFirst();
                                        if (oldArrivals != null) {
                                            arrivals.setFavorite(oldArrivals.isFavorite());
                                        }
                                        realm.executeTransaction(r -> r.copyToRealmOrUpdate(arrivals));
                                    } finally {
                                        realm.close();
                                    }
                                    return arrivals;
                                })))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
