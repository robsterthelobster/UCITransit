package com.robsterthelobster.ucitransit.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ftinc.scoop.Scoop;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.LocationRequest;
import com.robsterthelobster.ucitransit.R;
import com.robsterthelobster.ucitransit.UCITransitApp;
import com.robsterthelobster.ucitransit.data.ArrivalsAdapter;
import com.robsterthelobster.ucitransit.data.BusApiService;
import com.robsterthelobster.ucitransit.data.models.Arrivals;
import com.robsterthelobster.ucitransit.data.models.ArrivalsFields;
import com.robsterthelobster.ucitransit.data.models.Prediction;
import com.robsterthelobster.ucitransit.data.models.Route;
import com.robsterthelobster.ucitransit.data.models.RouteFields;
import com.robsterthelobster.ucitransit.data.models.Segment;
import com.robsterthelobster.ucitransit.data.models.SegmentData;
import com.robsterthelobster.ucitransit.data.models.Stop;
import com.robsterthelobster.ucitransit.data.models.StopFields;
import com.robsterthelobster.ucitransit.utils.Constants;
import com.robsterthelobster.ucitransit.utils.Utils;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
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
    RecyclerView recyclerView;
    @BindView(R.id.empty_text)
    TextView emptyText;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    BusApiService apiService;
    @Inject
    Realm realm;

    RealmResults<Route> routeResults;
    ArrivalsAdapter arrivalsAdapter;
    ArrivalsAdapter emptyAdapter;

    Subscription fetchInitialRouteSub;
    Subscription fetchInitialStopSub;
    Subscription fetchInitialSegmentSub;
    Subscription fetchArrivalsSub;
    Subscription permissionSub;
    Subscription locationSub;
    Subscription menuItemSub;
    Observable<Location> locationUpdatesObservable;

    ReactiveLocationProvider locationProvider;
    Location mLocation;

    SharedPreferences prefs;
    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Scoop.getInstance().apply(this);
        setContentView(R.layout.activity_main);
        UCITransitApp.getComponent(this).inject(this);
        ButterKnife.bind(this);

        MobileAds.initialize(getApplicationContext(), getString(R.string.app_id));
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        locationProvider = new ReactiveLocationProvider(this);
        locationProvider.getLastKnownLocation()
                .subscribe(new Subscriber<Location>() {
                    final String TAG = "lastKnownLocationSub";

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.getMessage());
                        showToast("Google Play Services not compatible.", Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onNext(Location location) {
                        mLocation = location;
                    }
                });

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

        if (realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }

        // date from 30 seconds ago
        date = new Date(System.currentTimeMillis() - 30*1000);
        RealmResults<Arrivals> arrivals = realm
                .where(Arrivals.class)
                // arrival time should be in the future, with a 30 second buffer
                .greaterThan(ArrivalsFields.ARRIVAL_TIME, date)
                .equalTo(ArrivalsFields.IS_NEARBY, true)
                .findAllSorted("isFavorite", Sort.DESCENDING);

        arrivalsAdapter = new ArrivalsAdapter(arrivals, true, true, realm);
        emptyAdapter = new ArrivalsAdapter(arrivals, true, true, realm);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(arrivalsAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        swipeRefreshLayout.setOnRefreshListener(this::refreshTask);

        if (realm.isEmpty()) {
            fetchInitialRouteData();
        } else {
            routeResults = realm.where(Route.class).findAllSorted("shortName");
            setUpNavigationView();
            refreshTask();
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        Scoop.getInstance().apply(this, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, Constants.RC_CHANGE_THEME);
                //startActivityForResult(ScoopSettingsActivity.createIntent(this), Constants.RC_CHANGE_THEME);
                break;
            case R.id.action_refresh:
                Log.i(TAG, "Refresh menu item selected");
                refreshTask();
                break;
            case R.id.action_empty_realm:
                realm.executeTransaction(realm -> realm.deleteAll());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
        recyclerView.setAdapter(null);
        Utils.unsubscribe(fetchInitialSegmentSub);
        Utils.unsubscribe(fetchInitialRouteSub);
        Utils.unsubscribe(fetchArrivalsSub);
        Utils.unsubscribe(permissionSub);
        Utils.unsubscribe(locationSub);
        Utils.unsubscribe(menuItemSub);
        Utils.unsubscribe(fetchInitialStopSub);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constants.RC_CHANGE_THEME) {
            Log.d(TAG, "recreate");
            Handler handler = new Handler();
            handler.postDelayed(this::recreate, 0);
        }
    }

    private void setUpNavigationView() {
        navigationView.setItemIconTintList(null);
        Menu menu = navigationView.getMenu();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            menu = menu.addSubMenu("Routes");
        }
        final Menu navMenu = menu;
        Observable.from(routeResults).subscribe(route -> {
            final Intent intent = new Intent(this, DetailActivity.class);
            String routeName = route.getShortName() + " " + route.getLongName();
            intent.putExtra(Constants.ROUTE_ID_KEY, route.getRouteId());
            intent.putExtra(Constants.ROUTE_NAME_KEY, routeName);
            MenuItem item = navMenu.add(routeName);
            item.setIntent(intent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Drawable icon = getDrawable(R.drawable.ic_directions_bus_white_24dp);
                int routeColor = Color.parseColor(route.getColor());
                if (icon != null) {
                    icon.mutate().setColorFilter(routeColor, PorterDuff.Mode.MULTIPLY);
                }
                item.setIcon(icon);
            }
        });
    }

    private void callLocationService() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000 * LOCATION_REFRESH_RATE);

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
                            refreshTask();
                        }
                    });
        }
    }

    /**
     * fetch stops first
     *
     * for each route, insert all the stops into route for easy access
     *
     */
    private void fetchInitialRouteData() {
        if (!Utils.isNetworkConnected(this)) {
            setEmptyView();
        }

        fetchInitialStopSub = apiService.getStops(Constants.AGENCY_ID)
                .flatMap(stopData -> Observable.from(stopData.getData()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Stop>() {
                    final String TAG = "fetchInitialStopSub";

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                        refreshTask();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(Stop stop) {
                        try (Realm realm = Realm.getDefaultInstance()) {
                            realm.executeTransaction(r -> r.copyToRealmOrUpdate(stop));
                        }
                    }
                });

        fetchInitialRouteSub = apiService.getRoutes(Constants.AGENCY_ID)
                .flatMap(routeData -> Observable.from(routeData.getData().getRoutes()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Route>() {
                    final String TAG = "fetchInitialRouteSub";

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                        routeResults = realm.where(Route.class).findAll();
                        fetchSegments();
                        setUpNavigationView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(Route route) {
                        try (Realm realm = Realm.getDefaultInstance()) {
                            // converts color into hex color for later use
                            route.setColor("#" + route.getColor());
                            RealmList<Stop> stops = new RealmList<>();
                            for(String stopId : route.getStopIdList()){
                                Stop stop = realm.where(Stop.class)
                                        .equalTo(StopFields.STOP_ID, Integer.parseInt(stopId))
                                        .findFirst();
                                stops.add(stop);
                            }
                            route.setStops(stops);
                            realm.executeTransaction(r -> r.copyToRealmOrUpdate(route));
                        }
                    }
                });
    }

    private void fetchSegments(){

        fetchInitialSegmentSub = Observable.defer(() -> {
            final Realm threadRealm = Realm.getDefaultInstance();
            RealmResults<Route> routeResults = threadRealm.where(Route.class).findAll();
            return Observable.from(routeResults)
                    .flatMap(route -> apiService.getSegments(Constants.AGENCY_ID, route.getRouteId())
                            .flatMap(segmentData -> Observable.from(segmentData.getData())
                                    .flatMap(segment -> {
                                        segment.setRouteId(route.getRouteId());
                                        return Observable.from(segmentData.getData());
                                    })))
                    .doOnCompleted(threadRealm::close);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Segment>() {
                    private final static String TAG = "fetchSegmentsSub";

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(Segment segment) {
                        try (Realm realm = Realm.getDefaultInstance()) {
                            realm.executeTransaction(r -> r.copyToRealmOrUpdate(segment));
                        }
                    }
                });
    }

    private void fetchArrivals() {
        boolean showAd = prefs.getBoolean(getString(R.string.key_ad_pref), true);
        if (!Utils.isNetworkConnected(this)) {
            showToast("Network is not available", Toast.LENGTH_SHORT);
            setEmptyView();
        } else {
            if (mLocation == null) {
                emptyText.setText(R.string.empty_location_message);
            } else {
                recyclerView.setAdapter(arrivalsAdapter);
                emptyText.setText(R.string.empty_default_message);
                swipeRefreshLayout.setRefreshing(true);
            }
            fetchArrivalsSub = getArrivalsObservable()
                    .subscribe(new Subscriber<Arrivals>() {
                        final String TAG = "ArrivalsSub";

                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "onCompleted");
                            swipeRefreshLayout.setRefreshing(false);
                            //arrivalsAdapter.setFooter(showAd);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, e.getMessage());
                        }

                        @Override
                        public void onNext(Arrivals arrivals) {}
                    });
        }
        arrivalsAdapter.notifyDataSetChanged();
    }

    /**
     * check if there are routes in the database, if not --> go get them
     *
     */
    private Observable<Arrivals> getArrivalsObservable() {

        Observable<Arrivals> arrivalsObservable;
        if (routeResults == null || routeResults.isEmpty()) {
            this.fetchInitialRouteData();
        }

        arrivalsObservable = Observable.defer(() -> {
            final Realm threadRealm = Realm.getDefaultInstance();
            RealmResults<Stop> stopRealmResults = threadRealm.where(Stop.class).findAll();
            return Observable.from(stopRealmResults).flatMap(stop -> Observable.from(stop.getRoutes())
                    .filter(routeId -> {
                        boolean isNearby;
                        if (mLocation == null) {
                            isNearby = false;
                        } else {
                            Location stopLocation = new Location("stop");
                            stopLocation.setLatitude(stop.getLocation().getLatitude());
                            stopLocation.setLongitude(stop.getLocation().getLongitude());
                            isNearby = stopLocation.distanceTo(mLocation) <= DISTANCE_FENCE;
                        }
                        return isNearby;
                    })
                    .flatMap(routeId -> apiService.getArrivals(Constants.AGENCY_ID, routeId, stop.getStopId())
                            .flatMap(arrivalData -> Observable.from(arrivalData.getData()).flatMap(arrivals -> {
                                Route route = threadRealm.where(Route.class)
                                        .equalTo(RouteFields.ROUTE_ID, routeId)
                                        .findFirst();
                                arrivals.setRoute(route);
                                arrivals.setStop(stop);
                                arrivals.setId(routeId + stop.getStopId());
                                arrivals.setRouteId(routeId);
                                arrivals.setNearby(true);

                                RealmList<Prediction> predictionList = arrivals.getArrivals();
                                for(int i = 0; i < predictionList.size(); i++){
                                    Date arrivalTime = predictionList.get(i).getArrivalAt();
                                    if(i == 0 ){
                                        arrivals.setArrivalTime(arrivalTime);
                                    }else if(i == 1){
                                        arrivals.setSecondaryArrivalTime(arrivalTime);
                                    }else{
                                        break;
                                    }
                                }
                                Arrivals oldArrivals = threadRealm
                                        .where(Arrivals.class)
                                        .equalTo(ArrivalsFields.ID, arrivals.getId())
                                        .findFirst();

                                if (oldArrivals != null) {
                                    arrivals.setFavorite(oldArrivals.isFavorite());
                                }

                                threadRealm.executeTransaction(r -> r.copyToRealmOrUpdate(arrivals));

                                return Observable.from(arrivalData.getData());
                            }))))
                    .doOnCompleted(threadRealm::close);})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        return arrivalsObservable;
    }

    private void setEmptyView() {
        showToast("Network is not available", Toast.LENGTH_SHORT);
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setAdapter(emptyAdapter);
        emptyText.setText(R.string.empty_network_message);
    }

    private void refreshTask() {
        Observable.just(0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (realm.isEmpty()) {
                        fetchInitialRouteData();
                    } else {
                        date = new Date(System.currentTimeMillis() - 30*1000);
                        fetchArrivals();
                    }
                });
    }

    private void showToast(String message, int length) {
        Toast.makeText(this, message, length).show();
    }
}
