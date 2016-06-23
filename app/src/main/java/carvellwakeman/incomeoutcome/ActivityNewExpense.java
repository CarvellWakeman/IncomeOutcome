package carvellwakeman.incomeoutcome;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.*;
import android.widget.*;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.joda.time.LocalDate;


public class ActivityNewExpense extends AppCompatActivity
{
    //Data structure IDs
    int _profileID;
    int _expenseID;
    int _editCopyOrClone; //edit (1), copy(2), clone(3)

    LocalDate _cloneDate;

    //String _blacklist_parentID;
    //LocalDate _blacklistDate;
    //Expense clone;

    //Helper variables
    Double tCost;
    Double sCost;
    Double splitPercent;
    boolean OtherPersonDoesNotExistBypass = false;
    boolean CategoryDoesNotExistBypass = false;

    //Adapters
    ArrayAdapter<String> otherPeopleAdapter;
    ArrayAdapter<String> categoryAdapter;

    //Fragments
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    FragmentTimePeriod fragment_timePeriod;


    //Views
    Toolbar toolbar;

    NoDefaultSpinner spinner_categories;
    Spinner spinner_otherPeople;

    CheckBox checkBox_split;

    Switch switch_paidBy;

    EditText editText_placeOfPurchase;
    EditText editText_description;
    EditText editText_cost;
    EditText editText_personA_cost;
    EditText editText_personB_cost;

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
        _profileID = -1;
        _expenseID = 0;
        _editCopyOrClone = 0;
        _cloneDate = null;


        //Time Period fragment
        fragment_timePeriod = new FragmentTimePeriod();



        //Find Views
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        cardView_splitPercentage = (CardView) findViewById(R.id.card_newExpense_split);

        frameLayout_timePeriod = (FrameLayout) findViewById(R.id.frameLayout_timePeriod);

        spinner_categories = (NoDefaultSpinner) findViewById(R.id.spinner_newExpense_categories);
        spinner_otherPeople = (Spinner) findViewById(R.id.spinner_newExpense_otherpeople);

        checkBox_split = (CheckBox) findViewById(R.id.checkBox_newExpense_splitEnabled);
        checkBox_split.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Profile pr = ProfileManager.GetProfileByID(_profileID);
                if (pr != null) {
                    Expense ex = pr.GetExpense(_expenseID);
                    if (ex != null) {
                        if (!ProfileManager.HasOtherPerson(ex.GetSplitWith()) && !OtherPersonDoesNotExistBypass) {
                            new AlertDialog.Builder(ActivityNewExpense.this).setTitle(R.string.confirm_areyousure_nolongerexists)
                                    .setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            spinner_otherPeople.setAdapter(otherPeopleAdapter);
                                            spinner_otherPeople.setEnabled(true);
                                            newExpenseSplitClick(v);
                                            OtherPersonDoesNotExistBypass = true;
                                        }})
                                    .setNegativeButton(R.string.confirm_no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            checkBox_split.setChecked(true);
                                        }})
                                    .setCancelable(false)
                                    .create().show();
                        }
                        else { newExpenseSplitClick(v); }
                    }
                    else { newExpenseSplitClick(v); }
                }
                else { newExpenseSplitClick(v); }

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

        editText_cost = ((TextInputLayout)findViewById(R.id.TIL_newExpense_cost)).getEditText();

        editText_personA_cost = ((TextInputLayout)findViewById(R.id.TIL_newExpense_PersonACost)).getEditText();
        editText_personB_cost = ((TextInputLayout)findViewById(R.id.TIL_newExpense_PersonBCost)).getEditText();

        textView_percentageSplit = (TextView) findViewById(R.id.textView_newExpense_split);
        textView_splitNotice = (TextView) findViewById(R.id.textView_newExpense_splitNotice);

        discreteSeekBar_split = (DiscreteSeekBar) findViewById(R.id.seekBar_newExpense);

        //Categories spinner
        categoryAdapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_title,  ProfileManager.GetCategoryTitles()){
            @Override
            public boolean isEnabled(int position){
                if(position == 0) { return false; } //Hint
                else { return true; }
            }

        };
        categoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_list_primary);
        spinner_categories.setAdapter(categoryAdapter);
        spinner_categories.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    Profile pr = ProfileManager.GetProfileByID(_profileID);
                    if (pr != null) {
                        Expense ex = pr.GetExpense(_expenseID);
                        if (ex != null) {
                            if (!ProfileManager.HasCategory(ex.GetCategory()) && !CategoryDoesNotExistBypass) {
                                new AlertDialog.Builder(ActivityNewExpense.this).setTitle(R.string.confirm_areyousure_nolongerexists2)
                                        .setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                spinner_categories.setAdapter(categoryAdapter);
                                                spinner_categories.performClick();
                                                CategoryDoesNotExistBypass = true;
                                            }})
                                        .setNegativeButton(R.string.confirm_no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                spinner_categories.onDetachedFromWindow(); //Close spinner
                                            }})
                                        .setCancelable(false)
                                        .create().show();
                            }

                        }

                    }

                }
                return false;
            }
        });

        //Other People spinner
        otherPeopleAdapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_title, ProfileManager.GetOtherPeople());
        otherPeopleAdapter.setDropDownViewResource(R.layout.spinner_dropdown_list_primary);
        //otherPeopleAdapter.add(getBaseContext().getString(R.string.add_otherperson));
        spinner_otherPeople.setAdapter(otherPeopleAdapter);
        spinner_otherPeople.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ( otherPeopleAdapter.getItem(position).equals(getBaseContext().getString(R.string.add_otherperson)) ){
                    //newExpenseNewPersonClick(view);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


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
            //button_newPerson.setVisibility(View.GONE);
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
        _profileID = intent.getIntExtra("profile", -1);
        _expenseID = intent.getIntExtra("expense", 0);
        _editCopyOrClone = intent.getIntExtra("edit", 0);
        _cloneDate = (LocalDate) intent.getSerializableExtra("cloneDate");

        //Copy expense details if expense was provided in the intent
        if (_expenseID != 0){
            EditExpense();
        }


        //Clear timeperiod blacklistdates queue
        Profile pr = ProfileManager.GetProfileByID(_profileID);
        if (pr != null){
            Expense ex = pr.GetExpense(_expenseID);
            if (ex != null) {
                if (ex.GetTimePeriod() != null) {
                    ex.GetTimePeriod().ClearBlacklistQueue();
                }
            }
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
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                return true;
            case R.id.toolbar_save: //SAVE button
                FinishNewExpense();
                return true;
            default:
                return false;
        }
    }

    //Send back a RESULT_CANCELED to MainActivity when back is pressed
    @Override
    public void onBackPressed()
    {
        //Send back a RESULT_CANCELED to MainActivity
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);

        super.onBackPressed();
    }

    //Get return results from activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            //Configure the ability to split expense
            if (ProfileManager.GetOtherPeopleCount() > 0){
                checkBox_split.setVisibility(View.VISIBLE);
                textView_splitNotice.setVisibility(View.GONE);
                //button_newPerson.setVisibility(View.GONE);

                //Reset adapter
                otherPeopleAdapter.clear();
                //otherPeopleAdapter.add(getBaseContext().getString(R.string.add_otherperson));
                otherPeopleAdapter.addAll(ProfileManager.GetOtherPeople());
            }
        }
    }


    //Edit Expense
    public void EditExpense()
    {
        if (_profileID != -1) {
            if (_expenseID != 0) {
                Profile pr = ProfileManager.GetProfileByID(_profileID);
                Expense ex = (pr != null ? pr.GetExpense(_expenseID) : null);

                //If we were sent an expense instead of creating a new one
                if (ex != null) {

                    //Set activity title appropriately
                    if (_editCopyOrClone == 2){ toolbar.setTitle("Copy Expense"); } else { toolbar.setTitle("Edit Expense"); }

                    //Copy category
                    if (ProfileManager.HasCategory(ex.GetCategory())) {
                        spinner_categories.setSelection(ProfileManager.GetCategoryIndex(ex.GetCategory())+1, true); //+1 for "select a category.."
                    }
                    //Copy company
                    editText_placeOfPurchase.setText(ex.GetSourceName());
                    //Copy editText_description
                    editText_description.setText(ex.GetDescription());

                    //Copy time period (If no clone date was provided)
                    if (_cloneDate == null) {
                        fragment_timePeriod.SetTimePeriod(ex.GetTimePeriod());
                    }
                    else { fragment_timePeriod.SetTimePeriod(new TimePeriod(_cloneDate)); }


                    //Copy editText_cost
                    editText_cost.setText(String.valueOf(ex.GetValue()));

                    //Only copy if this expense was split
                    if (!ex.GetSplitWith().equals("")) {
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
                        if (ProfileManager.HasOtherPerson(ex.GetSplitWith())) {
                            spinner_otherPeople.setSelection(otherPeopleAdapter.getPosition(ex.GetSplitWith()), true);
                        }
                        else{
                            spinner_otherPeople.setEnabled(false);
                            String[] arr = { ex.GetSplitWith() };
                            ArrayAdapter ad = new ArrayAdapter<>(this, R.layout.spinner_dropdown_title, arr);
                            spinner_otherPeople.setAdapter(ad);
                        }
                        //button_newPerson.setVisibility(View.VISIBLE);

                        //textView_percentageSplit cardview
                        cardView_splitPercentage.setVisibility(View.VISIBLE);
                    }
                }
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
        if (spinner_categories.getSelectedItemPosition() != 0 || CategoryDoesNotExistBypass) {
            //Update editText_cost
            UpdateCost();

            //Find profile
            Profile pr = null;
            if (_profileID != -1) {
                pr = ProfileManager.GetProfileByID(_profileID);
            }


            //If profile exists
            if (pr != null) {

                //Find original transaction
                Expense originalExp = pr.GetExpense(_expenseID);

                //Create intent to send back
                Intent intent = new Intent();

                //Create a new transaction or locate the one we're editing
                Expense newExp;

                if (_expenseID != 0) {

                    if (_editCopyOrClone == 1) { //edit
                        newExp = originalExp;
                    }
                    else { newExp = new Expense(); }
                }
                else {
                    newExp = new Expense();
                }


                //If the transaction now exists
                if (newExp != null) {

                    //Set Cost
                    newExp.SetValue(tCost);

                    //Set Company
                    newExp.SetSourceName(editText_placeOfPurchase.getText().toString());

                    //Set Category
                    if (ProfileManager.HasCategory((String) spinner_categories.getSelectedItem()) || CategoryDoesNotExistBypass && _expenseID != 0 || _expenseID == 0) {
                        newExp.SetCategory((String) spinner_categories.getSelectedItem());
                    }

                    //Set Description
                    newExp.SetDescription(editText_description.getText().toString());

                    //Set Time Period
                    TimePeriod tp = fragment_timePeriod.GetTimePeriod();

                    //Set time period
                    newExp.SetTimePeriod(tp);

                    //Set textView_percentageSplit value (If applicable)
                    if (checkBox_split.isChecked()) {
                        //Set who paid (Me, by default)
                        newExp.SetIPaid(switch_paidBy.isChecked());
                        newExp.SetSplitValue(spinner_otherPeople.getSelectedItem().toString(), sCost);
                    }
                    else {
                        newExp.SetSplitValue(null, 0.0);
                        //Set who paid (Me, by default)
                        newExp.SetIPaid(true);
                    }




                    //Return new (or edited) transaction and profile in intent
                    intent.putExtra("profile", _profileID);
                    intent.putExtra("newExpense", newExp);

                    //Return original transaction if cloned, and clone date
                    if (_editCopyOrClone == 3) {
                        intent.putExtra("originalExpense", _expenseID);
                        intent.putExtra("cloneDate", _cloneDate);
                    }

                    //Clear timeperiod blacklistdates queue
                    if (originalExp != null) {
                        if (originalExp.GetTimePeriod() != null) {
                            originalExp.GetTimePeriod().RemoveBlacklistDateQueue();
                        }
                    }

                    //Set result and finish activity
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else {
                    Toast.makeText(this, "Missing Expense Data", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(this, "Missing Category", Toast.LENGTH_LONG).show();
        }

    }


}
