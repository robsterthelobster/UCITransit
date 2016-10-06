package com.robsterthelobster.ucitransit.utils;

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

    /**
    * Direction comes in shorthand N,E,S,W and combination
    * The icon used is already facing right so E = 0
    */
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
}

