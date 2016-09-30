package com.robsterthelobster.ucitransit;

import com.robsterthelobster.ucitransit.module.RealmModule;
import com.robsterthelobster.ucitransit.module.RestModule;
import com.robsterthelobster.ucitransit.ui.DetailActivity;
import com.robsterthelobster.ucitransit.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;
import io.realm.Realm;

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
}
