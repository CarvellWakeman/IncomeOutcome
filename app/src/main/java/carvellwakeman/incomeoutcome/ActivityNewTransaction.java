package carvellwakeman.incomeoutcome;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.*;
import android.widget.*;
import org.joda.time.Days;
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
        EditInstance,
    }

    //Object data
    Budget _budget;
    Transaction _transaction;
    Category _category;

    ArrayList<LocalDate> _blacklistDatesRemovalQueue;

    //Configuration data
    LocalDate _paidBack;
    TimePeriod _timePeriod;

    //Split dynamic views
    HashMap<Person, ViewHolderSplit> active_people;
    ViewHolderSplit modifyingSplitHolder;


    //Views
    Toolbar toolbar;

    ScrollView scrollView;

    CheckBox checkBox_split;
    
    Switch switch_override_series;

    TextInputLayout TIL_cost;

    EditText editText_source;
    EditText editText_description;
    EditText editText_cost;

    TextView textView_source;
    TextView textView_date;
    TextView textView_repeat;

    ImageView imageView_repeatIcon;
    ImageView imageView_dateIcon;

    Button button_addSplit;
    Button button_removeSplit;
    Button button_selectCategory;

    CardView cardView_paidBack;
    CardView cardView_cost;
    CardView cardView_category;
    CardView cardView_description;
    CardView cardView_timeperiod;

    CheckBox checkBox_paidBack;

    LinearLayout linearLayout_split;
    LinearLayout linearLayout_splitContainer;
    LinearLayout linearLayout_newTransaction_series_override;
    LinearLayout linearLayout_newTransaction_date;
    LinearLayout linearLayout_newTransaction_repeat;

    FrameLayout frameLayout_blacklistDates;
    Card blacklistDates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);

        _blacklistDatesRemovalQueue = new ArrayList<>();

        //Get the intent that opened this activity
        Intent intent = getIntent();
        _activitytype = intent.getIntExtra("activitytype", -1);
        _editState = EDIT_STATE.values()[intent.getIntExtra("editstate",0)];
        _budget = BudgetManager.getInstance().GetBudget(intent.getIntExtra("budget",-1));


        //Check that intent data is good
        if (_budget != null) {

            if (_activitytype >= 0) {

                //Initial data
                LocalDate _start_date = LocalDate.now();

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

                linearLayout_split = (LinearLayout) findViewById(R.id.linearLayout_newTransaction_split);
                linearLayout_splitContainer = (LinearLayout) findViewById(R.id.linearLayout_newTransaction_splitContainer);
                linearLayout_newTransaction_date = (LinearLayout) findViewById(R.id.linearLayout_newTransaction_date);
                linearLayout_newTransaction_repeat = (LinearLayout) findViewById(R.id.linearLayout_newTransaction_repeat);
                linearLayout_newTransaction_series_override = (LinearLayout) findViewById(R.id.linearLayout_newTransaction_series_override);
                
                checkBox_paidBack = (CheckBox) findViewById(R.id.checkBox_newTransaction_paidback);
                checkBox_split = (CheckBox) findViewById(R.id.checkBox_newTransaction_splitEnabled);
                
                switch_override_series = (Switch) findViewById(R.id.switch_override_series);

                editText_source = ((TextInputLayout) findViewById(R.id.TIL_newTransaction_source)).getEditText();
                editText_description = ((TextInputLayout) findViewById(R.id.TIL_newTransaction_description)).getEditText();
                TIL_cost = (TextInputLayout) findViewById(R.id.TIL_newTransaction_cost);
                editText_cost = TIL_cost.getEditText();

                textView_source = (TextView) findViewById(R.id.textView_newTransaction_source);
                textView_date = (TextView) findViewById(R.id.textView_newTransaction_date);
                textView_repeat = (TextView) findViewById(R.id.textView_newTransaction_repeat);

                imageView_repeatIcon = (ImageView) findViewById(R.id.imageView_repeatIcon);
                imageView_dateIcon = (ImageView) findViewById(R.id.imageView_dateIcon);

                button_addSplit = (Button) findViewById(R.id.button_newTransaction_addsplit);
                button_removeSplit = (Button) findViewById(R.id.button_newTransaction_removesplit);
                button_selectCategory = (Button) findViewById(R.id.button_newTransaction_selectCategory);

                frameLayout_blacklistDates = (FrameLayout) findViewById(R.id.frameLayout_blacklistdates);


                //Add view actions

                //Configure toolbar
                toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
                toolbar.inflateMenu(R.menu.toolbar_menu_save);
                setSupportActionBar(toolbar);

                //Paid back
                cardView_paidBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!checkBox_paidBack.isChecked()) {
                            checkBox_paidBack.setChecked(true);
                            //Open paidback date picker
                            _paidBack = LocalDate.now();
                            new DatePickerDialog(ActivityNewTransaction.this, datePickerDate2, _paidBack.getYear(), _paidBack.getMonthOfYear() - 1, _paidBack.getDayOfMonth()).show();
                        } else {
                            checkBox_paidBack.setChecked(false);
                            checkBox_paidBack.setText( Helper.getString(R.string.confirm_paidback) );
                        }
                    }
                });

                //Configure date
                linearLayout_newTransaction_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Hide soft keyboard
                        Helper.hideSoftKeyboard(ActivityNewTransaction.this, view);

                        //Open date picker dialog
                        LocalDate _start_date = _timePeriod.GetDate();
                        new DatePickerDialog(ActivityNewTransaction.this, datePickerDate, _start_date.getYear(), _start_date.getMonthOfYear() - 1, _start_date.getDayOfMonth()).show();
                    }
                });

                //Configure repeat
                linearLayout_newTransaction_repeat.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        //Hide soft keyboard
                        Helper.hideSoftKeyboard(ActivityNewTransaction.this, view);

                        //Open repeat dialog fragment
                        Helper.OpenDialogFragment(ActivityNewTransaction.this, DialogFragmentRepeat.newInstance(ActivityNewTransaction.this, _timePeriod), true);
                    }
                });
                
                //Configure series override
                switch_override_series.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        SetSeriesOverride(b);
                        // Reset to original TP
                        if (!b){
                            _timePeriod = new TimePeriod();
                            _timePeriod.DeepCopy(_transaction.GetTimePeriod());
                            UpdateDateFormat();
                        }
                    }
                });
                linearLayout_newTransaction_series_override.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        switch_override_series.setChecked(!switch_override_series.isChecked());
                    }
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
                    //spinner_categories.setVisibility(View.VISIBLE);

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

                    // Select Category
                    button_selectCategory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Check if there are any categories
                            if (CategoryManager.getInstance().GetCategoriesCount() > 0) {
                                Intent intent = new Intent(ActivityNewTransaction.this, ActivityManageCategories.class);
                                intent.putExtra("select", true);
                                startActivityForResult(intent, 3);
                            } else { //Open category manager to add people
                                Intent intent = new Intent(ActivityNewTransaction.this, ActivityManageCategories.class);
                                intent.putExtra("addnew", true);
                                intent.putExtra("select",true);
                                startActivityForResult(intent, 3);
                            }
                        }
                    });

                } else if (_activitytype == 1) { // TODO Income

                }

                // Add user as a split
                AddSplitPerson(Person.Me);

                // Edit state options
                if (_editState == EDIT_STATE.NewTransaction) {
                    //Toolbar title
                    if (_activitytype == 0) { //Expense
                        toolbar.setTitle(R.string.title_newexpense);
                    }
                    else if (_activitytype == 1) { //Income
                        toolbar.setTitle(R.string.title_newincome);
                    }

                    _timePeriod = new TimePeriod(_start_date);
                }
                else if (_editState == EDIT_STATE.Edit) {
                    _transaction = _budget.GetTransaction(intent.getIntExtra("transaction", -1));
                    if (_transaction != null) {
                        LoadTransaction(_transaction);

                        //Toolbar title
                        if (_timePeriod.DoesRepeat()) {
                            toolbar.setTitle(R.string.title_edittransactionseries);
                        }
                        else {
                            toolbar.setTitle(R.string.title_edittransaction);

                            // Show paid back
                            cardView_paidBack.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Helper.Print(this, getString(R.string.error_transaction_not_found));
                        finish();
                    }
                }
                else if (_editState == EDIT_STATE.EditInstance){
                    _transaction = (Transaction) intent.getSerializableExtra("transaction");
                    if (_transaction != null) {
                        LoadTransaction(_transaction);

                        //Toolbar title
                        toolbar.setTitle(R.string.title_edittransactioninstance);

                        // Show paid back
                        cardView_paidBack.setVisibility(View.VISIBLE);
                    }
                    else {
                        Helper.Print(this, getString(R.string.error_transaction_not_found));
                        finish();
                    }
                }


                 // Block repeat button for child transaction
                if (_transaction != null && _transaction.GetParentID() > 0){
                    SetSeriesOverride(false);
                    linearLayout_newTransaction_series_override.setVisibility(View.VISIBLE);
                }

                UpdateDateFormat();
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
            //Original date
            LocalDate _original_date = new LocalDate(_timePeriod.GetFirstOccurrence());
            Helper.Log(ActivityNewTransaction.this, "ActNewTran", "DateChange Date:" + _timePeriod.GetDate().toString());

            //Set date
            LocalDate _start_date = new LocalDate(year, monthOfYear+1, dayOfMonth);
            Helper.Log(ActivityNewTransaction.this, "ActNewTran", "DateChange To:" + _start_date.toString());


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
            UpdateBlacklistDates(_original_date);
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
                    _category = category;
                    button_selectCategory.setText(category.GetTitle());
                }
            } else if (requestCode == 4) { // Repeating (time period)
                LocalDate _original_date = new LocalDate(_timePeriod.GetFirstOccurrence());
                Helper.Log(ActivityNewTransaction.this, "ActNewTran", "Before Receiving FO:" + _timePeriod.GetFirstOccurrence());
                Helper.Log(ActivityNewTransaction.this, "ActNewTran", "Before Receiving:" + _timePeriod.GetID());

                // Format repeat text
                _timePeriod = (TimePeriod) data.getSerializableExtra("timeperiod");
                Helper.Log(ActivityNewTransaction.this, "ActNewTran", "Receiving FO:" + _timePeriod.GetFirstOccurrence().toString());
                Helper.Log(ActivityNewTransaction.this, "ActNewTran", "Receiving:" + _timePeriod.GetID());

                if (_timePeriod != null) {
                    UpdateDateFormat();
                    UpdateBlacklistDates(_original_date);
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

                // Flush blacklist queue
                if (_transaction != null) {
                    for (LocalDate d : _blacklistDatesRemovalQueue) {
                        // Delete associated transaction if it exists
                        BlacklistDate bd = _timePeriod.GetBlacklistDate(d);
                        if (bd != null){
                            Transaction tran = _budget.GetTransaction(bd.transactionID);
                            if (tran != null) {
                                DatabaseManager.getInstance().remove(tran);
                                _budget.RemoveTransaction(bd.transactionID);
                            }
                        }
                        _timePeriod.RemoveBlacklistDate(d);
                    }
                }

                // Try to finish up transaction
                FinishTransaction();

                setResult(1);
                finish();

                return true;
        }
        return false;
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

    public void UpdateBlacklistDates(LocalDate originalDate){
        Helper.Log(ActivityNewTransaction.this, "ActNewTran", "OriginalFO: " + originalDate.toString());
        Helper.Log(ActivityNewTransaction.this, "ActNewTran", "CurrentFO: " + _timePeriod.GetFirstOccurrence().toString());

        // Update blacklist dates by difference
        for (BlacklistDate bd : _timePeriod.GetBlacklistDates()){
            int dbt = Days.daysBetween(originalDate, _timePeriod.GetFirstOccurrence()).getDays();
            Helper.Log(ActivityNewTransaction.this, "ActNewTran", "Days Between:" + dbt);
            LocalDate newDate = bd.date.plusDays( dbt );
            Helper.Log(ActivityNewTransaction.this, "ActNewTran", "Update bd " + bd.date.toString() + " to " + newDate.toString());

            _timePeriod.RemoveBlacklistDate(bd.date);
            _timePeriod.AddBlacklistDate(bd.transactionID, newDate, bd.edited);
        }
    }

    public void SetSeriesOverride(boolean override){
        // Block repeat input and date input
        linearLayout_newTransaction_repeat.setEnabled(override);
        linearLayout_newTransaction_date.setEnabled(override);

        if (override) {
            imageView_repeatIcon.clearColorFilter();
            textView_repeat.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, getTheme()));

            imageView_dateIcon.clearColorFilter();
            textView_date.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, getTheme()));

        } else {
            imageView_repeatIcon.setColorFilter(getResources().getColor(R.color.ltgray));
            textView_repeat.setTextColor(getResources().getColor(R.color.ltgray));

            imageView_dateIcon.setColorFilter(getResources().getColor(R.color.ltgray));
            textView_date.setTextColor(getResources().getColor(R.color.ltgray));
        }
    }


    // Split person dynamic views
    public void AddSplitPerson(Person person){
        if (!active_people.containsKey(person)) {
            //Create new split person view group
            ViewHolderSplit svh = new ViewHolderSplit(ActivityNewTransaction.this, person, getLayoutInflater(), linearLayout_splitContainer);

            //Special case for you
            if (person.GetID() == Person.Me.GetID()) {
                modifyingSplitHolder = svh;
                svh.percentage.setProgress(100);
                modifyingSplitHolder = null;
            }

            active_people.put(person, svh);

            UpdateSplitViewHolderCosts();

            //Disable viewholder if only one split person
            if (active_people.size() == 1){
                for (Map.Entry<Person, ViewHolderSplit> entry : active_people.entrySet()) {
                    entry.getValue().SetEnabled(false);
                    entry.getValue().paid.setChecked(true);
                }
            } else {
                for (Map.Entry<Person, ViewHolderSplit> entry : active_people.entrySet()) {
                    entry.getValue().SetEnabled(true);
                    entry.getValue().edited = false;
                }
            }
        }
    }
    public void RemoveSplitPerson(Person person){
        if (active_people.containsKey(person)) {
            ViewHolderSplit svh = active_people.get(person);
            linearLayout_splitContainer.removeView(svh.base);

            active_people.remove(person);

            UpdateSplitViewHolderCosts();


            //Disable viewholder if only one split person
            if (active_people.size() == 1){
                for (Map.Entry<Person, ViewHolderSplit> entry : active_people.entrySet()) {
                    entry.getValue().SetEnabled(false);
                    entry.getValue().paid.setChecked(true);
                }
            } else {
                for (Map.Entry<Person, ViewHolderSplit> entry : active_people.entrySet()) { entry.getValue().SetEnabled(true); }
            }
        }
    }

    public void UpdateSplitViewHolderCosts(){
        for (Map.Entry<Person, ViewHolderSplit> entry : active_people.entrySet()) {
            if (entry != null) {
                ViewHolderSplit svh = entry.getValue();

                modifyingSplitHolder = svh;
                svh.cost.setText(String.valueOf(GetCost() / active_people.size())); // Even split
                svh.percentage.setProgress(100 / active_people.size()); // Even percentage
                modifyingSplitHolder = null;
            }
        }
    }


    // Get cost in cost edittext
    public double GetCost(){
        if (!editText_cost.getText().toString().equals("")) {
            try {
                return Double.valueOf(editText_cost.getText().toString());
            } catch (Exception ex){ return 0.0d; }
        }
        return 0.0d;
    }


    // Setup blacklist dates
    public void SetupBlacklistDates(){
        if (_timePeriod != null && _timePeriod.GetBlacklistDates().size() > 0){
            LayoutInflater inflater = getLayoutInflater();//(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)

            blacklistDates = new Card(this, inflater, frameLayout_blacklistDates, 0);
            blacklistDates.setTitle(R.string.subtitle_blacklist);

            for (final BlacklistDate bd : _timePeriod.GetBlacklistDates()){
                Setting setting = new Setting(getLayoutInflater(), R.drawable.ic_delete_white_24dp, bd.date.toString(Helper.getString(R.string.date_format)), getString(bd.edited ? R.string.tt_edited : R.string.tt_deleted),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            if (_blacklistDatesRemovalQueue.contains(bd.date)){
                                _blacklistDatesRemovalQueue.remove(bd.date);
                                view.setAlpha(1.0f);
                            } else {
                                // Remove associated transaction if it exists
                                if (bd.transactionID > 0){
                                    // Confirm removal of blacklist date
                                    AlertDialog.Builder alert = new AlertDialog.Builder(ActivityNewTransaction.this);
                                    alert.setMessage(R.string.info_tran_instance_delete);
                                    alert.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                                        @Override public void onClick(DialogInterface dialog, int which) {
                                            _blacklistDatesRemovalQueue.add(bd.date);
                                            view.setAlpha(130.0f/255.0f);
                                        }
                                    });
                                    alert.setNegativeButton(R.string.action_cancel, null);
                                    alert.show();
                                } else {
                                    _blacklistDatesRemovalQueue.add(bd.date);
                                    view.setAlpha(130.0f/255.0f);
                                }
                            }

                            //if (blacklistDates.ChildCount() == 0){
                            //    blacklistDates.getBase().setVisibility(View.GONE);
                            //}
                        }
                    }
                );
                //setting.SetIconColor(R.color.red); // Not working for some reason
                blacklistDates.AddView(setting.getView());
            }
        }
    }


    // Load transaction into fields
    public void LoadTransaction(final Transaction _transaction){
        // Copy timeperiod
        _timePeriod = new TimePeriod();
        _timePeriod.DeepCopy(_transaction.GetTimePeriod());

        // Paid back
        if (_transaction.GetPaidBack() != null){
            _paidBack = _transaction.GetPaidBack();
            checkBox_paidBack.setChecked(true);
            checkBox_paidBack.setText( String.format(Helper.getString(R.string.info_paidback_format), _paidBack.toString(Helper.getString(R.string.date_format))) );
        }

        // Cost
        editText_cost.setText(String.valueOf(_transaction.GetValue()));

        // Split
        checkBox_split.setChecked(_transaction.IsSplit());

        // Add all split users
        for (Map.Entry<Integer, Double> entry : _transaction.GetSplitArray().entrySet()){
            Person p = PersonManager.getInstance().GetPerson(entry.getKey());

            if (p!=null) {
                // Ignore the user
                if (entry.getKey() != Person.Me.GetID()) {
                    AddSplitPerson(p);
                }
            }
        }

        // Set values for all split people (Done seperately because AddSplitPerson adjusts cost
        for (Map.Entry<Integer, Double> entry : _transaction.GetSplitArray().entrySet()){
            Person p = PersonManager.getInstance().GetPerson(entry.getKey());
            Double v = entry.getValue();

            if (p!=null) {
                ViewHolderSplit svh = active_people.get(p);
                if (svh != null) {
                    modifyingSplitHolder = svh;
                    svh.cost.setText(String.valueOf(v));
                    svh.paid.setChecked(p.GetID() == _transaction.GetPaidBy());
                    svh.percentage.setProgress( (int) ((v / _transaction.GetValue()) * 100) );
                    modifyingSplitHolder = null;
                }
            }
        }

        // Blacklist dates
        SetupBlacklistDates();

        // Category
        Category cat = CategoryManager.getInstance().GetCategory(_transaction.GetCategory());
        if (cat != null){
            _category = cat;
            button_selectCategory.setText(cat.GetTitle());
        }

        // Source
        editText_source.setText(_transaction.GetSource());

        // Descsription
        editText_description.setText(_transaction.GetDescription());

        UpdateDateFormat();
    }

    // Gather data from views, build a transaction object, and save it to the database
    public void FinishTransaction() {
        Helper.Log(this, "ActNewTran", "FinishTran TimePeriod:" + (_timePeriod==null ? "null" : String.valueOf(_timePeriod.GetID())));
        Helper.Log(this, "ActNewTran", "FinishTran TimePeriod Date:" + (_timePeriod==null ? "null" : _timePeriod.GetDate().toString()));

        // If the user selected a category
        if ( _category != null || _activitytype == 1) {

            // Determine if we are editing an existing transaction or creating a new one
            if (_editState == EDIT_STATE.NewTransaction){
                _transaction = new Transaction(_activitytype);
            }

            // Get cost
            double cost = GetCost();

            // Set Type
            _transaction.SetType(_activitytype);

            // Set Cost
            _transaction.SetValue( cost );

            // Set source
            _transaction.SetSource( editText_source.getText().toString() );

            // Set Description
            _transaction.SetDescription( editText_description.getText().toString() );

            // Expense only
            if (_activitytype == 0){
                // Set Category
                if (_category != null){ _transaction.SetCategory(_category.GetID()); }

                // Set Split value
                if (checkBox_split.isChecked()) {
                    _transaction.ClearSplit();
                    for (Map.Entry<Person, ViewHolderSplit> entry : active_people.entrySet()) {
                        Helper.Log(this, "ActNewTran", "SplitWith:" + entry.getKey().GetName() + " for " + String.valueOf(entry.getValue().GetCost()));
                        _transaction.SetSplit(entry.getKey().GetID(), entry.getValue().GetCost());
                        if (entry.getValue().GetPaid()) { _transaction.SetPaidBy(entry.getKey().GetID()); }
                    }
                }

                // Set paidback
                if (checkBox_paidBack.isChecked()) {
                    _transaction.SetPaidBack( _paidBack );
                } else {
                    _transaction.SetPaidBack(null);
                }

            }


            DatabaseManager dm = DatabaseManager.getInstance();

            // Blacklist date for instance transaction
            if (_editState == EDIT_STATE.EditInstance) {
                Transaction tranp = _budget.GetTransaction(_transaction.GetParentID());
                if (tranp != null) {
                    Helper.Log(this, "ActNewTran", "Parent:" + tranp.GetID());
                    Helper.Log(this, "ActNewTran", "EditingTPDate:" + _transaction.GetTimePeriod().GetDate().toString());

                    tranp.GetTimePeriod().AddBlacklistDate(_transaction.GetID(), _transaction.GetTimePeriod().GetDate(), true);
                    dm.insert(tranp, true); // Update parent
                } else { Helper.Log(this, "ActNewTran", "Parent:null ID:" + _transaction.GetParentID()); }
            }

            // Set budget id
            _transaction.SetBudgetID(_budget.GetID());

            // Set Time Period
            TimePeriod tp = new TimePeriod();
            tp.DeepCopy(_timePeriod);
            _transaction.SetTimePeriod( tp );
            Helper.Log(ActivityNewTransaction.this, "ActNewTran", "Finish Transaction ID:" + _transaction.GetID());
            Helper.Log(ActivityNewTransaction.this, "ActNewTran", "Finish TimePeriod  ID:" + _transaction.GetTimePeriod().GetID());
            Helper.Log(ActivityNewTransaction.this, "ActNewTran", "Finish Date:" + _transaction.GetTimePeriod().GetDate().toString());

            // Add to budget
            if (_editState == EDIT_STATE.NewTransaction || _editState == EDIT_STATE.EditInstance){
                _budget.AddTransaction(_transaction);
                Helper.Log(this, "ActNewTran", "Add Transaction:" + _transaction.GetID());
            }

            //Add to/update database
            dm.insert(_transaction, true);
            //dm.insertSetting(_budget, true); //Unneccesary?

        } else { // Failure (no category)
            Helper.Log(this, "ActNewTran", "Error: Pick a category");
            Helper.PrintUser(this, Helper.getString(R.string.info_select_category));
        }
    }

}
