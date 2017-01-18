package carvellwakeman.incomeoutcome;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;

import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.FSize;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardTransaction extends Card
{
    Context _context;
    int _profileID;

    int keyType;
    int activityType;
    int defaultActivityType;
    int defaultKeyType;

    TextView textView_title;
    TextView textView_nodata;

    CardView cardView;

    Spinner spinner_keyType;
    android.support.v7.widget.SwitchCompat switch_showLegend;

    Button button_viewDetails;

    PieChart chart;
    PieDataSet dataSet;

    boolean isExpanded = false;


    public CardTransaction(ViewGroup insertPoint, int index, int profileID, int defaultActivityType, int defaultKeyType, String title, Context context, LayoutInflater inflater, int layout){
        super(context, inflater, layout, insertPoint, index);
        this._context = context;
        _profileID = profileID;
        this.defaultActivityType = defaultActivityType;
        this.activityType = defaultActivityType;
        this.defaultKeyType = defaultKeyType;
        this.keyType = defaultKeyType;
        int keyTypeArray = (activityType==0 ? R.array.keytype_array_ex : R.array.keytype_array_in);

        //Title
        textView_title = (TextView) getBase().findViewById(R.id.textView_cardTransaction_title);
        textView_title.setText(title);

        //No Data notice
        textView_nodata = (TextView) getBase().findViewById(R.id.textView_cardTransaction_nodata);

        //Show More switch
        switch_showLegend = (android.support.v7.widget.SwitchCompat) getBase().findViewById(R.id.switch_cardTransaction);
        switch_showLegend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SetExpanded(switch_showLegend.isChecked());
            }
        });

        //Spinner keytype
        spinner_keyType = (Spinner) getBase().findViewById(R.id.spinner_cardTransaction);
        if (activityType==0) {
            ArrayAdapter adapter = ArrayAdapter.createFromResource(context, keyTypeArray, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_list_primary);
            spinner_keyType.setAdapter(adapter);
            spinner_keyType.setSelection(keyType);
            //OnClick Listener
            spinner_keyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (keyType != position) { //Key Type changed
                        keyType = position;
                        SetData();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
        else { spinner_keyType.setVisibility(View.GONE); }

        //View details button
        button_viewDetails = (Button) getBase().findViewById(R.id.button_cardTransaction_viewdetails);

        button_viewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile _profile = ProfileManager.getInstance().GetProfileByID(_profileID);
                if (_profile != null) {
                    //_profile.SetFilterMethod(ProfileManager.FILTER_METHODS.NONE, null);
                    //_profile.SetSortMethod(ProfileManager.SORT_METHODS.DATE_DOWN);

                    _profile.CalculateTimeFrame(activityType);
                    _profile.CalculateTotalsInTimeFrame(activityType, activityType);

                    //Start income (details) activity and send it the profile we clicked on
                    Intent intent = new Intent(_context, ActivityDetailsTransaction.class);
                    intent.putExtra("activitytype", activityType);
                    intent.putExtra("keytype", activityType);
                    intent.putExtra("profile",_profileID);
                    ((ActivityMain)_context).startActivityForResult(intent, 0);
                }
                else {
                    ProfileManager.PrintUser(_context, "ERROR: Profile not found, could not start transaction details activity");
                }
            }
        });

        //Parent layout
        cardView = (CardView) getBase().findViewById(R.id.cardTransaction);

        //Pie chart
        chart = (PieChart) getBase().findViewById(R.id.pieChart_cardTransaction);

        chart.setDescription("");

        chart.setHighlightPerTapEnabled(true);

        chart.setTouchEnabled(true);

        chart.setDrawHoleEnabled(true);
        chart.setDrawCenterText(true);
        chart.setRotationEnabled(true);
        chart.setDrawSlicesUnderHole(true);

        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleColor(Color.WHITE);

        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(55f);
        chart.setTransparentCircleRadius(58f);
        chart.setCenterTextSize(22);


        //Clicking on slices
        //MarkerViewTransaction mv = new MarkerViewTransaction(context, R.layout.marker_layout, chart);
        //chart.setMarkerView(mv);




        //Legend
        Legend l = chart.getLegend();
        l.setPosition(Legend.LegendPosition.LEFT_OF_CHART_CENTER);
        l.setDrawInside(false);

        //Slice labels
        chart.setEntryLabelTextSize(10);
        chart.setEntryLabelColor(Color.BLACK);

        //Gather Data
        //SetData();

        //Contract the card view

        //SetExpanded(true);

        SetExpanded(false);
    }

    public void SetProfileID(int profileID){ _profileID = profileID; }

    public void SetData(){
        Profile _profile = ProfileManager.getInstance().GetProfileByID(_profileID);
        if (_profile != null){
            _profile.CalculateTimeFrame(activityType);

            //Get expense data
            HashMap<String,Transaction> transactions = _profile.CalculateTotalsInTimeFrame(activityType, keyType, true);

            if (transactions.keySet().size() > 0) {
                //Total
                Double total = 0d;

                //Convert transaction data to a list of PieEntry
                List<PieEntry> entries = new ArrayList<>();
                List<Integer> colors = new ArrayList<>();
                for (Map.Entry<String, Transaction> entry : transactions.entrySet()) {
                    if (entry.getValue().GetValue() > 0) {
                        total += (entry.getValue().GetValue() - entry.getValue().GetSplitValue());

                        entries.add(new PieEntry(entry.getValue().GetValue().floatValue(), entry.getKey()));

                        //Special case if keytype is category
                        Category cat = ProfileManager.getInstance().GetCategory(entry.getValue().GetCategory());
                        if (cat != null && keyType == 2) { colors.add(cat.GetColor()); }
                        else { colors.add(ProfileManager.ColorFromString(entry.getKey())); }
                    }
                }

                //Sepcial case if keytype is debt
                if (keyType==0){ chart.setDrawCenterText(false); }

                //Add data to pie chart
                dataSet = new PieDataSet(entries, "");
                dataSet.setColors(colors);
                dataSet.setSliceSpace(1f);
                dataSet.setSelectionShift(5f);

                dataSet.setValueTextSize(8);
                dataSet.setValueFormatter(new CurrencyValueFormatter(2));

                dataSet.setValueLinePart1OffsetPercentage(80.0f);
                dataSet.setValueLinePart1Length(0.6f);
                dataSet.setValueLinePart2Length(0.8f);
                dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

                PieData data = new PieData(dataSet);
                chart.setData(data);
                chart.setCenterText(ProfileManager.getString(R.string.info_total_newline) + ProfileManager.currencyFormat.format(total));
                chart.invalidate(); //Refresh

                chart.setVisibility(View.VISIBLE);
                if (activityType==0){ spinner_keyType.setVisibility(View.VISIBLE); }
                switch_showLegend.setVisibility(View.VISIBLE);
                //relativeLayout_controls.setVisibility(View.VISIBLE);
                //textView_nodata.setVisibility(View.GONE);
                button_viewDetails.setText(R.string.action_viewdetails);
            }
            else { //No data
                chart.setVisibility(View.GONE);
                spinner_keyType.setVisibility(View.GONE);
                switch_showLegend.setVisibility(View.GONE);
                //relativeLayout_controls.setVisibility(View.GONE);
                //textView_nodata.setVisibility(View.VISIBLE);
                String transactionType = ProfileManager.getString(activityType==0 ? R.string.format_nodata_viewdetails_expense : R.string.misc_income);
                button_viewDetails.setText(String.format(_context.getString(R.string.format_nodata_viewdetails), transactionType));
            }
        }


        //SetExpanded(true);

        SetExpanded(isExpanded);

        chart.animateY(1400, Easing.EasingOption.EaseInSine);
        chart.spin(2000, 0, 360, Easing.EasingOption.EaseOutSine);
    }

    public void SetExpanded(boolean expanded){
        if (chart != null && dataSet != null) {
            isExpanded = expanded;

            if (expanded) {
                chart.setExtraOffsets(20, 10, 10, 10);
                chart.getLegend().setEnabled(true);
                chart.setDrawEntryLabels(false);

                dataSet.setValueTextColor(ProfileManager.getColor(R.color.white));
                dataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
                dataSet.setDrawValues(true);
            }
            else {
                chart.setExtraOffsets(20, 20, 20, 20);
                chart.getLegend().setEnabled(false);
                chart.setDrawEntryLabels(true);

                dataSet.setValueTextColor(ProfileManager.getColor(R.color.black));
                dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                dataSet.setDrawValues(true);
            }

            chart.calculateOffsets();
            chart.invalidate();
        }
    }

}
