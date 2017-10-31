package com.robsterthelobster.ucitransit.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.robsterthelobster.ucitransit.R;
import com.robsterthelobster.ucitransit.UCITransitApp;
import com.robsterthelobster.ucitransit.data.ArrivalsAdapter;
import com.robsterthelobster.ucitransit.data.BusApiService;
import com.robsterthelobster.ucitransit.data.models.Arrivals;
import com.robsterthelobster.ucitransit.data.models.ArrivalsFields;
import com.robsterthelobster.ucitransit.data.models.Prediction;
import com.robsterthelobster.ucitransit.data.models.Route;
import com.robsterthelobster.ucitransit.data.models.RouteFields;
import com.robsterthelobster.ucitransit.data.models.Stop;
import com.robsterthelobster.ucitransit.data.models.StopFields;
import com.robsterthelobster.ucitransit.utils.Constants;
import com.robsterthelobster.ucitransit.utils.Utils;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
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
    EmptyRecyclerView recyclerView;
    @BindView(R.id.fragment_empty_view)
    TextView emptyText;
    @BindView(R.id.fragment_swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.empty_fragment_swipe_layout)
    SwipeRefreshLayout emptyRefreshLayout;

    @Inject
    Realm realm;
    @Inject
    BusApiService apiService;

    String routeId;
    Date date;
    ArrivalsAdapter arrivalsAdapter;
    Subscription fetchArrivalsSub;
    SharedPreferences prefs;

    public PredictionFragment() {
    }

    public static PredictionFragment newInstance(String routeId) {
        PredictionFragment fragment = new PredictionFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ROUTE_ID_KEY, routeId);
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
        routeId = arguments.getString(Constants.ROUTE_ID_KEY);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        if(realm.isClosed()){
            realm = Realm.getDefaultInstance();
        }
        // date from 30 seconds ago
        date = new Date(System.currentTimeMillis() - 30*1000);

        RealmResults<Arrivals> arrivals = realm
                .where(Arrivals.class)
                // arrival time should be in the future, with a 30 second buffer
                .greaterThan(ArrivalsFields.ARRIVAL_TIME, date)
                .equalTo(ArrivalsFields.ROUTE_ID, routeId)
                .findAll();

        arrivalsAdapter = new ArrivalsAdapter(arrivals, true, true, realm);
        recyclerView.setAdapter(arrivalsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setEmptyView(emptyText);
        recyclerView.setSwipeRefreshLayout(swipeRefreshLayout);
        recyclerView.setEmptySwipeRefreshLayout(emptyRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::refreshTask);
        emptyRefreshLayout.setOnRefreshListener(this::refreshTask);
        refreshTask();

        return view;
    }

    private Observable<Arrivals> getArrivalsObservable() {

        return Observable.defer(()->{
            final Realm threadRealm = Realm.getDefaultInstance();
            return apiService.getArrivals(Constants.AGENCY_ID, routeId)
                    .flatMap(arrivalData -> Observable.from(arrivalData.getData())
                            .flatMap(arrivals -> {
                                Route route = threadRealm.where(Route.class)
                                        .equalTo(RouteFields.ROUTE_ID, routeId)
                                        .findFirst();
                                Stop stop = threadRealm.where(Stop.class)
                                        .equalTo(StopFields.STOP_ID, arrivals.getStopId())
                                        .findFirst();
                                arrivals.setRoute(route);
                                arrivals.setStop(stop);
                                arrivals.setId(routeId + arrivals.getStopId());
                                arrivals.setRouteId(routeId);

                                RealmList<Prediction> predictionList = arrivals.getArrivals();
                                for (int i = 0; i < predictionList.size(); i++) {
                                    Date arrivalTime = predictionList.get(i).getArrivalAt();
                                    if (i == 0) {
                                        arrivals.setArrivalTime(arrivalTime);
                                    } else if (i == 1) {
                                        arrivals.setSecondaryArrivalTime(arrivalTime);
                                    } else {
                                        break;
                                    }
                                }
                                Arrivals oldArrivals = threadRealm
                                        .where(Arrivals.class)
                                        .equalTo(ArrivalsFields.ID, arrivals.getId())
                                        .findFirst();

                                if (oldArrivals != null) {
                                    arrivals.setFavorite(oldArrivals.isFavorite());
                                    arrivals.setNearby(oldArrivals.isNearby());
                                }
                                threadRealm.executeTransaction(r -> r.copyToRealmOrUpdate(arrivals));
                                return Observable.from(arrivalData.getData());
                            })).doOnCompleted(threadRealm::close);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void fetchArrivals() {
        boolean showAd = prefs.getBoolean(getString(R.string.key_ad_pref), true);

        if (!Utils.isNetworkConnected(getContext())) {
            Utils.showToast(getContext(),"Network is not available");
        } else {
            //recyclerView.setAdapter(arrivalsAdapter);
            emptyText.setText(R.string.empty_server_message);
            swipeRefreshLayout.setRefreshing(true);
            fetchArrivalsSub = getArrivalsObservable()
                    .subscribe(new Subscriber<Arrivals>() {
                        final String TAG = "ArrivalsSub";

                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "onCompleted");
                            swipeRefreshLayout.setRefreshing(false);
                            emptyRefreshLayout.setRefreshing(false);
                            //arrivalsAdapter.setFooter(showAd);
                            if(arrivalsAdapter.getItemCount() == 0){
                                emptyText.setText(R.string.empty_server_message);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, e.getMessage());
                        }

                        @Override
                        public void onNext(Arrivals arrivals) {}
                    });
        }
    }

    public void refreshTask(){
        Observable.just(0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    fetchArrivals();
                    date = new Date(System.currentTimeMillis() - 30*1000);
                    arrivalsAdapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Utils.unsubscribe(fetchArrivalsSub);
    }
}