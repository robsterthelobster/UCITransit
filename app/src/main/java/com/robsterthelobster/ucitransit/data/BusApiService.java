package com.robsterthelobster.ucitransit.data;


import com.robsterthelobster.ucitransit.data.models.ArrivalData;
import com.robsterthelobster.ucitransit.data.models.RouteData;
import com.robsterthelobster.ucitransit.data.models.SegmentData;
import com.robsterthelobster.ucitransit.data.models.StopData;
import com.robsterthelobster.ucitransit.data.models.VehicleData;

import retrofit2.http.GET;
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

    @GET("vehicles.json")
    Observable<VehicleData> getVehicles(@Query("agencies") String agency);

    @GET("vehicles.json")
    Observable<VehicleData> getVehicles(@Query("agencies") String agency, @Query("routes") int route);

    @GET("arrival-estimates.json")
    Observable<ArrivalData> getArrivals(@Query("agencies") String agency);

    @GET("arrival-estimates.json")
    Observable<ArrivalData> getArrivals(@Query("agencies") String agency, @Query("routes") String route);

    @GET("arrival-estimates.json")
    Observable<ArrivalData> getArrivals(@Query("agencies") String agency, @Query("routes") String route, @Query("stops") int stop);

    @GET("arrival-estimates.json")
    Observable<ArrivalData> getArrivalsByStop(@Query("agencies") String agency, @Query("stops") int stop);

    @GET("segments.json")
    Observable<SegmentData> getSegments(@Query("agencies") String agency, @Query("routes") String route);
}