package carvellwakeman.incomeoutcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import org.joda.time.LocalDate;

import java.util.ArrayList;


public class ActivityDetailsTransaction extends AppCompatActivity implements GestureDetector.OnGestureListener
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
        setContentView(R.layout.activity_details_transaction);

        //Toolbar menus
        toolbar_menus = new ArrayList<>();

        //Get the intent that opened this activity
        Intent intent = getIntent();


        //Determine if this is an expense or income activity
        activityType = intent.getIntExtra("activitytype", -1);
        keyType = intent.getIntExtra("keytype", -1);

        if (activityType == -1){ //None (error)
            ProfileManager.Print("Error opening details activity, no type (expense/income) specified.");
            finish();
        }
        else if (activityType == 0) { //Expense
            toolbar_menus.add(R.menu.submenu_sort);
            toolbar_menus.add(R.menu.submenu_filter);
            toolbar_menus.add(R.menu.submenu_paidback);
            ac_editing_activity = ActivityNewTransaction.class;
        }
        else if (activityType == 1) { //Income
            toolbar_menus.add(R.menu.submenu_sort);
            toolbar_menus.add(R.menu.submenu_filter);
            ac_editing_activity = ActivityNewTransaction.class;
        }

        //Set our activity's data
        _profileID = intent.getIntExtra("profile", -1);
        _profile = ProfileManager.GetProfileByID(_profileID);
        if (_profile == null)
        {
            Toast.makeText(this, "Invalid Profile Data, Cannot Open details activity.", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            //Find Views
            toolbar = (Toolbar) findViewById(R.id.toolbar);

            totalsView = (RecyclerView) findViewById(R.id.recyclerView_transaction_totals);
            elementsView = (RecyclerView) findViewById(R.id.recyclerView_transaction_elements);


            //Swiping gesture setup
            gestureDetector = new GestureDetector(this, this);
            gestureListener = new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            };

            toolbar.setOnTouchListener(gestureListener);



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

            //Title
            if (activityType == 0) { //Expense
                toolbar.setTitle(R.string.title_expenses);
            }
            else if (activityType == 1) { //Income
                toolbar.setTitle(R.string.title_income);
            }
            //Subtitle
            toolbar.setSubtitle(_profile.GetDateFormatted());


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
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        for(int m : toolbar_menus){ getMenuInflater().inflate(m, menu); }

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

            case R.id.toolbar_sort_category:
                if (_profile.GetSortMethod() == ProfileManager.SORT_METHODS.CATEGORY_UP) { _profile.Sort(ProfileManager.SORT_METHODS.CATEGORY_DOWN); } else { _profile.Sort(ProfileManager.SORT_METHODS.CATEGORY_UP); }
                elementsAdapter.notifyDataSetChanged();
                return true;
            case R.id.toolbar_sort_source:
                if (_profile.GetSortMethod() == ProfileManager.SORT_METHODS.SOURCE_UP) { _profile.Sort(ProfileManager.SORT_METHODS.SOURCE_DOWN); } else { _profile.Sort(ProfileManager.SORT_METHODS.SOURCE_UP); }
                elementsAdapter.notifyDataSetChanged();
                return true;
            case R.id.toolbar_sort_cost:
                if (_profile.GetSortMethod() == ProfileManager.SORT_METHODS.COST_UP) { _profile.Sort(ProfileManager.SORT_METHODS.COST_DOWN); } else { _profile.Sort(ProfileManager.SORT_METHODS.COST_UP); }
                elementsAdapter.notifyDataSetChanged();
                return true;
            case R.id.toolbar_sort_date:
                if (_profile.GetSortMethod() == ProfileManager.SORT_METHODS.DATE_UP) { _profile.Sort(ProfileManager.SORT_METHODS.DATE_DOWN); } else { _profile.Sort(ProfileManager.SORT_METHODS.DATE_UP); }
                elementsAdapter.notifyDataSetChanged();
                return true;
            case R.id.toolbar_sort_paidby:
                if (_profile.GetSortMethod() == ProfileManager.SORT_METHODS.PAIDBY_UP) { _profile.Sort(ProfileManager.SORT_METHODS.PAIDBY_DOWN); } else { _profile.Sort(ProfileManager.SORT_METHODS.PAIDBY_UP); }
                elementsAdapter.notifyDataSetChanged();
                return true;

            case R.id.toolbar_filter_none:
                _profile.Filter(ProfileManager.FILTER_METHODS.NONE, null, activityType);
                elementsAdapter.notifyDataSetChanged();
                totalsAdapter.notifyDataSetChanged();
                return true;
            case R.id.toolbar_filter_category:
                ProfileManager.OpenDialogFragment(this, DialogFragmentFilter.newInstance(this, _profile, ProfileManager.FILTER_METHODS.CATEGORY), true);
                return true;
            case R.id.toolbar_filter_source:
                ProfileManager.OpenDialogFragment(this, DialogFragmentFilter.newInstance(this, _profile, ProfileManager.FILTER_METHODS.SOURCE), true);
                return true;
            case R.id.toolbar_filter_whopaid:
                ProfileManager.OpenDialogFragment(this, DialogFragmentFilter.newInstance(this, _profile, ProfileManager.FILTER_METHODS.PAIDBY), true);
                return true;

            case R.id.toolbar_paidback: //Expense only
                ProfileManager.OpenDialogFragment(this, DialogFragmentPaidBack.newInstance(this, _profile), true);
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

    //Gestures
    @Override
    public boolean onTouchEvent(MotionEvent me) { return gestureDetector.onTouchEvent(me); }
    @Override
    public boolean onDown(MotionEvent e) {return true;}
    @Override
    public void onLongPress(MotionEvent e) {}
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {return true;}
    @Override
    public void onShowPress(MotionEvent e) {}
    @Override
    public boolean onSingleTapUp(MotionEvent e) { return true; }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        final int SWIPE_MIN_DISTANCE = 120;
        final int SWIPE_MAX_OFF_PATH = 250;
        final int SWIPE_THRESHOLD_VELOCITY = 200;

        if (e1 != null && e2 != null) {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) { return false; }

            if (_profile != null) {
                //Right to Left
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    _profile.TimePeriodPlus(1);
                }
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    _profile.TimePeriodMinus(1);
                }

                this.recreate();
                _profile.CalculateTimeFrame(activityType);
                _profile.CalculateTotalsInTimeFrame(activityType, keyType);
            }
        }
        return true;
    }


    //Get return results from activities
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            /*
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
            */

        }

        //Update timeframe for profile
        _profile.CalculateTimeFrame(activityType);
        _profile.CalculateTotalsInTimeFrame(activityType, keyType);
        if (elementsAdapter != null) { elementsAdapter.notifyDataSetChanged(); }
        if (totalsAdapter != null) { totalsAdapter.notifyDataSetChanged(); }
        //expenseAdapter.notifyItemRangeRemoved(0, _profile.GetExpenseSourcesInTimeFrameSize());
        //UpdateAdapters();
    }

    public void UpdateAdapters(){
        elementsView.setAdapter(null);
        totalsView.setAdapter(null);

        elementsAdapter = new AdapterDetailsTransaction(this, _profileID, activityType);
        elementsView.setAdapter(elementsAdapter);

        totalsAdapter = new AdapterTransactionTotals(this, _profileID, activityType, keyType);
        totalsView.setAdapter(totalsAdapter);
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
            Toast.makeText(this, "Could not duplicate transaction - profile not found.", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "Could not edit transaction - profile not found.", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "Could not clone transaction - profile not found.", Toast.LENGTH_LONG).show();
        }
    }


    //Delete an Transaction
    public void deleteTransaction(Transaction tran, boolean deleteParent, boolean deleteChildren){

        // If this is a child expense, blacklist the date in the parent expense, else, delete as normal
        if (deleteParent) {
            //Remove expense from profile and update expense list
            _profile.RemoveTransaction(tran, deleteChildren);
            if (elementsAdapter != null) { elementsAdapter.notifyDataSetChanged(); }

            //Update cost totals
            _profile.CalculateTotalsInTimeFrame(activityType, keyType);
            if (totalsAdapter != null) { totalsAdapter.notifyDataSetChanged(); }
        }
        else {
            blacklistTransaction(tran);
        }

        _profile.CalculateTimeFrame(activityType);
        _profile.CalculateTotalsInTimeFrame(activityType, keyType);
    }

    // Blacklist a date
    public void blacklistTransaction(Transaction tran){
        //Find parent transaction and add a blacklist date to its transaction
        Transaction tr = _profile.GetParentTransactionFromTimeFrameTransaction(tran);
        tr.GetTimePeriod().AddBlacklistDate(tran.GetTimePeriod().GetDate(), false);

        //Update database
        ProfileManager.InsertTransactionDatabase(_profile, tr, true);

        //Update transaction list
        _profile.CalculateTimeFrame(activityType);
        _profile.CalculateTotalsInTimeFrame(activityType, keyType);

        if (elementsAdapter != null) { elementsAdapter.notifyDataSetChanged(); }

        //Update cost totals
        _profile.CalculateTotalsInTimeFrame(activityType, keyType);
        if (totalsAdapter != null) { totalsAdapter.notifyDataSetChanged(); }
    }

}
