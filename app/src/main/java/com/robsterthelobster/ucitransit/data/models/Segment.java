package com.robsterthelobster.ucitransit.data.models;

import io.realm.RealmObject;

/**
 * Created by robin on 10/16/2017.
 */

public class Segment extends RealmObject{

    public int routeID;
    public String direction;
}
