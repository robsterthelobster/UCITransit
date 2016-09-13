package com.robsterthelobster.ucitransit.data;

import com.robsterthelobster.ucitransit.data.models.*;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by robin on 6/21/2016.
 * Retrofit interface
 */
public interface BusApiService {
    @GET("Region/0/Routes")
    Observable<List<Route>> getRoutes();

    @GET("Route/{route}/Direction/0/Stops")
    Observable<List<Stop>> getStops(@Path("route") int route);

    @GET("Route/{route}/Stop/{stop}/Arrivals")
    Observable<Arrivals> getArrivalTimes(@Path("route") int route, @Path("stop") int stop);

    @GET("Route/{route}/Vehicles")
    Observable<List<Vehicle>> getVehicles(@Path("route") int route);
}