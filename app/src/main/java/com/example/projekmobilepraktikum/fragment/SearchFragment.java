package com.example.projekmobilepraktikum.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projekmobilepraktikum.adapter.NewsAdapter;
import com.example.projekmobilepraktikum.api.ApiClient;
import com.example.projekmobilepraktikum.database.AppDatabase;
import com.example.projekmobilepraktikum.database.FavoriteArticle;
import com.example.projekmobilepraktikum.databinding.FragmentSearchBinding;
import com.example.projekmobilepraktikum.model.Article;
import com.example.projekmobilepraktikum.model.NewsResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment untuk melakukan pencarian berita dan menampilkan hasilnya
 */
public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private NewsAdapter newsAdapter;
    // Objek database lokal
    private AppDatabase appDatabase;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Menggunakan ViewBinding untuk inflate layout
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi database lokal
        appDatabase = AppDatabase.getInstance(requireContext());

        setupRecyclerView();

        // Setup fungsi pencarian
        setupSearch();
    }

    /**
     * Menginisialisasi RecyclerView dan menghubungkan dengan adapter
     */

    private void setupRecyclerView() {
        //
        newsAdapter = new NewsAdapter(new ArrayList<>(), new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Article article) {
            }

            @Override
            public void onFavoriteClick(Article article) {
                // Saat user klik ikon favorit
                toggleFavorite(article);
            }
        });

        // Mengatur layout RecyclerView
        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSearchResults.setAdapter(newsAdapter);
    }

    /**
     * Mengatur fungsi pencarian berdasarkan input pengguna
     */
    private void setupSearch() {
        // Tombol untuk menghapus teks pencarian
        binding.btnClearSearch.setOnClickListener(v -> {
            binding.etSearch.setText("");
            binding.btnClearSearch.setVisibility(View.GONE);
            hideKeyboard();
        });

        // Tampilkan tombol clear saat user mulai mengetik
        binding.etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Menangani aksi "Search" pada keyboard
        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = binding.etSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchNews(query); // Lakukan pencarian
                    hideKeyboard();
                    return true;
                } else {
                    Toast.makeText(requireContext(), "Masukkan kata kunci pencarian", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        });
    }

    /**
     * Menyembunyikan keyboard setelah pencarian
     */
    private void hideKeyboard() {
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager)
                requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(binding.etSearch.getWindowToken(), 0);
        }
    }

    /**
     * Fungsi utama untuk mencari berita dari API berdasarkan keyword
     */
    private void searchNews(String query) {
        // Tampilkan loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutEmptyState.setVisibility(View.GONE);

        ApiClient.getInstance().getNewsApiService().searchNews(
                query,
                ApiClient.getApiKey()
        ).enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    // Ambil artikel dari response
                    List<Article> articles = response.body().getArticles();

                    // Filter berdasarkan keyword di title
                    List<Article> filtered = new ArrayList<>();
                    for (Article article : articles) {
                        if (article.getTitle() != null && article.getTitle().toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(article);
                        }
                    }
                    updateRecyclerView(filtered);
                } else {
                    // Gagal mendapatkan data dari API
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                // Tampilkan pesan error jaringan saja, tanpa fallback ke database lokal
                showNetworkError();
            }
        });
    }

    /**
     * Menampilkan layout kosong jika tidak ada hasil
     */
    private void showEmptyState() {
        if (newsAdapter.getItemCount() == 0) {
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Menampilkan pesan error jaringan di layar
     */
    private void showNetworkError() {
        binding.layoutEmptyState.setVisibility(View.VISIBLE);
        Toast.makeText(requireContext(), "Terjadi gangguan jaringan. Silakan periksa koneksi internet Anda.", Toast.LENGTH_LONG).show();
    }

    /**
     * Mengupdate data di RecyclerView
     */
    private void updateRecyclerView(List<Article> articles) {
        if (articles != null && !articles.isEmpty()) {
            checkFavoritesStatus(articles); // Update status favorit
            newsAdapter.updateData(articles); // Tampilkan data
            binding.layoutEmptyState.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
            newsAdapter.updateData(new ArrayList<>());
        }
    }

    /**
     * Mengecek apakah artikel sudah difavoritkan
     */
    private void checkFavoritesStatus(List<Article> articles) {
        executor.execute(() -> {
            for (Article article : articles) {
                boolean isFavorite = appDatabase.favoriteArticleDao().isArticleFavorite(article.getUrl());
                article.setFavorite(isFavorite);
            }

            // Update UI di main thread
            requireActivity().runOnUiThread(() -> newsAdapter.notifyDataSetChanged());
        });
    }

    /**
     * Menambahkan atau menghapus artikel dari favorit
     */
    private void toggleFavorite(Article article) {
        executor.execute(() -> {
            try {
                boolean isFavorite = appDatabase.favoriteArticleDao().isArticleFavorite(article.getUrl());

                if (isFavorite) {
                    // Hapus dari favorit
                    appDatabase.favoriteArticleDao().deleteByUrl(article.getUrl());
                    requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Dihapus dari favorit", Toast.LENGTH_SHORT).show()
                    );
                } else {
                    // Tambahkan ke favorit
                    FavoriteArticle favoriteArticle = createFavoriteArticleFromArticle(article);
                    appDatabase.favoriteArticleDao().insert(favoriteArticle);
                    requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
                    );
                }

                // Adapter akan langsung menampilkan perubahan visualnya

            } catch (Exception e) {
                android.util.Log.e("SearchFragment", "Error toggling favorite: " + e.getMessage());
                requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), "Terjadi kesalahan saat mengubah status favorit", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    /**
     * Konversi Article menjadi FavoriteArticle untuk disimpan ke database
     */
    private FavoriteArticle createFavoriteArticleFromArticle(Article article) {
        return new FavoriteArticle(
                article.getUrl(),
                article.getTitle(),
                article.getAuthor(),
                article.getDescription(),
                article.getUrlToImage(),
                article.getPublishedAt(),
                article.getContent(),
                article.getSource() != null ? article.getSource().getName() : "",
                System.currentTimeMillis()
        );
    }

    /**
     * Hapus binding untuk menghindari memory leak
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

