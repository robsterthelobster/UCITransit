package com.robsterthelobster.ucitransit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.robsterthelobster.ucitransit.R;

import rx.Subscription;

/**
 * Created by robin on 9/20/2016.
 */

public class Utils {

    public static void unsubscribe(Subscription subscription){
        if(subscription != null){
            subscription.unsubscribe();
        }
    }

    public static float getRotationFromDirection(String direction){
        switch (direction){
            case "N":
                return 270f;
            case "NW":
                return 225f;
            case "NE":
                return 315f;
            case "E":
                return 0f;
            case "S":
                return 90f;
            case "SE":
                return 45f;
            case "SW":
                return 135f;
            case "W":
                return 180f;
            default:
                return 0f;
        }
    }

    public static boolean isNetworkConnected(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static void setTheme(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String themePref = sharedPref.getString(context.getString(R.string.key_theme_pref), "");
        switch(themePref){
            case Constants.THEME_DARK:
                context.setTheme(R.style.AppTheme_Dark);
                break;
            case Constants.THEME_LIGHT:
                context.setTheme(R.style.AppTheme_Light);
                break;
            default:
                context.setTheme(R.style.AppTheme);

        }
    }
}

