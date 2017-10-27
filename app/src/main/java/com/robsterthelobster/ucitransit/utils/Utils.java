package com.robsterthelobster.ucitransit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.robsterthelobster.ucitransit.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public static String getTimeDifferenceInMinutes(Date date){
        return ((date.getTime() - new Date().getTime())/1000/60) + " min";
    }

    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}

