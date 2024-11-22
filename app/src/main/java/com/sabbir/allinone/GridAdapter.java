package com.sabbir.allinone;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.sabbir.allinone.RefreshRate.RefreshRateTest;
import com.sabbir.allinone.TouchSampleRate.TouchSampleRateTest;

import java.util.List;

public class GridAdapter extends BaseAdapter {
    private Context context;
    private List<TestItem> testItems;
    private LayoutInflater inflater;

    public GridAdapter(Context context, List<TestItem> testItems) {
        this.context = context;
        this.testItems = testItems;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return testItems.size();
    }

    @Override
    public TestItem getItem(int position) {
        return testItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Ensure square aspect ratio
        int parentWidth = parent.getWidth();
        ViewGroup.LayoutParams params = convertView.getLayoutParams();
        params.height = parentWidth / 2; // Assuming 2 columns
        convertView.setLayoutParams(params);

        TestItem item = getItem(position);
        holder.button.setText(item.getName());

        holder.button.setOnClickListener(v -> {
            switch (item.getName()) {
                case "Memory Test":
                    performMemoryTest();
                    break;
                case "Cpu Throttle Test":
                    performCpuThrottleTest();
                    break;
                case "Refresh Rate Test":
                    performRefreshRateTest();
                    break;
                case "Touch Sample Rate Test":
                    performTouchSampleRateTest();
                    break;
                case "Ram Info":
                    showRamInfo();
                    break;
                case "Factory Test":
                    performFactoryTest();
                    break;
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        Button button;

        ViewHolder(View view) {
            button = view.findViewById(R.id.testButton);
        }
    }

    // Test method implementations
    private void performMemoryTest() {
        Toast.makeText(context, "Performing Memory Test", Toast.LENGTH_SHORT).show();
    }

    private void performCpuThrottleTest() {
        Toast.makeText(context, "Performing CPU Throttle Test", Toast.LENGTH_SHORT).show();
    }

    private void performRefreshRateTest() {
        Intent intent = new Intent(context, RefreshRateTest.class);
        context.startActivity(intent);
    }

    private void performTouchSampleRateTest() {
        Intent intent = new Intent(context, TouchSampleRateTest.class);
        context.startActivity(intent);
    }

    private void showRamInfo() {
        Toast.makeText(context, "Showing RAM Information", Toast.LENGTH_SHORT).show();
    }

    private void performFactoryTest() {
        Toast.makeText(context, "Performing Factory Test", Toast.LENGTH_SHORT).show();
    }
}