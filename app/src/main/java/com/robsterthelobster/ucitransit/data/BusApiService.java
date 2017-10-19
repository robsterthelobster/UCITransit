package com.robsterthelobster.ucitransit.data;


import com.robsterthelobster.ucitransit.BuildConfig;
import com.robsterthelobster.ucitransit.data.models.Route;
import com.robsterthelobster.ucitransit.data.models.RouteData;
import com.robsterthelobster.ucitransit.data.models.Stop;
import com.robsterthelobster.ucitransit.data.models.StopData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by robin on 6/21/2016.
 * Retrofit interface
 */
public interface BusApiService {

    @GET("routes.json")
    Observable<RouteData> getRoutes(@Query("agencies") String agency);

    @GET("stops.json")
    Observable<StopData> getStops(@Query("agencies") String agency);

}