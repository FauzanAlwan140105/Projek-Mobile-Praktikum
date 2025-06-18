package com.example.projekmobilepraktikum.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projekmobilepraktikum.adapter.NewsAdapter;
import com.example.projekmobilepraktikum.database.AppDatabase;
import com.example.projekmobilepraktikum.database.FavoriteArticle;
import com.example.projekmobilepraktikum.databinding.FragmentFavoritesBinding;
import com.example.projekmobilepraktikum.model.Article;
import com.example.projekmobilepraktikum.model.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FavoritesFragment extends Fragment {
    private FragmentFavoritesBinding binding;
    private NewsAdapter newsAdapter;
    private AppDatabase appDatabase;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi database lokal
        appDatabase = AppDatabase.getInstance(requireContext());

        setupRecyclerView();

        // Memuat data favorit dari database
        loadFavorites();
    }

    private void setupRecyclerView() {
        newsAdapter = new NewsAdapter(new ArrayList<>(), new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Article article) {
            }

            @Override
            public void onFavoriteClick(Article article) {
                removeFromFavorites(article); // Menghapus artikel dari favorit
            }
        });

        // Menyiapkan RecyclerView
        binding.recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewFavorites.setAdapter(newsAdapter);
    }

    private void loadFavorites() {
        // Mengamati data artikel favorit dari Room Database
        appDatabase.favoriteArticleDao().getAllFavoriteArticles().observe(getViewLifecycleOwner(), favoriteArticles -> {
            if (favoriteArticles != null && !favoriteArticles.isEmpty()) {
                // Konversi dari entitas database ke model artikel
                List<Article> articles = convertToArticles(favoriteArticles);

                // Tampilkan artikel favorit di adapter
                newsAdapter.updateData(articles);
                binding.recyclerViewFavorites.setVisibility(View.VISIBLE);
                binding.tvNoFavorites.setVisibility(View.GONE);
            } else {
                // Jika kosong, tampilkan pesan "tidak ada favorit"
                binding.recyclerViewFavorites.setVisibility(View.GONE);
                binding.tvNoFavorites.setVisibility(View.VISIBLE);
                newsAdapter.updateData(new ArrayList<>());
            }
        });
    }

    private void removeFromFavorites(Article article) { // Menghapus artikel dari database
        executor.execute(() -> {
            try {
                appDatabase.favoriteArticleDao().deleteByUrl(article.getUrl());

                // Tampilkan notifikasi berhasil di thread utama
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Artikel dihapus dari favorit", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
            }
        });
    }


   //Mengonversi daftar FavoriteArticle dari database menjadi daftar Article untuk ditampilkan di RecyclerView
    private List<Article> convertToArticles(List<FavoriteArticle> favoriteArticles) {
        List<Article> articles = new ArrayList<>();

        for (FavoriteArticle favoriteArticle : favoriteArticles) {
            Article article = new Article();
            article.setTitle(favoriteArticle.getTitle());
            article.setAuthor(favoriteArticle.getAuthor());
            article.setDescription(favoriteArticle.getDescription());
            article.setUrl(favoriteArticle.getUrl());
            article.setUrlToImage(favoriteArticle.getUrlToImage());
            article.setPublishedAt(favoriteArticle.getPublishedAt());
            article.setContent(favoriteArticle.getContent());

            // Membuat objek Source untuk artikel
            Source source = new Source();
            source.setName(favoriteArticle.getSourceName());
            article.setSource(source);

            article.setFavorite(true);

            articles.add(article);
        }

        return articles;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Hindari memory leak
    }
}
