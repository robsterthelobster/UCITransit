package com.robsterthelobster.ucitransit;

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
}

