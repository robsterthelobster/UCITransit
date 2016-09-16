package com.robsterthelobster.ucitransit.module;

import com.robsterthelobster.ucitransit.data.BusApiService;

import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by robin on 9/13/2016.
 */
@Module
public class RestModule {

    private final String BASE_URL = "https://www.ucishuttles.com/";

    @Provides
    @Singleton
    BusApiService provideApiService(){
        OkHttpClient client = new OkHttpClient.Builder().hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                //.client(client)
                .build();

        return retrofit.create(BusApiService.class);
    }
}
