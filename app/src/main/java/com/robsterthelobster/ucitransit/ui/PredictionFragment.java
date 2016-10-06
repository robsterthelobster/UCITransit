package com.robsterthelobster.ucitransit.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robsterthelobster.ucitransit.R;
import com.robsterthelobster.ucitransit.UCITransitApp;
import com.robsterthelobster.ucitransit.data.ArrivalsAdapter;
import com.robsterthelobster.ucitransit.data.BusApiService;
import com.robsterthelobster.ucitransit.data.models.Arrivals;
import com.robsterthelobster.ucitransit.utils.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by robin on 10/3/2016.
 */
public class PredictionFragment extends Fragment {

    @BindView(R.id.fragment_recycler_view)
    RealmRecyclerView recyclerView;

    @Inject
    Realm realm;
    @Inject
    BusApiService apiService;

    public PredictionFragment() {}

    public static PredictionFragment newInstance(String routeName){
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
        String routeName = arguments.getString(Constants.ROUTE_ID_KEY);

        RealmResults<Arrivals> arrivals = realm
                .where(Arrivals.class)
                .equalTo("isCurrent", true)
                .equalTo("routeName", routeName)
                .findAll();

        ArrivalsAdapter arrivalsAdapter = new ArrivalsAdapter(getContext(), arrivals, true, false);
        recyclerView.setAdapter(arrivalsAdapter);

        return view;
    }
}