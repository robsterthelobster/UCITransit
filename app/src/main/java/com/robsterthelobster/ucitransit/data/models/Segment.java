package com.robsterthelobster.ucitransit.data.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by robin on 10/27/2017.
 */

public class Segment extends RealmObject{

    // need a custom id as routes and segments are shared
    // storing duplicate segments but easier access
    @PrimaryKey
    private String id;
    private String routeId;
    private String segmentId;
    private String segmentCode;

    public String getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(String segmentId) {
        this.segmentId = segmentId;
    }

    public String getSegmentCode() {
        return segmentCode;
    }

    public void setSegmentCode(String segmentCode) {
        this.segmentCode = segmentCode;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
