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

    private final Context context;

    public RealmModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    Realm provideRealmInstance(){
        RealmConfiguration realmConfig = new RealmConfiguration
                .Builder(context)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);
        return Realm.getDefaultInstance();
    }
}
