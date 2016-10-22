package com.robsterthelobster.ucitransit.data;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ftinc.scoop.Scoop;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.robsterthelobster.ucitransit.R;
import com.robsterthelobster.ucitransit.data.models.Arrivals;
import com.robsterthelobster.ucitransit.data.models.Prediction;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

/**
 * Created by robin on 9/20/2016.
 */

public class ArrivalsAdapter
        extends RealmBasedRecyclerViewAdapter<Arrivals, ArrivalsAdapter.ViewHolder> {

    private Realm realm;
    private boolean routeColorOn = false;

    public ArrivalsAdapter(Context context, RealmResults<Arrivals> realmResults, boolean automaticUpdate, boolean animateResults, Realm realm) {
        super(context, realmResults, automaticUpdate, animateResults);
        this.realm = realm;
        if(Scoop.getInstance().getCurrentFlavor().getName().contains("Route")){
            routeColorOn = true;
        }
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate((R.layout.prediction_card), viewGroup, false);
        return new ViewHolder((LinearLayout) view);
    }

    @Override
    public void onBindRealmViewHolder(ArrivalsAdapter.ViewHolder viewHolder, int position) {
        final Arrivals arrivals = realmResults.get(position);
        RealmList<Prediction> predictionRealmList = arrivals.getPredictions();
        String minutes = "NA";
        String secondaryMinutes = "NA";
        int size = predictionRealmList.size();
        if (size > 0) {
            minutes = predictionRealmList.get(0).getMinutes() + " min";
            if (size > 1) {
                secondaryMinutes = predictionRealmList.get(1).getMinutes() + " min";
            }
        }
        // hot fix -- hardcode the weird name in
        if(arrivals.getRouteId() == 3164){
            viewHolder.routeText.setText("A Route A - INNOVATION AND IBW");
        }else{
            viewHolder.routeText.setText(arrivals.getRouteName());
        }
        if(routeColorOn) {
            viewHolder.cardView.setBackgroundColor(Color.parseColor(arrivals.getColor()));
        }
        viewHolder.arrivalText.setText(minutes);
        viewHolder.stopText.setText(String.valueOf(arrivals.getStopName()));
        viewHolder.secondaryArrivalText.setText(secondaryMinutes);
        viewHolder.favoriteCheck.setOnCheckedChangeListener(null);
        viewHolder.favoriteCheck.setChecked(arrivals.isFavorite());
        viewHolder.favoriteCheck.setOnCheckedChangeListener(
                (checkBox, checked) -> {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && checked) {
                        checkBox.setButtonDrawable(R.drawable.ic_star_yellow);
                    }else{
                        checkBox.setChecked(checked);
                    }
                    realm.executeTransaction(r -> {
                        arrivals.setFavorite(checked);
                        r.copyToRealmOrUpdate(arrivals);
                    });
                });
    }

    @Override
    public void onBindFooterViewHolder(ViewHolder holder, int position) {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("D29968901604B90992E8590DBB4904C7")
                .build();
        holder.adView.loadAd(adRequest);
    }

    @Override
    public ViewHolder onCreateFooterViewHolder(ViewGroup viewGroup) {
        View v = inflater.inflate(R.layout.ad_view, viewGroup, false);
        return new ViewHolder((FrameLayout) v);
    }

    public void setFooter(boolean showAd) {
        if(showAd && realmResults.size() > 0){
            addFooter();
        }else{
            removeFooter();
        }
    }

    class ViewHolder extends RealmViewHolder {
        private float EXPAND_CARD_RATIO = 0.33f;

        LinearLayout container;
        @BindView(R.id.card_view)
        CardView cardView;
        @BindView(R.id.prediction_route_name)
        TextView routeText;
        @BindView(R.id.prediction_stop_name)
        TextView stopText;
        @BindView(R.id.prediction_favorite_button)
        CheckBox favoriteCheck;
        @BindView(R.id.prediction_arrival_time)
        TextView arrivalText;
        @BindView(R.id.prediction_arrival_time_alt)
        TextView secondaryArrivalText;

        @Nullable
        AdView adView;

        private int originalHeight = 0;
        private int expandingHeight = 0;
        private boolean isViewExpanded = false;

        ViewHolder(FrameLayout container) {
            super(container);
            adView = (AdView) container.findViewById(R.id.adView);
        }

        ViewHolder(LinearLayout container) {
            super(container);
            this.container = container;
            ButterKnife.bind(this, container);

            cardView.setOnClickListener(this::expandCard);
            if (!isViewExpanded) {
                secondaryArrivalText.setVisibility(View.GONE);
                secondaryArrivalText.setEnabled(false);
            }
        }

        void expandCard(View view){
            if (originalHeight == 0) {
                originalHeight = view.getHeight();
                expandingHeight = (int)(originalHeight * EXPAND_CARD_RATIO);
            }

            ValueAnimator valueAnimator;
            if (!isViewExpanded) {
                secondaryArrivalText.setVisibility(View.VISIBLE);
                secondaryArrivalText.setEnabled(true);
                isViewExpanded = true;
                valueAnimator = ValueAnimator.ofInt(originalHeight,
                        originalHeight + expandingHeight);
            } else {
                isViewExpanded = false;
                valueAnimator = ValueAnimator.ofInt(originalHeight + expandingHeight,
                        originalHeight);

                Animation a = new AlphaAnimation(1.00f, 0.00f); // Fade out
                a.setDuration(100);
                a.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        secondaryArrivalText.setVisibility(View.GONE);
                        secondaryArrivalText.setEnabled(false);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                secondaryArrivalText.startAnimation(a);
            }
            valueAnimator.setDuration(100);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.addUpdateListener(animation -> {
                view.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                view.requestLayout();
            });
            valueAnimator.start();
        }
    }
}
