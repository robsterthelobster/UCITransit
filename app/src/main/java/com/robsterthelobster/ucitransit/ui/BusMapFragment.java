package com.robsterthelobster.ucitransit.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.robsterthelobster.ucitransit.R;
import com.robsterthelobster.ucitransit.UCITransitApp;
import com.robsterthelobster.ucitransit.data.BusApiService;
import com.robsterthelobster.ucitransit.data.models.Arrivals;
import com.robsterthelobster.ucitransit.data.models.ArrivalsFields;
import com.robsterthelobster.ucitransit.data.models.Coordinate;
import com.robsterthelobster.ucitransit.data.models.Route;
import com.robsterthelobster.ucitransit.data.models.RouteFields;
import com.robsterthelobster.ucitransit.data.models.Stop;
import com.robsterthelobster.ucitransit.data.models.StopFields;
import com.robsterthelobster.ucitransit.utils.Constants;
import com.robsterthelobster.ucitransit.utils.SnackbarManager;
import com.robsterthelobster.ucitransit.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import rx.Subscription;

/**
 * Created by robin on 9/29/2016.
 */

public class BusMapFragment extends Fragment implements OnMapReadyCallback {

    final String TAG = BusMapFragment.class.getSimpleName();
    final int MAP_PADDING = 200;

    @BindView(R.id.map_button_center)
    Button centerButton;
    @Inject
    Realm realm;
    @Inject
    BusApiService apiService;

    GoogleMap map;
    Route route;
    Snackbar snackbar;
    SnackbarManager snackbarManager;
    CoordinatorLayout snackbarLayout;

    List<Marker> stopMarkers;
    HashMap<String, Marker> vehicleMarkers;
    Subscription vehicleSub;

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
        String routeId = arguments.getString(Constants.ROUTE_ID_KEY);
        realm = Realm.getDefaultInstance();

        route = realm.where(Route.class).equalTo(RouteFields.ROUTE_ID, routeId).findFirst();
        stopMarkers = new ArrayList<>();
        vehicleMarkers = new HashMap<>();

        snackbarLayout =
                container.getRootView().findViewById(R.id.detail_content);
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        return view;
    }

    private void setUpStopMarkers() {
        Log.d(TAG, "setUpStopMarkers");
        for (Stop stop : route.getStops()) {
            Coordinate coordinate = stop.getLocation();
            LatLng latLng = new LatLng(coordinate.getLatitude(), coordinate.getLongitude());
            Marker marker = map.addMarker(new MarkerOptions()
                    .icon(getBitmapDescriptor(
                            R.drawable.ic_directions_bus_black_24dp,
                            Color.parseColor(route.getColor())))
                    .position(latLng)
                    .title(stop.getName()));
            marker.setTag(route.getRouteId() + "" + stop.getStopId());
            stopMarkers.add(marker);
        }
        centerMapToStops();
    }

    private void setUpRouteSegments(){
        PolyUtil.decode("");

    }

//    private void fetchVehicleData() {
//        final Context context = getContext();
//        int id = route.getId();
//        vehicleSub = Observable.interval(0, 30, TimeUnit.SECONDS)
//                .flatMap(tick -> apiService.getVehicles(id))
//                .flatMap(Observable::from)
//                .distinct()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Vehicle>() {
//                    final String TAG = "fetchVehicleData";
//
//                    @Override
//                    public void onCompleted() {
//                        Log.d(TAG, "onCompleted");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d(TAG, e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(Vehicle vehicle) {
//                        Log.d(TAG, "onNext");
//                        String key = "Bus " + vehicle.getName();
//                        LatLng latLng = new LatLng(vehicle.getLatitude(), vehicle.getLongitude());
//                        float rotation = Utils.getRotationFromDirection(vehicle.getHeading());
//                        if (vehicleMarkers.containsKey(key)) {
//                            Marker marker = vehicleMarkers.get(key);
//                            marker.setPosition(latLng);
//                            marker.setRotation(rotation);
//                        } else {
//                            Marker marker = map.addMarker(new MarkerOptions()
//                                    .icon(getBitmapDescriptor(R.drawable.bus_tracker,
//                                            context.getResources().getColor(R.color.colorPrimary)))
//                                    .position(latLng)
//                                    .rotation(rotation)
//                                    .flat(true)
//                                    .title(key));
//                            marker.setTag(vehicle);
//                            vehicleMarkers.put(key, marker);
//                        }
//                    }
//                });
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
//        map.setOnMarkerClickListener(marker -> {
//            if (stopMarkers.contains(marker)) {
//                Arrivals arrivals =
//                        realm.where(Arrivals.class).equalTo(ArrivalsFields.ID, (String) marker.getTag()).findFirst();
//                if (!arrivals.getPredictions().isEmpty()) {
//                    Prediction prediction = arrivals.getPredictions().first();
//                    showSnackbar("Arrives in " + prediction.getMinutes() + " min");
//                }
//            }
//            if (vehicleMarkers.containsKey(marker.getTitle())) {
//                Vehicle vehicle = (Vehicle) vehicleMarkers.get(marker.getTitle()).getTag();
//                if (vehicle != null)
//                    showSnackbar("Bus is " + vehicle.getApcPercentage() + "% full");
//            }
//            return false;
//        });
        centerButton.setVisibility(View.VISIBLE);
        map.setOnMyLocationButtonClickListener(() -> false);
        map.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }else{
            map.setMyLocationEnabled(true);
        }
        setUpStopMarkers();
        //fetchVehicleData();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Utils.unsubscribe(vehicleSub);
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
    @OnClick(R.id.map_button_center)
    public void centerMapToStops(){
        if(stopMarkers.isEmpty()){
            return;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : stopMarkers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING);
        map.moveCamera(cu);
    }

    public void showSnackbar(final String str) {
        snackbarManager = new SnackbarManager(() -> {
            snackbar = Snackbar.make(snackbarLayout, str, Snackbar.LENGTH_LONG);
            View snackView = snackbar.getView();
            TextView textView =
                    (TextView) snackView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.setAction(getString(R.string.snackbar_dismiss), view -> snackbarManager = null);
            return snackbar;
        });
        snackbarManager.show(this);
    }
}
