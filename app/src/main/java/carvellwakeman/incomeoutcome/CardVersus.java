package carvellwakeman.incomeoutcome;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
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

    TextView textView_title;
    TextView textView_nodata;

    CardView cardView;

    HorizontalBarChart chart;
    BarDataSet dataSet;

    public CardVersus(int profileID, Context context, LayoutInflater inflater, int layout){
        super(context, inflater, layout);
        _profileID = profileID;

        //Title
        textView_title = (TextView) v.findViewById(R.id.textView_cardVersus_title);

        //No Data notice
        textView_nodata = (TextView) v.findViewById(R.id.textView_cardVersus_nodata);

        //Parent layout
        cardView = (CardView) v.findViewById(R.id.cardVersus);

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
        chart.getAxisRight().setEnabled(false);

        SetData();
    }


    public void SetData(){
        Profile _profile = ProfileManager.GetProfileByID(_profileID);
        if (_profile != null){

            //Get expense data
            LocalDate origStart = _profile.GetStartTime();
            LocalDate origEnd = _profile.GetEndTime();
            int back = 4;
            _profile.TimePeriodMinus(back-1);
            final ArrayList<Transaction> pastTransactionPeriods = new ArrayList<>();
            for (int i = 0; i < back; i++){
                _profile.CalculateTimeFrame(null);
                pastTransactionPeriods.add(_profile.CalculatePeriodTotalBetweenDates());
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

                    String date = "";
                    Double val = tr.GetValue();

                    TimePeriod tp = tr.GetTimePeriod();
                    if (tp != null && tp.GetDate() != null){ date = tp.GetDate().toString(ProfileManager.simpleDateFormatNoDay); }

                    entries.add(new BarEntry(i, val.floatValue()));

                    if (val >= 0) { //TODO: Better colors
                        colors.add(ProfileManager.getColor(R.color.green));
                    } else {
                        colors.add(ProfileManager.getColor(R.color.red));
                    }
                }

                XAxis xAxis = chart.getXAxis();
                xAxis.setValueFormatter(new AxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        String date = "";
                        Transaction tr = pastTransactionPeriods.get(Math.round(value));

                        if (tr != null){
                            TimePeriod tp = tr.GetTimePeriod();
                            if (tp != null){
                                if (tp.GetDate() != null){
                                    SimpleDateFormat formatter = new SimpleDateFormat(ProfileManager.simpleDateFormatShortNoDay, ProfileManager.locale);
                                    return formatter.format(tp.GetDate().toDate());
                                }
                            }
                        }

                        return "No Data";
                    }
                    @Override public int getDecimalDigits() {
                        return 0;
                    }
                });


                //Add data to chart
                dataSet = new BarDataSet(entries, "");
                dataSet.setColors(colors);

                chart.getAxisLeft().setAxisMaxValue(dataSet.getYMax() * 1.2f);

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
