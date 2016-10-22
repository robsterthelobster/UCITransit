package com.robsterthelobster.ucitransit;

import android.app.Application;
import android.content.Context;
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
                .addFlavor(getString(R.string.title_og_light), R.style.AppTheme_Light, true)
                .addFlavor(getString(R.string.title_og_dark), R.style.AppTheme_Dark)
                .addFlavor(getString(R.string.title_theme_light), R.style.Theme_Scoop_Light)
                .addFlavor(getString(R.string.title_theme_dark), R.style.AppTheme_Dark)
                .addFlavor("Alternate 1", R.style.Theme_Scoop_Alt1)
                .addFlavor("Alternate 2", R.style.Theme_Scoop_Alt2)
                .setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this))
                .initialize();
    }

    public static UCITransitComponent getComponent(Context context){
        return ((UCITransitApp) context.getApplicationContext()).component;
    }


}
