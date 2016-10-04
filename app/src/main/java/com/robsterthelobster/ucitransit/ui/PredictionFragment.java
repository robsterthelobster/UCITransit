package com.robsterthelobster.ucitransit.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robsterthelobster.ucitransit.R;
import com.robsterthelobster.ucitransit.data.PredictionAdapter;
import com.robsterthelobster.ucitransit.data.models.Prediction;
import com.robsterthelobster.ucitransit.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;

public class PredictionFragment extends Fragment {

    @BindView(R.id.fragment_recycler_view) RealmRecyclerView recyclerView;

    int routeId;
    Realm realm;

    public PredictionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prediction_list, container, false);
        ButterKnife.bind(this, view);

        Bundle arguments = getArguments();
        if (arguments != null) {
            routeId = arguments.getInt(Constants.ROUTE_ID_KEY);
        }

        realm = Realm.getDefaultInstance();
        RealmResults<Prediction> predictions = realm
                .where(Prediction.class)
                .equalTo("isCurrent", true)
                .equalTo("routeId", routeId)
                .findAll();

        PredictionAdapter predictionAdapter = new PredictionAdapter(getContext(), predictions, true, false);
        recyclerView.setAdapter(predictionAdapter);

        return view;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        realm.close();
    }

}
