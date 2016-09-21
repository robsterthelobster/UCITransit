package com.robsterthelobster.ucitransit;

import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by robin on 9/20/2016.
 */

public abstract class BaseRealmTest {

    Realm realm;

    @Before
    public void initRealm(){
        RealmConfiguration config =
                new RealmConfiguration.Builder(InstrumentationRegistry.getContext()).build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
    }

    @After
    public void cleanRealm(){
        realm.deleteAll();
    }
}
