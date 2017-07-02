package carvellwakeman.incomeoutcome;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.*;
import android.widget.*;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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
    LocalDate _paidBack;
    TimePeriod _timePeriod;

    //Adapters
    ArrayAdapter<String> categoryAdapter;

    //Split dynamic views
    HashMap<Person, ViewHolderSplit> active_people;
    ViewHolderSplit modifyingSplitHolder;


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
    LinearLayout linearLayout_splitContainer;
    LinearLayout linearLayout_timeperiod_date;
    LinearLayout linearLayout_timeperiod_repeat;


    RecyclerView recyclerView_blacklistDates;



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
            Helper.Log(this, "ActNewTran", "ActNewTran passed " + String.valueOf(intent.getIntExtra("transaction", -1)));
            if (_transaction != null){ Helper.Log(this, "ActNewTran", "ActNewTran passed " + _transaction.GetSource()); }

            if (_activitytype >= 0) {

                //Initial data
                LocalDate _start_date = LocalDate.now();
                _timePeriod = new TimePeriod(_start_date);
                Helper.Log(this, "ActNewTran", "Init TimePeriod:" + (_timePeriod==null ? "null" : String.valueOf(_timePeriod.GetID())));

                active_people = new HashMap<>();
                modifyingSplitHolder = null;


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
                linearLayout_splitContainer = (LinearLayout) findViewById(R.id.linearLayout_newTransaction_splitContainer);
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
                        LocalDate _start_date = _timePeriod.GetDate();
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

                    //Cost formatting
                    editText_cost.setKeyListener(DigitsKeyListener.getInstance(false, true));
                    editText_cost.addTextChangedListener(new TextWatcher() {
                        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                            UpdateSplitViewHolderCosts();
                        }
                        @Override public void afterTextChanged(Editable s) {}
                    });

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
                                intent.putExtra("select",true);
                                startActivityForResult(intent, 1);
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
                                startActivityForResult(intent, 3);
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
                else if (_activitytype == 1) { // TODO Income
                }

                //You
                Person you = new Person(Helper.getString(R.string.misc_your));
                you.SetID(-1);

                //Person visibility
                AddSplitPerson(you);

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

                    LoadTransaction(_transaction);
                }
                else if (_editState == EDIT_STATE.Duplicate) {
                    //Toolbar title
                    toolbar.setTitle(R.string.title_copytransaction);

                    LoadTransaction(_transaction);
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
            LocalDate _start_date = new LocalDate(year, monthOfYear+1, dayOfMonth);

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

            UpdateDateFormat();
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
            int id = data.getIntExtra("entity", -1);

            if (requestCode == 1){ // Add split person
                Person person = PersonManager.getInstance().GetPerson(id);
                if (person != null) {
                    AddSplitPerson(person);
                }
            } else if (requestCode == 2){ // Remove split person
                Person person = PersonManager.getInstance().GetPerson(id);
                if (person != null) {
                    RemoveSplitPerson(person);
                }
            } else if (requestCode == 3){ // Add category (and select it)
                Category category = CategoryManager.getInstance().GetCategory(id);

                if (category != null) {
                    spinner_categories.setSelection(CategoryManager.getInstance().GetCategories().indexOf(category)+1); //+1 for 'Add New...'
                }
            } else if (requestCode == 4) { // Repeating (time period)
                // Format repeat text
                _timePeriod = (TimePeriod) data.getSerializableExtra("timeperiod");
                Helper.Log(this, "ActNewTran", "OnActRes TimePeriod:" + (_timePeriod==null ? "null" : String.valueOf(_timePeriod.GetID())));

                if (_timePeriod != null) {
                    textView_repeat.setText(_timePeriod.GetRepeatStringShort());
                }
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
                // Close keyboard
                Helper.hideSoftKeyboard(ActivityNewTransaction.this, null);

                // Try to finish up transaction
                int result = FinishTransaction();

                if (result == 1){
                    // Send back in intent
                    Intent intent = new Intent();
                    intent.putExtra("transaction", _transaction);
                    intent.putExtra("timeperiod", _timePeriod);
                    setResult(1, intent);
                    finish();
                }
                else if (result == 0){
                    Helper.Log(this, "ActNewTran", "1Error: Pick a category");
                    Helper.PrintUser(this, Helper.getString(R.string.info_select_category));
                }
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

    // Updaters
    public void UpdateDateFormat(){
        //Format date text
        textView_date.setText( _timePeriod.GetDate().toString(Helper.getString(R.string.date_format)) );

        //Format repeat text
        textView_repeat.setText( _timePeriod.GetRepeatStringShort() );
    }

    //Split person dynamic views
    public void AddSplitPerson(Person person){
        if (!active_people.containsKey(person)) {
            //Create new split person view group
            ViewHolderSplit svh = new ViewHolderSplit(ActivityNewTransaction.this, person, getLayoutInflater(), linearLayout_splitContainer);

            //Special case for you
            if (person.GetID() == 0) {
                modifyingSplitHolder = svh;
                svh.paid.setChecked(true);
                svh.percentage.setProgress(100);
                modifyingSplitHolder = null;
            }

            active_people.put(person, svh);

            UpdateSplitViewHolderCosts();

            //Disable viewholder if only one split person
            if (active_people.size() == 1){
                svh.SetEnabled(false);
            } else {
                for (Map.Entry<Person, ViewHolderSplit> entry : active_people.entrySet()){
                    entry.getValue().SetEnabled(true);
                    entry.getValue().cost.setText( String.valueOf( GetCost() / active_people.size() ) ); //Set split to be even
                }
            }
        }
    }
    public void RemoveSplitPerson(Person person){
        if (active_people.containsKey(person)) {
            linearLayout_splitContainer.removeView(active_people.get(person).base);

            active_people.remove(person);
            UpdateSplitViewHolderCosts();

            //Disable viewholder if only one split person
            for (Map.Entry<Person, ViewHolderSplit> entry : active_people.entrySet()){
                entry.getValue().SetEnabled(active_people.size() != 1);
                entry.getValue().cost.setText( String.valueOf( GetCost() / active_people.size() ) ); //Set split to be even
            }
        }
    }

    public void UpdateSplitViewHolderCosts(){
        for (Map.Entry<Person, ViewHolderSplit> entry : active_people.entrySet()) {
            if (entry != null) {
                ViewHolderSplit svh = entry.getValue();

                modifyingSplitHolder = svh;
                svh.cost.setText( String.valueOf( (svh.percentage.getProgress()/100.0) * GetCost() )  );
                modifyingSplitHolder = null;
            }
        }
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

    public double GetCost(){
        if (!editText_cost.getText().toString().equals("")) {
            try {
                return Double.valueOf(editText_cost.getText().toString());
            } catch (Exception ex){ return 0.0d; }
        }
        return 0.0d;
    }

    // Load transaction into fields
    public void LoadTransaction(new_Transaction _transaction){
        Helper.Log(this, "ActNewTran", "LoadTransaction");
        Helper.Log(this, "ActNewTran", "LoadTransaction TimePeriod:" + (_timePeriod==null ? "null" : String.valueOf(_timePeriod.GetID())));

        // Cost
        editText_cost.setText(String.valueOf(_transaction.GetValue()));

        // Split
        if (_transaction.IsSplit()) {
            checkBox_split.setChecked(true);
        }
        for (Map.Entry<Integer, Double> entry : _transaction.GetSplitArray().entrySet()){
            Person p = PersonManager.getInstance().GetPerson(entry.getKey());
            Double v = entry.getValue();

            // Not me
            if (entry.getKey()!=0 && p!=null) {
                AddSplitPerson(p);
            }

            ViewHolderSplit svh = active_people.get(p);
            if (svh != null) {
                svh.cost.setText(String.valueOf(v));
            }

        }

        // Category
        Category cat = CategoryManager.getInstance().GetCategory(_transaction.GetCategory());
        if (cat != null){ spinner_categories.setSelection(categoryAdapter.getPosition(cat.GetTitle())); }

        // Source
        editText_source.setText(_transaction.GetSource());

        // Descsription
        editText_description.setText(_transaction.GetDescription());

        // Date
        _timePeriod = _transaction.GetTimePeriod();

        UpdateDateFormat();
    }

    //Gather data from views, build a transaction object, and save it to the database
    public int FinishTransaction()
    {
        Helper.Log(this, "ActNewTran", "Initial");
        Helper.Log(this, "ActNewTran", "FinishTran TimePeriod:" + (_timePeriod==null ? "null" : String.valueOf(_timePeriod.GetID())));

        //If the user selected a category
        if (spinner_categories.getSelectedItemPosition() != 0 || _activitytype == 1) {
            Helper.Log(this, "ActNewTran", "Category was selected");

            //Determine if we are editing an existing transaction (EditUpdate), or creating a new one (EditGhost, Duplicate, _transactionID == 0)
            if (_editState == EDIT_STATE.NewTransaction){
                Helper.Log(this, "ActNewTran", "EditState: New transaction");
                _transaction = new new_Transaction(_activitytype);
            }
            else if (_editState == EDIT_STATE.Edit){
                Helper.Log(this, "ActNewTran", "EditState: Edit Transaction");
            }
            else if (_editState == EDIT_STATE.Duplicate) {
                Helper.Log(this, "ActNewTran", "EditState: Duplicate Transaction");
            }

            //Get cost
            double cost = GetCost();

            //Set Type
            _transaction.SetType(_activitytype);

            //Set Cost
            _transaction.SetValue( cost );

            //Set source
            _transaction.SetSource( editText_source.getText().toString() );

            //Set Description
            _transaction.SetDescription( editText_description.getText().toString() );

            //Set Time Period
            Helper.Log(this, "ActNewTran", "TimePeriod:" + (_timePeriod==null ? "null" : String.valueOf(_timePeriod.GetID())));
            _transaction.SetTimePeriod( _timePeriod );
            Helper.Log(this, "ActNewTran", "Transaction TimePeriod:" + (_transaction.GetTimePeriod()==null ? "null" : String.valueOf(_transaction.GetTimePeriod().GetID())));

            //Expense only
            if (_activitytype == 0){
                Helper.Log(this, "ActNewTran", "Expense transaction");
                //Set Category
                Category category = CategoryManager.getInstance().GetCategory(spinner_categories.getSelectedItem().toString());
                if (category != null){ _transaction.SetCategory(category.GetID()); }

                //Set Split value
                if (checkBox_split.isChecked()) {
                    for (Map.Entry<Person, ViewHolderSplit> entry : active_people.entrySet()) {
                        Helper.Log(this, "ActNewTran", "SplitWith:" + entry.getKey().GetName() + " for " + String.valueOf(entry.getValue().GetCost()));
                        _transaction.SetSplit(entry.getKey().GetID(), entry.getValue().GetCost());
                        if (entry.getValue().GetPaid()) { _transaction.SetPaidBy(entry.getKey().GetID()); }
                    }
                }

                //Set paidback
                if (checkBox_paidBack.isChecked()) {
                    _transaction.SetPaidBack( _paidBack );
                } else {
                    _transaction.SetPaidBack(null);
                }

            }

            //Set budget id
            _transaction.SetBudgetID(_budget.GetID());

            //Add to budget
            if (_editState == EDIT_STATE.NewTransaction || _editState == EDIT_STATE.Duplicate){
                _budget.AddTransaction(_transaction);
            }

            return 1; //Success
        } else {
            return 0; //Failure (no category)
        }
    }


}
