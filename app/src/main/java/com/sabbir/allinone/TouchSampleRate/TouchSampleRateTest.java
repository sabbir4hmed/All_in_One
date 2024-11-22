package com.sabbir.allinone.TouchSampleRate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sabbir.allinone.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TouchSampleRateTest extends AppCompatActivity {
    private View touchAreaView;
    private TextView resultTextView;
    private Button startButton;
    private Button resetButton;

    private List<Long> touchTimestamps = new ArrayList<>();
    private long testStartTime = 0;
    private static final long TEST_DURATION = 5000; // 5 seconds test duration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_sample_rate_test);

        initializeViews();
        setupTouchListener();
        setupButtons();
    }

    private void initializeViews() {
        touchAreaView = findViewById(R.id.touchAreaView);
        resultTextView = findViewById(R.id.sampleRateTextView);
        startButton = findViewById(R.id.startButton);
        resetButton = findViewById(R.id.resetButton);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupTouchListener() {
        touchAreaView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Reset data when touch starts
                    resetTouchData();
                    testStartTime = System.nanoTime(); // Use nanosecond precision
                    recordTouchTimestamp();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    recordTouchTimestamp();

                    // Check test duration
                    long currentTime = System.nanoTime();
                    long elapsedTime = (currentTime - testStartTime) / 1_000_000; // Convert to milliseconds
                    if (elapsedTime >= TEST_DURATION) {
                        calculateTouchSampleRate();
                        return false;
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                    recordTouchTimestamp();
                    calculateTouchSampleRate();
                    return true;
            }
            return false;
        });
    }

    private void recordTouchTimestamp() {
        touchTimestamps.add(System.nanoTime());
    }

    private void calculateTouchSampleRate() {
        if (touchTimestamps.size() < 2) {
            displayResult("Not enough data to calculate touch sample rate");
            return;
        }

        // Calculate precise intervals
        List<Double> intervals = new ArrayList<>();
        for (int i = 1; i < touchTimestamps.size(); i++) {
            double interval = (touchTimestamps.get(i) - touchTimestamps.get(i-1)) / 1_000_000.0; // Convert to milliseconds
            intervals.add(interval);
        }

        // Calculate sampling rate statistics
        double minInterval = intervals.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double maxInterval = intervals.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        double avgInterval = intervals.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        // Calculate sampling rates
        double minSampleRate = 1000.0 / maxInterval;
        double maxSampleRate = 1000.0 / minInterval;
        double avgSampleRate = 1000.0 / avgInterval;

        // Calculate total test duration
        long totalTestDuration = (touchTimestamps.get(touchTimestamps.size() - 1) - touchTimestamps.get(0)) / 1_000_000;

        // Prepare result string with detailed information
        String resultText = String.format(
                Locale.getDefault(),
                "Touch Sampling Rate Analysis:\n" +
                        "Total Test Duration: %d ms\n" +
                        "Total Touch Events: %d\n\n" +
                        "Interval Analysis:\n" +
                        "Min Interval: %.2f ms\n" +
                        "Max Interval: %.2f ms\n" +
                        "Avg Interval: %.2f ms\n\n" +
                        "Sample Rate Estimates:\n" +
                        "Min Sample Rate: %.2f Hz\n" +
                        "Max Sample Rate: %.2f Hz\n" +
                        "Avg Sample Rate: %.2f Hz",
                totalTestDuration,
                touchTimestamps.size(),
                minInterval,
                maxInterval,
                avgInterval,
                minSampleRate,
                maxSampleRate,
                avgSampleRate
        );

        displayResult(resultText);
    }

    private void displayResult(String result) {
        resultTextView.setText(result);
    }

    private void setupButtons() {
        startButton.setOnClickListener(v -> {
            resetTouchData();
            resultTextView.setText("Start swiping on the touch area");
        });

        resetButton.setOnClickListener(v -> {
            resetTouchData();
            resultTextView.setText("");
        });
    }

    private void resetTouchData() {
        touchTimestamps.clear();
        testStartTime = 0;
    }
}