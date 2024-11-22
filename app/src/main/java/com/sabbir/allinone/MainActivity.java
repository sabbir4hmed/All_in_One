package com.sabbir.allinone;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private GridView gridView;
    private GridAdapter gridAdapter;
    private List<TestItem> testItems;

    private TextView copyrightTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize GridView
        gridView = findViewById(R.id.gridView);
        copyrightTextView = findViewById(R.id.copyrightTextView);

        // Prepare test items
        prepareTestItems();

        // Setup GridView
        setupGridView();

        //setup copyright text
        setupCopyrightText();
    }

    private void setupCopyrightText() {
        // Set the copyright text
        try {
            // Get app version
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;

            // Get current year
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);

            // Set copyright text
            String copyrightText = String.format("App Version %s\n© %d All Rights Reserved", version, currentYear);
            copyrightTextView.setText(copyrightText);

        } catch (PackageManager.NameNotFoundException e) {
            // Fallback text if version can't be retrieved
            copyrightTextView.setText("App Version Unknown\n© " + Calendar.getInstance().get(Calendar.YEAR) + " All Rights Reserved");
            e.printStackTrace();
        }
    }


    private void prepareTestItems() {
        testItems = new ArrayList<>();
        testItems.add(new TestItem("Memory Test"));
        testItems.add(new TestItem("Cpu Throttle Test"));
        testItems.add(new TestItem("Refresh Rate Test"));
        testItems.add(new TestItem("Touch Sample Rate Test"));
        testItems.add(new TestItem("Ram Info"));
        testItems.add(new TestItem("Factory Test"));
    }

    private void setupGridView() {
        // Create and set adapter
        gridAdapter = new GridAdapter(this, testItems);
        gridView.setAdapter(gridAdapter);
    }
}