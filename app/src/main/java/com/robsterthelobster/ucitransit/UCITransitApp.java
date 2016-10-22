package com.robsterthelobster.ucitransit;

import android.app.Application;
import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.ftinc.scoop.Scoop;
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
                .restModule(new RestModule(this))
                .build();

        Scoop.waffleCone()
                .addFlavor("Light with Route Colors", R.style.AppTheme_Light)
                .addFlavor("Dark with Route Colors", R.style.AppTheme_Dark)
                .addFlavor("Light", R.style.Theme_Scoop_Light)
                .addFlavor("Dark", R.style.AppTheme_Dark)
                .addFlavor("Android Default", R.style.AppTheme_Default, true)
                .addFlavor("Alternate 1", R.style.Theme_Scoop_Alt1)
                .addFlavor("Alternate 2", R.style.Theme_Scoop_Alt2)
                .setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this))
                .initialize();
    }

    public static UCITransitComponent getComponent(Context context){
        return ((UCITransitApp) context.getApplicationContext()).component;
    }


}
