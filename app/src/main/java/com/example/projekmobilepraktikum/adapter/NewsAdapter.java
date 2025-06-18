package com.example.projekmobilepraktikum.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projekmobilepraktikum.ArticleDetailActivity;
import com.example.projekmobilepraktikum.R;
import com.example.projekmobilepraktikum.databinding.ItemNewsBinding;
import com.example.projekmobilepraktikum.model.Article;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<Article> articles;
    private final OnItemClickListener listener;

    // Format tanggal untuk parsing dan display
    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

    /**
     * Constructor untuk adapter
     * @param articles list artikel yang akan ditampilkan
     * @param listener listener untuk handle click events
     */

    //
    // @param articles daftar artikel yang akan ditampilkan
    public NewsAdapter(List<Article> articles, OnItemClickListener listener) {
        this.articles = articles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menggunakan ViewBinding untuk inflate layout
        ItemNewsBinding binding = ItemNewsBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new NewsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        // Bind data artikel ke view
        holder.bind(articles.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    /**
     * Update data artikel dan notify adapter
     * @param newArticles list artikel baru
     */
    public void updateData(List<Article> newArticles) {
        this.articles = newArticles;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder untuk item berita
     */
    class NewsViewHolder extends RecyclerView.ViewHolder {
        private final ItemNewsBinding binding;

        public NewsViewHolder(@NonNull ItemNewsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         *
         * @param article artikel yang akan ditampilkan
         * @param listener listener untuk handle click events
         */
        public void bind(Article article, OnItemClickListener listener) {

            binding.tvTitle.setText(article.getTitle());

            // Set deskripsi jika tersedia
            if (article.getDescription() != null && !article.getDescription().isEmpty()) {
                binding.tvDescription.setText(article.getDescription());
                binding.tvDescription.setVisibility(android.view.View.VISIBLE);
            } else {
                binding.tvDescription.setVisibility(android.view.View.GONE);
            }

            // Set sumber berita
            if (article.getSource() != null) {
                binding.tvSource.setText(article.getSource().getName());
            } else {
                binding.tvSource.setText("Tidak diketahui");
            }

            // Format PUBLIKASI
            try {
                Date date = apiDateFormat.parse(article.getPublishedAt());
                if (date != null) {
                    binding.tvPublishedDate.setText(displayDateFormat.format(date));
                } else {
                    binding.tvPublishedDate.setText(article.getPublishedAt());
                }
            } catch (ParseException e) {
                binding.tvPublishedDate.setText(article.getPublishedAt());
            }


            // Load gambar menggunakan Glide library
            Glide.with(binding.getRoot())
                    .load(article.getUrlToImage())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image) // Gambar jika terjadi error
                    .centerCrop()
                    .into(binding.ivArticleImage);


            // Set listener untuk klik pada keseluruhan item
            binding.getRoot().setOnClickListener(v -> {
                // Panggil listener untuk item click
                if (listener != null) {
                    listener.onItemClick(article);
                }

                Context context = v.getContext(); // Dapatkan context dari view yang diklik
                Intent intent = new Intent(context, ArticleDetailActivity.class);
                // Konversi artikel ke JSON untuk dikirim melalui intent
                intent.putExtra(ArticleDetailActivity.EXTRA_ARTICLE, new Gson().toJson(article));
                context.startActivity(intent);
            });

            updateFavoriteButtonState(binding, article);

            // Set listener untuk tombol favorit
            binding.btnFavorite.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFavoriteClick(article);

                    // data di database diupdate di Fragment
                    article.setFavorite(!article.isFavorite());
                    updateFavoriteButtonState(binding, article);
                }
            });
        }


        // update ikon favorit
        private void updateFavoriteButtonState(ItemNewsBinding binding, Article article) {
            binding.btnFavorite.setImageResource(
                    article.isFavorite() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border
            );
        }
    }

    /**
     * Interface untuk handle click events
     */
    public interface OnItemClickListener {
        void onItemClick(Article article);
        void onFavoriteClick(Article article);
    }
}
