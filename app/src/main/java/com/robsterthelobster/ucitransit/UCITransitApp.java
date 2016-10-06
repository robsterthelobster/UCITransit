package com.robsterthelobster.ucitransit;

import android.app.Application;
import android.content.Context;

import com.robsterthelobster.ucitransit.module.RealmModule;
import com.robsterthelobster.ucitransit.module.RestModule;

/**
 * Created by robin on 9/9/2016.
 */
public class UCITransitApp extends Application {

    private UCITransitComponent component;

    @Override
    public void onCreate(){
        super.onCreate();

        component = DaggerUCITransitComponent
                .builder()
                .realmModule(new RealmModule(this))
                .restModule(new RestModule())
                .build();
    }

    public static UCITransitComponent getComponent(Context context){
        return ((UCITransitApp) context.getApplicationContext()).component;
    }
}
