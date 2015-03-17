package com.motivus.ece.motivus;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;

public class StackedBarActivity extends FragmentActivity {

    private BarChart mChart;
    private float mMaxNumAppointment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bar_chart);

        mMaxNumAppointment = 0;
        mChart = (BarChart) findViewById(R.id.chart1);
        mChart.setDescription("");
        mChart.setMaxVisibleValueCount(60);
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(false);
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);
        mChart.setDrawValuesForWholeStack(true);

        setData(5);

        XAxis xLabels = mChart.getXAxis();
        xLabels.setPosition(XAxis.XAxisPosition.TOP);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setStartAtZero(true);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawTopYLabelEntry(true);
        leftAxis.setAxisMaxValue(mMaxNumAppointment + 1);
        leftAxis.setLabelCount((int)mMaxNumAppointment + 1);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stacked_bar, menu);
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

    private void setData(int count) {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add("Week" + (i + 1));
        }

        AppointmentStatistic[] appointmentStatistics = Database.getInstance(getApplicationContext()).getAppointmentStatistics_Weekly(count);
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        for (int i = 0; i < count; i++) {
            float val1 = appointmentStatistics[i].accomplishedAppointment;
            float val2 = appointmentStatistics[i].totalAppointment - appointmentStatistics[i].accomplishedAppointment;

            if(mMaxNumAppointment <  appointmentStatistics[i].totalAppointment)
                mMaxNumAppointment = appointmentStatistics[i].totalAppointment;
            BarEntry barEntry = new BarEntry(new float[] {
                    val1, val2
            }, i);
            yVals.add(barEntry);
        }

        BarDataSet set = new BarDataSet(yVals, "");
        set.setColors(getColors());
        set.setStackLabels(new String[] {
                "Accomplished", "Unaccomplished"
        });

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set);

        BarData data = new BarData(xVals, dataSets);
        mChart.setData(data);
        mChart.invalidate();
    }

    private int[] getColors() {

        int stackSize = 2;

        // have as many colors as stack-values per entry
        int []colors = new int[stackSize];

        for(int i = 0; i < stackSize; i++) {
            colors[i] = ColorTemplate.VORDIPLOM_COLORS[i];
        }

        return colors;
    }
}
