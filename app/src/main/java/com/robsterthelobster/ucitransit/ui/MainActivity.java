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
import com.robsterthelobster.ucitransit.DaggerUCITransitComponent;
import com.robsterthelobster.ucitransit.R;
import com.robsterthelobster.ucitransit.UCITransitComponent;
import com.robsterthelobster.ucitransit.Utils;
import com.robsterthelobster.ucitransit.data.BusApiService;
import com.robsterthelobster.ucitransit.data.models.Arrivals;
import com.robsterthelobster.ucitransit.data.models.Prediction;
import com.robsterthelobster.ucitransit.data.models.Route;
import com.robsterthelobster.ucitransit.data.models.Stop;
import com.robsterthelobster.ucitransit.utils.PredictionAdapter;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
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

    Realm realm;
    PredictionAdapter predictionAdapter;

    Subscription routeData;
    Subscription stopData;
    Subscription stopRoutes;
    Subscription subscription;
    Observable<Location> locationUpdatesObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UCITransitComponent component = DaggerUCITransitComponent.create();
        component.inject(this);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Must be done during an initialization phase like onCreate
        RxPermissions.getInstance(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        callLocationService();
                    } else {
                        Toast.makeText(this, "Location Permission denied.", Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        RealmConfiguration realmConfig = new RealmConfiguration
                .Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm.deleteRealm(realmConfig); // for dev, delete database every time

        realm = Realm.getDefaultInstance();

        RealmResults<Prediction> predictions = realm
                .where(Prediction.class).findAll();

        predictionAdapter = new PredictionAdapter(this, predictions, true, true);
        recyclerView.setAdapter(predictionAdapter);
        recyclerView.setOnRefreshListener(this::testRefresh);

        if (realm.isEmpty()) {
            fetchInitialRouteData();
        }
    }

    private void testRefresh() {
        Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
        realm.where(Prediction.class).findAll().asObservable()
                .subscribe(predictionAdapter::updateRealmResults);
        RealmResults<Prediction> predictions = realm.where(Prediction.class).findAll();
        predictionAdapter.updateRealmResults(predictions);
        recyclerView.setRefreshing(false);
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (locationUpdatesObservable != null) {
            locationUpdatesObservable
                    .subscribe(new Subscriber<Location>() {
                        @Override
                        public void onCompleted() {
                            Log.d("Location obs", "Completed");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("Location obs error", e.toString());
                        }

                        @Override
                        public void onNext(Location location) {
                            Log.d("Location", location.toString());
                        }
                    });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.unsubscribe(subscription);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
        Utils.unsubscribe(stopRoutes);
    }

    private void fetchInitialRouteData() {
        final Intent intent = new Intent(this, DetailActivity.class);

//        apiService.getRoutes().switchMap(routes -> {
//            final Observable<Route> routeObservable = Observable.from(routes);
//            return routeObservable
//                    .switchMap(route -> {
//                        apiService.getStops(route.getId());
//                        return Observable.from(routes);
//                    });
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Route>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.d("route", "completed");
//                        fetchArrivals();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d("route", e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(Route route) {
//                        Log.d("route", route.getDisplayName());
//                        MenuItem item = navigationView.getMenu().add(route.getDisplayName());
//                        item.setIntent(intent);
//                    }
//                });

//        routeData = apiService.getRoutes()
//                .switchMap(routes -> Observable.from(routes)
//                        .switchMap(route -> {
//                            return apiService.getStops(route.getId())
//                                    .switchMap(stops -> {
//                                        return Observable.from(routes);
//                                    });
//                        }))
//
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Route>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.d("route", "completed");
//                        fetchArrivals();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d("route", e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(Route route) {
//                        Log.d("route", route.getDisplayName());
//                        MenuItem item = navigationView.getMenu().add(route.getDisplayName());
//                        item.setIntent(intent);
//                    }
//                });

        routeData = apiService.getRoutes()
                .flatMap(Observable::from).map(route -> {
                    RealmList<Stop> stops = new RealmList<>();
                    apiService.getStops(route.getId()).subscribe(new Subscriber<List<Stop>>() {
                        @Override
                        public void onCompleted() {
                            Log.d("stops", "completed");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("stops", e.getMessage());
                        }

                        @Override
                        public void onNext(List<Stop> stop) {
                            stops.addAll(stop);
                        }
                    });
                    route.setStops(stops);
                    final Realm realm = Realm.getDefaultInstance();
                    try{
                        realm.executeTransaction(r -> r.copyToRealmOrUpdate(route));
                    }finally {
                        realm.close();
                    }
                    return route;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Route>() {
                    @Override
                    public void onCompleted() {
                        Log.d("route", "completed");
                        fetchArrivals();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("route", e.getMessage());
                    }

                    @Override
                    public void onNext(Route route) {
                        Log.d("route", route.getDisplayName());
                        MenuItem item = navigationView.getMenu().add(route.getDisplayName());
                        item.setIntent(intent);
                    }
                });

    }

    private void fetchArrivals() {
        RealmResults<Route> routes = realm.where(Route.class).findAll();
        Log.d("predictions", "size is " + routes.size());
        for (Route route : routes) {
            Log.d("predictions", "route name: " + route.getDisplayName());
            for (Stop stop : route.getStops()) {
                Log.d("predictions", "      stop name: " + stop.getName());
                apiService.getArrivalTimes(route.getId(), stop.getId())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<Arrivals>() {
                            @Override
                            public void onCompleted() {
                                Log.d("predictions", "completed");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("predictions", e.getMessage());
                            }

                            @Override
                            public void onNext(Arrivals arrivals) {
                                final Realm realm = Realm.getDefaultInstance();
                                try{
                                    realm.executeTransaction(r -> r.copyToRealmOrUpdate(arrivals.getPredictions()));
                                }finally {
                                    realm.close();
                                }
                            }
                        });
            }
        }

//        Observable.from(routes)
//                .flatMap(route -> Observable.from(route.getStops())
//                        .flatMap(stop -> apiService.getArrivalTimes(route.getId(), stop.getId())))
//                .subscribeOn(Schedulers.io())
//                .subscribe(new Subscriber<Arrivals>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.d("predictions", "completed");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d("predictions", e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(Arrivals arrivals) {
//                        for (Prediction prediction : arrivals.getPredictions()) {
//                            Log.d("predictions", prediction.getBusName() + " " + prediction.getArriveTime());
//                        }
//                        final Realm realm = Realm.getDefaultInstance();
//                        try{
//                            realm.beginTransaction();
//                            realm.copyToRealmOrUpdate(arrivals.getPredictions());
//                            realm.commitTransaction();
//                        }finally {
//                            realm.close();
//                        }
//                    }
//                });

    }

    private void callLocationService() {
        Log.d("callLocationService", "called");
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000 * 5); // number of seconds

        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(this);
        locationUpdatesObservable = locationProvider.getUpdatedLocation(locationRequest);
    }
}
