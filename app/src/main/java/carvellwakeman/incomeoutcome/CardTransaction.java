package carvellwakeman.incomeoutcome;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import org.joda.time.LocalDate;

import java.util.*;

public class CardTransaction extends Card implements OnChartValueSelectedListener, SortFilterActivity
{
    Context _context;
    int _budgetID;

    int keyType;
    Transaction.TRANSACTION_TYPE _activityType;

    CardView cardView;

    Spinner spinner_keyType;

    TextView textView_viewdetails;
    LinearLayout button_viewDetails;

    ImageView button_filter;

    RelativeLayout relativeLayout_filter;
    TextView textView_filters;

    // Chart and data
    PieChart chart;
    PieDataSet dataSet;

    // Filtering
    HashMap<Helper.FILTER_METHODS, String> filterMethods;


    public CardTransaction(final Activity context, ViewGroup insertPoint, int index, int budgetID, final Transaction.TRANSACTION_TYPE activityType, LayoutInflater inflater, int layout){
        super(context, inflater, layout, insertPoint, index);
        this._context = context;
        this._budgetID = budgetID;

        this._activityType = activityType;

        int keyTypeArray = (activityType==Transaction.TRANSACTION_TYPE.Expense ? R.array.keytype_array_ex : R.array.keytype_array_in);

        // filtering
        filterMethods = new HashMap<>();

        textView_filters = (TextView) getBase().findViewById(R.id.textView_filters);
        relativeLayout_filter = (RelativeLayout) getBase().findViewById(R.id.relativeLayout_filter);

        relativeLayout_filter.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                filterMethods.clear();
                Refresh();
            }
        });

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

                    filterMethods.clear();
                    CheckShowFiltersNotice();

                    Refresh();
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        //View details button
        textView_viewdetails = (TextView) getBase().findViewById(R.id.textView_cardTransaction_viewdetails);
        button_viewDetails = (LinearLayout) getBase().findViewById(R.id.button_cardTransaction_viewdetails);
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

        // Filter button
        button_filter = (ImageView) getBase().findViewById(R.id.imageView_cardTransaction_filter);
        button_filter.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(context, button_filter);
                //Inflating the Popup using xml file
                if (activityType == Transaction.TRANSACTION_TYPE.Expense) {
                    popup.getMenuInflater().inflate(R.menu.submenu_filter_expense, popup.getMenu());
                } else if (activityType == Transaction.TRANSACTION_TYPE.Income){
                    popup.getMenuInflater().inflate(R.menu.submenu_filter_income, popup.getMenu());
                }

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            // Filters
                            case R.id.toolbar_filter_category:
                            case R.id.toolbar_filter_source:
                            case R.id.toolbar_filter_paidby:
                            case R.id.toolbar_filter_splitwith:
                            case R.id.toolbar_filter_paidback:
                                Helper.FILTER_METHODS method = Helper.FILTER_METHODS.values()[item.getOrder()];
                                Helper.OpenDialogFragment(context, DialogFragmentFilter.newInstance(CardTransaction.this, context, _budgetID, method, context.getString(Helper.filterTitles.get(method)), activityType), true);
                                break;

                            default:
                                break;
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });


        //Parent layout
        cardView = (CardView) getBase().findViewById(R.id.cardTransaction);

        //Pie chart
        chart = (PieChart) getBase().findViewById(R.id.pieChart_cardTransaction);

        chart.setDescription("");

        chart.setHighlightPerTapEnabled(true);
        chart.setOnChartValueSelectedListener(this);

        chart.setTouchEnabled(true);

        chart.setDrawCenterText(true);

        chart.setDrawHoleEnabled(true);
        chart.setRotationEnabled(true);
        chart.setDrawSlicesUnderHole(true);

        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleColor(Color.WHITE);

        chart.setDragDecelerationFrictionCoef(0.96f);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(55f);
        chart.setTransparentCircleRadius(58f);
        chart.setCenterTextSize(22);

        chart.setExtraOffsets(0, 10, 0, 10);
        chart.setDrawEntryLabels(false);

        //Legend
        Legend l = chart.getLegend();
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);
        l.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        l.setDrawInside(false);
        l.setEnabled(true);
        l.setWordWrapEnabled(true);

        //Slice labels
        chart.setEntryLabelTextSize(10);
        chart.setEntryLabelColor(Color.BLACK);
    }


    public void Refresh(){
        CheckShowFiltersNotice();


        Budget _budget = BudgetManager.getInstance().GetBudget(_budgetID);
        if (_budget != null){
            if (chart != null) { chart.clear(); }
            if (dataSet != null) { dataSet.clear(); }

            //Get expense data
            ArrayList<Transaction> transactions = _budget.GetTransactionsInTimeframe(_context,  _activityType, Helper.SORT_METHODS.DATE_DSC, filterMethods );

            if (transactions.size() > 0) {
                //Total
                Double total = 0d;
                Double val = 0d;

                //Convert transaction data to a list of PieEntry
                List<PieEntry> entries = new ArrayList<>();
                HashMap<String, Double> source = new HashMap<>();
                SparseArray<Double> debt = new SparseArray<>();
                SparseArray<Double> category = new SparseArray<>();
                List<Integer> colors = new ArrayList<>();

                for (Transaction t : transactions) {
                    if (t.GetValue() > 0) {

                        if (keyType == 0) { // Source
                            val = t.GetSplit(Person.Me.GetID());

                            // Add to existing value
                            String emptySafeSource = (t.GetSource().equals("") ? _context.getString(R.string.info_nosource) : t.GetSource());
                            if (source.get(emptySafeSource) != null) { val += source.get(emptySafeSource); }
                            source.put(emptySafeSource, val);
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

                                    // Add to existing value
                                    if (debt.get(entry.getKey()) != null) { val += debt.get(entry.getKey()); }
                                    debt.put(entry.getKey(), val);

                                }
                            }
                        }

                    }
                }

                // Source case
                if (keyType == 0){
                    for (HashMap.Entry<String, Double> sourceVal : source.entrySet()){
                        val = sourceVal.getValue();
                        PieEntry entry = new PieEntry(val.floatValue(), sourceVal.getKey());
                        entry.setData(sourceVal.getKey());

                        entries.add(entry);
                        colors.add(Helper.ColorFromString(sourceVal.getKey()));

                        // Sum up the values
                        total += val;
                    }
                }

                // Category case
                else if (keyType == 1){
                    CategoryManager cm = CategoryManager.getInstance();
                    Category cat;

                    int size = category.size();
                    for (int i = 0; i < size; i++){
                        cat = cm.GetCategory(category.keyAt(i));
                        val = category.get(category.keyAt(i));

                        PieEntry entry = new PieEntry(val.floatValue(), cat.GetTitle());
                        entry.setData(cat.GetID());

                        entries.add(entry);
                        colors.add(cat.GetColor());

                        // Sum up the values
                        total += val;
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
                            PieEntry entry = new PieEntry(val.floatValue(), p.GetName());
                            entry.setData(p.GetID());

                            entries.add(entry);
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

                textView_viewdetails.setText(R.string.action_viewdetails);
            }
            else { //No data
                //chart.setVisibility(View.GONE);
                chart.setData(null);

                if (filterMethods.size() == 0) {
                    String transactionType = _context.getString(_activityType == Transaction.TRANSACTION_TYPE.Expense ? R.string.format_nodata_viewdetails_expense : R.string.misc_income);
                    textView_viewdetails.setText(String.format(_context.getString(R.string.format_nodata_viewdetails), transactionType));
                }
            }

        }

        // Animation
        chart.animateY(800, Easing.EasingOption.EaseInSine);
        chart.spin(1400, 0, 360, Easing.EasingOption.EaseOutSine);
    }

    public void SetBudget(int id){ _budgetID = id; }


    // Filtering
    public void AddFilterMethod(Helper.FILTER_METHODS method, String data){
        filterMethods.put(method, data);
    }
    public void CheckShowFiltersNotice(){
        relativeLayout_filter.setVisibility( (filterMethods.size() > 0 ? View.VISIBLE : View.GONE) );
        button_filter.setVisibility( (filterMethods.size() == 0 ? View.VISIBLE : View.GONE) );

        textView_filters.setText(Helper.FilterString(_context, filterMethods));
    }


    // Clicking on pie chart slices
    @Override
    public void onValueSelected(Entry e, Highlight h){
        if (keyType == 0){ // Source
            String[] oldLabels = chart.getLegend().getLabels();
            List<String> newLabels = new ArrayList<>();
            for (String s : oldLabels){
                String cleanS = s.replace("[", "").replace("]", "");
                if (s.equals(e.getData())){
                    newLabels.add(String.format("[%s]", cleanS));
                } else {
                    newLabels.add(cleanS);
                }
            }
            chart.getLegend().setComputedLabels(newLabels);

        } else if (keyType == 1){ // Category
            CategoryManager cm = CategoryManager.getInstance();
            Category cat = cm.GetCategory((int)e.getData());

            if (cat != null){
                String[] oldLabels = chart.getLegend().getLabels();
                List<String> newLabels = new ArrayList<>();
                for (String s : oldLabels){
                    String cleanS = s.replace("[", "").replace("]", "");
                    if (s.equals(cat.GetTitle())){
                        newLabels.add(String.format("[%s]", cleanS));
                    } else {
                        newLabels.add(cleanS);
                    }
                }
                chart.getLegend().setComputedLabels(newLabels);
            }
        } else if (keyType == 2){ // Debt
            PersonManager pm = PersonManager.getInstance();
            Person p = pm.GetPerson((int)e.getData());

            if (p != null){
                String[] oldLabels = chart.getLegend().getLabels();
                List<String> newLabels = new ArrayList<>();
                for (String s : oldLabels){
                    String cleanS = s.replace("[", "").replace("]", "");
                    if (s.equals(p.GetName())){
                        newLabels.add(String.format("[%s]", cleanS));
                    } else {
                        newLabels.add(cleanS);
                    }
                }
                chart.getLegend().setComputedLabels(newLabels);
            }
        }
    }

    @Override public void onNothingSelected(){
        String[] oldLabels = chart.getLegend().getLabels();
        List<String> newLabels = new ArrayList<>();
        if (oldLabels != null) {
            for (String s : oldLabels) {
                newLabels.add(s.replace("[", "").replace("]", ""));
            }
            chart.getLegend().setComputedLabels(newLabels);
        }
    }
}
