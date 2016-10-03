package com.robsterthelobster.ucitransit.ui;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.robsterthelobster.ucitransit.DaggerUCITransitComponent;
import com.robsterthelobster.ucitransit.R;
import com.robsterthelobster.ucitransit.UCITransitComponent;
import com.robsterthelobster.ucitransit.module.RealmModule;
import com.robsterthelobster.ucitransit.utils.Utils;
import com.robsterthelobster.ucitransit.data.BusApiService;
import com.robsterthelobster.ucitransit.data.PredictionAdapter;
import com.robsterthelobster.ucitransit.data.models.Arrivals;
import com.robsterthelobster.ucitransit.data.models.Prediction;
import com.robsterthelobster.ucitransit.data.models.Route;
import com.robsterthelobster.ucitransit.data.models.Stop;
import com.robsterthelobster.ucitransit.utils.Constants;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.List;

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
    PredictionAdapter predictionAdapter;

    Subscription routeData;
    Subscription stopRoutes;
    Subscription subscription;
    Observable<Location> locationUpdatesObservable;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UCITransitComponent component = DaggerUCITransitComponent.builder()
                .realmModule(new RealmModule(this))
                .build();
        component.inject(this);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

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

        RealmResults<Prediction> predictions = realm
                .where(Prediction.class).findAll();

        predictionAdapter = new PredictionAdapter(this, predictions, true, false);
        recyclerView.setAdapter(predictionAdapter);
        recyclerView.setOnRefreshListener(() -> fetchArrivals());

        if (realm.isEmpty()) {
            fetchInitialRouteData();
        } else {
            routeResults = realm.where(Route.class).findAll();
            setUpNavigationView();
            fetchArrivals();
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        Utils.unsubscribe(subscription);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
        Utils.unsubscribe(stopRoutes);
    }

    private void setUpNavigationView() {
        final Intent intent = new Intent(this, DetailActivity.class);
        Observable.from(routeResults).subscribe(route -> {
            intent.putExtra(Constants.ROUTE_ID_KEY, route.getId());
            MenuItem item = navigationView.getMenu().add(route.getDisplayName());
            item.setIntent(intent);
        });
    }

    private void fetchInitialRouteData() {
        routeData = apiService.getRoutes()
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
                        Log.d("routeData", "completed");
                        routeResults = realm.where(Route.class).findAll();
                        setUpNavigationView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("routeData", e.getMessage());
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
        Observable.defer(() -> {
            final Realm realm = Realm.getDefaultInstance();
            RealmResults<Route> routeResults = realm.where(Route.class).findAll();
            return Observable.from(routeResults);
        })
                .flatMap(route -> Observable.from(route.getStops())
                        .flatMap(stop -> apiService.getArrivalTimes(route.getId(), stop.getId())))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Arrivals>() {

                    final String TAG = "predictionData";

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "completed");
                        recyclerView.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(Arrivals arrivals) {
                        final Realm realm = Realm.getDefaultInstance();
                        try {
                            RealmList<Prediction> predictionList = arrivals.getPredictions();
                            int size = predictionList.size();
                            if (size > 0) {
                                final Prediction prediction = predictionList.get(0);
                                if (size > 1) {
                                    prediction.setSecondaryMinutes(
                                            predictionList.get(1).getSecondaryMinutes());
                                }
                                RealmResults<Route> routeResult = realm.where(Route.class)
                                        .equalTo("id", prediction.getRouteId())
                                        .findAll();
                                RealmResults<Stop> stopResult = realm.where(Stop.class)
                                        .equalTo("id", prediction.getStopId())
                                        .findAll();
                                Route route = routeResult.first();
                                Stop stop = stopResult.first();
                                prediction.setId(route.getId() + prediction.getStopId());
                                prediction.setColor(route.getColor());
                                prediction.setStopName(stop.getName());
                                realm.executeTransaction(r -> r.copyToRealmOrUpdate(prediction));
                            }
                        } finally {
                            realm.close();
                        }
                    }
                });
    }

    private void callLocationService() {
        Log.d("callLocationService", "called");
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000 * 5); // number of seconds

        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(this);
        locationUpdatesObservable = locationProvider.getUpdatedLocation(locationRequest);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}
