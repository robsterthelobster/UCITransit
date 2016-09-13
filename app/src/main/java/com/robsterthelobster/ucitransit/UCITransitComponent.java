package com.robsterthelobster.ucitransit;

import com.robsterthelobster.ucitransit.module.RestModule;
import com.robsterthelobster.ucitransit.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by robin on 9/9/2016.
 */
@Component(modules = {
        RestModule.class
})
@Singleton
public interface UCITransitComponent {
    void inject(MainActivity mainActivity);
}
