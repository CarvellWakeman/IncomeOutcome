package carvellwakeman.incomeoutcome;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.joda.time.LocalDate;

import java.io.Serializable;


public class NewExpenseActivity extends AppCompatActivity
{
    //Data structure IDs
    String _profileID;
    int _expenseID;
    
    String _blacklist_parentID;
    LocalDate _blacklistDate;
    Expense clone;

    //Helper variables
    Double tCost;
    Double sCost;
    Double splitPercent;
    
    //Adapters
    ArrayAdapter<String> otherPeopleAdapter;

    //Fragments
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    TimePeriodFragment fragment_timePeriod;


    //Views
    Toolbar toolbar;

    Spinner spinner_categories;
    Spinner spinner_otherPeople;

    CheckBox checkBox_split;

    Switch switch_paidBy;

    EditText editText_placeOfPurchase;
    EditText editText_description;
    EditText editText_cost;
    EditText editText_personA_cost;
    EditText editText_personB_cost;

    Button button_newPerson;

    TextView textView_percentageSplit;
    TextView textView_splitNotice;

    CardView cardView_splitPercentage;

    FrameLayout frameLayout_timePeriod;

    DiscreteSeekBar discreteSeekBar_split;


    boolean noInfiniteLoopPlease = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);

        //Variable defaults
        noInfiniteLoopPlease = true;
        tCost = sCost = splitPercent = 0d;
        _profileID = "";
        _expenseID = 0;


        //Time Period fragment
        fragment_timePeriod = new TimePeriodFragment();


        //Find Views
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        cardView_splitPercentage = (CardView) findViewById(R.id.card_newExpense_split);

        frameLayout_timePeriod = (FrameLayout) findViewById(R.id.frameLayout_timePeriod);

        spinner_categories = (Spinner) findViewById(R.id.spinner_newExpense_categories);
        spinner_otherPeople = (Spinner) findViewById(R.id.spinner_newExpense_otherpeople);

        checkBox_split = (CheckBox) findViewById(R.id.checkBox_newExpense_splitEnabled);
        checkBox_split.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newExpenseSplitClick(v);
            }
        });

        switch_paidBy = (Switch) findViewById(R.id.switch_paidBy);
        switch_paidBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch_paidBy.isChecked() && spinner_otherPeople.getCount() > 0){ switch_paidBy.setText(switch_paidBy.getTextOn()); } else { switch_paidBy.setText(switch_paidBy.getTextOff()); }
            }
        });

        editText_placeOfPurchase = ((TextInputLayout)findViewById(R.id.TIL_newExpense_placepurchase)).getEditText();

        editText_description = ((TextInputLayout)findViewById(R.id.TIL_newExpense_description)).getEditText();

        button_newPerson = (Button) findViewById(R.id.button_newExpense_addPerson);
        button_newPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newExpenseNewPersonClick(v);
            }
        });

        editText_cost = ((TextInputLayout)findViewById(R.id.TIL_newExpense_cost)).getEditText();

        editText_personA_cost = ((TextInputLayout)findViewById(R.id.TIL_newExpense_PersonACost)).getEditText();
        editText_personB_cost = ((TextInputLayout)findViewById(R.id.TIL_newExpense_PersonBCost)).getEditText();

        textView_percentageSplit = (TextView) findViewById(R.id.textView_newExpense_split);
        textView_splitNotice = (TextView) findViewById(R.id.textView_newExpense_splitNotice);

        discreteSeekBar_split = (DiscreteSeekBar) findViewById(R.id.seekBar_newExpense);

        //Categories spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_title,  ProfileManager.categories){
            @Override
            public boolean isEnabled(int position) {
                return (position !=0);
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_list_primary);
        spinner_categories.setAdapter(adapter);

        //Other People spinner
        //otherPeopleAdapter = new ArrayAdapterNewButton<>(this, R.layout.spinner_dropdown_title, 0, ProfileManager.GetOtherPeopleNames());
        otherPeopleAdapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_title, ProfileManager.GetOtherPeopleNames());
        otherPeopleAdapter.setDropDownViewResource(R.layout.spinner_dropdown_list_primary);
        spinner_otherPeople.setAdapter(otherPeopleAdapter);


        //Configure toolbar
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.inflateMenu(R.menu.toolbar_menu_save);
        setSupportActionBar(toolbar);


        //Set TimePeriod fragment
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout_timePeriod, fragment_timePeriod);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


        //Set Title
        toolbar.setTitle("New Expense");

        //Configure the ability to split expense
        if (ProfileManager.GetOtherPeopleCount() > 0){
            checkBox_split.setVisibility(View.VISIBLE);
            textView_splitNotice.setVisibility(View.GONE);
            button_newPerson.setVisibility(View.GONE);
        }

        //discreteSeekBar_split.setIndicatorFormatter("%s%%");

        //Cost formatting
        editText_cost.setKeyListener(DigitsKeyListener.getInstance(false, true));
        editText_cost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                UpdateCostBasedOnSeekBar();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        //Seekbar editText_cost split percentage
        if (discreteSeekBar_split != null && editText_personA_cost != null && editText_personB_cost != null && editText_cost != null) {
            discreteSeekBar_split.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                @Override
                public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int i, boolean b) {
                    if (getDouble(editText_cost.getText().toString()) != 0.0f) {
                        UpdateCostBasedOnSeekBar();
                    }
                }
                @Override
                public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {}
                @Override
                public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {}
            });
        }


        //EditText PersonA/PersonB formatting
        editText_personA_cost.setKeyListener(DigitsKeyListener.getInstance(false, true));
        editText_personA_cost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if (editText_personB_cost != null && editText_personA_cost != null && discreteSeekBar_split != null) {
                    if (noInfiniteLoopPlease) {
                        noInfiniteLoopPlease = false;

                        String r = editText_personA_cost.getText().toString();
                        Double p = getDouble(r);

                        String cr = editText_cost.getText().toString();
                        Double tc = getDouble(cr);

                        discreteSeekBar_split.setProgress(100 - (int) ((p / tc) * 100));

                        if (!editText_personB_cost.getText().toString().equals("")) {
                            editText_personB_cost.setText(ProfileManager.decimalFormat.format(tc - p));
                        }
                        else if (editText_cost.getText().toString().equals("")) {
                            editText_cost.setText(ProfileManager.decimalFormat.format(p));
                            editText_personB_cost.setText(ProfileManager.decimalFormat.format(0));
                        }

                        UpdateCost();
                    }
                    else {
                        noInfiniteLoopPlease = true;
                    }
                }
            }
        });

        editText_personB_cost.setKeyListener(DigitsKeyListener.getInstance(false, true));
        editText_personB_cost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int afragmentTransactioner) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if (editText_personB_cost != null && editText_personA_cost != null && discreteSeekBar_split != null) {
                    if (noInfiniteLoopPlease) {
                        noInfiniteLoopPlease = false;

                        String r = editText_personB_cost.getText().toString();
                        Double p = getDouble(r);

                        String cr = editText_cost.getText().toString();
                        Double tc = getDouble(cr);

                        discreteSeekBar_split.setProgress((int) ((p / tc) * 100));

                        if (!editText_personA_cost.getText().toString().equals("")) {
                            editText_personA_cost.setText(ProfileManager.decimalFormat.format(tc - p));
                        }
                        else if (editText_cost.getText().toString().equals("")) {
                            editText_cost.setText(ProfileManager.decimalFormat.format(p));
                            editText_personA_cost.setText(ProfileManager.decimalFormat.format(0));
                        }

                        UpdateCost();
                    }
                    else {
                        noInfiniteLoopPlease = true;
                    }
                }
            }
        });



        //Get intent from launching activity
        Intent intent = getIntent();
        _profileID = intent.getStringExtra("profile");
        _expenseID = intent.getIntExtra("expense", 0);
        _blacklist_parentID = intent.getStringExtra("blacklist_parent");
        _blacklistDate = (LocalDate) intent.getSerializableExtra("blacklist_date");
        clone = (Expense)intent.getSerializableExtra("clone");

        //Expense possibilities
        //New expense
        //New expense (from existing expense)
        //Edit expense
        if (clone != null) {
            CloneExpense(clone);
            _expenseID = 0;
        }
        else {
            EditExpense(_profileID, _expenseID);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_save, menu);
        return true;
    }

    //Toolbar button handling
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home: //X close button
                finish();
                return true;
            case R.id.toolbar_save: //SAVE button
                FinishNewExpense();
                return true;
            default:
                return false;
        }
    }

    //Get return results from activities
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            //Configure the ability to split expense
            if (ProfileManager.GetOtherPeopleCount() > 0){
                checkBox_split.setVisibility(View.VISIBLE);
                textView_splitNotice.setVisibility(View.GONE);
                button_newPerson.setVisibility(View.GONE);

                //Reset adapter
                otherPeopleAdapter.clear();
                otherPeopleAdapter.addAll(ProfileManager.GetOtherPeopleNames());
            }
        }
    }


    //Edit Expense
    public void EditExpense(String profileID, int expenseID)
    {
        if (profileID != null) {
            if (expenseID != 0) {
                Profile pr = ProfileManager.GetProfile(profileID);
                Expense ex = (pr != null ? pr.GetExpense(expenseID) : null);

                //If we were sent an expense instead of creating a new one
                if (ex != null) {
                    toolbar.setTitle("Edit Expense");

                    //Copy details from expense object to edit page

                    //Copy category
                    if (ProfileManager.categories.contains(ex.GetCategory())) {
                        spinner_categories.setSelection(ProfileManager.categories.indexOf(ex.GetCategory()), true);
                    }
                    //Copy company
                    editText_placeOfPurchase.setText(ex.GetCompany());
                    //Copy editText_description
                    editText_description.setText(ex.GetDescription());

                    //Copy time period
                    fragment_timePeriod.SetTimePeriod(ex.GetTimePeriod());


                    //Copy editText_cost
                    editText_cost.setText(String.valueOf(ex.GetValue()));

                    //Only copy if this expense was split
                    if (ex.GetSplitWith() != null) {
                        //Copy sub costs
                        editText_personA_cost.setText(String.valueOf(ex.GetValue() - ex.GetSplitValue()));
                        editText_personB_cost.setText(String.valueOf(ex.GetSplitValue()));

                        //Copy progress bar value
                        if (ex.GetValue() > 0) {
                            discreteSeekBar_split.setProgress((int) ((ex.GetSplitValue() / ex.GetValue()) * 100));
                        }

                        //Update editText_cost
                        UpdateCost();

                        //Copy who paid switch
                        switch_paidBy.setChecked(ex.GetIPaid());
                        switch_paidBy.setText((ex.GetIPaid() ? switch_paidBy.getTextOn() : switch_paidBy.getTextOff()));

                        //textView_percentageSplit checkbox
                        checkBox_split.setChecked(true);
                        checkBox_split.setVisibility((spinner_otherPeople.getCount() > 0 ? View.VISIBLE : View.GONE));

                        //textView_percentageSplit spinner
                        spinner_otherPeople.setVisibility(View.VISIBLE);
                        spinner_otherPeople.setSelection(otherPeopleAdapter.getPosition(ex.GetSplitWith().GetName()), true);

                        //textView_percentageSplit cardview
                        cardView_splitPercentage.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    // Clone Expense
    public void CloneExpense(Expense cloneExpense)
    {
        if (cloneExpense != null) {

            toolbar.setTitle("Edit Expense");

            //Copy details from expense object to edit page

            //Copy category
            if (ProfileManager.categories.contains(cloneExpense.GetCategory())) {
                spinner_categories.setSelection(ProfileManager.categories.indexOf(cloneExpense.GetCategory()), true);
            }
            //Copy company
            editText_placeOfPurchase.setText(cloneExpense.GetCompany());
            //Copy editText_description
            editText_description.setText(cloneExpense.GetDescription());

            //Copy time period
            fragment_timePeriod.SetTimePeriod(cloneExpense.GetTimePeriod());


            //Copy editText_cost
            editText_cost.setText(String.valueOf(cloneExpense.GetValue()));

            //Only copy if this expense was split
            if (cloneExpense.GetSplitWith() != null) {
                //Copy sub costs
                editText_personA_cost.setText(String.valueOf(cloneExpense.GetValue() - cloneExpense.GetSplitValue()));
                editText_personB_cost.setText(String.valueOf(cloneExpense.GetSplitValue()));

                //Copy progress bar value
                if (cloneExpense.GetValue() > 0) {
                    discreteSeekBar_split.setProgress((int) ((cloneExpense.GetSplitValue() / cloneExpense.GetValue()) * 100));
                }

                //Update editText_cost
                UpdateCost();

                //Copy who paid switch
                switch_paidBy.setChecked(cloneExpense.GetIPaid());
                switch_paidBy.setText((cloneExpense.GetIPaid() ? switch_paidBy.getTextOn() : switch_paidBy.getTextOff()));

                //textView_percentageSplit checkbox
                checkBox_split.setChecked(true);
                checkBox_split.setVisibility((spinner_otherPeople.getCount() > 0 ? View.VISIBLE : View.GONE));

                //textView_percentageSplit spinner
                spinner_otherPeople.setVisibility(View.VISIBLE);
                spinner_otherPeople.setSelection(otherPeopleAdapter.getPosition(cloneExpense.GetSplitWith().GetName()), true);

                //textView_percentageSplit cardview
                cardView_splitPercentage.setVisibility(View.VISIBLE);
            }
        }
    }


    //Cost updates
    public void UpdateCost()
    {
        //Find total editText_cost
        tCost = getDouble(editText_cost.getText().toString());

        //Find other person editText_cost
        sCost = getDouble(editText_personB_cost.getText().toString());

        //Find personA/B split values based on costs
        splitPercent = (sCost / tCost);

        //Update split text percentages
        if (tCost > 0) {
            textView_percentageSplit.setText("Percentage Split - " + ProfileManager.decimalFormat.format( 100- (splitPercent * 100.00f) ) + "% / " + ProfileManager.decimalFormat.format(splitPercent * 100.00f) + "%");
        }
        else
        {
            textView_percentageSplit.setText("Percentage Split");
        }
    }

    public void UpdateCostBasedOnSeekBar()
    {
        if (noInfiniteLoopPlease)
        {
            UpdateCost();

            noInfiniteLoopPlease = false;
            editText_personA_cost.setText(ProfileManager.decimalFormat.format(((float) (100 - discreteSeekBar_split.getProgress()) / 100.00f) * tCost));

            noInfiniteLoopPlease = false;
            editText_personB_cost.setText(ProfileManager.decimalFormat.format(((float) discreteSeekBar_split.getProgress() / 100.00f) * tCost));

            UpdateCost();
        }
    }


    //Helpers
    public Double getDouble(String str)
    {
        try {
            return Double.parseDouble(str);
        }
        catch (NumberFormatException ex) {
            return 0.0d;
        }
    }


    //Buttons
    public void newExpenseNewPersonClick(View v){

        Intent intent = new Intent(NewExpenseActivity.this, OtherPersonActivity.class);
        intent.putExtra("profile", ProfileManager.GetCurrentProfile().toString());
        startActivityForResult(intent, 0);
    }

    public void newExpenseSplitClick(View v)
    {
        //Change checkbox text
        checkBox_split.setText((checkBox_split.isChecked() ? "Split Cost with" : "Split Cost"));

        //Make Visible or Gone if split is checked or not
        spinner_otherPeople.setVisibility( (checkBox_split.isChecked() ? View.VISIBLE : View.GONE) );
        cardView_splitPercentage.setVisibility((checkBox_split.isChecked() ? View.VISIBLE : View.GONE));

        //Update costs
        UpdateCostBasedOnSeekBar();
    }


    public void FinishNewExpense()
    {
        //If the user selected a category
        if (spinner_categories.getSelectedItemPosition() != 0) {
            //Update editText_cost
            UpdateCost();

            //Create intent to send back
            Intent intent = new Intent();

            //Create a new expense or locate the one we're editing
            Expense exp;
            if (_profileID != null && _expenseID != 0) {
                Profile pr = ProfileManager.GetProfile(_profileID);
                exp = (pr != null ? pr.GetExpense(_expenseID) : null);
            }
            else {
                exp = new Expense();
            }

            //If the expense now exists
            if (exp != null) {

                // If Expense was cloned, copy parentID into new object
                if (clone != null){
                    exp.SetParentID(clone.GetParentID());
                }

                //Set Cost
                exp.SetValue(tCost);

                //Set Company
                exp.SetCompany(editText_placeOfPurchase.getText().toString());

                //Set Category
                exp.SetCategory((String) spinner_categories.getSelectedItem());

                //Set Description
                exp.SetDescription(editText_description.getText().toString());

                //Set Time Period
                TimePeriod tp = fragment_timePeriod.GetTimePeriod();

                //Set time period
                exp.SetTimePeriod(tp);

                //Set textView_percentageSplit value (If applicable)
                if (checkBox_split.isChecked()) {
                    exp.SetSplitValue(ProfileManager.GetOtherPersonByName(spinner_otherPeople.getSelectedItem().toString()), sCost);
                }
                else {
                    exp.SetSplitValue(null, 0.0);
                }

                //Set who paid (Me, by default)
                exp.SetIPaid(switch_paidBy.isChecked());


                //Put the newly generated expense into the intent if one was allocated
                if (_profileID != null && _expenseID == 0) {
                    intent.putExtra("profile", _profileID);
                    intent.putExtra("expense", exp);
                    intent.putExtra("blacklist_parent", _blacklist_parentID);
                    intent.putExtra("blacklist_date", _blacklistDate);
                }

                //Set result and end this activity
                setResult(RESULT_OK, intent);
                finish();
            }
            else {
                Toast.makeText(this, "Missing Expense Data", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Missing Category", Toast.LENGTH_LONG).show();
        }

    }


}
