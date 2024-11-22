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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sabbir.allinone.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TouchSampleRateTest extends AppCompatActivity {
    private View touchAreaView;
    private LineChart intervalChart;
    private TextView resultTextView;
    private Button startButton;
    private Button resetButton;

    private List<Long> touchTimestamps = new ArrayList<>();
    private List<Double> touchIntervals = new ArrayList<>();
    private long testStartTime = 0;
    private static final long TEST_DURATION = 5000; // 5 seconds test duration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_sample_rate_test);

        initializeViews();
        setupTouchListener();
        setupButtons();
        setupChart();
    }

    private void initializeViews() {
        touchAreaView = findViewById(R.id.touchAreaView);
        intervalChart = findViewById(R.id.intervalChart);
        resultTextView = findViewById(R.id.resultTextView);
        startButton = findViewById(R.id.startButton);
        resetButton = findViewById(R.id.resetButton);
    }



    private void setupChart() {
        intervalChart.setTouchEnabled(true);
        intervalChart.setDragEnabled(true);
        intervalChart.setScaleEnabled(true);
        intervalChart.setPinchZoom(true);

        Description description = new Description();
        description.setText("Touch Interval Analysis");
        intervalChart.setDescription(description);

        XAxis xAxis = intervalChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = intervalChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = intervalChart.getAxisRight();
        rightAxis.setEnabled(false);

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < touchIntervals.size(); i++) {
            entries.add(new Entry(i, touchIntervals.get(i).floatValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Touch Intervals");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        intervalChart.setData(lineData);
        intervalChart.invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupTouchListener() {
        touchAreaView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    resetTouchData();
                    testStartTime = System.nanoTime();
                    recordTouchTimestamp();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    recordTouchTimestamp();

                    long currentTime = System.nanoTime();
                    long elapsedTime = (currentTime - testStartTime) / 1_000_000;
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
        long currentTimestamp = System.nanoTime();
        touchTimestamps.add(currentTimestamp);
    }

    private void calculateTouchSampleRate() {
        if (touchTimestamps.size() < 2) {
            resultTextView.setText("Not enough data to calculate touch sample rate");
            return;
        }

        // Calculate precise intervals
        touchIntervals.clear();
        for (int i = 1; i < touchTimestamps.size(); i++) {
            double interval = (touchTimestamps.get(i) - touchTimestamps.get(i-1)) / 1_000_000.0;
            touchIntervals.add(interval);
        }

        // Calculate statistics
        double minInterval = touchIntervals.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double maxInterval = touchIntervals.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        double avgInterval = touchIntervals.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        // Calculate sampling rates
        double minSampleRate = 1000.0 / maxInterval;
        double maxSampleRate = 1000.0 / minInterval;
        double avgSampleRate = 1000.0 / avgInterval;

        // Prepare result string
        String resultText = String.format(
                Locale.getDefault(),
                "Touch Sampling Rate Analysis:\n" +
                        "Total Events: %d\n" +
                        "Min Interval: %.2f ms\n" +
                        "Max Interval: %.2f ms\n" +
                        "Avg Interval: %.2f ms\n\n" +
                        "Sample Rates:\n" +
                        "Min: %.2f Hz\n" +
                        "Max: %.2f Hz\n" +
                        "Average: %.2f Hz",
                touchTimestamps.size(),
                minInterval,
                maxInterval,
                avgInterval,
                minSampleRate,
                maxSampleRate,
                avgSampleRate
        );

        // Display result text
        resultTextView.setText(resultText);

        // Create chart
        createIntervalChart();
    }

    private void createIntervalChart() {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < touchIntervals.size(); i++) {
            entries.add(new Entry(i, touchIntervals.get(i).floatValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Touch Intervals (ms)");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.RED);
        dataSet.setDrawCircles(true);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        intervalChart.setData(lineData);
        intervalChart.invalidate(); // Refresh chart
    }

    private void setupButtons() {
        startButton.setOnClickListener(v -> {
            resetTouchData();
            resultTextView.setText("Start swiping on the touch area");
            intervalChart.clear();
        });

        resetButton.setOnClickListener(v -> {
            resetTouchData();
            resultTextView.setText("");
            intervalChart.clear();
        });
    }

    private void resetTouchData() {
        touchTimestamps.clear();
        touchIntervals.clear();
        testStartTime = 0;
    }
}

