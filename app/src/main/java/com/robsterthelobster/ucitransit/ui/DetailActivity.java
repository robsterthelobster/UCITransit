package com.robsterthelobster.ucitransit.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.robsterthelobster.ucitransit.DaggerUCITransitComponent;
import com.robsterthelobster.ucitransit.R;
import com.robsterthelobster.ucitransit.UCITransitApp;
import com.robsterthelobster.ucitransit.UCITransitComponent;
import com.robsterthelobster.ucitransit.module.RealmModule;
import com.robsterthelobster.ucitransit.module.RestModule;
import com.robsterthelobster.ucitransit.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private final String TAG = DetailActivity.class.getSimpleName();

    @BindView(R.id.viewpager) ViewPager mViewPager;
    @BindView(R.id.detail_toolbar) Toolbar toolbar;
    @BindView(R.id.sliding_tabs) TabLayout tabLayout;

    static String routeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        routeName = bundle.getString(Constants.ROUTE_ID_KEY);
        Log.d(TAG, "route: " + routeName);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setTitle(routeName);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return PredictionFragment.newInstance(routeName);
                case 1:
                    return BusMapFragment.newInstance(routeName);
                default:
                    Log.e(TAG, "Not a valid position");
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Stops";
                case 1:
                    return "Route Map";
            }
            return null;
        }
    }
}
