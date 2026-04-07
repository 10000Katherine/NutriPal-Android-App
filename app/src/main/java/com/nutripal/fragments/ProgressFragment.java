package com.nutripal.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.nutripal.R;
import com.nutripal.viewmodels.ProgressViewModel;
import java.util.List;


public class ProgressFragment extends Fragment {

    private ProgressViewModel viewModel;
    private BarChart barChart;
    private TextView textWeeklySummary;
    private String latestWeeklySummary = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(ProgressViewModel.class);
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barChart = view.findViewById(R.id.chart);
        textWeeklySummary = view.findViewById(R.id.text_weekly_summary);
        Button shareWeeklySummaryButton = view.findViewById(R.id.button_share_weekly_summary);

        shareWeeklySummaryButton.setOnClickListener(v -> shareWeeklySummary());

        viewModel.getChartData().observe(getViewLifecycleOwner(), chartData -> {
            if (chartData != null) {
                setupChart(chartData.calorieGoal, chartData.labels);
                updateChartData(chartData.entries);
            } else {
                barChart.clear();
                barChart.setNoDataText("No data to display yet.");
                barChart.invalidate();
            }
        });

        viewModel.getWeeklySummary().observe(getViewLifecycleOwner(), summary -> {
            if (summary == null || summary.trim().isEmpty()) {
                latestWeeklySummary = "No weekly summary available yet.";
            } else {
                latestWeeklySummary = summary;
            }
            textWeeklySummary.setText(latestWeeklySummary);
        });
    }

    private void shareWeeklySummary() {
        if (latestWeeklySummary == null || latestWeeklySummary.trim().isEmpty()) {
            Toast.makeText(getContext(), "No weekly summary to share yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My NutriPal Weekly Summary");
        shareIntent.putExtra(Intent.EXTRA_TEXT, latestWeeklySummary);

        startActivity(Intent.createChooser(shareIntent, "Share weekly summary with"));
    }

    private void setupChart(int calorieGoal, List<String> labels) {

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        xAxis.setValueFormatter(new DateAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);


        LimitLine goalLine = new LimitLine(calorieGoal, "Goal");
        goalLine.setLineWidth(2f);
        goalLine.setLineColor(Color.RED);
        goalLine.enableDashedLine(10f, 10f, 0f);
        goalLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        goalLine.setTextSize(10f);

        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(goalLine);

        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
    }

    private void updateChartData(List<BarEntry> entries) {
        BarDataSet dataSet = new BarDataSet(entries, "Daily Calories");
        dataSet.setColor(Color.rgb(0, 150, 136));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.animateY(1000);
    }

    private static class DateAxisValueFormatter extends ValueFormatter {
        private final List<String> labels;


        private DateAxisValueFormatter(List<String> labels) {
            this.labels = labels;
        }

        @Override
        public String getAxisLabel(float value, com.github.mikephil.charting.components.AxisBase axis) {
            int index = (int) value;
            if (index >= 0 && index < labels.size()) {
                return labels.get(index);
            }
            return "";
        }
    }
}
