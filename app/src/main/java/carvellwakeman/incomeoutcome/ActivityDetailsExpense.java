package carvellwakeman.incomeoutcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.Toast;
import org.joda.time.LocalDate;


public class ActivityDetailsExpense extends AppCompatActivity
{
    AdapterDetailsExpense expenseAdapter;
    AdapterExpenseTotals totalsAdapter;

    LinearLayout linearlayoutParent;
    NpaLinearLayoutManager linearLayoutManager;
    NpaLinearLayoutManager linearLayoutManager2;

    Toolbar toolbar;

    RecyclerView totalsView;
    RecyclerView elementsView;


    int _profileID;
    Profile _profile;

    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_details);

        //Get the intent that opened this tab(activity)
        Intent intent = getIntent();

        //Set our activity's data
        _profileID = intent.getIntExtra("profile", -1);
        _profile = ProfileManager.GetProfileByID(_profileID);
        if (_profile == null)
        {
            Toast.makeText(this, "Invalid Profile Data, Cannot Open.", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            //Find Views
            linearlayoutParent = (LinearLayout) findViewById(R.id.linearLayout_expenses);

            toolbar = (Toolbar) findViewById(R.id.toolbar);

            totalsView = (RecyclerView) findViewById(R.id.recyclerView_expense_totals);
            elementsView = (RecyclerView) findViewById(R.id.recyclerView_ExpenseSources);


            //Swiping gesture setup
            gestureDetector = new GestureDetector(this, new FlingGestureDetector());
            gestureListener = new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            };

            totalsView.setOnTouchListener(gestureListener);
            elementsView.setOnTouchListener(gestureListener);


            //Configure toolbar
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            toolbar.inflateMenu(R.menu.toolbar_menu_sortfilter);
            setSupportActionBar(toolbar);

            //Set title
            toolbar.setTitle("Expenses");
            toolbar.setSubtitle(_profile.GetDateFormatted());


            //Set totals adapter
            totalsAdapter = new AdapterExpenseTotals(this, _profileID);
            totalsView.setAdapter(totalsAdapter);

            //LinearLayoutManager for RecyclerView
            linearLayoutManager2 = new NpaLinearLayoutManager(this);
            linearLayoutManager2.setOrientation(NpaLinearLayoutManager.VERTICAL);
            linearLayoutManager2.scrollToPosition(0);
            totalsView.setLayoutManager(linearLayoutManager2);


            //Set recyclerView adapter
            expenseAdapter = new AdapterDetailsExpense(this, _profileID);
            elementsView.setAdapter(expenseAdapter);

            //LinearLayoutManager for RecyclerView
            linearLayoutManager = new NpaLinearLayoutManager(this);
            linearLayoutManager.setOrientation(NpaLinearLayoutManager.VERTICAL);
            linearLayoutManager.scrollToPosition(0);
            elementsView.setLayoutManager(linearLayoutManager);

            //Populate recyclerview
            Populate();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu_sortfilter, menu);

        return true;
    }


    //Toolbar button handling
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return false;
        }
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

            //Find returned expense
            Profile pr = ProfileManager.GetProfileByID(data.getIntExtra("profile", -1));
            Expense newExp = (Expense) data.getSerializableExtra("newExpense");

            switch (requestCode) {
                case 0: //New expense (copy)
                    if (resultCode == RESULT_OK) {
                        if (pr != null) {
                            if (newExp != null) {
                                pr.AddExpense(newExp);
                            }
                        }
                    }
                    break;
                case 1: //Edit expense
                    if (resultCode == RESULT_OK) {
                        if (pr != null) {
                            if (newExp != null) {
                                pr.UpdateExpense(newExp);
                            }
                        }
                    }
                    break;
                case 2: // Clone expense
                    if (resultCode == RESULT_OK) {
                        if (pr != null) {
                            if (newExp != null) {
                                pr.AddExpense(newExp);

                                Expense originalEx = pr.GetExpense((int) data.getSerializableExtra("originalExpense"));

                                //Add child
                                if (originalEx != null) {
                                    originalEx.AddChild(newExp, true);
                                    newExp.SetParentID(originalEx.GetID());
                                }


                                //Blacklist old date
                                //LocalDate _cloneDate = (LocalDate) data.getSerializableExtra("cloneDate");

                                //if (originalEx != null) {
                                //    TimePeriod tp = originalEx.GetTimePeriod();
                                //    if (tp != null) {
                                //        if (_cloneDate != null) {
                                //            ProfileManager.Print("Blacklist Clone Date: " + _cloneDate.toString(ProfileManager.simpleDateFormat));
                                //            tp.AddBlacklistDate(_cloneDate, true);
                                //            //Update original transaction and its timeperiod
                                //            pr.UpdateExpense(originalEx);
                                //        }
                                //    }
                                //}

                            }

                        }
                    }
                    break;
            }


            //Update timeframe for profile
            _profile.CalculateTimeFrame();
            expenseAdapter.notifyDataSetChanged();
            _profile.GetTotalCostPerPersonInTimeFrame();
            totalsAdapter.notifyDataSetChanged();
        }
    }


    //Populate listview
    public void Populate()
    {
        //Sort Transactions
        //_tab.Sort(TabManager.SORT_METHODS.DEFAULT);

        //Update totals
        //updateTotals();
    }

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
            Toast.makeText(this, "Could not edit expense, could not find profile.", Toast.LENGTH_LONG).show();
        }
    }
    */
    public void copyExpense(Expense expense, int profileID){
        Intent intent = new Intent(ActivityDetailsExpense.this, ActivityNewExpense.class);
        if (profileID != -1) {
            intent.putExtra("profile", profileID);
            intent.putExtra("expense", expense.GetID());
            intent.putExtra("edit", 2);
            startActivityForResult(intent, 0);
        }
        else{
            Toast.makeText(this, "Could not copy expense - profile not found.", Toast.LENGTH_LONG).show();
        }
    }
    public void editExpense(Expense expense, int profileID){
        Intent intent = new Intent(ActivityDetailsExpense.this, ActivityNewExpense.class);
        if (profileID != -1) {
            intent.putExtra("profile", profileID);
            intent.putExtra("expense", expense.GetID());
            intent.putExtra("edit", 1);
            startActivityForResult(intent, 1);
        }
        else{
            Toast.makeText(this, "Could not edit expense - profile not found.", Toast.LENGTH_LONG).show();
        }
    }
    public void cloneExpense(Expense expense, int profileID, LocalDate date){
        Intent intent = new Intent(ActivityDetailsExpense.this, ActivityNewExpense.class);
        if (profileID != -1) {
            intent.putExtra("profile", profileID);
            intent.putExtra("expense", expense.GetID());
            intent.putExtra("edit", 3);
            intent.putExtra("cloneDate", date);
            startActivityForResult(intent, 2);
        }
        else{
            Toast.makeText(this, "Could not clone expense - profile not found.", Toast.LENGTH_LONG).show();
        }
    }


    // Delete an expense
    public void deleteExpense(Expense expense, boolean deleteParent, boolean deleteChildren){

        // If this is a child expense, blacklist the date in the parent expense, else, delete as normal
        if (deleteParent) {
            //Remove expense from profile and update expense list
            _profile.RemoveExpense(expense, deleteChildren);
            expenseAdapter.notifyDataSetChanged();

            //Update cost totals
            _profile.GetTotalCostPerPersonInTimeFrame();
            totalsAdapter.notifyDataSetChanged();
        }
        else {
            blacklistExpense(expense);
        }
    }

    // Blacklist a date
    public void blacklistExpense(Expense expense){
        //Find parent transaction and add a blacklist date to its transaction
        Expense exp = _profile.GetParentExpenseFromTimeFrameExpense(expense);
        exp.GetTimePeriod().AddBlacklistDate(expense.GetTimePeriod().GetDate(), false);

        //Update database
        ProfileManager.InsertExpenseDatabase(_profile, exp, true);

        //Update expense list
        _profile.CalculateTimeFrame();
        expenseAdapter.notifyDataSetChanged();

        //Update cost totals
        _profile.GetTotalCostPerPersonInTimeFrame();
        totalsAdapter.notifyDataSetChanged();
    }

}
