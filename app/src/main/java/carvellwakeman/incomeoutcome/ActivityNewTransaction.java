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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.*;
import android.widget.*;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.joda.time.LocalDate;
import org.joda.time.Period;

import java.util.ArrayList;


public class ActivityNewTransaction extends AppCompatActivity
{
    //Activity state
    int _activitytype;
    EDIT_STATE _editState;
    public enum EDIT_STATE
    {
        NewTransaction,
        Edit,
        Duplicate
    }

    //Object data
    Budget _budget;
    new_Transaction _transaction;


    //Configuration data
    LocalDate _start_date;
    LocalDate _paidBack;
    TimePeriod _timePeriod;

    //Adapters
    ArrayAdapter<String> categoryAdapter;
    AdapterSplitCost splitAdapter;

    //Views
    Toolbar toolbar;

    ScrollView scrollView;

    Spinner spinner_categories;

    CheckBox checkBox_split;

    TextInputLayout TIL_cost;

    EditText editText_source;
    EditText editText_description;
    EditText editText_cost;

    TextView textView_source;
    TextView textView_date;
    TextView textView_repeat;

    Button button_categoryNotice;
    Button button_addSplit;
    Button button_removeSplit;

    CardView cardView_paidBack;
    CardView cardView_cost;
    CardView cardView_category;
    CardView cardView_description;
    CardView cardView_timeperiod;
    CardView cardView_blacklist;

    CheckBox checkBox_paidBack;

    LinearLayout linearLayout_split;
    LinearLayout linearLayout_timeperiod_date;
    LinearLayout linearLayout_timeperiod_repeat;


    RecyclerView recyclerView_split;
    RecyclerView recyclerView_blacklistDates;

    NpaLinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);


        //Get the intent that opened this activity
        Intent intent = getIntent();
        _activitytype = intent.getIntExtra("activitytype", -1);
        _editState = EDIT_STATE.values()[intent.getIntExtra("editstate",0)];
        _budget = BudgetManager.getInstance().GetBudget(intent.getIntExtra("budget",-1));


        //Check that intent data is good
        if (_budget != null) {
            _transaction = _budget.GetTransaction(intent.getIntExtra("transaction", -1));

            if (_activitytype >= 0) {

                //Initial data
                _start_date = LocalDate.now();
                _timePeriod = new TimePeriod(_start_date);


                //Find Views
                toolbar = (Toolbar) findViewById(R.id.toolbar);

                scrollView = (ScrollView) findViewById(R.id.scrollView_newTransaction);

                cardView_paidBack = (CardView) findViewById(R.id.card_newTransaction_paidBack);
                cardView_cost = (CardView) findViewById(R.id.card_newTransaction_cost);
                cardView_category = (CardView) findViewById(R.id.card_newTransaction_category);
                cardView_description = (CardView) findViewById(R.id.card_newTransaction_description);
                cardView_timeperiod = (CardView) findViewById(R.id.card_newTransaction_timeperiod);
                cardView_blacklist = (CardView) findViewById(R.id.card_timeperiod_blacklist);

                linearLayout_split = (LinearLayout) findViewById(R.id.linearLayout_newTransaction_split);
                linearLayout_timeperiod_date = (LinearLayout) findViewById(R.id.linearLayout_newTransaction_date) ;
                linearLayout_timeperiod_repeat = (LinearLayout) findViewById(R.id.linearLayout_newTransaction_repeat) ;

                spinner_categories = (Spinner) findViewById(R.id.spinner_newTransaction_categories);

                checkBox_paidBack = (CheckBox) findViewById(R.id.checkBox_newTransaction_paidback);
                checkBox_split = (CheckBox) findViewById(R.id.checkBox_newTransaction_splitEnabled);

                editText_source = ((TextInputLayout) findViewById(R.id.TIL_newTransaction_source)).getEditText();
                editText_description = ((TextInputLayout) findViewById(R.id.TIL_newTransaction_description)).getEditText();
                TIL_cost = (TextInputLayout) findViewById(R.id.TIL_newTransaction_cost);
                editText_cost = TIL_cost.getEditText();

                textView_source = (TextView) findViewById(R.id.textView_newTransaction_source);
                textView_date = (TextView) findViewById(R.id.textView_newTransaction_date);
                textView_repeat = (TextView) findViewById(R.id.textView_newTransaction_repeat);

                button_addSplit = (Button) findViewById(R.id.button_newTransaction_addsplit);
                button_removeSplit = (Button) findViewById(R.id.button_newTransaction_removesplit);
                button_categoryNotice = (Button) findViewById(R.id.button_newTransaction_categorynotice);

                recyclerView_split = (RecyclerView) findViewById(R.id.recyclerView_newTransaction_split);
                recyclerView_blacklistDates = (RecyclerView) findViewById(R.id.recyclerView_blacklistDates);


                //Add view actions

                //Configure toolbar
                toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
                toolbar.inflateMenu(R.menu.toolbar_menu_save);
                setSupportActionBar(toolbar);

                //Paid back
                checkBox_paidBack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                         if (b) {
                            //Disable the rest of the layout

                            //Open paidback date picker
                             _paidBack = LocalDate.now();
                            new DatePickerDialog(ActivityNewTransaction.this, datePickerDate2, _paidBack.getYear(), _paidBack.getMonthOfYear() - 1, _paidBack.getDayOfMonth()).show();
                        } else {
                            checkBox_paidBack.setText( Helper.getString(R.string.confirm_paidback) );
                        }
                    }
                });

                //Configure date
                linearLayout_timeperiod_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Hide soft keyboard
                        Helper.hideSoftKeyboard(ActivityNewTransaction.this, view);

                        //Open date picker dialog
                        new DatePickerDialog(ActivityNewTransaction.this, datePickerDate, _start_date.getYear(), _start_date.getMonthOfYear()-1, _start_date.getDayOfMonth()).show();
                    }
                });

                //Configure repeat
                linearLayout_timeperiod_repeat.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        //Hide soft keyboard
                        Helper.hideSoftKeyboard(ActivityNewTransaction.this, view);

                        //Open repeat dialog fragment
                        Helper.OpenDialogFragment(ActivityNewTransaction.this, DialogFragmentRepeat.newInstance(ActivityNewTransaction.this, _timePeriod), true);
                    }
                });

                //Format date text
                textView_date.setText( _start_date.toString(Helper.getString(R.string.date_format)) );

                //Format repeat text
                textView_repeat.setText( _timePeriod.GetRepeatStringShort() );

                //Cost formatting
                editText_cost.setKeyListener(DigitsKeyListener.getInstance(false, true));
                editText_cost.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }//UpdateCostBasedOnSeekBar(); }
                    @Override public void afterTextChanged(Editable s) {}
                });


                //Scrollview
                scrollView.setOnTouchListener(new View.OnTouchListener() {
                    @Override public boolean onTouch(View view, MotionEvent motionEvent) {
                        //Hide soft keyboard
                        Helper.hideSoftKeyboard(ActivityNewTransaction.this, view);
                        return false;
                    }
                });


                //Conditional view actions based on editstate and activitytype
                if (_activitytype == 0) { //Expense
                    //Expense related views
                    textView_source.setVisibility(View.VISIBLE);
                    spinner_categories.setVisibility(View.VISIBLE);

                    //Split Cost checkbox
                    checkBox_split.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            //Hide soft keyboard
                            Helper.hideSoftKeyboard(ActivityNewTransaction.this, checkBox_split);

                            linearLayout_split.setVisibility((b ? View.VISIBLE : View.GONE));
                        }
                    });

                    //Add Split button
                    button_addSplit.setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            //Check if there are any people to split with
                            if (PersonManager.getInstance().GetPeopleCount() > 0) {
                                Intent intent = new Intent(ActivityNewTransaction.this, ActivityManagePeople.class);
                                intent.putExtra("select", true);
                                startActivityForResult(intent, 1);
                            } else { //Open people manager to add people
                                Intent intent = new Intent(ActivityNewTransaction.this, ActivityManagePeople.class);
                                intent.putExtra("addnew", true);
                                startActivity(intent);
                            }
                        }
                    });

                    //Remove split button
                    button_removeSplit.setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            //Open people manager to remove people
                            Intent intent = new Intent(ActivityNewTransaction.this, ActivityManagePeople.class);
                            intent.putExtra("select", true);
                            startActivityForResult(intent, 2);
                        }
                    });

                    //Split recyclerview
                    splitAdapter = new AdapterSplitCost(this);
                    recyclerView_split.setAdapter(splitAdapter);

                    linearLayoutManager = new NpaLinearLayoutManager(this);
                    recyclerView_split.setLayoutManager(linearLayoutManager);




                    //Category spinner
                    categoryAdapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_title){
                        @Override public boolean isEnabled(int position){ return position != 0; } //Can't select the first or last item
                    };
                    categoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_list_primary);
                    //Add new category if "add new" is selected
                    spinner_categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if (i == spinner_categories.getCount()-1) {
                                spinner_categories.setSelection(0);
                                Intent intent = new Intent(ActivityNewTransaction.this, ActivityManageCategories.class);
                                intent.putExtra("addnew", true);
                                startActivity(intent);
                            }
                        }
                        @Override public void onNothingSelected(AdapterView<?> adapterView) {}
                    });
                    //Close keyboard when spinner is touched
                    spinner_categories.setOnTouchListener(new View.OnTouchListener() {
                        @Override public boolean onTouch(View view, MotionEvent motionEvent) {
                            Helper.hideSoftKeyboard(ActivityNewTransaction.this, view);
                            return false;
                        }
                    });
                    spinner_categories.setAdapter(categoryAdapter);

                    SetAdapterData(); //Adds "select a category" and "add new"

                }
                else if (_activitytype == 1) { //Income
                }

                if (_editState == EDIT_STATE.NewTransaction) {
                    //Toolbar title
                    if (_activitytype == 0) { //Expense
                        toolbar.setTitle(R.string.title_newexpense);
                    }
                    else if (_activitytype == 1) { //Income
                        toolbar.setTitle(R.string.title_newincome);
                    }
                }
                else if (_editState == EDIT_STATE.Edit) {
                    //Toolbar title
                    toolbar.setTitle(R.string.title_edittransaction);

                }
                else if (_editState == EDIT_STATE.Duplicate) {
                    //Toolbar title
                    toolbar.setTitle(R.string.title_copytransaction);

                }


            } else {
                Helper.Print(this, "Bad activitytype specified"); //TODO: add to strings
            }
        }
    }


    //Date picker dialogs
    DatePickerDialog.OnDateSetListener datePickerDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            //Set date
            _start_date = new LocalDate(year, monthOfYear+1, dayOfMonth);

            //Time Period
            _timePeriod.SetDate(_start_date);

            _timePeriod.SetRepeatDayOfMonth(1);
            _timePeriod.SetDateOfYear(null);

            switch(_timePeriod.GetRepeatFrequency()){
                case MONTHLY:
                    _timePeriod.SetRepeatDayOfMonth(_start_date.getDayOfMonth());
                    break;
                case YEARLY:
                    _timePeriod.SetDateOfYear(_start_date);
                    break;
            }

            //Format date text
            textView_date.setText( _start_date.toString(Helper.getString(R.string.date_format)) );

            //Format repeat text
            textView_repeat.setText( _timePeriod.GetRepeatStringShort() );
        }
    };
    DatePickerDialog.OnDateSetListener datePickerDate2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            //Set date
            _paidBack = new LocalDate(year, monthOfYear+1, dayOfMonth);

            //Format checkbox text
            checkBox_paidBack.setText( String.format(Helper.getString(R.string.info_paidback_format), _paidBack.toString(Helper.getString(R.string.date_format))) );
        }
    };

    //Repeat dialog result
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            int id = data.getIntExtra("person", -1);
            Person person = PersonManager.getInstance().GetPerson(id);

            switch (requestCode){
                case 1: //Add person
                    if (person != null) {
                        splitAdapter.SetActive(person, true);
                    }
                    break;
                case 2: //Remove person
                    if (person != null) {
                        splitAdapter.SetActive(person, false);
                    }
                    break;
                default: //Repeat dialog fragment
                    _timePeriod = (TimePeriod) data.getSerializableExtra("timeperiod");

                    //Format repeat text
                    textView_repeat.setText(_timePeriod.GetRepeatStringShort());
                    break;
            }
        }
    }


    //Inflate toolbar with save options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_save, menu);
        return true;
    }

    //Toolbar button handling
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //X close button
                BackAction();
                return true;
            case R.id.toolbar_save: //SAVE button
                FinishTransaction();
                finish();
                return true;
        }
        return false;
    }

    //Return to activity
    @Override
    public void onResume(){
        //Refresh adapters
        SetAdapterData();
        categoryAdapter.notifyDataSetChanged();

        super.onResume();
    }

    @Override public void onBackPressed() { BackAction(); }

    public void BackAction(){
        //Send back a RESULT_CANCELED to MainActivity
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }


    //Set adapter data
    public void SetAdapterData(){
        //Categories
        ArrayList<String> categories = CategoryManager.getInstance().GetCategoriesTitles();
        categories.add(0, Helper.getString(R.string.select_category));
        categories.add(Helper.getString(R.string.add_new));

        categoryAdapter.clear();
        categoryAdapter.addAll(categories);

    }



    //Gather data from views, build a transaction object, and save it to the database
    public void FinishTransaction()
    {
        //If the user selected a category
        if (spinner_categories.getSelectedItemPosition() != 0 || _activitytype == 1) {

            //If budget exists
            if (_budget != null) { //Not really necessary

                //Determine if we are editing an existing transaction (EditUpdate), or creating a new one (EditGhost, Duplicate, _transactionID == 0)
                if (_editState == EDIT_STATE.NewTransaction){
                    _transaction = new new_Transaction(_activitytype);
                }
                else if (_editState == EDIT_STATE.Edit){

                }
                else if (_editState == EDIT_STATE.Duplicate) {

                }

                //Get cost
                double cost = Double.valueOf(editText_cost.getText().toString());

                //Set Type
                _transaction.SetType(_activitytype);

                //Set Cost
                _transaction.SetValue( cost );

                //Set source
                _transaction.SetSource( editText_source.getText().toString() );

                //Set Description
                _transaction.SetDescription( editText_description.getText().toString() );

                //Set Time Period
                _transaction.SetTimePeriod( _timePeriod );

                //Expense only
                if (_activitytype == 0){
                    //Set Category
                    Category category = CategoryManager.getInstance().GetCategory(spinner_categories.getSelectedItem().toString());
                    if (category != null){ _transaction.SetCategory(category.GetID()); }

                    //Set Split value
                    if (checkBox_split.isChecked()) {
                        for (Person p : splitAdapter.GetPeople()){
                            _transaction.SetSplit(p.GetID(), splitAdapter.GetSplit(p));
                            if (splitAdapter.Getpaid(p)) { _transaction.SetPaidBy(p.GetID()); }
                        }
                    } else {
                        _transaction.SetPaidBy( 0 );
                        _transaction.SetSplit( 0,cost );
                    }

                    //Set paidback
                    if (checkBox_paidBack.isChecked()) {
                        _transaction.SetPaidBack( _paidBack );
                    } else {
                        _transaction.SetPaidBack(null);
                    }

                }

                //Add to/update database
                DatabaseManager dm = DatabaseManager.getInstance();

                dm.insert(_transaction, true);
                dm.insert(_transaction.GetID(), _timePeriod, true);
                dm.insertSetting(_budget, true);

                finish();
            }
        } else {
            Helper.PrintUser(this, Helper.getString(R.string.info_select_category));
        }

    }


}
