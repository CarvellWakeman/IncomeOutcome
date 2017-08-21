package carvellwakeman.incomeoutcome;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.SparseArray;
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
    int _budgetID;

    int keyType;
    Transaction.TRANSACTION_TYPE _activityType;

    CardView cardView;

    Spinner spinner_keyType;

    Button button_viewDetails;

    PieChart chart;
    PieDataSet dataSet;


    public CardTransaction(ViewGroup insertPoint, int index, int budgetID, Transaction.TRANSACTION_TYPE activityType, Context context, LayoutInflater inflater, int layout){
        super(context, inflater, layout, insertPoint, index);
        this._context = context;
        this._budgetID = budgetID;

        this._activityType = activityType;

        int keyTypeArray = (activityType==Transaction.TRANSACTION_TYPE.Expense ? R.array.keytype_array_ex : R.array.keytype_array_in);


        //Spinner keytype
        spinner_keyType = (Spinner) getBase().findViewById(R.id.spinner_cardTransaction);
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

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        //View details button
        button_viewDetails = (Button) getBase().findViewById(R.id.button_cardTransaction_viewdetails);
        button_viewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start the details activity
                Intent intent = new Intent(_context, ActivityDetailsTransaction.class);
                intent.putExtra("activitytype", _activityType.ordinal());
                intent.putExtra("budget", _budgetID);
                _context.startActivity(intent);
            }
        });

        //Parent layout
        cardView = (CardView) getBase().findViewById(R.id.cardTransaction);

        //Pie chart
        chart = (PieChart) getBase().findViewById(R.id.pieChart_cardTransaction);

        chart.setDescription("");

        chart.setHighlightPerTapEnabled(true);

        chart.setTouchEnabled(true);

        chart.setDrawCenterText(true);

        chart.setDrawHoleEnabled(true);
        chart.setRotationEnabled(true);
        chart.setDrawSlicesUnderHole(true);

        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleColor(Color.WHITE);

        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(55f);
        chart.setTransparentCircleRadius(58f);
        chart.setCenterTextSize(22);

        chart.setExtraOffsets(20, 10, 10, 10);
        chart.setDrawEntryLabels(false);

        //Legend
        Legend l = chart.getLegend();
        l.setPosition(Legend.LegendPosition.LEFT_OF_CHART_CENTER);
        l.setDrawInside(false);
        l.setEnabled(true);

        //Slice labels
        chart.setEntryLabelTextSize(10);
        chart.setEntryLabelColor(Color.BLACK);
    }


    public void SetData(){
        Budget _budget = BudgetManager.getInstance().GetBudget(_budgetID);
        if (_budget != null){

            //Get expense data
            ArrayList<Transaction> transactions = _budget.GetTransactionsInTimeframe( _activityType );

            if (transactions.size() > 0) {
                //Total
                Double total = 0d;
                Double val = 0d;

                //Convert transaction data to a list of PieEntry
                List<PieEntry> entries = new ArrayList<>();
                SparseArray<Double> debt = new SparseArray<>();
                SparseArray<Double> category = new SparseArray<>();
                List<Integer> colors = new ArrayList<>();

                for (Transaction t : transactions) {
                    if (t.GetValue() > 0) {

                        if (keyType == 0) { // Source
                            if (t.IsSplit()) {
                                val = t.GetSplit(Person.Me.GetID());
                            } else {
                                val = t.GetValue();
                            }

                            entries.add(new PieEntry(val.floatValue(), t.GetSource()));

                            colors.add(Helper.ColorFromString(t.GetSource()));

                            // Sum up the values
                            total += val;
                        }
                        else if (keyType == 1) { // Category
                            Category cat = CategoryManager.getInstance().GetCategory(t.GetCategory());
                            if (cat == null) { cat = Category.Deleted; }
                            val = t.GetSplit(Person.Me.GetID());

                            // Add to existing value
                            if (category.get(cat.GetID()) != null) { val += category.get(cat.GetID()); }
                            category.put(cat.GetID(), val);
                        }
                        else if (keyType == 2){ // Debt
                            for (HashMap.Entry<Integer, Double> entry : t.GetSplitArray().entrySet()) {
                                if (entry.getKey() != Person.Me.GetID()) {
                                    val = t.GetDebt(Person.Me.GetID(), entry.getKey()) - t.GetDebt(entry.getKey(), Person.Me.GetID());
                                    Helper.Log(_context, "CardTran", "Debt:" + String.valueOf(val) + " " + t.GetSource() + " " + PersonManager.getInstance().GetPerson(entry.getKey()).GetName());

                                    // Add to existing value
                                    if (debt.get(entry.getKey()) != null) { val += debt.get(entry.getKey()); }
                                    debt.put(entry.getKey(), val);

                                }
                            }
                        }

                    }
                }

                // Category case
                if (keyType == 1){
                    CategoryManager cm = CategoryManager.getInstance();
                    Category cat;

                    int size = category.size();
                    for (int i = 0; i < size; i++){
                        cat = cm.GetCategory(category.keyAt(i));
                        entries.add(new PieEntry(category.get(category.keyAt(i)).floatValue(), cat.GetTitle()));
                        colors.add(cat.GetColor());

                        // Sum up the values
                        total += category.get(category.keyAt(i));
                    }
                }

                // Debt case
                else if (keyType == 2){
                    PersonManager pm = PersonManager.getInstance();
                    Person p;
                    int size = debt.size();
                    for (int i = 0; i < size; i++){
                        p = pm.GetPerson(debt.keyAt(i));
                        val = Math.max(0.0d, debt.get(debt.keyAt(i)).floatValue());
                        if (val > 0d) {
                            entries.add(new PieEntry(val.floatValue(), p.GetName()));
                            colors.add(Helper.ColorFromString(p.GetName()));

                            // Sum up the values
                            total += Math.max(0, debt.get(debt.keyAt(i)));
                        }
                    }
                }


                //Add data to pie chart
                dataSet = new PieDataSet(entries, "");
                dataSet.setColors(colors);
                dataSet.setSliceSpace(1f);
                dataSet.setSelectionShift(5f);
                dataSet.setValueTextColor(_context.getResources().getColor(R.color.white));

                dataSet.setValueTextSize(8);
                dataSet.setValueFormatter(new CurrencyValueFormatter(2));

                // Value lines
                //dataSet.setValueLinePart1OffsetPercentage(80.0f);
                //dataSet.setValueLinePart1Length(0.6f);
                //dataSet.setValueLinePart2Length(0.8f);
                //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                //dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                //dataSet.setValueTextColor(Helper.getColor(R.color.white));
                dataSet.setDrawValues(true);

                chart.setData(new PieData(dataSet));
                if (total != 0d) {
                    chart.setCenterText(Helper.currencyFormat.format(total));
                    chart.setDrawCenterText(true);
                }
                else if (keyType == 2){ // debt
                    chart.setCenterText(_context.getString(R.string.info_no_debt));
                }
                else {
                    chart.setDrawCenterText(false);
                }
                chart.invalidate(); //Refresh

                chart.setVisibility(View.VISIBLE);

                button_viewDetails.setText(R.string.action_viewdetails);
            }
            else { //No data
                chart.setVisibility(View.GONE);

                String transactionType = Helper.getString(_activityType == Transaction.TRANSACTION_TYPE.Expense ? R.string.format_nodata_viewdetails_expense : R.string.misc_income);
                button_viewDetails.setText(String.format(_context.getString(R.string.format_nodata_viewdetails), transactionType));
            }

        }

        chart.animateY(1400, Easing.EasingOption.EaseInSine);
        chart.spin(2000, 0, 360, Easing.EasingOption.EaseOutSine);
    }

    public void SetBudget(int id){ _budgetID = id; }

}
