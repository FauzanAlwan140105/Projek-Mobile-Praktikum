package com.example.projekmobilepraktikum.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Kelas model untuk respons dari News API
 * Merepresentasikan struktur data yang diterima dari API
 */
public class NewsResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("totalResults")
    private int totalResults;

    @SerializedName("articles")
    private List<Article> articles;

    // Getter dan setter
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}
