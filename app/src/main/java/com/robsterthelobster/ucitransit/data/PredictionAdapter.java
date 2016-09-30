package com.robsterthelobster.ucitransit.data;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robsterthelobster.ucitransit.R;
import com.robsterthelobster.ucitransit.data.models.Prediction;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

/**
 * Created by robin on 9/20/2016.
 */

public class PredictionAdapter
        extends RealmBasedRecyclerViewAdapter<Prediction, PredictionAdapter.ViewHolder>{

    public PredictionAdapter(Context context, RealmResults<Prediction> realmResults, boolean automaticUpdate, boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate((R.layout.prediction_card), viewGroup, false);
        return new ViewHolder((LinearLayout) view);
    }

    @Override
    public void onBindRealmViewHolder(PredictionAdapter.ViewHolder viewHolder, int position) {
        final Prediction prediction = realmResults.get(position);
        String toDoFormat = prediction.getMinutes() + " min";
        viewHolder.cardView.setBackgroundColor(Color.parseColor(prediction.getColor()));
        viewHolder.arrivalText.setText(toDoFormat);
        viewHolder.routeText.setText(prediction.getRouteName());
        viewHolder.stopText.setText(String.valueOf(prediction.getStopName()));
    }

    public class ViewHolder extends RealmViewHolder{

        LinearLayout container;
        @BindView(R.id.card_view) CardView cardView;
        @BindView(R.id.prediction_route_name) TextView routeText;
        @BindView(R.id.prediction_stop_name) TextView stopText;
        @BindView(R.id.prediction_favorite_button) CheckBox favoriteCheck;
        @BindView(R.id.prediction_arrival_time) TextView arrivalText;

        public ViewHolder(LinearLayout container) {
            super(container);
            this.container = container;
            ButterKnife.bind(this, container);
        }
    }
}
