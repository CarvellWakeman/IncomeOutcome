package carvellwakeman.incomeoutcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import org.joda.time.LocalDate;


public class ExpenseActivity extends AppCompatActivity
{
    ExpenseAdapter expenseAdapter;
    TotalsAdapter totalsAdapter;

    NpaLinearLayoutManager linearLayoutManager;
    NpaLinearLayoutManager linearLayoutManager2;

    Toolbar toolbar;

    RecyclerView totalsView;
    RecyclerView elementsView;


    String _profileID;
    Profile _profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_details);

        //Get the intent that opened this tab(activity)
        Intent intent = getIntent();


        //Set our activity's data
        _profileID = intent.getStringExtra("profile");
        _profile = ProfileManager.GetProfile(_profileID);
        if (_profile == null)
        {
            Toast.makeText(this, "Invalid Profile Data, Cannot Open.", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            //Find Views
            toolbar = (Toolbar) findViewById(R.id.toolbar);

            totalsView = (RecyclerView) findViewById(R.id.recyclerView_expense_totals);
            elementsView = (RecyclerView) findViewById(R.id.recyclerView_ExpenseSources);


            //Configure toolbar
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            toolbar.inflateMenu(R.menu.toolbar_menu);
            setSupportActionBar(toolbar);

            //Set title
            toolbar.setTitle("Expenses " + _profile.GetDateFormatted());


            //Set totals adapter
            totalsAdapter = new TotalsAdapter(this, _profileID);
            totalsView.setAdapter(totalsAdapter);

            //LinearLayoutManager for RecyclerView
            linearLayoutManager2 = new NpaLinearLayoutManager(this);
            linearLayoutManager2.setOrientation(NpaLinearLayoutManager.VERTICAL);
            linearLayoutManager2.scrollToPosition(0);
            totalsView.setLayoutManager(linearLayoutManager2);


            //Set recyclerView adapter
            expenseAdapter = new ExpenseAdapter(this, _profileID);
            elementsView.setAdapter(expenseAdapter);

            //LinearLayoutManager for RecyclerView
            linearLayoutManager = new NpaLinearLayoutManager(this);
            linearLayoutManager.setOrientation(NpaLinearLayoutManager.VERTICAL);
            linearLayoutManager.scrollToPosition(0);
            elementsView.setLayoutManager(linearLayoutManager);

            /*
            ItemTouchHelper ith = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                @Override
                public boolean isLongPressDragEnabled() {
                    return true;
                }


                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    ProfileManager.Print("Swiped " + String.valueOf(direction));
                }
            });
            ith.attachToRecyclerView(elementsView);
*/

            //Populate recyclerview
            Populate();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

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

        switch (requestCode){
            case 0: //New expense
                if (resultCode == RESULT_OK){

                }
                break;
            case 1: //Edit expense
                if (resultCode == RESULT_OK){

                }
                break;
            case 2: // Clone expense
                if (resultCode == RESULT_OK) {
                    Profile pr = ProfileManager.GetProfile(data.getStringExtra("profile"));
                    Expense ex = null;
                    if (pr != null) {
                        ex = (Expense) data.getSerializableExtra("expense");
                        ProfileManager.Print("CopiedParentID 2:" + ex.GetParentID());
                        if (ex != null) { pr.AddExpense(ex); }
                    }
                    else {
                        Toast.makeText(this, "Could not edit expense, profile not found", Toast.LENGTH_SHORT).show();
                    }

                    // Blacklist old expense date since this was a clone
                    Expense bl = _profile.GetExpense(data.getStringExtra("blacklist_parent"));
                    LocalDate ld = (LocalDate) data.getSerializableExtra("blacklist_date");

                    if (ex != null && bl != null && ld != null) {
                        bl.GetTimePeriod().AddBlacklistDate(ld, true);

                        _profile.CalculateTimeFrame();
                        expenseAdapter.notifyDataSetChanged();

                        _profile.GetTotalCostPerPersonInTimeFrame();
                        totalsAdapter.notifyDataSetChanged();
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


    //Populate listview
    public void Populate()
    {
        //Sort Transactions
        //_tab.Sort(TabManager.SORT_METHODS.DEFAULT);

        //Update totals
        //updateTotals();
    }

    //Edit an expense
    public void editExpense(Expense expense, String profileID, Boolean isParent)
    {
        Intent intent = new Intent(ExpenseActivity.this, NewExpenseActivity.class);
        if (_profile != null) {
            intent.putExtra("profile", profileID);

            if (isParent) {
                intent.putExtra("expense", expense.toString());
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

    // Delete an expense
    public void deleteExpense(Expense expense, Boolean deleteParent){
        // If this is a child expense, blacklist the date in the parent expense, else, delete as normal
        if (deleteParent) {
            _profile.RemoveExpense(expense);
            expenseAdapter.notifyDataSetChanged();

            _profile.GetTotalCostPerPersonInTimeFrame();
            totalsAdapter.notifyDataSetChanged();
        }
        else {
            blacklistExpense(expense);
        }
    }

    // Blacklist a date
    public void blacklistExpense(Expense expense){
        Expense exp = _profile.GetParentExpenseFromTimeFrameExpense(expense);
        exp.GetTimePeriod().AddBlacklistDate(expense.GetTimePeriod().GetDate(), false);

        _profile.CalculateTimeFrame();
        expenseAdapter.notifyDataSetChanged();

        _profile.GetTotalCostPerPersonInTimeFrame();
        totalsAdapter.notifyDataSetChanged();
    }

}
