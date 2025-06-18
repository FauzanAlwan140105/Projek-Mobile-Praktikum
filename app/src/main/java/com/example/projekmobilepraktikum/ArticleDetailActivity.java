package com.example.projekmobilepraktikum;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.projekmobilepraktikum.model.Article;
import com.google.gson.Gson;

public class ArticleDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ARTICLE = "extra_article";
    private static final String TAG = "ArticleDetailActivity";

    private TextView tvTitle;
    private TextView tvSource;
    private TextView tvPublishedAt;
    private TextView tvContent;
    private ImageView ivArticleImage;
    private Button btnReadMore;
    private String articleUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        // inisialisasi semua elemen UI
        tvTitle = findViewById(R.id.tv_detail_title);
        tvSource = findViewById(R.id.tv_detail_source);
        tvPublishedAt = findViewById(R.id.tv_detail_published_at);
        tvContent = findViewById(R.id.tv_detail_content);
        ivArticleImage = findViewById(R.id.iv_detail_image);
        btnReadMore = findViewById(R.id.btn_read_more);

        // Mengambil data  dari intent
        String articleJson = getIntent().getStringExtra(EXTRA_ARTICLE);
        if (articleJson != null) {
            Article article = new Gson().fromJson(articleJson, Article.class);
            displayArticleDetails(article); // Menampilkan detail artikel
            articleUrl = article.getUrl();
            setupReadMoreButton();
        } else {
            Toast.makeText(this, "Artikel tidak ditemukan", Toast.LENGTH_SHORT).show();
        }

//        // Mengaktifkan tombol kembali di ActionBar
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle("Detail Berita");
//        }
    }

    // Menampilkan detail artikel ke tampilan UI
    private void displayArticleDetails(Article article) {
        tvTitle.setText(article.getTitle());
        if (article.getSource() != null) {
            tvSource.setText("Sumber: " + article.getSource().getName());
        } else {
            tvSource.setText("Sumber: Tidak diketahui");
        }
        tvPublishedAt.setText("Diterbitkan: " + article.getPublishedAt());

        String content = article.getContent();
        tvContent.setText(content != null ? content : "Konten tidak tersedia");

        btnReadMore.setText("Baca Selengkapnya");

        // Menampilkan gambar artikel dengan Glide
        Glide.with(this)
                .load(article.getUrlToImage())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .centerCrop()
                .into(ivArticleImage);
    }

    // "Baca Selengkapnya"
    private void setupReadMoreButton() {
        btnReadMore.setOnClickListener(view -> {
            if (articleUrl != null && !articleUrl.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Link artikel tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Menangani aksi tombol kembali di ActionBar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
