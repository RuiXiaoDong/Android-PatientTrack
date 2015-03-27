package com.motivus.ece.motivus;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class BarChartActivity extends FragmentActivity {

    protected BarChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bar_chart);

        //Category
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(this,
                R.layout.spinner_item, Database.AppointmentCategory);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinCategory = (Spinner)findViewById(R.id.spinner_category);
        spinCategory.setAdapter(adapterCategory);
        spinCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int categoryIndex = Database.AppointmentCategory.indexOf(spinCategory.getSelectedItem().toString());
                setData(5, categoryIndex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        mChart = (BarChart) findViewById(R.id.chart1);
        mChart.setDescription("");
        mChart.setMaxVisibleValueCount(60);
        mChart.setDrawBarShadow(true);
        mChart.setDrawValueAboveBar(true);
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(8);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8);

        int categoryIndex = Database.AppointmentCategory.indexOf(spinCategory.getSelectedItem().toString());
        setData(5, categoryIndex);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bar_chart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setData(int count, int category) {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add("Week" + (i + 1));
        }

        AppointmentStatistic[] appointmentStatistics = Database.getInstance(getApplicationContext()).getAppointmentStatistics_Weekly(count, category);
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        for (int i = 0; i < count; i++) {
            float val = appointmentStatistics[i].rate * 100;
            yVals.add(new BarEntry(val, i));
        }

        BarDataSet set = new BarDataSet(yVals, "Accomplishment Rate");
        set.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);

        mChart.setData(data);
        mChart.invalidate();
    }
}
