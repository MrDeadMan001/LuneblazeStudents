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

/**
 * Created by Sunny on 24-12-2017.
 */

public class ApiClientShortDuration {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if (AppKeys.BASE_URL.contains("luneblaze")) {
            String authToken = Credentials.basic(AES.decrypt(AppKeys.API_AUTH_USERNAME, AppKeys.enKey),
                    AES.decrypt(AppKeys.API_AUTH_PASSWORD, AppKeys.enKey));

            AuthenticationInterceptor authInterceptor =
                    new AuthenticationInterceptor(authToken);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .addInterceptor(authInterceptor)
                    .readTimeout(AppKeys.API_TIMEOUT_SHORT, TimeUnit.SECONDS)
                    .connectTimeout(AppKeys.API_TIMEOUT_SHORT, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(AppKeys.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();

            return retrofit;

        } else {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .readTimeout(AppKeys.API_TIMEOUT_SHORT, TimeUnit.SECONDS)
                    .connectTimeout(AppKeys.API_TIMEOUT_SHORT, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(AppKeys.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
            return retrofit;
        }
    }
}

