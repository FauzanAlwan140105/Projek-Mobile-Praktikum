package com.example.projekmobilepraktikum.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * DAO (Data Access Object) untuk mengakses dan memanipulasi data favorit artikel
 */
@Dao
public interface FavoriteArticleDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteArticle article);

    @Delete
    void delete(FavoriteArticle article);

    @Query("DELETE FROM favorite_articles WHERE url = :url")
    void deleteByUrl(String url);


     //Mengambil semua artikel favorit

    @Query("SELECT * FROM favorite_articles ORDER BY addedAt DESC")
    LiveData<List<FavoriteArticle>> getAllFavoriteArticles();

    /**
     * Memeriksa apakah artikel sudah difavoritkan berdasarkan URL
     * @param url URL artikel yang dicari
     * @return true jika artikel sudah difavoritkan, false jika belum
     */
    @Query("SELECT EXISTS (SELECT 1 FROM favorite_articles WHERE url = :url)")
    boolean isArticleFavorite(String url);

    // Mencari artikel favorit berdasarkan judul
    @Query("SELECT * FROM favorite_articles WHERE title LIKE '%' || :searchQuery || '%'")
    List<FavoriteArticle> searchFavorites(String searchQuery);

    @Query("SELECT * FROM favorite_articles ORDER BY addedAt DESC")
    List<FavoriteArticle> getAllFavoriteArticlesDirectly();
}
