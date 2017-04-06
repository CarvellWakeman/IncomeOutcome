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
    //Activity type (Expense or income) (0 or 1)
    int activityType = -1;

    ArrayList<Integer> toolbar_menus;

    AdapterDetailsTransaction transactionsAdapter;
    //AdapterTransactionTotals totalsAdapter;

    NpaLinearLayoutManager linearLayoutManager;
    //NpaLinearLayoutManager linearLayoutManager2;

    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;

    //RecyclerView totalsView;
    RecyclerView transactionsView;

    FloatingActionButton button_new;

    TextView textView_nodata;


    //int _profileID;
    Budget _budget;

    ImageView button_nextPeriod;
    ImageView button_prevPeriod;
    //CheckBox checkbox_showall;

    //ProfileManager.CallBack sortFilterCallBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_transaction);

        //Toolbar menus
        toolbar_menus = new ArrayList<>();

        //Get the intent that opened this activity
        Intent intent = getIntent();

        //Callback for sorting and filtering
        //sortFilterCallBack = new ProfileManager.CallBack() { @Override public void call() {
        //    _profile.CalculateTimeFrame(activityType);
        //    _profile.CalculateTotalsInTimeFrame(activityType, keyType);
        //    transactionsAdapter.notifyDataSetChanged();
        //    totalsAdapter.notifyDataSetChanged();
        //    CheckShowNoDataNotice();
        //}};


        //Determine if this is an expense or income activity
        activityType = intent.getIntExtra("activitytype", -1);
        //keyType = intent.getIntExtra("keytype", -1);

        //Toolbar menu options (dependent upon transaction type)
        if (activityType == -1){ //None (error)
            Helper.PrintUser(this, "Error opening details activity, no type (expense/income) specified.");
            finish();
        }
        else if (activityType == 0) { //Expense
            toolbar_menus.add(R.menu.submenu_sort_expense);
            toolbar_menus.add(R.menu.submenu_filter_expense);
            toolbar_menus.add(R.menu.submenu_paidback);
            //ac_editing_activity = ActivityNewTransaction.class;
        }
        else if (activityType == 1) { //Income
            toolbar_menus.add(R.menu.submenu_sort_income);
            toolbar_menus.add(R.menu.submenu_filter_income);
            //ac_editing_activity = ActivityNewTransaction.class;
        }


        //Get transaction data
        _budget = BudgetManager.getInstance().GetBudget(intent.getIntExtra("budget", -1));
        if (_budget == null) {
            Helper.PrintUser(this, "Invalid Budget Provided, Cannot Open details activity.");
            finish();
        } else {
            //Find Views
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar) ;

            //Period management
            button_nextPeriod = (ImageView) findViewById(R.id.button_nextPeriod);
            button_prevPeriod = (ImageView) findViewById(R.id.button_prevPeriod);
            //checkbox_showall = (CheckBox) findViewById(R.id.checkbox_showall);

            button_nextPeriod.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    if (_budget != null){
                        //checkbox_showall.setChecked(false);
                        _budget.MoveTimePeriod(1);
                        RefreshActivity();
                    }
                }
            });
            button_prevPeriod.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    if (_budget != null){
                        //checkbox_showall.setChecked(false);
                        _budget.MoveTimePeriod(-1);
                        RefreshActivity();
                    }
                }
            });
            /*
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
            */

            //totalsView = (RecyclerView) findViewById(R.id.recyclerView_transaction_totals);
            transactionsView = (RecyclerView) findViewById(R.id.recyclerView_transaction_elements);

            textView_nodata = (TextView) findViewById(R.id.textView_transaction_nodata);

            button_new = (FloatingActionButton) findViewById(R.id.FAB_transaction_new);
            button_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (_budget != null) {
                        //Intent intent = new Intent(ActivityDetailsTransaction.this, ActivityNewTransaction.class);
                        //intent.putExtra("activitytype", activityType);
                        //intent.putExtra("budget", _budget.GetID());
                        //startActivityForResult(intent, 4);
                    }
                    else {
                        Helper.PrintUser(ActivityDetailsTransaction.this, "ERROR: Budget not found, could not start New Transaction Activity");
                    }
                }
            });

            //Hide floating action button when recyclerView is scrolled
            transactionsView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 10 && button_new.isShown()) { button_new.hide(); }
                    else if (dy < 0 && !button_new.isShown()){button_new.show(); }
                }
            });


            //Configure toolbar
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            for(int m : toolbar_menus){ toolbar.inflateMenu(m); }
            setSupportActionBar(toolbar);

            SetToolbarTitle();



            //Set totals adapter
            /*
            if (_profile.GetStartTime() != null && _profile.GetEndTime() != null) { //Only set up totals if there is a valid timeframe
                totalsAdapter = new AdapterTransactionTotals(this, _profileID, activityType, keyType);
                totalsView.setAdapter(totalsAdapter);

                //LinearLayoutManager for RecyclerView
                linearLayoutManager2 = new NpaLinearLayoutManager(this);
                linearLayoutManager2.setOrientation(NpaLinearLayoutManager.VERTICAL);
                linearLayoutManager2.scrollToPosition(0);
                totalsView.setLayoutManager(linearLayoutManager2);
            }
            */

            //Set transactions adapter and linearLayoutManager
            transactionsAdapter = new AdapterDetailsTransaction(this, _budget.GetID(), activityType);
            transactionsView.setAdapter(transactionsAdapter);

            linearLayoutManager = new NpaLinearLayoutManager(this);
            linearLayoutManager.setOrientation(NpaLinearLayoutManager.VERTICAL);
            linearLayoutManager.scrollToPosition(0);
            transactionsView.setLayoutManager(linearLayoutManager);



            //No data, display message
            CheckShowNoDataNotice();

            CheckHideRecyclerviews();

            //Sort and filter bubbles
            //SortFilterOptions.DisplayFilter(this, _profile.GetFilterMethod(), _profile.GetFilterData(), sortFilterCallBack);
            //SortFilterOptions.DisplaySort(this, _profile.GetSortMethod(), sortFilterCallBack);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        //if (_profile != null) { checkbox_showall.setChecked(_profile.GetShowAll()); }

        CheckShowNoDataNotice();
        //RefreshActivity();
        //UpdateAdapters();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        for(int m : toolbar_menus){ getMenuInflater().inflate(m, menu); }
        return true;
    }


    //Toolbar button handling
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home: //Back button
                BackAction();
                finish();
                return true;
            case R.id.toolbar_paidback: //Expense only //TODO: Implement
                //Helper.OpenDialogFragment(ActivityDetailsTransaction.this, DialogFragmentPaidBack.newInstance(ActivityDetailsTransaction.this, new CallBack() { @Override public void call() {
                //    transactionsAdapter.notifyDataSetChanged();
                //    totalsAdapter.notifyDataSetChanged();
                //}}, _budget), true);
                return true;
            default:
                //SortFilterOptions.Run(this, _profile, item, sortFilterCallBack);
                break;
        }


        return true;
    }


    //Send back a RESULT_OK to MainActivity when back is pressed
    @Override
    public void onBackPressed() { BackAction(); }

    //Exit function
    public void BackAction(){
        //Send back a RESULT_OK to MainActivity
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    //Get return results from activities
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //TODO: Necessary?
        //if (data != null) {}

        //Update timeframe for budget
        //RefreshActivity();
        //UpdateAdapters();
    }



    public void CheckShowNoDataNotice(){
        if (transactionsAdapter != null) {
            if (transactionsAdapter.getItemCount() == 0) {
                textView_nodata.setVisibility(View.VISIBLE);
                return;
            }
        }
        textView_nodata.setVisibility(View.GONE);
    }

    public void CheckHideRecyclerviews(){
        //if (_profile.GetTransactionTotals().size() <= 0) { totalsView.setVisibility(View.GONE); } else { totalsView.setVisibility(View.VISIBLE); }
        //if (_profile.GetTransactionsSize() <= 0) { transactionsView.setVisibility(View.GONE); } else { transactionsView.setVisibility(View.VISIBLE); }
    }

    public void SetToolbarTitle(){
        if (getSupportActionBar() != null) {
            if (activityType == 0) { getSupportActionBar().setTitle(R.string.title_expenses); }
            else if (activityType == 1) { getSupportActionBar().setTitle(R.string.title_income); }

            getSupportActionBar().setSubtitle(_budget.GetDateFormatted());
        }
    }

    public void RefreshActivity(){ //TODO: Rewrite
        UpdateAdapters();

        CheckShowNoDataNotice();

        SetToolbarTitle();

        //CheckHideRecyclerviews();

        //_budget.CalculateTimeFrame(activityType);
        //_profile.CalculateTotalsInTimeFrame(activityType, keyType);
        //if (totalsAdapter != null) { totalsAdapter.notifyDataSetChanged(); }
        //if (transactionsAdapter != null) { transactionsAdapter.notifyDataSetChanged(); }

        //SetToolbarTitle();
        //CheckShowNoDataNotice();
        //CheckHideRecyclerviews();
    }

    public void UpdateAdapters(){
        transactionsAdapter.GetTransactions();
        transactionsAdapter.notifyDataSetChanged();

        //totalsView.setAdapter(null);

        //transactionsView.getRecycledViewPool().clear();
        //transactionsAdapter.notifyDataSetChanged();

        //totalsAdapter = new AdapterTransactionTotals(this, _profileID, activityType, keyType);
        //totalsView.setAdapter(totalsAdapter);

        //CheckShowNoDataNotice();
    }

    public new_Transaction GetTransactionParent(new_Transaction transaction) {
        if (transaction.GetParentID() != 0) {
            return _budget.GetTransaction(transaction.GetParentID());
        }
        return transaction;
    }

    public void handleTransaction(new_Transaction tran, MenuItem action){
        //Find parent transaction
        final new_Transaction tranp;
        if (tran.GetParentID() != 0) {
            tranp = _budget.GetTransaction(tran.GetParentID());
        } else { tranp = tran; }

        //Take action on it
        if (tranp != null) {
            //If the expense is not a ghost expense (only exists in the _timeframe array), then edit it normally, else clone it and blacklist the old date
            switch (action.getItemId()) {
                case R.id.transaction_edit_instance: //Edit(instance)
                    //If edited expense exists, edit it. If not, clone it (and then edit it)
                    if ( _budget.GetTransaction(tran.GetID()) != null ) {
                        //Transaction is an original
                        if (tran.GetID() == tranp.GetID() && tran.GetTimePeriod()!=null && (tran.GetTimePeriod().DoesRepeat() || tranp.GetTimePeriod().DoesRepeat()) ){ //Editing parent as an instance
                            cloneTransaction(tranp, _budget.GetID(), tran.GetTimePeriod().GetDate());
                        } else { //Transaction is a child of an original
                            editTransaction(tran, _budget.GetID());
                        }
                    } else { //Ghost
                        cloneTransaction(tranp, _budget.GetID(), tran.GetTimePeriod().GetDate());
                    }
                    break;

                case R.id.transaction_edit_all: //Edit(all / parent)
                    editTransaction(tranp, _budget.GetID());
                    break;

                case R.id.transaction_delete_instance: //Delete(instance)
                    //If edited expense exists (is not a ghost)
                    if (_budget.GetTransaction(tran.GetID()) != null) {
                        //If tranp has children (ie, tranp and tran are not the same transaction)
                        if (tranp.GetTimePeriod().DoesRepeat()){ //tranp has children, blacklist its date
                            tranp.GetTimePeriod().AddBlacklistDate(tranp.GetTimePeriod().GetDate(), false);
                            DatabaseManager.getInstance().insert(tranp, true);
                        } else {
                            //ArrayList<Integer> children = tranp.GetChildren();
                            //for (int i = 0; i < children.size(); i++){ _budget.RemoveTransaction(children.get(i)); }
                            _budget.RemoveTransaction(tranp);
                            DatabaseManager.getInstance().remove(tranp);
                        }
                    } else { //Ghost
                        tranp.GetTimePeriod().AddBlacklistDate(tran.GetTimePeriod().GetDate(), false);
                        DatabaseManager.getInstance().insert(tranp, true);
                    }

                    RefreshActivity();
                    break;

                case R.id.transaction_delete_all: //Delete(all, parent)
                    ArrayList<Integer> children = tranp.GetChildren();
                    for (int i = 0; i < children.size(); i++){
                        new_Transaction child = _budget.GetTransaction(children.get(i));
                        DatabaseManager.getInstance().remove(child);
                        _budget.RemoveTransaction(child);
                    }

                    _budget.RemoveTransaction(tranp);
                    DatabaseManager.getInstance().remove(tranp);

                    RefreshActivity();
                    break;

                //case R.id.transaction_duplicate: //Duplicate
                //    duplicateTransaction(tran, _budget.GetID());
                //    break;
            }


        }
    }

    public void duplicateTransaction(new_Transaction tran, int profileID){
        /*
        Intent intent = new Intent(ActivityDetailsTransaction.this, ac_editing_activity);
        if (profileID != -1) {
            intent.putExtra("activitytype", activityType);
            intent.putExtra("profile", profileID);
            intent.putExtra("transaction", tran.GetID());
            intent.putExtra("editstate", ActivityNewTransaction.EDIT_STATE.Duplicate.ordinal());
            startActivityForResult(intent, 0);
        }
        else{
            Helper.PrintUser(this, "Could not duplicate transaction - budget not found.");
        }
        */
    }
    public void editTransaction(new_Transaction tran, int budgetID){
    /*
        Intent intent = new Intent(ActivityDetailsTransaction.this, ac_editing_activity);
        if (budgetID != -1) {
            intent.putExtra("activitytype", activityType);
            intent.putExtra("budget", budgetID);
            intent.putExtra("transaction", tran.GetID());
            intent.putExtra("editstate", ActivityNewTransaction.EDIT_STATE.EditUpdate.ordinal());
            startActivityForResult(intent, 1);
        }
        else{
            Helper.PrintUser(this, "Could not edit transaction - budget not found.");
        }
        */
    }
    public void cloneTransaction(new_Transaction tran, int budgetID, LocalDate date){
    /*
        Intent intent = new Intent(ActivityDetailsTransaction.this, ac_editing_activity);
        if (profileID != -1) {
            intent.putExtra("activitytype", activityType);
            intent.putExtra("budget", budgetID);
            intent.putExtra("transaction", tran.GetID());
            intent.putExtra("editstate", ActivityNewTransaction.EDIT_STATE.EditGhost.ordinal());
            intent.putExtra("cloneDate", date);
            startActivityForResult(intent, 2);
        }
        else{
            Helper.PrintUser(this, "Could not clone transaction - profile not found.");
        }
        */
    }


}