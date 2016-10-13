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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.LocationRequest;
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
import io.realm.Sort;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    final String TAG = MainActivity.class.getSimpleName();
    final float DISTANCE_FENCE = 500; // meters
    final int LOCATION_REFRESH_RATE = 30; // seconds

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.realm_recycler_view)
    RealmRecyclerView recyclerView;
    @BindView(R.id.empty_text)
    TextView emptyText;

    @Inject
    BusApiService apiService;
    @Inject
    Realm realm;

    RealmResults<Route> routeResults;
    ArrivalsAdapter arrivalsAdapter;
    ArrivalsAdapter emptyAdapter;

    Subscription fetchRouteSub;
    Subscription fetchArrivalsSub;
    Subscription permissionSub;
    Subscription locationSub;
    Subscription menuItemSub;
    Observable<Location> locationUpdatesObservable;

    Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UCITransitApp.getComponent(this).inject(this);
        ButterKnife.bind(this);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");

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

        RealmResults<Arrivals> arrivals = realm
                .where(Arrivals.class)
                .equalTo("isCurrent", true)
                .equalTo("isNearby", true)
                .findAllSorted("isFavorite", Sort.DESCENDING);

        arrivalsAdapter = new ArrivalsAdapter(this, arrivals, true, true, realm);
        arrivalsAdapter.addFooter();
        emptyAdapter = new ArrivalsAdapter(this,
                realm.where(Arrivals.class).equalTo("id", "noid").findAll(),
                false, false, realm);
        recyclerView.setAdapter(arrivalsAdapter);
        recyclerView.setOnRefreshListener(this::refreshTask);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_refresh:
                Log.i(TAG, "Refresh menu item selected");
                recyclerView.setRefreshing(true);
                fetchArrivals();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
        Utils.unsubscribe(fetchRouteSub);
        Utils.unsubscribe(fetchArrivalsSub);
        Utils.unsubscribe(permissionSub);
        Utils.unsubscribe(locationSub);
        Utils.unsubscribe(menuItemSub);
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
                .setInterval(1000 * LOCATION_REFRESH_RATE);

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
                            fetchArrivals();
                        }
                    });
        }
    }

    private void fetchInitialRouteData() {
        if(!Utils.isNetworkConnected(this)){
            setEmptyView();
        }
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

    private void refreshTask(){
        Observable.just(0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if(realm.isEmpty()){
                        fetchInitialRouteData();
                    }else {
                        fetchArrivals();
                    }
                });
    }

    private void fetchArrivals() {
        if(!Utils.isNetworkConnected(this)){
            setEmptyView();
        } else if(mLocation == null){
            emptyText.setText(R.string.empty_location_message);
            recyclerView.setRefreshing(false);
        } else {
            recyclerView.setAdapter(arrivalsAdapter);
            recyclerView.setRefreshing(true);
            emptyText.setText(R.string.empty_default_message);
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
                                isNearby = stopLocation.distanceTo(mLocation) <= DISTANCE_FENCE;
                            }
                            final boolean result = isNearby;
                            final Realm realm = Realm.getDefaultInstance();
                            try {
                                String id = route.getId() + "" + stop.getId();
                                Arrivals arrivals =
                                        realm.where(Arrivals.class).equalTo("id", id).findFirst();
                                if(arrivals != null) {
                                    realm.executeTransaction(r -> {
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

    private void setEmptyView(){
        Toast.makeText(this, "Network is not available", Toast.LENGTH_SHORT).show();
        recyclerView.setRefreshing(false);
        recyclerView.setAdapter(emptyAdapter);
        emptyText.setText(R.string.empty_network_message);
    }
}
