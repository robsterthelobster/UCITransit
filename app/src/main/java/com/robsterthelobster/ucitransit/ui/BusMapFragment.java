package com.robsterthelobster.ucitransit.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.robsterthelobster.ucitransit.R;
import com.robsterthelobster.ucitransit.UCITransitApp;
import com.robsterthelobster.ucitransit.data.BusApiService;
import com.robsterthelobster.ucitransit.data.models.Route;
import com.robsterthelobster.ucitransit.data.models.Stop;
import com.robsterthelobster.ucitransit.utils.Constants;

import java.util.List;

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

    @Inject
    Realm realm;
    @Inject
    BusApiService apiService;

    GoogleMap map;
    Route route;
    List<Marker> stopMarkers;

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

        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        return view;
    }

    private void setUpMarkers() {
        Observable.from(route.getStops())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Stop>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Stop stop) {

                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        setUpMarkers();
    }
}
