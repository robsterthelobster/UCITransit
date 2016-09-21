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
import com.jakewharton.rxbinding.support.design.widget.RxNavigationView;
import com.robsterthelobster.ucitransit.DaggerUCITransitComponent;
import com.robsterthelobster.ucitransit.R;
import com.robsterthelobster.ucitransit.UCITransitComponent;
import com.robsterthelobster.ucitransit.Utils;
import com.robsterthelobster.ucitransit.data.BusApiService;
import com.robsterthelobster.ucitransit.data.models.Route;
import com.robsterthelobster.ucitransit.data.models.Stop;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Scheduler;
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

    @Inject
    BusApiService apiService;

    /**
     * All my subscriptions
     * Not all of them will be used, most of for testing at the moment
     **/
    Subscription stopRoutes;
    Subscription subscription;
    Subscription navViewSub;
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

        navViewSub = RxNavigationView
                .itemSelections(navigationView)
                .map(MenuItem::getItemId)
                .subscribe(id -> {
                    Log.d("navViewSub", "id is " + id);
                    if(id == R.id.nav_gallery){
                        Intent intent = new Intent(this, DetailActivity.class);
                        startActivity(intent);
                    }
                });

        // Must be done during an initialization phase like onCreate
        RxPermissions.getInstance(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        getLocation();
                    } else {
                        Toast.makeText(this, "Location Permission denied.", Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        // Create a RealmConfiguration that saves the Realm file in the app's "files" directory.
        RealmConfiguration realmConfig = new RealmConfiguration
                .Builder(this)
                .deleteRealmIfMigrationNeeded() // if schema is changed, just set up new database
                .build();
        //Realm.deleteRealm(realmConfig);
        Realm.setDefaultConfiguration(realmConfig);

        //fetchRouteData();
        callRealm();
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
    public void onStart(){
        super.onStart();
        if(locationUpdatesObservable != null) {
            locationUpdatesObservable
                    .subscribe(location -> {
                        Log.d("Location", location.toString());
                        locationTestToast(location);
                    });
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        Utils.unsubscribe(subscription);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.unsubscribe(stopRoutes);
    }

    private void fetchRouteData() {

        final Scheduler scheduler = Schedulers.from(Executors.newSingleThreadExecutor());

        stopRoutes = Observable.interval(60, TimeUnit.SECONDS, scheduler)
                .flatMap(n -> apiService.getRoutes())
                .flatMap(Observable::from)
                .flatMap(route -> apiService.getStops(route.getId())
                        .flatMap(stops -> {
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(stops);

                            final RealmList<Stop> realmStops = new RealmList<>();
                            realmStops.addAll(stops);
                            route.setStops(realmStops);

                            realm.copyToRealmOrUpdate(route);
                            realm.commitTransaction();
                            realm.close();

                            return Observable.from(stops);
                        }))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<Stop>() {
                    @Override
                    public void onCompleted() {
                        Log.d("fetchRouteData", "Completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Error", e.toString());
                    }

                    @Override
                    public void onNext(Stop stop) {
                        //Log.d("RouteStop", stop.getName()+"");
                    }
                });
    }

    private void callRealm() {

        Realm realm = Realm.getDefaultInstance();
        realm.where(Route.class).findAll().asObservable()
                .flatMap(Observable::from)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Route>() {
                    int count = 0;

                    @Override
                    public void onCompleted() {
                        Log.d("RealmRoute", "count is " + count);
                        Log.d("RealmStop", "Completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Error", e.toString());
                    }

                    @Override
                    public void onNext(Route route) {
                        count++;
                        Log.d("RealmRoute", route.getDisplayName());

//                        if(route.getName().equals("A - AV-Admin-ARC")){
//                            realm.beginTransaction();
//                            for(Stop stop : route.getStops()){
//                                stop.setFavorited(true);
//                            }
//
//                            realm.copyToRealmOrUpdate(route);
//                            realm.commitTransaction();
//                        }
                    }
                });
    }

    private void getLocation() {
        Log.d("getLocation", "called");
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000 * 5); // number of seconds

        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(this);
        locationUpdatesObservable = locationProvider.getUpdatedLocation(locationRequest);
    }

    private void locationTestToast(Location location){
        String text = "Location: " + location.getLatitude() + ". " + location.getLongitude();
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
