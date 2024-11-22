package com.sabbir.allinone.RefreshRate;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sabbir.allinone.R;

public class RefreshRateTest extends AppCompatActivity {
    private TextView refreshRateInfoTextView;
    private TextView animationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_rate_test);

        // Initialize views
        refreshRateInfoTextView = findViewById(R.id.refreshRateInfoTextView);
        animationTextView = findViewById(R.id.animationTextView);

        // Display refresh rate information
        float refreshRate = getDeviceRefreshRate();
        displayRefreshRateInfo(refreshRate);

        // Setup custom animation based on refresh rate
        setupCustomMarqueeAnimation(refreshRate);
    }

    private void displayRefreshRateInfo(float refreshRate) {
        String refreshRateInfo = String.format("Device Refresh Rate: %.2f Hz", refreshRate);
        refreshRateInfoTextView.setText(refreshRateInfo);
    }

    private void setupCustomMarqueeAnimation(float refreshRate) {
        // Calculate animation duration based on refresh rate
        int duration = calculateAnimationDuration(refreshRate);

        // Create custom translation animation
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 1.0f,   // Start X
                Animation.RELATIVE_TO_PARENT, -1.0f,  // End X
                Animation.RELATIVE_TO_SELF, 0.0f,     // Start Y
                Animation.RELATIVE_TO_SELF, 0.0f      // End Y
        );

        // Set animation properties
        animation.setDuration(duration);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);

        // Apply animation to TextView
        animationTextView.startAnimation(animation);
    }

    private int calculateAnimationDuration(float refreshRate) {
        // Adjust animation duration based on refresh rate
        int baseDuration = 5000; // Base duration of 5 seconds

        if (refreshRate >= 120) {
            // Faster animation for high refresh rates
            return baseDuration / 2; // 2.5 seconds
        } else if (refreshRate >= 90) {
            // Medium speed for 90 Hz
            return baseDuration * 3 / 4; // 3.75 seconds
        } else {
            // Slower animation for 60 Hz or lower
            return baseDuration; // 5 seconds
        }
    }

    private float getDeviceRefreshRate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getDisplayRefreshRate();
        } else {
            return getDefaultRefreshRate();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private float getDisplayRefreshRate() {
        DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        Display display = displayManager.getDisplay(Display.DEFAULT_DISPLAY);
        return display.getRefreshRate();
    }

    private float getDefaultRefreshRate() {
        // Fallback method for older devices
        return 60.0f; // Default to 60 Hz
    }
}