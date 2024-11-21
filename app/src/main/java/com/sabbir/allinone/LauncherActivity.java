package com.sabbir.allinone;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class LauncherActivity extends AppCompatActivity {
    private ConstraintLayout rootLayout;
    private TextView appNameText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge
        enableEdgeToEdge();

        setContentView(R.layout.activity_launcher);

        // Initialize views
        rootLayout = findViewById(R.id.rootLayout);
        appNameText = findViewById(R.id.appNameText);
        progressBar = findViewById(R.id.progressBar);

        // Apply theme-specific styling
        applyThemeStyles();

        // Optional: Add animation to the logo
        ImageView logoImage = findViewById(R.id.splashLogo);
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        logoImage.startAnimation(fadeIn);

        // Navigate to MainActivity after delay
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 2000);
    }

    private void applyThemeStyles() {
        // Get current night mode state
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                // Dark mode
                rootLayout.setBackgroundColor(getResources().getColor(R.color.background_dark));
                appNameText.setTextColor(getResources().getColor(R.color.text_light));
                progressBar.setIndeterminateTintList(
                        getResources().getColorStateList(R.color.progress_color_dark)
                );
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                // Light mode
                rootLayout.setBackgroundColor(getResources().getColor(R.color.background_light));
                appNameText.setTextColor(getResources().getColor(R.color.text_dark));
                progressBar.setIndeterminateTintList(
                        getResources().getColorStateList(R.color.progress_color_light)
                );
                break;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                // Default to light mode
                rootLayout.setBackgroundColor(getResources().getColor(R.color.background_light));
                appNameText.setTextColor(getResources().getColor(R.color.text_dark));
                break;
        }
    }

    private void enableEdgeToEdge() {
        // Make the app full screen and handle system bars
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Hide system bars
        WindowInsetsControllerCompat insetsController = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.hide(WindowInsetsCompat.Type.systemBars());
        insetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        // Set layout to full screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        // Transparent system bars
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Reapply styles when configuration changes
        applyThemeStyles();
    }
}