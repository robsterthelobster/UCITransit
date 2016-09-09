package com.robsterthelobster.ucitransit;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

/**
 * Created by robin on 9/9/2016.
 */
@Module
public class UCITransitModule {
    final UCITransitApp app;

    public UCITransitModule(UCITransitApp app){
        this.app = app;
    }

    @Provides
    UCITransitApp provideUCITransitApp() {
        return app;
    }

    @Provides
    Application provideApplication(UCITransitApp app) {
        return app;
    }
}
