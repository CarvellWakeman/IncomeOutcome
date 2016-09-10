package carvellwakeman.incomeoutcome;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import org.joda.time.LocalDate;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CardVersus extends Card
{
    int _profileID;

    int monthsBackMax = 20;
    int monthsBackMin = 2;
    int monthsBack = 5;

    TextView textView_title;
    TextView textView_nodata;

    CardView cardView;

    ImageView button_monthsBackUp;
    ImageView button_monthsBackDown;

    HorizontalBarChart chart;
    BarDataSet dataSet;

    public CardVersus(ViewGroup insertPoint, int index, int profileID, Context context, LayoutInflater inflater, int layout){
        super(context, inflater, layout, insertPoint, index);
        _profileID = profileID;

        //Title
        textView_title = (TextView) v.findViewById(R.id.textView_cardVersus_title);

        //No Data notice
        textView_nodata = (TextView) v.findViewById(R.id.textView_cardVersus_nodata);

        //Parent layout
        cardView = (CardView) v.findViewById(R.id.cardVersus);

        //Control buttons
        button_monthsBackUp = (ImageView) v.findViewById(R.id.button_cardVersus_up);
        button_monthsBackDown = (ImageView) v.findViewById(R.id.button_cardVersus_down);

        button_monthsBackUp.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                int t = monthsBack;

                monthsBack++;
                monthsBack = Math.min(Math.max(monthsBack, monthsBackMin), monthsBackMax);
                if (monthsBack != t){ SetData(); }
            }});
        button_monthsBackDown.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                int t = monthsBack;

                monthsBack--;
                monthsBack = Math.min(Math.max(monthsBack, monthsBackMin), monthsBackMax);
                if (monthsBack != t){ SetData(); }
            }});

        //Pie chart
        chart = (HorizontalBarChart) v.findViewById(R.id.barChart_cardVersus);

        chart.setDescription("");

        chart.setFitBars(true);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.setDrawValueAboveBar(true);

        chart.setDoubleTapToZoomEnabled(false);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //Now left
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(12f);
        xAxis.setGranularity(1f);


        YAxis left = chart.getAxisLeft();
        left.setDrawLabels(false);
        left.setDrawAxisLine(false);
        left.setDrawGridLines(false);
        left.setDrawZeroLine(true);
        left.setZeroLineColor(Color.BLACK);
        left.setZeroLineWidth(1f);
        left.setSpaceTop(100f);
        left.setSpaceBottom(100f);
        //left.setAxisMaxValue(dataSet.getYMax() * 1.5f);
        //left.setAxisMinValue(dataSet.getYMin() * 1.5f);
        //left.setCenterAxisLabels(true);
        //chart.setExtraOffsets(0,0,100,0);
        chart.getAxisRight().setEnabled(false);

        chart.getLegend().setEnabled(false);

        SetData();
    }

    public void SetProfileID(int profileID){ _profileID = profileID; }

    public void SetData(){
        Profile _profile = ProfileManager.getInstance().GetProfileByID(_profileID);
        if (_profile != null){

            //Clear chart info
            if (dataSet != null) { dataSet.clear(); }

            //Get expense data
            LocalDate origStart = _profile.GetStartTime();
            LocalDate origEnd = _profile.GetEndTime();

            _profile.TimePeriodMinus(monthsBack-1);

            final ArrayList<Transaction> pastTransactionPeriods = new ArrayList<>();
            Transaction tran;
            for (int i = 0; i < monthsBack; i++){
                _profile.CalculateTimeFrame(null);
                tran = _profile.CalculatePeriodTotalBetweenDates(context);
                if (tran != null){ pastTransactionPeriods.add(tran); }
                _profile.TimePeriodPlus(1);
            }

            //Reset time periods back to original dates
            _profile.SetStartTime(origStart);
            _profile.SetEndTime(origEnd);


            if (pastTransactionPeriods.size() > 0) {
                //Convert transactions data to a list of entries
                List<BarEntry> entries = new ArrayList<>();
                List<Integer> colors = new ArrayList<>();
                for (int i = 0; i < pastTransactionPeriods.size(); i++){
                    Transaction tr = pastTransactionPeriods.get(i);

                    Double val = tr.GetValue();
                    entries.add(new BarEntry(i, val.floatValue()));

                    if (val >= 0) { //TODO: Better colors
                        colors.add(ProfileManager.getColor(R.color.green));
                    } else {
                        colors.add(ProfileManager.getColor(R.color.red));
                    }
                }

                XAxis xAxis = chart.getXAxis();
                //xAxis.setLabelCount(monthsBack, true);
                xAxis.setValueFormatter(new AxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        int index = Math.round(value);
                        if (index < monthsBack) {
                            Transaction tr = pastTransactionPeriods.get(index);

                            if (tr != null) {
                                TimePeriod tp = tr.GetTimePeriod();
                                if (tp != null) {
                                    if (tp.GetDate() != null) {
                                        SimpleDateFormat formatter = new SimpleDateFormat(ProfileManager.simpleDateFormatShortNoDay, App.GetLocale());
                                        return formatter.format(tp.GetDate().toDate());
                                        //return String.valueOf(value);
                                    }
                                }
                            }
                        }

                        return "No Date";
                    }
                    @Override public int getDecimalDigits() {
                        return 0;
                    }
                });



                //Add data to chart
                dataSet = new BarDataSet(entries, "");
                dataSet.setColors(colors);

                //chart.setMaxVisibleValueCount(monthsBack+10);

                dataSet.setValueTextSize(12);
                dataSet.setValueFormatter(new CurrencyValueFormatter(2));


                BarData barData = new BarData(dataSet);
                barData.setBarWidth(0.95f);
                chart.setData(barData);
                chart.invalidate(); //Refresh

                chart.setVisibility(View.VISIBLE);
                textView_nodata.setVisibility(View.GONE);
            }
            else { //No data
                chart.setVisibility(View.GONE);
                textView_nodata.setVisibility(View.VISIBLE);
            }
        }

        chart.animateY(2500);
    }
}
