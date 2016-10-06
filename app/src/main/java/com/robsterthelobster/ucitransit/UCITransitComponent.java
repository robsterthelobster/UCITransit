package com.robsterthelobster.ucitransit;

import com.robsterthelobster.ucitransit.module.RealmModule;
import com.robsterthelobster.ucitransit.module.RestModule;
import com.robsterthelobster.ucitransit.ui.BusMapFragment;
import com.robsterthelobster.ucitransit.ui.DetailActivity;
import com.robsterthelobster.ucitransit.ui.MainActivity;
import com.robsterthelobster.ucitransit.ui.PredictionFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by robin on 9/9/2016.
 */
@Component(modules = {
        RestModule.class,
        RealmModule.class
})
@Singleton
public interface UCITransitComponent {
    void inject(MainActivity mainActivity);
    void inject(DetailActivity detailActivity);
    void inject(BusMapFragment busMapFragment);
    void inject(PredictionFragment predictionFragment);
}
