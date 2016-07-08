package carvellwakeman.incomeoutcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import org.joda.time.LocalDate;


public class ActivityDetailsIncome extends AppCompatActivity implements GestureDetector.OnGestureListener
{
    AdapterDetailsIncome incomeAdapter;
    AdapterIncomeTotals totalsAdapter;

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
        setContentView(R.layout.activity_income_details);

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
            toolbar = (Toolbar) findViewById(R.id.toolbar);

            totalsView = (RecyclerView) findViewById(R.id.recyclerView_income_totals);
            elementsView = (RecyclerView) findViewById(R.id.recyclerView_IncomeSources);


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
            toolbar.inflateMenu(R.menu.toolbar_menu_sortfilter);
            setSupportActionBar(toolbar);

            //Set title
            toolbar.setTitle("Income");
            toolbar.setSubtitle(_profile.GetDateFormatted());

            //Set totals adapter
            if (_profile.GetStartTime() != null && _profile.GetEndTime() != null) { //Only set up totals if there is a valid timeframe
                totalsAdapter = new AdapterIncomeTotals(this, _profileID);
                totalsView.setAdapter(totalsAdapter);

                //LinearLayoutManager for RecyclerView
                linearLayoutManager2 = new NpaLinearLayoutManager(this);
                linearLayoutManager2.setOrientation(NpaLinearLayoutManager.VERTICAL);
                linearLayoutManager2.scrollToPosition(0);
                totalsView.setLayoutManager(linearLayoutManager2);
            }

            //Set recyclerView adapter
            incomeAdapter = new AdapterDetailsIncome(this, _profileID);
            elementsView.setAdapter(incomeAdapter);

            //LinearLayoutManager for RecyclerView
            linearLayoutManager = new NpaLinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
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

            /*
            case R.id.toolbar_sort_date:
                if (_tab.GetSortMethod() == TabManager.SORT_METHODS.DATE_UP) { _tab.Sort(TabManager.SORT_METHODS.DATE_DOWN); } else { _tab.Sort(TabManager.SORT_METHODS.DATE_UP); }
                adapter.notifyDataSetChanged();
                return true;
            case R.id.toolbar_sort_cost:
                if (_tab.GetSortMethod() == TabManager.SORT_METHODS.COST_UP) { _tab.Sort(TabManager.SORT_METHODS.COST_DOWN); } else { _tab.Sort(TabManager.SORT_METHODS.COST_UP); }
                adapter.notifyDataSetChanged();
                return true;
            case R.id.toolbar_sort_category:
                _tab.Sort(TabManager.SORT_METHODS.CATEGORY);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.toolbar_sort_company:
                _tab.Sort(TabManager.SORT_METHODS.COMPANY);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.toolbar_sort_paidby:
                if (_tab.GetSortMethod() == TabManager.SORT_METHODS.PAIDBY_UP) { _tab.Sort(TabManager.SORT_METHODS.PAIDBY_DOWN); } else { _tab.Sort(TabManager.SORT_METHODS.PAIDBY_UP); }
                adapter.notifyDataSetChanged();
                return true;


            case R.id.toolbar_filter:
                //Filter dialog
                final Dialog filterDialog = new Dialog(TabActivity.this);
                filterDialog.setContentView(R.layout.dialog_filter1);
                filterDialog.setTitle("Filter");

                //Find components
                final RadioGroup radioGroup = (RadioGroup) filterDialog.findViewById(R.id.filter_radioGroup);
                final RadioButton radioButton1 = (RadioButton) filterDialog.findViewById(R.id.filter_radioButton1);
                final RadioButton radioButton2 = (RadioButton) filterDialog.findViewById(R.id.filter_radioButton2);
                final RadioButton radioButton3 = (RadioButton) filterDialog.findViewById(R.id.filter_radioButton3);

                final Button continueButton = (Button) filterDialog.findViewById(R.id.filter_button_continue);
                final Button cancelButton = (Button) filterDialog.findViewById(R.id.filter_button_cancel);

                //Allow continue button to be clicked
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (radioGroup.getCheckedRadioButtonId() != -1) {
                            continueButton.setEnabled(true);
                        }
                    }
                });

                //ContinueButton action
                continueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Clear filter
                        ClearFilter();

                        //Dimiss dialog
                        filterDialog.dismiss();

                        //Conditional radioButton actions
                        if (radioGroup.getVisibility() == View.VISIBLE) {

                            if (radioButton1.isChecked()) {
                                FilterByCategoryDialog();
                            }
                            else if (radioButton2.isChecked()) {
                                FilterByCompanyDialog();
                            }
                            else if (radioButton3.isChecked()) {
                                FilterByWhoPaidDialog();
                            }

                            radioGroup.clearCheck();
                        }
                    }
                });

                //CancelButton action
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filterDialog.dismiss();
                    }
                });


                //Show the filter dialog
                filterDialog.show();


                return true;
            */
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
            Profile pr = ProfileManager.GetCurrentProfile();
            if (pr != null) {
                //Right to Left
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    pr.TimePeriodPlus(1);
                }
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    pr.TimePeriodMinus(1);
                }

                this.recreate();
                pr.CalculateTimeFrame();
            }
        }
        return true;
    }


    //Get return results from activities
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {

            //Find returned income
            Profile pr = ProfileManager.GetProfileByID(data.getIntExtra("profile", -1));
            Income newInc = (Income) data.getSerializableExtra("newIncome");


            switch (requestCode) {
                case 0: //New income (copy)
                    if (resultCode == RESULT_OK) {
                        if (pr != null) {
                            if (newInc != null) {
                                pr.AddIncome(newInc);
                            }
                        }
                    }
                    break;
                case 1: //Edit income
                    if (resultCode == RESULT_OK) {
                        if (pr != null) {
                            if (newInc != null) {
                                pr.UpdateIncome(newInc);
                            }
                        }
                    }
                    break;
                case 2: // Clone income
                    if (resultCode == RESULT_OK) {
                        if (pr != null) {
                            if (newInc != null) {
                                pr.AddIncome(newInc);

                                Income originalIn = pr.GetIncome((int) data.getSerializableExtra("originalIncome"));

                                //Add child
                                if (originalIn != null) {
                                    originalIn.AddChild(newInc, true);
                                    newInc.SetParentID(originalIn.GetID());
                                }
                            }
                        }
                    }

                    break;
            }


            //Update timeframe for profile
            _profile.CalculateTimeFrame();
            incomeAdapter.notifyDataSetChanged();
            _profile.GetTotalIncomePerSourceInTimeFrame();
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


    //Edit an income
    public void editIncome(Income income, int profileID){
        Intent intent = new Intent(ActivityDetailsIncome.this, ActivityNewIncome.class);
        if (profileID != -1) {
            intent.putExtra("profile", profileID);
            intent.putExtra("income", income.GetID());
            intent.putExtra("edit", 1);
            startActivityForResult(intent, 1);
        }
        else{
            Toast.makeText(this, "Could not edit income, profile not found.", Toast.LENGTH_LONG).show();
        }
    }
    public void copyIncome(Income income, int profileID){
        Intent intent = new Intent(ActivityDetailsIncome.this, ActivityNewIncome.class);
        if (profileID != -1) {
            intent.putExtra("profile", profileID);
            intent.putExtra("income", income.GetID());
            intent.putExtra("edit", 2);
            startActivityForResult(intent, 0);
        }
        else{
            Toast.makeText(this, "Could not copy income, profile not found.", Toast.LENGTH_LONG).show();
        }
    }
    public void cloneIncome(Income income, int profileID, LocalDate date){
        Intent intent = new Intent(ActivityDetailsIncome.this, ActivityNewIncome.class);
        if (profileID != -1) {
            intent.putExtra("profile", profileID);
            intent.putExtra("income", income.GetID());
            intent.putExtra("edit", 3);
            intent.putExtra("cloneDate", date);
            startActivityForResult(intent, 2);
        }
        else{
            Toast.makeText(this, "Could not clone income, profile not found.", Toast.LENGTH_LONG).show();
        }
    }


    // Delete an income
    public void deleteIncome(Income income, boolean deleteParent, boolean deleteChildren){
        // If this is a child income, blacklist the date in the parent income, else, delete as normal
        if (deleteParent) {
            //Remove income from profile and update expense list
            _profile.RemoveIncome(income, deleteChildren);
            incomeAdapter.notifyDataSetChanged();

            //Update cost totals
            _profile.GetTotalIncomePerSourceInTimeFrame();
            totalsAdapter.notifyDataSetChanged();
        }
        else {
            blacklistIncome(income);
        }
    }

    // Blacklist a date
    public void blacklistIncome(Income income){
        //Find parent transaction and add a blacklist date to its transaction
        Income inp = _profile.GetParentIncomeFromTimeFrameIncome(income);
        inp.GetTimePeriod().AddBlacklistDate(income.GetTimePeriod().GetDate(), false);

        //Update database
        ProfileManager.InsertIncomeDatabase(_profile, inp, true);

        //Update income list
        _profile.CalculateTimeFrame();
        incomeAdapter.notifyDataSetChanged();

        //Update cost totals
        _profile.GetTotalIncomePerSourceInTimeFrame();
        totalsAdapter.notifyDataSetChanged();
    }

}
