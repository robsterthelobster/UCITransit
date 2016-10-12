package com.robsterthelobster.ucitransit.module;

import android.content.Context;

import com.robsterthelobster.ucitransit.R;
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

    private final Context context;

    public RestModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    BusApiService provideApiService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.root_url))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(BusApiService.class);
    }
}
