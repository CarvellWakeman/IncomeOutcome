package carvellwakeman.incomeoutcome.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import carvellwakeman.incomeoutcome.R;
import carvellwakeman.incomeoutcome.adapters.AdapterDetailsTotals;
import carvellwakeman.incomeoutcome.adapters.AdapterDetailsTransaction;
import carvellwakeman.incomeoutcome.data.BudgetManager;
import carvellwakeman.incomeoutcome.data.DatabaseManager;
import carvellwakeman.incomeoutcome.dialogs.DialogFragmentFilter;
import carvellwakeman.incomeoutcome.dialogs.DialogFragmentPaidBack;
import carvellwakeman.incomeoutcome.helpers.Helper;
import carvellwakeman.incomeoutcome.interfaces.RunnableParam;
import carvellwakeman.incomeoutcome.interfaces.SortFilterActivity;
import carvellwakeman.incomeoutcome.models.Budget;
import carvellwakeman.incomeoutcome.models.TimePeriod;
import carvellwakeman.incomeoutcome.models.Transaction;
import carvellwakeman.incomeoutcome.viewmodels.NpaLinearLayoutManager;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;


public class ActivityDetailsTransaction extends AppCompatActivity implements SortFilterActivity
{
    //Activity type (Expense or income) (0 or 1)
    Transaction.TRANSACTION_TYPE activityType = Transaction.TRANSACTION_TYPE.Expense;

    ArrayList<Integer> toolbar_menus;

    AdapterDetailsTransaction transactionsAdapter;
    AdapterDetailsTotals totalsAdapter;

    NpaLinearLayoutManager linearLayoutManagerTransactions;

    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;

    RecyclerView transactionsView;

    LinearLayout totalsContainer;

    FloatingActionButton button_new;

    TextView textView_nodata;
    TextView textView_filters;

    Budget _budget;

    ImageView button_nextPeriod;
    ImageView button_prevPeriod;

    RelativeLayout relativeLayout_filter;
    View filter_divider;

    // Sort and Filter
    public Helper.SORT_METHODS sortMethod;
    public HashMap<Helper.FILTER_METHODS, String> filterMethods;

    // Show all
    LocalDate prev_startDate;
    LocalDate prev_endDate;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_transaction);

        //Toolbar menus
        toolbar_menus = new ArrayList<>();

        // Sorting and filtering
        sortMethod = Helper.SORT_METHODS.DATE_ASC;
        filterMethods = new HashMap<>();

        //Get the intent that opened this activity
        Intent intent = getIntent();

        //Determine if this is an expense or income activity
        activityType = Transaction.TRANSACTION_TYPE.values()[intent.getIntExtra("activitytype", 0)];

        //Toolbar menu options (dependent upon transaction type)
        if (activityType == Transaction.TRANSACTION_TYPE.Expense) { //Expense
            toolbar_menus.add(R.menu.submenu_sort_expense);
            toolbar_menus.add(R.menu.submenu_filter_expense);
            toolbar_menus.add(R.menu.submenu_paidback);
        }
        else if (activityType == Transaction.TRANSACTION_TYPE.Income) { //Income
            toolbar_menus.add(R.menu.submenu_sort_income);
            toolbar_menus.add(R.menu.submenu_filter_income);
        }
        toolbar_menus.add(R.menu.submenu_showall);


        //Get transaction data
        _budget = BudgetManager.getInstance().GetBudget(intent.getIntExtra("budget", -1));
        if (_budget == null) {
            Helper.PrintUser(this, "Invalid Budget Provided, Cannot Open details activity.");
            finish();
        } else {

            // Find Views
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar) ;

            // Period management
            button_nextPeriod = (ImageView) findViewById(R.id.button_nextPeriod);
            button_prevPeriod = (ImageView) findViewById(R.id.button_prevPeriod);

            button_nextPeriod.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    if (_budget != null){
                        prev_startDate = prev_endDate = null;
                        _budget.MoveTimePeriod(1);
                        DatabaseManager.getInstance(ActivityDetailsTransaction.this)._insertSetting(_budget, true);
                        Refresh();
                    }
                }
            });
            button_prevPeriod.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    if (_budget != null){
                        prev_startDate = prev_endDate = null;
                        _budget.MoveTimePeriod(-1);
                        DatabaseManager.getInstance(ActivityDetailsTransaction.this)._insertSetting(_budget, true);
                        Refresh();
                    }
                }
            });

            totalsContainer = (LinearLayout) findViewById(R.id.linearLayout_totals_container);
            transactionsView = (RecyclerView) findViewById(R.id.recyclerView_transaction_elements);

            textView_nodata = (TextView) findViewById(R.id.textView_transaction_nodata);
            textView_filters = (TextView) findViewById(R.id.textView_filters);
            textView_filters.setTextColor(Helper.getColor(R.color.ltgray));

            button_new = (FloatingActionButton) findViewById(R.id.FAB_transaction_new);
            button_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                if (_budget != null) {
                    Intent intent = new Intent(ActivityDetailsTransaction.this, ActivityNewTransaction.class);
                    intent.putExtra("activitytype", activityType.ordinal());
                    intent.putExtra("budget", _budget.GetID());
                    startActivityForResult(intent, 1);
                }
                else {
                    Helper.PrintUser(ActivityDetailsTransaction.this, "ERROR: Budget not found, could not start New Transaction Activity");
                }
                }
            });

            // Hide floating action button when recyclerView is scrolled
            transactionsView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 10 && button_new.isShown()) { button_new.hide(); }
                    else if (dy < 0 && !button_new.isShown()){button_new.show(); }
                }
            });

            filter_divider = findViewById(R.id.divider);
            relativeLayout_filter = (RelativeLayout) findViewById(R.id.relativeLayout_filter);

            relativeLayout_filter.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                filterMethods.clear();
                Refresh();
                }
            });


            // Configure toolbar
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            for(int m : toolbar_menus){ toolbar.inflateMenu(m); }
            setSupportActionBar(toolbar);

            SetToolbarTitle();

            // Set totals adapter
            totalsAdapter = new AdapterDetailsTotals(this, totalsContainer, _budget.GetID(), activityType);

            // Set transactions adapter and linearLayoutManager
            transactionsAdapter = new AdapterDetailsTransaction(this, _budget.GetID(), activityType);
            transactionsView.setAdapter(transactionsAdapter);

            linearLayoutManagerTransactions = new NpaLinearLayoutManager(this);
            transactionsView.setLayoutManager(linearLayoutManagerTransactions);

            CheckShowNoDataNotice();
        }
    }


    @Override
    public void onResume(){ // TODO: Necessary?
        super.onResume();
        Refresh();
    }


    // Options menu creation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        for(int m : toolbar_menus){ getMenuInflater().inflate(m, menu); }
        return true;
    }

    // Options menu handling
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home: // Up button
                BackAction();
                return true;
            case R.id.toolbar_paidback: // Paid Back (expenses only)
                Helper.OpenDialogFragment(ActivityDetailsTransaction.this, DialogFragmentPaidBack.newInstance(ActivityDetailsTransaction.this,
                    new RunnableParam() { @Override public void run(Object date) {
                       SetTransactionsPaidBack((LocalDate)date);
                    }
                }, _budget), true);
                return true;
            case R.id.toolbar_showall: // Show all transactions
                if (prev_startDate != null && prev_endDate != null) {
                    if (_budget.GetStartDate() == null && _budget.GetEndDate() == null) {
                        _budget.SetStartDate(prev_startDate);
                        _budget.SetEndDate(prev_endDate);
                    }

                    prev_startDate = null;
                    prev_endDate = null;

                    item.setTitle(getString(R.string.action_showall));
                } else {
                    prev_startDate = _budget.GetStartDate();
                    prev_endDate = _budget.GetEndDate();

                    _budget.SetStartDate(null);
                    _budget.SetEndDate(null);

                    item.setTitle(getString(R.string.action_showrange));
                }

                Refresh();
                return true;

            // Sorting
            case R.id.toolbar_sort_date:
            case R.id.toolbar_sort_cost:
            case R.id.toolbar_sort_category:
            case R.id.toolbar_sort_source:
            case R.id.toolbar_sort_paidby:
                sortMethod = Helper.SortSelect(sortMethod.ordinal(), item.getOrder());
                UpdateTransactionsAdapter();
                break;

            // Filters
            case R.id.toolbar_filter_category:
            case R.id.toolbar_filter_source:
            case R.id.toolbar_filter_paidby:
            case R.id.toolbar_filter_splitwith:
            case R.id.toolbar_filter_paidback:
                Helper.FILTER_METHODS method = Helper.FILTER_METHODS.values()[item.getOrder()];
                Helper.OpenDialogFragment(this, DialogFragmentFilter.newInstance(this, this, _budget.GetID(), method, getString(Helper.filterTitles.get(method)), activityType), true);
                break;

            default:
                break;
        }

        return true;
    }


    //Get return results from activities
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //TODO: Necessary?
        Refresh();

        //switch (requestCode){
        //case 1: // New Transaction
        if (resultCode==1){
            if (data != null){
                //final Transaction _transaction = (Transaction)data.getSerializableExtra("transaction");

                //Add to/update database synchronously
                //final DatabaseManager dm = DatabaseManager.getInstance();

                //dm._insert(_transaction, true);
                //dm._insertSetting(_budget, true);

                // Refresh activity to show changes
                //Helper.Log(ActivityDetailsTransaction.this, "ActDetTran", "Transaction added or updated");
            }
        } else { // Failure
        }
        //break;
        //default:


        //}
        //if (data != null) {}

        //Update timeframe for budget
        //Refresh();
        //UpdateAdapters();
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


    // Activity state
    public void CheckShowNoDataNotice(){
        if (transactionsAdapter != null) {
            if (transactionsAdapter.getItemCount() == 0) {
                textView_nodata.setVisibility(View.VISIBLE);
                button_new.show();
                return;
            }
        }
        textView_nodata.setVisibility(View.GONE);
    }

    public void CheckShowFiltersNotice(){
        relativeLayout_filter.setVisibility( (filterMethods.size() > 0 ? View.VISIBLE : View.GONE) );
        filter_divider.setVisibility( (filterMethods.size() > 0 ? View.VISIBLE : View.GONE) );
        textView_filters.setText(Helper.FilterString(this, filterMethods));
    }

    public void SetToolbarTitle(){
        if (getSupportActionBar() != null) {
            if (activityType == Transaction.TRANSACTION_TYPE.Expense) { getSupportActionBar().setTitle(R.string.title_expenses); }
            else if (activityType == Transaction.TRANSACTION_TYPE.Income) { getSupportActionBar().setTitle(R.string.title_income); }

            getSupportActionBar().setSubtitle(_budget.GetDateFormatted(ActivityDetailsTransaction.this));
        }
    }

    public void AddFilterMethod(Helper.FILTER_METHODS method, String data){
        filterMethods.put(method, data);
    }

    public void Refresh(){
        UpdateTransactionsAdapter();
        UpdateTotalsAdapter();

        CheckShowNoDataNotice();

        CheckShowFiltersNotice();

        SetToolbarTitle();
    }

    public void UpdateTransactionsAdapter(){
        // Transactions
        transactionsAdapter.GetTransactionsInTimeFrame(ActivityDetailsTransaction.this, sortMethod, filterMethods);
        transactionsAdapter.notifyDataSetChanged();
    }
    public void UpdateTotalsAdapter(){
        // Totals
        totalsAdapter.GetTotals(sortMethod, filterMethods);
        totalsAdapter.PopulateContainer();
    }


    // Paid back
    public void SetTransactionsPaidBack(LocalDate date){
        DatabaseManager dm = DatabaseManager.getInstance(this);

        // Update transactions
        for (Transaction t : transactionsAdapter.GetTransactions()){ //Transactions not being inserted into the database
            Transaction parentT = _budget.GetTransaction(t.GetParentID());

            TimePeriod tp = t.GetTimePeriod();

            // If transaction is an instance transaction
            if (_budget.GetTransaction(t.GetID()) == null){
            //if (tp != null && tp.DoesRepeat()) {
                // Duplicate transaction and set as paid back, blacklist on parent
                if (parentT != null) {
                    t.SetPaidBack(date);
                    t.SetParentID(parentT.GetID());

                    _budget.AddTransaction(t);
                    parentT.GetTimePeriod().AddBlacklistDate(t.GetID(), t.GetTimePeriod().GetDate(), true);

                    dm.insert(parentT, true); // Update parent
                    dm.insert(t, false); // Insert child
                }
            } else { // Set paid back date
                if (t.GetPaidBack() == null || date == null) {
                    t.SetPaidBack(date);
                }

                // Check for copy (and remove if instance is one)
                if (t.GetParentID() > 0 && t.shallowEquals(parentT)){
                    _budget.RemoveTransaction(t);
                    parentT.GetTimePeriod().RemoveBlacklistDate(t.GetTimePeriod().GetDate());
                    dm.remove(t);
                    dm.insert(parentT, true); // Update parent
                } else {
                    dm.insert(t, true);
                }
            }
        }
        Refresh();
    }

}
