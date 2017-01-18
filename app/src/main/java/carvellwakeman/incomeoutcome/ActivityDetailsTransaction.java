package carvellwakeman.incomeoutcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import org.joda.time.LocalDate;

import java.util.ArrayList;


public class ActivityDetailsTransaction extends AppCompatActivity
{
    //Activity type (Expense or income)
    int activityType = -1;
    int keyType = -1;

    ArrayList<Integer> toolbar_menus;

    Class ac_editing_activity;

    AdapterDetailsTransaction elementsAdapter;
    AdapterTransactionTotals totalsAdapter;

    NpaLinearLayoutManager linearLayoutManager;
    NpaLinearLayoutManager linearLayoutManager2;

    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;

    RecyclerView totalsView;
    RecyclerView elementsView;

    FloatingActionButton button_new;

    TextView textView_nodata;


    int _profileID;
    Profile _profile;

    ImageView button_nextPeriod;
    ImageView button_prevPeriod;
    CheckBox checkbox_showall;

    ProfileManager.CallBack sortFilterCallBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_transaction);

        //Toolbar menus
        toolbar_menus = new ArrayList<>();

        //Get the intent that opened this activity
        Intent intent = getIntent();

        //Callback for sorting and filtering
        sortFilterCallBack = new ProfileManager.CallBack() { @Override public void call() {
            _profile.CalculateTimeFrame(activityType);
            _profile.CalculateTotalsInTimeFrame(activityType, keyType);
            elementsAdapter.notifyDataSetChanged();
            totalsAdapter.notifyDataSetChanged();
            CheckShowNoDataNotice();
        }};


        //Determine if this is an expense or income activity
        activityType = intent.getIntExtra("activitytype", -1);
        keyType = intent.getIntExtra("keytype", -1);

        if (activityType == -1){ //None (error)
            ProfileManager.PrintUser(this, "Error opening details activity, no type (expense/income) specified.");
            finish();
        }
        else if (activityType == 0) { //Expense
            toolbar_menus.add(R.menu.submenu_sort_expense);
            toolbar_menus.add(R.menu.submenu_filter_expense);
            toolbar_menus.add(R.menu.submenu_paidback);
            ac_editing_activity = ActivityNewTransaction.class;
        }
        else if (activityType == 1) { //Income
            toolbar_menus.add(R.menu.submenu_sort_income);
            toolbar_menus.add(R.menu.submenu_filter_income);
            ac_editing_activity = ActivityNewTransaction.class;
        }

        //Set our activity's data
        _profileID = intent.getIntExtra("profile", -1);
        _profile = ProfileManager.getInstance().GetProfileByID(_profileID);
        if (_profile == null)
        {
            ProfileManager.Print(this, "Invalid Profile Data, Cannot Open details activity.");
            finish();
        }
        else {
            //Find Views
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar) ;

            //Period management
            button_nextPeriod = (ImageView) findViewById(R.id.button_nextPeriod);
            button_prevPeriod = (ImageView) findViewById(R.id.button_prevPeriod);
            checkbox_showall = (CheckBox) findViewById(R.id.checkbox_showall);

            button_nextPeriod.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    if (_profile != null){
                        checkbox_showall.setChecked(false);
                        _profile.TimePeriodPlus(ActivityDetailsTransaction.this, 1);
                        RefreshActivity();
                    }
                }
            });
            button_prevPeriod.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    if (_profile != null){
                        checkbox_showall.setChecked(false);
                        _profile.TimePeriodMinus(ActivityDetailsTransaction.this, 1);
                        RefreshActivity();
                    }
                }
            });
            checkbox_showall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (_profile != null){
                        //if (b){
                        //    storedStartTime = _profile.GetStartTime();
                        //    storedEndTime = _profile.GetEndTime();
                        //}
                        //_profile.SetStartTime( (b ? null : storedStartTime) );
                        //_profile.SetEndTime( (b ? null : storedEndTime) );
                        _profile.SetShowAll(b);
                        RefreshActivity();
                    }
                }
            });

            totalsView = (RecyclerView) findViewById(R.id.recyclerView_transaction_totals);
            elementsView = (RecyclerView) findViewById(R.id.recyclerView_transaction_elements);

            textView_nodata = (TextView) findViewById(R.id.textView_transaction_nodata);

            button_new = (FloatingActionButton) findViewById(R.id.FAB_transaction_new);
            button_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Profile pr = ProfileManager.getInstance().GetCurrentProfile();
                    if (pr != null) {
                        Intent intent = new Intent(ActivityDetailsTransaction.this, ActivityNewTransaction.class);
                        intent.putExtra("activitytype", activityType);
                        intent.putExtra("profile", _profileID);
                        startActivityForResult(intent, 4);
                    }
                    else {
                        ProfileManager.PrintUser(ActivityDetailsTransaction.this, "ERROR: Profile not found, could not start New Transaction Activity");
                    }
                }
            });
            //Hide floating action button when recyclerView is scrolled
            elementsView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 10 && button_new.isShown()) { button_new.hide(); }
                    else if (dy < 0 && !button_new.isShown()){button_new.show(); }
                }
            });


            //Configure toolbar
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            for(int m : toolbar_menus){ toolbar.inflateMenu(m); }
            setSupportActionBar(toolbar);

            SetToolbarTitle();



            //Set totals adapter
            if (_profile.GetStartTime() != null && _profile.GetEndTime() != null) { //Only set up totals if there is a valid timeframe
                totalsAdapter = new AdapterTransactionTotals(this, _profileID, activityType, keyType);
                totalsView.setAdapter(totalsAdapter);

                //LinearLayoutManager for RecyclerView
                linearLayoutManager2 = new NpaLinearLayoutManager(this);
                linearLayoutManager2.setOrientation(NpaLinearLayoutManager.VERTICAL);
                linearLayoutManager2.scrollToPosition(0);
                totalsView.setLayoutManager(linearLayoutManager2);
            }

            //Set recyclerView adapter
            elementsAdapter = new AdapterDetailsTransaction(this, _profileID, activityType);
            elementsView.setAdapter(elementsAdapter);

            //LinearLayoutManager for RecyclerView
            linearLayoutManager = new NpaLinearLayoutManager(this);
            linearLayoutManager.setOrientation(NpaLinearLayoutManager.VERTICAL);
            linearLayoutManager.scrollToPosition(0);
            elementsView.setLayoutManager(linearLayoutManager);

            //No data, display message
            CheckShowNoDataNotice();

            CheckHideRecyclerviews();

            //Sort and filter bubbles
            SortFilterOptions.DisplayFilter(this, _profile.GetFilterMethod(), _profile.GetFilterData(), sortFilterCallBack);
            SortFilterOptions.DisplaySort(this, _profile.GetSortMethod(), sortFilterCallBack);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        if (_profile != null) { checkbox_showall.setChecked(_profile.GetShowAll()); }

        CheckShowNoDataNotice();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        for(int m : toolbar_menus){ getMenuInflater().inflate(m, menu); }

        //for (int i = 0; i < menu.size(); i++) { menu.getItem(i).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM); }

        return true;
    }


    //Toolbar button handling
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                return true;
            case R.id.toolbar_paidback: //Expense only
                ProfileManager.OpenDialogFragment(ActivityDetailsTransaction.this, DialogFragmentPaidBack.newInstance(ActivityDetailsTransaction.this, new ProfileManager.CallBack() { @Override public void call() {
                    elementsAdapter.notifyDataSetChanged();
                    totalsAdapter.notifyDataSetChanged();
                }}, _profile), true);
                return true;
            default:
                SortFilterOptions.Run(this, _profile, item, sortFilterCallBack);
                break;
        }


        return true;
    }


    //Send back a RESULT_OK to MainActivity when back is pressed
    @Override
    public void onBackPressed()
    {
        //Send back a RESULT_OK to MainActivity
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);

        super.onBackPressed();
    }

    //Get return results from activities
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
        }

        //Update timeframe for profile
        RefreshActivity();
        UpdateAdapters();
    }

    public void CheckShowNoDataNotice(){
        if (elementsAdapter.getItemCount()==0) { textView_nodata.setVisibility(View.VISIBLE); } else { textView_nodata.setVisibility(View.GONE); }
    }

    public void CheckHideRecyclerviews(){
        if (_profile.GetTransactionTotals().size() <= 0) { totalsView.setVisibility(View.GONE); } else { totalsView.setVisibility(View.VISIBLE); }
        if (_profile.GetTransactionsSize() <= 0) { elementsView.setVisibility(View.GONE); } else { elementsView.setVisibility(View.VISIBLE); }
    }

    public void SetToolbarTitle(){
        if (getSupportActionBar() != null) {
            if (activityType == 0) { getSupportActionBar().setTitle(R.string.title_expenses); }
            else if (activityType == 1) { getSupportActionBar().setTitle(R.string.title_income); }

            getSupportActionBar().setSubtitle(_profile.GetDateFormatted());
        }
    }
    public void RefreshActivity(){
        _profile.CalculateTimeFrame(activityType);
        _profile.CalculateTotalsInTimeFrame(activityType, keyType);
        if (totalsAdapter != null) { totalsAdapter.notifyDataSetChanged(); }
        if (elementsAdapter != null) { elementsAdapter.notifyDataSetChanged(); }

        SetToolbarTitle();
        CheckShowNoDataNotice();
        CheckHideRecyclerviews();
    }

    public void UpdateAdapters(){
        //elementsView.setAdapter(null);
        totalsView.setAdapter(null);

        //elementsAdapter = new AdapterDetailsTransaction(this, _profileID, activityType);
        //elementsView.setAdapter(elementsAdapter);

        elementsView.getRecycledViewPool().clear();
        elementsAdapter.notifyDataSetChanged();

        totalsAdapter = new AdapterTransactionTotals(this, _profileID, activityType, keyType);
        totalsView.setAdapter(totalsAdapter);

        CheckShowNoDataNotice();
    }

<<<<<<< HEAD
=======

    //Edit an expense
    /*public void editExpense(Expense expense, String profileID, Boolean isParent)
    {
        Intent intent = new Intent(ExpenseActivity.this, NewExpenseActivity.class);
        if (_profile != null) {
            intent.putExtra("profile", profileID);

            if (isParent) {
                intent.putExtra("expense", expense.GetID());
                startActivityForResult(intent, 1);
            }
            else {
                // Clone expense's parent into a new expense, except for the parent's timePeriod
                Expense exp = _profile.GetParentExpenseFromTimeFrameExpense(expense);
                Expense ne = new Expense(exp);
                ne.SetParentID(exp.toString());
                ne.SetTimePeriod(new TimePeriod(expense.GetTimePeriod().GetDate()));
                ProfileManager.Print("CopiedParentID 1:" + ne.GetParentID());

                // Edit new clone expense
                intent.putExtra("clone", ne);
                intent.putExtra("blacklist_parent", exp.toString());
                intent.putExtra("blacklist_date", expense.GetTimePeriod().GetDate());
                startActivityForResult(intent, 2); //we're basically creating a new expense, so code 0
            }
        }
        else{
            ProfileManager.Print("Could not edit expense, could not find profile.");
        }
    }
    */
>>>>>>> a3d022a145349e1e5b377c7e8b74a1eeaac3a875
    public void duplicateTransaction(Transaction tran, int profileID){
        Intent intent = new Intent(ActivityDetailsTransaction.this, ac_editing_activity);
        if (profileID != -1) {
            intent.putExtra("activitytype", activityType);
            intent.putExtra("profile", profileID);
            intent.putExtra("transaction", tran.GetID());
            intent.putExtra("editstate", ActivityNewTransaction.EDIT_STATE.Duplicate.ordinal());
            startActivityForResult(intent, 0);
        }
        else{
            ProfileManager.Print(this, "Could not duplicate transaction - profile not found.");
        }
    }
    public void editTransaction(Transaction tran, int profileID){
        Intent intent = new Intent(ActivityDetailsTransaction.this, ac_editing_activity);
        if (profileID != -1) {
            intent.putExtra("activitytype", activityType);
            intent.putExtra("profile", profileID);
            intent.putExtra("transaction", tran.GetID());
            intent.putExtra("editstate", ActivityNewTransaction.EDIT_STATE.EditUpdate.ordinal());
            startActivityForResult(intent, 1);
        }
        else{
            ProfileManager.Print(this, "Could not edit transaction - profile not found.");
        }
    }
    public void cloneTransaction(Transaction tran, int profileID, LocalDate date){
        Intent intent = new Intent(ActivityDetailsTransaction.this, ac_editing_activity);
        if (profileID != -1) {
            intent.putExtra("activitytype", activityType);
            intent.putExtra("profile", profileID);
            intent.putExtra("transaction", tran.GetID());
            intent.putExtra("editstate", ActivityNewTransaction.EDIT_STATE.EditGhost.ordinal());
            intent.putExtra("cloneDate", date);
            startActivityForResult(intent, 2);
        }
        else{
            ProfileManager.Print(this, "Could not clone transaction - profile not found.");
        }
    }


    //Delete an Transaction
    public void deleteTransaction(Transaction tran, boolean deleteParent, boolean deleteChildren){

        // If this is a child expense, blacklist the date in the parent expense, else, delete as normal
        if (deleteParent) {
            //Remove expense from profile and update expense list
            _profile.RemoveTransaction(this, tran, deleteChildren);
        }
        else {
            blacklistTransaction(tran);
        }

        _profile.CalculateTimeFrame(activityType);
        _profile.CalculateTotalsInTimeFrame(activityType, keyType);

        if (elementsAdapter != null) { elementsAdapter.notifyDataSetChanged(); }
        if (totalsAdapter != null) { totalsAdapter.notifyDataSetChanged(); }

        UpdateAdapters();
    }

    // Blacklist a date
    public void blacklistTransaction(Transaction tran){
        //Find parent transaction and add a blacklist date to its transaction
        Transaction tr = _profile.GetParentTransactionFromTimeFrameTransaction(tran);
        tr.GetTimePeriod().AddBlacklistDate(tran.GetTimePeriod().GetDate(), false);

        //Update database
        ProfileManager.getInstance().DBInsertTransaction(this, _profile, tr, true);

        //Update transaction list
        //_profile.CalculateTimeFrame(activityType);
        //_profile.CalculateTotalsInTimeFrame(activityType, keyType);

        //if (elementsAdapter != null) { elementsAdapter.notifyDataSetChanged(); }
        //if (totalsAdapter != null) { totalsAdapter.notifyDataSetChanged(); }
    }

}
