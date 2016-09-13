package com.robsterthelobster.ucitransit.module;

import com.robsterthelobster.ucitransit.data.BusApiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by robin on 9/13/2016.
 */
@Module
public class RestModule {

    private final String BASE_URL = "http://www.ucishuttles.com/";

    @Provides
    @Singleton
    BusApiService provideApiService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(BusApiService.class);
    }
}
