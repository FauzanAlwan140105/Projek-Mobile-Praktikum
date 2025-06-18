package com.example.projekmobilepraktikum.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity class untuk menyimpan artikel favorit di database lokal
 * Room akan menggunakan kelas ini untuk membuat tabel di database SQLite
 */
@Entity(tableName = "favorite_articles")
public class FavoriteArticle {

    @PrimaryKey
    @NonNull
    private String url; // URL artikel digunakan sebagai primary key

    private String title;
    private String author;
    private String description;
    private String urlToImage;
    private String publishedAt;
    private String content;
    private String sourceName;
    private long addedAt;

    /**
     * Konstruktor untuk membuat instance favorit artikel
     */
    public FavoriteArticle(@NonNull String url, String title, String author,
                          String description, String urlToImage,
                          String publishedAt, String content, String sourceName, long addedAt) {
        this.url = url;
        this.title = title;
        this.author = author;
        this.description = description;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
        this.content = content;
        this.sourceName = sourceName;
        this.addedAt = addedAt;
    }

    // Getters dan Setters
    @NonNull
    public String getUrl() {
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public long getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(long addedAt) {
        this.addedAt = addedAt;
    }
}
