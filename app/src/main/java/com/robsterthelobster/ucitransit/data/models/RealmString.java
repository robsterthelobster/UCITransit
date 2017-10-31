package com.robsterthelobster.ucitransit.data.models;

import io.realm.RealmObject;

/**
 * Created by robin on 9/12/2016.
 */
public class RealmString extends RealmObject{
    public String string;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
