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

/**
 * Created by robin on 10/3/2016.
 */
public class PredictionFragment extends Fragment {

    @BindView(R.id.fragment_recycler_view)
    RealmRecyclerView recyclerView;

    Realm realm;

    public PredictionFragment() {
    }

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

        Bundle arguments = getArguments();
        String routeName = arguments.getString(Constants.ROUTE_ID_KEY);

        realm = Realm.getDefaultInstance();
        RealmResults<Prediction> predictions = realm
                .where(Prediction.class)
                .equalTo("isCurrent", true)
                .equalTo("routeName", routeName)
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