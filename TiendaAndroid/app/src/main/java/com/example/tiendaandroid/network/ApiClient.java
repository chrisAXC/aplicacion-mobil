package com.example.tiendaandroid.network;

import android.content.Context;
import com.example.tiendaandroid.utils.Constants;
import com.example.tiendaandroid.utils.SharedPrefManager;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    public static OkHttpClient getHttpClient(Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor tokenInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
                String token = sharedPrefManager.getToken();

                Request.Builder builder = originalRequest.newBuilder();

                if (token != null && !token.isEmpty()) {
                    builder.addHeader("Authorization", "Bearer " + token);
                }

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        };

        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(tokenInterceptor)
                .build();
    }

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(getHttpClient(context))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getService(Context context) {
        return getClient(context).create(ApiService.class);
    }
}