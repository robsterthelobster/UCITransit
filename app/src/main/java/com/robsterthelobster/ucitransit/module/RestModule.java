package com.robsterthelobster.ucitransit.module;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.robsterthelobster.ucitransit.BuildConfig;
import com.robsterthelobster.ucitransit.R;
import com.robsterthelobster.ucitransit.data.BusApiService;
import com.robsterthelobster.ucitransit.data.SegmentDeserializer;
import com.robsterthelobster.ucitransit.data.models.SegmentData;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.addInterceptor(chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader("X-Mashape-Key", BuildConfig.OPEN_API_KEY)
                    .addHeader("Accept", "application/json")
                    .build();
            return chain.proceed(request);
        });
        OkHttpClient client = builder.build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(SegmentData.class, new SegmentDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.root_url))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(BusApiService.class);
    }
}
