package com.example.projekmobilepraktikum.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Database utama aplikasi yang mengelola penyimpanan artikel favorit
 * Menggunakan Room Database untuk menyimpan data secara lokal
 */
@Database(entities = {FavoriteArticle.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Nama database
    private static final String DATABASE_NAME = "news_app_db";
    private static AppDatabase instance;

    public abstract FavoriteArticleDao favoriteArticleDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            // Migrasi dari versi 1 ke 2: tambah kolom addedAt
            Migration MIGRATION_1_2 = new Migration(1, 2) {
                @Override
                public void migrate(@NonNull SupportSQLiteDatabase database) {
                    database.execSQL("ALTER TABLE favorite_articles ADD COLUMN addedAt INTEGER DEFAULT 0 NOT NULL");
                }
            };
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration() // Menghapus dan membuat ulang database jika versi berubah
                    .build();
        }
        return instance;
    }
}
