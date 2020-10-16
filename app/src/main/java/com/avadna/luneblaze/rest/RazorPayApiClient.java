package com.avadna.luneblaze.rest;

import com.avadna.luneblaze.helperClasses.AES;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RazorPayApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        String authToken = Credentials.basic(AES.decrypt(AppKeys.RAZ_KEY_LIVE, AppKeys.enKey),
                AES.decrypt(AppKeys.RAZ_SEC_LIVE, AppKeys.enKey));

        AuthenticationInterceptor authInterceptor =
                new AuthenticationInterceptor(authToken);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(authInterceptor)
                .readTimeout(90, TimeUnit.SECONDS)
                .connectTimeout(90, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.razorpay.com/v1/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        return retrofit;


    }
}
