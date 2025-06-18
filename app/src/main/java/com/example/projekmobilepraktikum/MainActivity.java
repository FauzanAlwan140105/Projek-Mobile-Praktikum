package com.example.projekmobilepraktikum;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.projekmobilepraktikum.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private OnBackInvokedCallback onBackInvokedCallback;
    private boolean isDarkMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        // Terapkan tema (terang atau gelap)
        applyTheme();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Menyesuaikan padding agar konten tidak tertutup oleh status bar atau navigation bar
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (view, insets) -> {
            androidx.core.graphics.Insets systemInsets = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemInsets.left, systemInsets.top, systemInsets.right, systemInsets.bottom);
            return insets;
        });

        if (binding.toolbar != null) {
            setSupportActionBar(binding.toolbar);
        }

        // Menyiapkan navigasi antar fragment
        setupNavigation();
        setupBackCallback();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Menampilkan menu pada toolbar
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Menangani klik item menu
        if (item.getItemId() == R.id.action_toggle_theme) {
            // Mengubah mode terang <-> gelap
            toggleDarkMode();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Mengubah mode tema, kemudian menerapkannya
    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("dark_mode", isDarkMode);
        editor.apply();

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void setupBackCallback() {
        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedCallback = () -> {
                if (navController != null && !navController.popBackStack()) {
                    finish();
                }
            };
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    onBackInvokedCallback
            );
        }
    }

    // Menerapkan tema berdasarkan data yang tersimpan di SharedPreferences
    private void applyTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    // Mengatur sistem navigasi (ActionBar dan BottomNavigation)
    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_news,
                    R.id.navigation_search,
                    R.id.navigation_favorites
            ).build();

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            if (binding.bottomNavigation != null) {
                NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Mengatur navigasi ke atas (up button)
        if (navController != null) {
            return navController.navigateUp() || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister callback jika Android 13+ agar tidak terjadi memory leak
        if (Build.VERSION.SDK_INT >= 33 && onBackInvokedCallback != null) {
            getOnBackInvokedDispatcher().unregisterOnBackInvokedCallback(onBackInvokedCallback);
        }
    }
}
