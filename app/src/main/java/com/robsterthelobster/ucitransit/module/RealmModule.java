package com.robsterthelobster.ucitransit.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by robin on 9/29/2016.
 */
@Module
public class RealmModule {

    public RealmModule(Context context) {
        Realm.init(context);
        RealmConfiguration realmConfig = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);

    }

    @Provides
    @Singleton
    Realm provideRealmInstance(){
        return Realm.getDefaultInstance();
    }
}
