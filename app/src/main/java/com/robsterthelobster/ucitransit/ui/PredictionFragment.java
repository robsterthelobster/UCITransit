package com.robsterthelobster.ucitransit.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.robsterthelobster.ucitransit.R;
import com.robsterthelobster.ucitransit.UCITransitApp;
import com.robsterthelobster.ucitransit.data.BusApiService;
import com.robsterthelobster.ucitransit.utils.Constants;
import com.robsterthelobster.ucitransit.utils.Utils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by robin on 10/3/2016.
 */
public class PredictionFragment extends Fragment {

    @BindView(R.id.fragment_recycler_view)
    RealmRecyclerView recyclerView;
    @BindView(R.id.empty_text)
    TextView emptyText;

    @Inject
    Realm realm;
    @Inject
    BusApiService apiService;

    String routeName;
//    ArrivalsAdapter arrivalsAdapter;
//    ArrivalsAdapter emptyAdapter;
    Subscription fetchArrivalsSub;
    SharedPreferences prefs;

    public PredictionFragment() {
    }

    public static PredictionFragment newInstance(String routeName) {
        PredictionFragment fragment = new PredictionFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ROUTE_ID_KEY, routeName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prediction_list, container, false);
        ButterKnife.bind(this, view);
        UCITransitApp.getComponent(getContext()).inject(this);

        Bundle arguments = getArguments();
        routeName = arguments.getString(Constants.ROUTE_ID_KEY);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        if(realm.isClosed()){
            realm = Realm.getDefaultInstance();
        }

//        RealmResults<Arrivals> arrivals = realm
//                .where(Arrivals.class)
//                .equalTo("isCurrent", true)
//                .equalTo("routeName", routeName)
//                .findAll();
//
//        arrivalsAdapter = new ArrivalsAdapter(getContext(), arrivals, true, false, realm);
//        emptyAdapter = new ArrivalsAdapter(getContext(),
//                realm.where(Arrivals.class).equalTo(ArrivalsFields.ID, "").findAll(),
//                false, false, realm);
//        recyclerView.setAdapter(arrivalsAdapter);
//        recyclerView.setOnRefreshListener(this::refreshTask);
//        fetchArrivals();

        return view;
    }

//    private Observable<Arrivals> getArrivalsObservable() {
//        return Observable.defer(() -> {
//            final Realm threadRealm = Realm.getDefaultInstance();
//            Route route = threadRealm.where(Route.class).equalTo(RouteFields.NAME, routeName).findFirst();
//            return Observable.just(route)
//                    .doOnCompleted(threadRealm::close);
//        })
//                .flatMap(route -> Observable.from(route.getStops())
//                        .flatMap(stop -> apiService.getArrivalTimes(route.getId(), stop.getId())
//                                .map(arrivals -> {
//                                    if (arrivals.getPredictions().size() > 0) {
//                                        arrivals.setCurrent(true);
//                                    } else {
//                                        arrivals.setCurrent(false);
//                                    }
//                                    arrivals.setId(route.getId() + "" + stop.getId());
//                                    arrivals.setRouteId(route.getId());
//                                    arrivals.setStopId(stop.getId());
//                                    arrivals.setRouteName(route.getName());
//                                    arrivals.setStopName(stop.getName());
//                                    arrivals.setRouteColor(route.getColor());
//                                    final Realm realm = Realm.getDefaultInstance();
//                                    try {
//                                        Arrivals oldArrivals = realm.where(Arrivals.class).equalTo(ArrivalsFields.ID, arrivals.getId()).findFirst();
//                                        if (oldArrivals != null) {
//                                            arrivals.setNearby(oldArrivals.isNearby());
//                                            arrivals.setFavorite(oldArrivals.isFavorite());
//                                        }
//                                        arrivals.setNearby(false);
//                                        realm.executeTransaction(r -> r.copyToRealmOrUpdate(arrivals));
//                                    } finally {
//                                        realm.close();
//                                    }
//                                    return arrivals;
//                                })))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }

//    public void refreshTask(){
//        Observable.just(0)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(s -> fetchArrivals());
//    }

//    private void fetchArrivals() {
//        boolean showAd = prefs.getBoolean(getString(R.string.key_ad_pref), true);
//        if(!Utils.isNetworkConnected(getContext())){
//            Toast.makeText(getContext(), "Network is not available", Toast.LENGTH_SHORT)
//                    .show();
//            emptyText.setText(R.string.empty_network_message);
//            recyclerView.setAdapter(emptyAdapter);
//            recyclerView.setRefreshing(false);
//        }else{
//            recyclerView.setRefreshing(true);
//            recyclerView.setAdapter(arrivalsAdapter);
//            emptyText.setText(R.string.empty_server_message);
//            fetchArrivalsSub = getArrivalsObservable()
//                    .subscribe(new Subscriber<Arrivals>() {
//                        final String TAG = "DetailArrivalsSub";
//
//                        @Override
//                        public void onCompleted() {
//                            Log.d(TAG, "onCompleted");
//                            recyclerView.setRefreshing(false);
//                            arrivalsAdapter.setFooter(showAd);
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            Log.d(TAG, e.getMessage());
//                            this.onNext(null);
//                        }
//
//                        @Override
//                        public void onNext(Arrivals arrivals) {
//                            //Log.d(TAG, "onConNext: " + arrivals.getRouteName());
//                        }
//                    });
//        }
//    }
}