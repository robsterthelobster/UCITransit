package com.robsterthelobster.ucitransit.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.robsterthelobster.ucitransit.R;
import com.robsterthelobster.ucitransit.UCITransitApp;
import com.robsterthelobster.ucitransit.data.BusApiService;
import com.robsterthelobster.ucitransit.data.models.Route;
import com.robsterthelobster.ucitransit.data.models.Stop;
import com.robsterthelobster.ucitransit.data.models.Vehicle;
import com.robsterthelobster.ucitransit.utils.Constants;
import com.robsterthelobster.ucitransit.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by robin on 9/29/2016.
 */

public class BusMapFragment extends Fragment implements OnMapReadyCallback {

    final String TAG = BusMapFragment.class.getSimpleName();
    final int MAP_PADDING = 200;

    @Inject
    Realm realm;
    @Inject
    BusApiService apiService;

    GoogleMap map;
    Route route;
    List<Marker> stopMarkers;
    HashMap<String, Marker> vehicleMarkers;

    public static BusMapFragment newInstance(String routeName) {
        BusMapFragment fragment = new BusMapFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ROUTE_ID_KEY, routeName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        UCITransitApp.getComponent(getContext()).inject(this);

        Bundle arguments = getArguments();
        String routeName = arguments.getString(Constants.ROUTE_ID_KEY);
        realm = Realm.getDefaultInstance();

        route = realm.where(Route.class).equalTo("name", routeName).findFirst();
        stopMarkers = new ArrayList<>();
        vehicleMarkers = new HashMap<>();

        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        return view;
    }

    private void setUpStopMarkers() {
        Log.d(TAG, "setUpStopMarkers");
        for(Stop stop : route.getStops()){
            LatLng latLng = new LatLng(stop.getLatitude(), stop.getLongitude());
            stopMarkers.add(map.addMarker(new MarkerOptions()
                    .icon(getBitmapDescriptor(
                            R.drawable.ic_directions_bus_black_24dp,
                            Color.parseColor(route.getColor())))
                    .position(latLng)
                    .title(stop.getName())));
        }
        centerMapToStops(MAP_PADDING);
    }

    private void fetchVehicleData(){
        final Context context = getContext();
        int id = route.getId();
        Observable.interval(30, TimeUnit.SECONDS)
                .flatMap(tick -> apiService.getVehicles(id))
                .flatMap(Observable::from)
                .distinct()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Vehicle>() {
                    final String TAG = "fetchVehicleData";
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(Vehicle vehicle) {
                        Log.d(TAG, "onNext");
                        String key = vehicle.getName();
                        LatLng latLng = new LatLng(vehicle.getLatitude(), vehicle.getLongitude());
                        if(vehicleMarkers.containsKey(key)){
                            vehicleMarkers.get(key).setPosition(latLng);
                        }else{
                            Marker marker = map.addMarker(new MarkerOptions()
                                    .icon(getBitmapDescriptor(R.drawable.bus_tracker,
                                            context.getResources().getColor(R.color.colorPrimary)))
                                    .position(latLng)
                                    .rotation(Utils.getRotationFromDirection(vehicle.getHeading()))
                                    .flat(true)
                                    .title(vehicle.getName()));
                            vehicleMarkers.put(key, marker);
                        }
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        setUpStopMarkers();
        fetchVehicleData();
    }

    private BitmapDescriptor getBitmapDescriptor(int id, int color) {
        Drawable vectorDrawable = ContextCompat.getDrawable(getContext(), id);
        int h = vectorDrawable.getIntrinsicHeight();
        int w = vectorDrawable.getIntrinsicWidth();
        vectorDrawable.setBounds(0, 0, w, h);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (color != -1)
                vectorDrawable.setTint(color);
        }
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }

    // http://stackoverflow.com/questions/14828217/
    // android-map-v2-zoom-to-show-all-the-markers
    private void centerMapToStops(int padding){
        if(stopMarkers.isEmpty()){
            return;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : stopMarkers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.moveCamera(cu);
    }
}
