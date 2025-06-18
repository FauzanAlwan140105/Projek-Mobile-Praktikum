package com.example.projekmobilepraktikum.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Kelas untuk mengelola koneksi API
 * Menggunakan pola Singleton untuk memastikan hanya ada satu instance
 */
public class ApiClient {

    private static final String BASE_URL = "https://newsapi.org/";
    private static final String API_KEY = "85ee3ed826c547258f7997b06fddf910";

    private static ApiClient instance;
    private final NewsApiService newsApiService;

    /**
     * Konstruktor private untuk Singleton pattern
     */
    private ApiClient() {
        // menampilkan semua aktivitas HTTP
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder() // untuk kirim/terima data dari API.
                .addInterceptor(loggingInterceptor)
                .build();

        // Membuat Retrofit
        Retrofit retrofit = new Retrofit.Builder()  // penghubung UTAMA ke API
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()) // Mengubah json ke objek java
                .build();

        // objek hasil dari Retrofit
        newsApiService = retrofit.create(NewsApiService.class);
    }

    /**
     * Mendapatkan instance singleton dari ApiClient
     * @return instance ApiClient
     */

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }


    public NewsApiService getNewsApiService() {
        return newsApiService;
    }

    public static String getApiKey() {
        return API_KEY;
    }
}
