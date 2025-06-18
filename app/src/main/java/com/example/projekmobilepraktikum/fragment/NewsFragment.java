package com.example.projekmobilepraktikum.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projekmobilepraktikum.adapter.NewsAdapter;
import com.example.projekmobilepraktikum.api.ApiClient;
import com.example.projekmobilepraktikum.database.AppDatabase;
import com.example.projekmobilepraktikum.database.FavoriteArticle;
import com.example.projekmobilepraktikum.databinding.FragmentNewsBinding;
import com.example.projekmobilepraktikum.model.Article;
import com.example.projekmobilepraktikum.model.NewsResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment untuk menampilkan daftar berita terbaru
 */
public class NewsFragment extends Fragment {

    private FragmentNewsBinding binding;
    private NewsAdapter newsAdapter;
    private AppDatabase appDatabase;
    private final Executor executor = Executors.newSingleThreadExecutor();

    // Variabel untuk menangani rate limit API
    private boolean isRateLimited = false;
    private long lastApiCallTime = 0;
    // Waktu tunggu minimal antar API call jika rate limit (12 jam dalam milidetik)
    private static final long RATE_LIMIT_COOLDOWN = 12 * 60 * 60 * 1000;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inisialisasi ViewBinding untuk fragment
        binding = FragmentNewsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Agar konten fragment tidak tertutup status bar, notch, dan navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(0, top, 0, bottom);
            return insets;
        });

        // Inisialisasi database lokal
        appDatabase = AppDatabase.getInstance(requireContext());

        setupRecyclerView();

        // refresh
        binding.swipeRefreshLayout.setOnRefreshListener(this::fetchNews);

        // Memuat data berita saat fragment pertama kali tampil
        fetchNews();
    }

    /**
     * Inisialisasi RecyclerView dan NewsAdapter
     */
    private void setupRecyclerView() {
        newsAdapter = new NewsAdapter(new ArrayList<>(), new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Article article) {
            }

            @Override
            public void onFavoriteClick(Article article) {
                // Toggle status favorit artikel
                toggleFavorite(article);
            }
        });

        // Set layout manager dan adapter untuk RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(newsAdapter);
    }

    /**
     * Mengambil data berita dari API, handle rate limit dan error
     */
    private void fetchNews() {
        // Cek apakah sedang dalam periode rate limit
        long currentTime = System.currentTimeMillis();
        if (isRateLimited && currentTime - lastApiCallTime < RATE_LIMIT_COOLDOWN) {
            // Masih dalam periode rate limit,
            binding.progressBar.setVisibility(View.GONE);
            binding.swipeRefreshLayout.setRefreshing(false);
            binding.tvError.setVisibility(View.VISIBLE);
            binding.tvError.setText("API rate limit exceeded. Please try again later (limit resets after 12 hours).");
            // Tidak perlu loadFromCache, hanya tampilkan pesan error
            return;
        }

        // Tampilkan loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvError.setVisibility(View.GONE);

        // Mendapatkan tanggal (7 hari terakhir)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = dateFormat.format(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        String fromDate = dateFormat.format(calendar.getTime());

        // Update waktu terakhir API call
        lastApiCallTime = currentTime;

        // Panggil API untuk mendapatkan berita
        ApiClient.getInstance().getNewsApiService().getNews(
                "samsung", // kata kunci pencarian
                fromDate,
                today,
                "publishedAt", // sortir berdasarkan tanggal publish terbaru
                ApiClient.getApiKey()
        ).enqueue(new Callback<NewsResponse>() {

            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                if (binding == null) return;

                // Sembunyikan loading
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    // Jika berhasil, tampilkan data
                    isRateLimited = false;
                    List<Article> articles = response.body().getArticles(); ///////////////////////////////
                    updateRecyclerView(articles);
                } else {
                    // Jika gagal karena rate limit (HTTP 429)
                    if (response.code() == 429) {
                        handleRateLimitError(response.errorBody());
                    } else {
                        // Jika gagal karena sebab lain
                        showError("Failed to load articles. Please try again.");
                    }
                }
            }

            // Jika gagal koneksi
            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                if (binding == null) return;

                // Sembunyikan loading
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing(false);

                // Tampilkan pesan error jaringan saja
                showError("Terjadi gangguan jaringan. Silakan periksa koneksi internet Anda.");
            }
        });
    }

    /**
     * Menangani error rate limit API (HTTP 429)
     */
    private void handleRateLimitError(ResponseBody errorBody) {
        String errorMessage = "API rate limit exceeded. Please try again later.";

        // Parse pesan error dari body jika ada
        if (errorBody != null) {
            try {
                String errorJson = errorBody.string();
                JSONObject errorObj = new JSONObject(errorJson);
                if (errorObj.has("message")) {
                    errorMessage = errorObj.getString("message");
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        // Set flag rate limit
        isRateLimited = true;

        // Tampilkan error di UI
        showError(errorMessage);
    }

    /**
     * Menampilkan pesan error di layar
     */
    private void showError(String message) {
        if (binding != null) {
            binding.tvError.setVisibility(View.VISIBLE);
            binding.tvError.setText(message);
        }
    }

    /**
     * Menambah/menghapus artikel dari favorit
     */
    private void toggleFavorite(Article article) {
        // Operasi database dijalankan di background thread (executor)
        executor.execute(() -> {
            try {
                // Cek status favorit artikel
                boolean isFavorite = appDatabase.favoriteArticleDao().isArticleFavorite(article.getUrl());

                if (isFavorite) {
                    // Jika sudah favorit, hapus dari database
                    FavoriteArticle favoriteArticle = new FavoriteArticle(
                            article.getUrl(),
                            article.getTitle(),
                            article.getAuthor(),
                            article.getDescription(),
                            article.getUrlToImage(),
                            article.getPublishedAt(),
                            article.getContent(),
                            article.getSource() != null ? article.getSource().getName() : "",
                            0L
                    );
                    appDatabase.favoriteArticleDao().delete(favoriteArticle);
                    article.setFavorite(false);
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Dihapus dari favorit", Toast.LENGTH_SHORT).show());
                } else {
                    // Jika belum favorit, tambahkan ke database
                    FavoriteArticle favoriteArticle = new FavoriteArticle(
                            article.getUrl(),
                            article.getTitle(),
                            article.getAuthor(),
                            article.getDescription(),
                            article.getUrlToImage(),
                            article.getPublishedAt(),
                            article.getContent(),
                            article.getSource() != null ? article.getSource().getName() : "",
                            System.currentTimeMillis() // waktu penambahan ke favorit
                    );
                    appDatabase.favoriteArticleDao().insert(favoriteArticle);
                    article.setFavorite(true);
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Ditambahkan ke favorit", Toast.LENGTH_SHORT).show());
                }

                // Refresh tampilan RecyclerView agar ikon favorit berubah
                requireActivity().runOnUiThread(() -> newsAdapter.notifyDataSetChanged());
            } catch (Exception e) {
                // Jika error, tetap refresh supaya UI konsisten
                requireActivity().runOnUiThread(() -> newsAdapter.notifyDataSetChanged());
            }
        });
    }

    /**
     * Menampilkan pesan error pada layar jika data gagal dimuat.
     * Jika data kosong, tampilkan pesan error di tengah layar.
     * Jika data sudah ada, tampilkan toast saja.
     */
    private void showError() {
        // Pastikan binding tidak null sebelum digunakan
        if (binding == null) return;

        if (newsAdapter.getItemCount() == 0) {
            // Tampilkan pesan error di tengah layar jika tidak ada data
            binding.tvError.setVisibility(View.VISIBLE);
        } else {
            // Jika data sudah ada, cukup tampilkan toast
            Toast.makeText(requireContext(),
                    "Gagal memperbarui berita. Tarik untuk mencoba lagi.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Update RecyclerView dengan data artikel baru
     * @param articles List artikel yang akan ditampilkan
     */
    private void updateRecyclerView(List<Article> articles) {
        // Cek binding sebelum update UI
        if (binding == null) return;

        if (articles != null && !articles.isEmpty()) {
            // Perbarui status favorit artikel
            checkFavoritesStatus(articles);

            // Update adapter dan sembunyikan pesan error
            newsAdapter.updateData(articles);
            binding.tvError.setVisibility(View.GONE);
        } else {
            // Jika list kosong, tampilkan pesan error
            binding.tvError.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Mengecek status favorit setiap artikel di database
     * @param articles List artikel yang akan diperiksa
     */
    private void checkFavoritesStatus(List<Article> articles) {
        executor.execute(() -> {
            for (Article article : articles) {
                boolean isFavorite = appDatabase.favoriteArticleDao().isArticleFavorite(article.getUrl());
                article.setFavorite(isFavorite);
            }
            // Update UI di thread utama agar adapter ikut ter-refresh
            if (isAdded() && getActivity() != null) {
                requireActivity().runOnUiThread(() -> {
                    if (newsAdapter != null) {
                        newsAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hapus binding untuk menghindari memory leak
        binding = null;
    }
}