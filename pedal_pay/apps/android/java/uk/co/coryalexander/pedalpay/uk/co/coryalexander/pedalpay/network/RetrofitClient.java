package uk.co.coryalexander.pedalpay.uk.co.coryalexander.pedalpay.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://www.matthewfrankland.co.uk/pedalPay/";
    private static RetrofitClient instance;

    public static Retrofit getRetrofitInstance() {
        if(retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        }
        return retrofit;
    }

    public static synchronized RetrofitClient getInstance() {
        if(instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public PostDataService getDataService() {
        return getRetrofitInstance().create(PostDataService.class);
    }
}
