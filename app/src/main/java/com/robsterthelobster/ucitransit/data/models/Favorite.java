package com.robsterthelobster.ucitransit.data.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by robin on 9/16/2016.
 */
public class Favorite extends RealmObject{

    // this will be route + stop
    @PrimaryKey
    private Integer id;
    private Integer routeId;
    private Integer stopId;

}
