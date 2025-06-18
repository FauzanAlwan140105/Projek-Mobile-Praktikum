package com.example.projekmobilepraktikum.api;

import com.example.projekmobilepraktikum.model.NewsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface untuk definisi endpoint API
 * Menggunakan Retrofit untuk mengakses NewsAPI
 */
public interface NewsApiService {

    // Mendapatkan berita berdasarkan kategori
    @GET("v2/everything")
    Call<NewsResponse> getNews(
            @Query("q") String query,
            @Query("from") String fromDate,
            @Query("to") String toDate,
            @Query("sortBy") String sortBy,
            @Query("apiKey") String apiKey
    );
    @GET("v2/everything")
    Call<NewsResponse> searchNews(
            @Query("q") String query,
            @Query("apiKey") String apiKey
    );
}
