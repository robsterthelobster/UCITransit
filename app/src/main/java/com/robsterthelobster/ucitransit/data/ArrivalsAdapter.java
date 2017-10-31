package com.robsterthelobster.ucitransit.data;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.ftinc.scoop.Scoop;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.robsterthelobster.ucitransit.R;
import com.robsterthelobster.ucitransit.data.models.Arrivals;
import com.robsterthelobster.ucitransit.data.models.Route;
import com.robsterthelobster.ucitransit.data.models.Stop;
import com.robsterthelobster.ucitransit.utils.Utils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by robin on 9/20/2016.
 */

public class ArrivalsAdapter
        extends RealmRecyclerViewAdapter<Arrivals, RecyclerView.ViewHolder> {

    private Realm realm;
    private boolean routeColorOn = false;
    private boolean showAd = true;

    private final static int ARRIVALS_VIEW_TYPE = 0;
    private final static int AD_VIEW_TYPE = 1;

    public ArrivalsAdapter(@Nullable OrderedRealmCollection<Arrivals> data, boolean autoUpdate, boolean updateOnModification, Realm realm) {
        super(data, autoUpdate, updateOnModification);
        this.realm = realm;
        if (Scoop.getInstance().getCurrentFlavor().getName().contains("Route")) {
            routeColorOn = true;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case AD_VIEW_TYPE:
                View adCardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_view, parent, false);
                return new AdViewHolder(adCardView);
            case ARRIVALS_VIEW_TYPE:
            default:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.prediction_card, parent, false);
                return new ArrivalsHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);

        switch (viewType) {
            case AD_VIEW_TYPE:
                AdViewHolder adViewHolder = (AdViewHolder) holder;
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice("D29968901604B90992E8590DBB4904C7")
                        .build();
                adViewHolder.adView.loadAd(adRequest);
                break;
            case ARRIVALS_VIEW_TYPE:
            default:
                ArrivalsHolder arrivalsHolder = (ArrivalsHolder) holder;
                final Arrivals arrivals = getItem(position);

                Route route = null;
                Stop stop = null;

                if (arrivals != null) {
                    route = arrivals.getRoute();
                    stop = arrivals.getStop();
                }

                if (arrivals != null && stop != null && route != null) {
                    arrivalsHolder.arrivals = arrivals;

                    String arrivalTimeString = "NA";
                    String secondaryArrivalTimeString = "NA";
                    Date arrivalTime = arrivals.getArrivalTime();
                    Date secondaryTime = arrivals.getSecondaryArrivalTime();

                    if (routeColorOn) {
                        arrivalsHolder.cardView.setBackgroundColor(Color.parseColor(route.getColor()));
                    }

                    if (arrivalTime != null) {
                        arrivalTimeString = Utils.getTimeDifferenceInMinutes(arrivalTime);
                    }
                    if (secondaryTime != null) {
                        secondaryArrivalTimeString = Utils.getTimeDifferenceInMinutes(secondaryTime);
                    }
                    arrivalsHolder.routeText.setText(route.getShortName() + " " + route.getLongName());
                    arrivalsHolder.arrivalText.setText(arrivalTimeString);
                    arrivalsHolder.secondaryArrivalText.setText(secondaryArrivalTimeString);
                    arrivalsHolder.stopText.setText(stop.getName());
                    arrivalsHolder.favoriteCheck.setOnCheckedChangeListener(null);
                    arrivalsHolder.favoriteCheck.setChecked(arrivals.isFavorite());
                    arrivalsHolder.favoriteCheck.setOnCheckedChangeListener(
                            (checkBox, checked) -> {
                                checkBox.setChecked(checked);
                                realm.executeTransaction(r -> {
                                    arrivals.setFavorite(checked);
                                    r.copyToRealmOrUpdate(arrivals);
                                });
                                //notifyDataSetChanged();
                            });
                }
        }
    }

    @Override
    public int getItemCount() {
        super.getItemCount();
        if (getData() == null || !getData().isValid() || getData().size() == 0) {
            return 0;
        }

        if(showAd){
            return getData().size() + 1;
        }else{
            return getData().size();
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1 && showAd) {
            return AD_VIEW_TYPE;
        }
        return super.getItemViewType(position);
    }

    public void setShowAd(boolean showAd){
        this.showAd = showAd;
    }

    class ArrivalsHolder extends RecyclerView.ViewHolder {

        private float EXPAND_CARD_RATIO = 0.33f;

        Arrivals arrivals;

        @BindView(R.id.card_view)
        CardView cardView;
        @BindView(R.id.prediction_route_name)
        TextView routeText;
        @BindView(R.id.prediction_stop_name)
        TextView stopText;
        @BindView(R.id.prediction_favorite_button)
        AppCompatCheckBox favoriteCheck;
        @BindView(R.id.prediction_arrival_time)
        TextView arrivalText;
        @BindView(R.id.prediction_arrival_time_alt)
        TextView secondaryArrivalText;

        private int originalHeight = 0;
        private int expandingHeight = 0;
        private boolean isViewExpanded = false;

        ArrivalsHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                int[][] states = new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}};
                int[] colors = new int[]{
                        view.getContext().getResources().getColor(android.R.color.primary_text_light),
                        view.getContext().getResources().getColor(R.color.colorAccent)
                };
                favoriteCheck.setSupportButtonTintList(new ColorStateList(states, colors));
            }

            cardView.setOnClickListener(view1 -> {
                if(!(view1 instanceof AppCompatCheckBox) || !view1.equals(favoriteCheck)){
                    expandCard(view1);
                }
            });
            if (!isViewExpanded) {
                secondaryArrivalText.setVisibility(View.GONE);
            }
        }

        void expandCard(View view) {
            if (originalHeight == 0) {
                originalHeight = view.getHeight();
                expandingHeight = (int) (originalHeight * EXPAND_CARD_RATIO);
            }

            ValueAnimator valueAnimator;
            if (!isViewExpanded) {
                secondaryArrivalText.setVisibility(View.VISIBLE);
                valueAnimator = ValueAnimator.ofInt(originalHeight,
                        originalHeight + expandingHeight);
                isViewExpanded = true;
            } else {
                isViewExpanded = false;
                valueAnimator = ValueAnimator.ofInt(originalHeight + expandingHeight,
                        originalHeight);

                Animation a = new AlphaAnimation(1.00f, 1.00f); // Fade out
                a.setDuration(100);
                a.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        secondaryArrivalText.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                cardView.startAnimation(a);
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

    class AdViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.adView)
        AdView adView;

        public AdViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
