package com.robsterthelobster.ucitransit.ui;

import android.content.Intent;
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

import com.robsterthelobster.ucitransit.DaggerUCITransitComponent;
import com.robsterthelobster.ucitransit.R;
import com.robsterthelobster.ucitransit.UCITransitComponent;
import com.robsterthelobster.ucitransit.data.BusApiService;
import com.robsterthelobster.ucitransit.data.models.*;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Inject BusApiService apiService;

    Subscription stopRoutes;

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

        navigationView.setNavigationItemSelectedListener(this);

        fetchRouteData();
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
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

      if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(this, DetailActivity.class);
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void fetchRouteData(){
        // Create a RealmConfiguration that saves the Realm file in the app's "files" directory.
        RealmConfiguration realmConfig = new RealmConfiguration
                .Builder(this)
                .deleteRealmIfMigrationNeeded() // if schema is changed, just set up new database
                .build();
        Realm.setDefaultConfiguration(realmConfig);
        //Realm.deleteRealm(realmConfig);

        stopRoutes = apiService.getRoutes()
                .flatMap(routes -> {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();

                    realm.copyToRealmOrUpdate(routes);
                    // delete junction table
                    RealmResults<RouteStop> routeStops = realm.where(RouteStop.class).findAll();
                    routeStops.deleteAllFromRealm();

                    realm.commitTransaction();
                    realm.close();
                    return Observable.from(routes);
                })
                .flatMap(route -> apiService.getStops(route.getId())
                        .flatMap(stops -> {

                            // Get a Realm instance for this thread
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(stops);

                            for(Stop stop : stops){
                                RouteStop routeStop = realm.createObject(RouteStop.class);
                                routeStop.setRouteID(route.getId());
                                routeStop.setStopID(stop.getId());
                            }
                            realm.commitTransaction();

                            Observable<RealmResults<RouteStop>> routeStops
                                    = realm.where(RouteStop.class).findAll().asObservable();
                            return routeStops;
                        }))
                .flatMap(routeStop -> Observable.from(routeStop))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RouteStop>() {
                    int count = 0;

                    @Override
                    public void onCompleted() {
                        Log.d("fetchRouteData", "Completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Error", e.toString());
                    }

                    @Override
                    public void onNext(RouteStop stop) {
                        count++;
                        Log.d("RouteStop", stop.getRouteID()+"");
                    }
                });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopRoutes.unsubscribe();
    }
}
