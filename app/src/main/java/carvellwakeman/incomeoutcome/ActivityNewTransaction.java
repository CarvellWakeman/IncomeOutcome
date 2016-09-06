package carvellwakeman.incomeoutcome;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
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


public class ActivityNewTransaction extends AppCompatActivity
{
    //Activity type (Expense or Income)
    int activityType;

    //Data structure IDs
    int _profileID;
    //If editing or cloning
    int _transactionID;

    EDIT_STATE _editState;
    public enum EDIT_STATE
    {
        NewTransaction,
        EditUpdate,
        EditGhost,
        Duplicate
    }


    LocalDate _cloneDate;
    LocalDate _paidBack;

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

    android.support.v7.widget.SwitchCompat switch_paidBy;

    TextInputLayout TIL_cost;

    EditText editText_source;
    EditText editText_description;
    EditText editText_cost;
    EditText editText_personA_cost;
    EditText editText_personB_cost;

    TextView textView_source;
    TextView textView_percentageSplit;
    TextView textView_splitNotice;

    CardView cardView_paidBack;
    CardView cardView_cost;
    CardView cardView_splitPercentage;
    CardView cardView_category;
    CardView cardView_description;

    CheckBox checkBox_paidBack;

    LinearLayout linearLayout_splitCheckbox;
    FrameLayout frameLayout_timePeriod;

    DiscreteSeekBar discreteSeekBar_split;


    boolean noInfiniteLoopPlease = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);


        //Get the intent that opened this activity
        Intent intent = getIntent();
        activityType = intent.getIntExtra("activitytype", -1);


        //Variable defaults
        noInfiniteLoopPlease = true;
        tCost = sCost = splitPercent = 0d;
        _profileID = 0;
        _transactionID = 0;
        _cloneDate = null;


        //Find Views
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        cardView_splitPercentage = (CardView) findViewById(R.id.card_newTransaction_split);
        cardView_paidBack = (CardView) findViewById(R.id.card_newTransaction_paidBack);
        cardView_cost = (CardView) findViewById(R.id.card_newTransaction_cost);
        cardView_category = (CardView) findViewById(R.id.card_newTransaction_category);
        cardView_description = (CardView) findViewById(R.id.card_newTransaction_description);

        linearLayout_splitCheckbox = (LinearLayout) findViewById(R.id.linearLayout_newTransaction_splitCostCheckBox);
        frameLayout_timePeriod = (FrameLayout) findViewById(R.id.frameLayout_timePeriod);

        spinner_categories = (NoDefaultSpinner) findViewById(R.id.spinner_newTransaction_categories);
        spinner_otherPeople = (Spinner) findViewById(R.id.spinner_newTransaction_otherpeople);

        checkBox_paidBack = (CheckBox) findViewById(R.id.checkBox_newTransaction_paidback);
        checkBox_paidBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox_paidBack.isChecked()) {
                    final Profile pr = ProfileManager.GetCurrentProfile();
                    LocalDate c = null;

                    if (pr != null) {
                        final Transaction tr = pr.GetTransaction(_transactionID);
                        if (tr != null) { c = tr.GetPaidBack(); }
                    }
                    if (c == null) { c = new LocalDate(); }

                    DatePickerDialog d = new DatePickerDialog(ActivityNewTransaction.this, datePicker, c.getYear(), c.getMonthOfYear() - 1, c.getDayOfMonth());
                    d.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_NEGATIVE) {
                                checkBox_paidBack.setChecked(false);
                            }
                        }
                    });
                    d.show();
                }
                else{
                    _paidBack = null;
                    UpdatePaidBack();
                }
            }
        });

        checkBox_split = (CheckBox) findViewById(R.id.checkBox_newTransaction_splitEnabled);
        checkBox_split.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Profile pr = ProfileManager.GetProfileByID(_profileID);
                if (pr != null) {
                    Transaction tr = pr.GetTransaction(_transactionID);
                    if (tr != null) {
                        if (tr.GetSplitWith() != null && !ProfileManager.HasOtherPerson(tr.GetSplitWith()) && !OtherPersonDoesNotExistBypass) {
                            new AlertDialog.Builder(ActivityNewTransaction.this).setTitle(R.string.confirm_areyousure_nolongerexists)
                                    .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            spinner_otherPeople.setAdapter(otherPeopleAdapter);
                                            spinner_otherPeople.setEnabled(true);
                                            UpdateSplitClick();
                                            OtherPersonDoesNotExistBypass = true;
                                        }})
                                    .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            checkBox_split.setChecked(true);
                                        }})
                                    .setCancelable(false)
                                    .create().show();
                        }
                        else { UpdateSplitClick(); }
                    }
                    else { UpdateSplitClick(); }
                }
                else { UpdateSplitClick(); }

            }
        });

        switch_paidBy = (android.support.v7.widget.SwitchCompat) findViewById(R.id.switch_paidBy);
        switch_paidBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch_paidBy.isChecked() && spinner_otherPeople.getCount() > 0){ switch_paidBy.setText(switch_paidBy.getTextOn()); } else { switch_paidBy.setText(switch_paidBy.getTextOff()); }
            }
        });

        editText_source = ((TextInputLayout)findViewById(R.id.TIL_newTransaction_placepurchase)).getEditText();
        editText_description = ((TextInputLayout)findViewById(R.id.TIL_newTransaction_description)).getEditText();
        TIL_cost = (TextInputLayout)findViewById(R.id.TIL_newTransaction_cost);
        editText_cost = TIL_cost.getEditText();
        editText_personA_cost = ((TextInputLayout)findViewById(R.id.TIL_newTransaction_PersonACost)).getEditText();
        editText_personB_cost = ((TextInputLayout)findViewById(R.id.TIL_newTransaction_PersonBCost)).getEditText();

        textView_source = (TextView) findViewById(R.id.textView_newTransaction_source);
        textView_percentageSplit = (TextView) findViewById(R.id.textView_newTransaction_split);
        textView_splitNotice = (TextView) findViewById(R.id.textView_newTransaction_splitNotice);

        discreteSeekBar_split = (DiscreteSeekBar) findViewById(R.id.seekBar_newTransaction);

        //Categories spinner
        categoryAdapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_title,  ProfileManager.GetCategoryTitles()){
            @Override
            public boolean isEnabled(int position){
                return position != 0;
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
                        Transaction tr = pr.GetTransaction(_transactionID);
                        if (tr != null) {
                            if (!ProfileManager.HasCategory(tr.GetCategory()) && !CategoryDoesNotExistBypass) {
                                new AlertDialog.Builder(ActivityNewTransaction.this).setTitle(R.string.confirm_areyousure_nolongerexists2)
                                        .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                spinner_categories.setAdapter(categoryAdapter);
                                                spinner_categories.performClick();
                                                CategoryDoesNotExistBypass = true;
                                            }})
                                        .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
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
                    //newTransactionNewPersonClick(view);
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
        fragment_timePeriod = new FragmentTimePeriod();
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout_timePeriod, fragment_timePeriod);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


        //Set Title
        if (activityType == 0) { toolbar.setTitle(R.string.title_newexpense); }
        else if(activityType == 1){ toolbar.setTitle(R.string.title_newincome); }


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


        //Transaction type specifics
        if (activityType == 0){ //Expenses
            //Visibility
            linearLayout_splitCheckbox.setVisibility(View.VISIBLE);
            textView_source.setVisibility(View.VISIBLE);
            spinner_categories.setVisibility(View.VISIBLE);

            //Text
            TIL_cost.setHint(getString(R.string.header_cost));

            //Configure the ability to split expense
            if (ProfileManager.GetOtherPeopleCount() > 0){
                checkBox_split.setVisibility(View.VISIBLE);
                textView_splitNotice.setVisibility(View.GONE);
                //button_newPerson.setVisibility(View.GONE);
            }
        }
        else if (activityType == 1){ //Income
            //Text
            TIL_cost.setHint(getString(R.string.header_amount));
        }


        //Get intent from launching activity
        _profileID = intent.getIntExtra("profile", 0);
        _transactionID = intent.getIntExtra("transaction", 0);
        _editState = EDIT_STATE.values()[intent.getIntExtra("editstate", 0)];
        _cloneDate = (LocalDate) intent.getSerializableExtra("cloneDate");

        //Copy transaction details if transaction was provided in the intent
        if (_profileID != 0 && _transactionID != 0){
            //Copy transactions details into activity
            CopyTransactionDetails();
        }

        //Clear timeperiod blacklistdates queue
        Profile pr = ProfileManager.GetProfileByID(_profileID);
        if (pr != null){
            Transaction tr = pr.GetTransaction(_transactionID);
            if (tr != null) {
                if (tr.GetTimePeriod() != null) {
                    tr.GetTimePeriod().ClearBlacklistQueue();
                }
            }
        }
    }


    final DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            //Set local variable date
            _paidBack = new LocalDate(year, monthOfYear + 1, dayOfMonth);

            UpdatePaidBack();
        }
    };


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
                FinishTransaction();
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

    /*
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
    */


    //Edit Transaction
    public void CopyTransactionDetails()
    {
        if (_profileID != 0) {
            if (_transactionID != 0) {
                Profile pr = ProfileManager.GetProfileByID(_profileID);
                Transaction tr = (pr != null ? pr.GetTransaction(_transactionID) : null);

                //If we were sent an expense
                if (tr != null) {

                    //Set paidback checkbox visibility
                    if ( tr.GetSplitWith() != null && activityType == 0) {
                        cardView_paidBack.setVisibility(View.VISIBLE);
                        checkBox_paidBack.setChecked(tr.IsPaidBack());
                        _paidBack = tr.GetPaidBack();
                        UpdatePaidBack();
                    }

                    //Set activity title appropriately
                    if (_editState == EDIT_STATE.EditUpdate || _editState == EDIT_STATE.EditGhost){
                        TimePeriod tp = tr.GetTimePeriod();

                        if (tr.IsChild() || tp!=null && !tp.DoesRepeat() || _editState == EDIT_STATE.EditGhost) {
                            toolbar.setTitle(R.string.title_edittransaction);
                        } else { toolbar.setTitle(R.string.title_edittransactionseries); }
                    }
                    else if (_editState == EDIT_STATE.Duplicate) { toolbar.setTitle(R.string.title_copytransaction); }

                    //Copy category
                    if (ProfileManager.HasCategory(tr.GetCategory())) {
                        spinner_categories.setSelection(ProfileManager.GetCategoryIndex(tr.GetCategory())+1, true); //+1 for "select a category.."
                    }
                    //Copy source
                    editText_source.setText(tr.GetSourceName());

                    //Copy editText_description
                    editText_description.setText(tr.GetDescription());

                    //Copy time period (If no clone date was provided)
                    if (_cloneDate == null) {
                        fragment_timePeriod.SetTimePeriod(tr.GetTimePeriod());
                    }
                    else {
                        fragment_timePeriod.SetTimePeriod(new TimePeriod(_cloneDate));
                    }


                    //Copy editText_cost
                    editText_cost.setText(String.valueOf(tr.GetValue()));

                    //Only copy if this expense was split
                    if (tr.GetSplitWith() != null) {
                        //Copy sub costs
                        editText_personA_cost.setText(String.valueOf(tr.GetValue() - tr.GetSplitValue()));
                        editText_personB_cost.setText(String.valueOf(tr.GetSplitValue()));

                        //Copy progress bar value
                        if (tr.GetValue() > 0) {
                            discreteSeekBar_split.setProgress((int) ((tr.GetSplitValue() / tr.GetValue()) * 100));
                        }

                        //Update editText_cost
                        UpdateCost();

                        //Copy who paid switch
                        switch_paidBy.setChecked(tr.GetIPaid());
                        switch_paidBy.setText((tr.GetIPaid() ? switch_paidBy.getTextOn() : switch_paidBy.getTextOff()));

                        //textView_percentageSplit checkbox
                        checkBox_split.setChecked(true);
                        checkBox_split.setVisibility((spinner_otherPeople.getCount() > 0 ? View.VISIBLE : View.GONE));

                        //textView_percentageSplit spinner
                        spinner_otherPeople.setVisibility(View.VISIBLE);
                        if (ProfileManager.HasOtherPerson(tr.GetSplitWith())) {
                            spinner_otherPeople.setSelection(otherPeopleAdapter.getPosition(tr.GetSplitWith()), true);
                        }
                        else{
                            spinner_otherPeople.setEnabled(false);
                            String[] arr = { tr.GetSplitWith() };
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

    //EXPENSES ONLY
    public void UpdateSplitClick()
    {
        //Change checkbox text
        checkBox_split.setText((checkBox_split.isChecked() ? "Split Cost with" : "Split Cost"));

        //Make Visible or Gone if split is checked or not
        spinner_otherPeople.setVisibility( (checkBox_split.isChecked() ? View.VISIBLE : View.GONE) );
        cardView_splitPercentage.setVisibility((checkBox_split.isChecked() ? View.VISIBLE : View.GONE));

        //Update costs
        UpdateCostBasedOnSeekBar();
    }
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
    public void UpdatePaidBack(){
        //Update paidback checkbox
        if (_paidBack!=null){
            checkBox_paidBack.setText(String.format(getString(R.string.info_paidback_format),_paidBack.toString(ProfileManager.simpleDateFormat)));

            SetChildrenEnabled(cardView_splitPercentage, false);
            SetChildrenEnabled(cardView_cost, false);
            SetChildrenEnabled(cardView_category, false);
            SetChildrenEnabled(cardView_description, false);
            //SetChildrenEnabled(fragment_timePeriod.linearLayout_parent, false);
            fragment_timePeriod.SetChildrenEnabled(false);
        } else {
            checkBox_paidBack.setText(getString(R.string.confirm_paidback));

            SetChildrenEnabled(cardView_splitPercentage, true);
            SetChildrenEnabled(cardView_cost, true);
            SetChildrenEnabled(cardView_category, true);
            SetChildrenEnabled(cardView_description, true);
            //SetChildrenEnabled(fragment_timePeriod.linearLayout_parent, true);
            fragment_timePeriod.SetChildrenEnabled(true);
        }
    }


    //Helpers
    public void SetChildrenEnabled(View v, boolean enabled){
        try {
            for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
                View child = ((ViewGroup) v).getChildAt(i);
                child.setEnabled(enabled);
                SetChildrenEnabled(child, enabled);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public Double getDouble(String str)
    {
        try {
            return Double.parseDouble(str);
        }
        catch (NumberFormatException ex) {
            return 0.0d;
        }
    }


    public void FinishTransaction()
    {
        //If the user selected a category
        if (spinner_categories.getSelectedItemPosition() != 0 || CategoryDoesNotExistBypass || activityType == 1) {
            //Update editText_cost
            UpdateCost(); //TODO Necessary?

            //Find profile
            Profile pr = (_profileID != 0 ? ProfileManager.GetProfileByID(_profileID) : null);


            //If profile exists
            if (pr != null) {
                //Find originally passed transaction
                Transaction originalTr = pr.GetTransaction(_transactionID);
                Transaction newTr = null;


                //Determine if we are editing an existing transaction (EditUpdate), or creating a new one (EditGhost, Duplicate, _transactionID == 0)
                if (_editState == EDIT_STATE.EditUpdate){
                    newTr = originalTr;
                }
                else {//if (_editState == EDIT_STATE.EditGhost || _editState == EDIT_STATE.Duplicate || _transactionID == 0) {
                    newTr = new Transaction();
                }


                //Set transaction fields based on this form to the new transaction or the editing one
                if (newTr != null) {
                    //Set Type
                    newTr.SetType(Transaction.TRANSACTION_TYPE.values()[activityType]);

                    //Set Cost
                    newTr.SetValue(tCost);

                    //Set Company
                    newTr.SetSourceName(editText_source.getText().toString());

                    //Set Category
                    if (ProfileManager.HasCategory((String) spinner_categories.getSelectedItem()) || CategoryDoesNotExistBypass && _transactionID != 0 || _transactionID == 0) {
                        if ( spinner_categories.getSelectedItemPosition() != 0) {
                            newTr.SetCategory((String) spinner_categories.getSelectedItem());
                        }
                    }

                    //Set Description
                    newTr.SetDescription(editText_description.getText().toString());

                    //Set Time Period
                    newTr.SetTimePeriod(fragment_timePeriod.GetTimePeriod());

                    //Set Split value
                    if (checkBox_split.isChecked() && spinner_otherPeople.getSelectedItem() != null) {
                        newTr.SetIPaid(switch_paidBy.isChecked());
                        newTr.SetSplitValue(spinner_otherPeople.getSelectedItem().toString(), sCost);
                    }

                    //Set paidback
                    if (checkBox_paidBack.isChecked()) {
                        newTr.SetPaidBack(_paidBack);
                    }
                    else {
                        newTr.SetPaidBack(null);
                    }


                    //Remove blacklist dates that are in queue for removal
                    if (originalTr != null) {
                        if (originalTr.GetTimePeriod() != null) {
                            originalTr.GetTimePeriod().FlushBlacklistDateQueue();
                        }
                    }


                    //If EditUpdate, call pr.UpdateTransaction()
                    if (_editState == EDIT_STATE.EditUpdate) {
                        pr.UpdateTransaction(newTr);
                    }
                    else if (_editState == EDIT_STATE.EditGhost && originalTr != null) { //If EditGhost, originalTr.AddChild(), newTr.SetParent() (Blacklist taken care of by AddChild()
                        pr.CloneTransaction(originalTr, newTr);
                    }
                    else if (_editState == EDIT_STATE.Duplicate || _editState == EDIT_STATE.NewTransaction) { //Else, call pr.AddTransaction()
                        pr.AddTransaction(newTr);
                    }

                    //Finish()
                    finish();
                }

            }
        } else {
            ProfileManager.Print("Missing Category");
        }

    }


}
