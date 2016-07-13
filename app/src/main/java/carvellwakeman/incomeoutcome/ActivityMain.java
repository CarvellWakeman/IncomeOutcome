package carvellwakeman.incomeoutcome;


import android.app.DatePickerDialog;
//import android.content.DialogInterface;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import org.joda.time.LocalDate;

import android.support.v4.app.NotificationCompat;

//import org.joda.time.LocalDate;



public class ActivityMain extends AppCompatActivity
{


    Toolbar toolbar;

    private AdapterProfilesSpinner profile_adapter;

    Boolean FABMenuOpen;
    FloatingActionButton addNew;
    FloatingActionButton fab_newExpense;
    FloatingActionButton fab_newIncome;
    FloatingActionButton fab_newPerson;
    TextView textView_newExpense;
    TextView textView_newIncome;
    TextView textView_newPerson;

    Spinner spinner_profiles;

    Button button_setStartDate;
    Button button_setEndDate;

    TextView textView_startDate;
    TextView textView_endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the profile manager
        ProfileManager.initialize(this);

        //Default values
        FABMenuOpen = false;


        Income in1 = new Income();
        in1.SetValue(10.0);
        in1.SetStatic(true);
        //p1.AddIncome(in1);


        Expense ex1 = new Expense();
        //ex1.SetTimePeriod(new TimePeriod(LocalDate.now()));
        ex1.SetCategory("Snacks");
        ex1.SetSplitValue("Sabrina", 10.0);
        ex1.SetValue(20.0);

        TimePeriod tp = new TimePeriod();
        tp.SetDate(new LocalDate(2016, 3, 14));

        tp.SetRepeatFrequency(Repeat.WEEKLY);
        tp.SetDayOfWeek(0,true);
        tp.SetRepeatEveryN(1);
        tp.SetRepeatUntil(RepeatUntil.FOREVER);
        tp.SetRepeatUntilDate(null);
        tp.SetRepeatANumberOfTimes(1);
        //tp.SetRepeatANumberOfTimes(2);
        ex1.SetTimePeriod(tp);

        //p1.AddExpense(ex1);


        Expense ex2 = new Expense();
        ex2.SetTimePeriod(new TimePeriod(LocalDate.now()));
        ex2.SetCategory("Rent");
        ex2.SetSplitValue("Sabrina", 5.0);
        ex2.SetValue(10.0);
        //ex2.SetDescription("This is a very long and tedious description of exactly what I purchased on the date listed above. Does this description run across the rsoient fort of the sun? Will I ever do so much to look like as I am? How are they? Yes!");
        //p1.AddExpense(ex2);

        Expense ex3 = new Expense();
        ex3.SetValue(713.26);
        ex3.SetSourceName("Insurance #3");
        ex3.SetCategory("Groceries");
        ex3.SetDescription("This is a very long and tedious description of exactly what I purchased on the date listed above. Does this description run across the rsoient fort of the sun? Will I ever do so much to look like as I am? How are they? Yes!");
        ex3.SetSplitValue("John", 204.0);
        //p1.AddExpense(ex3);



        //Find views
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //income = (LinearLayout) findViewById(R.id.main_income);
        //recyclerView = (RecyclerView) findViewById(R.id.recyclerView_Tabs);
        addNew = (FloatingActionButton) findViewById(R.id.FAB_main_new);
        fab_newExpense = (FloatingActionButton) findViewById(R.id.FAB_main_newExpense);
        fab_newIncome = (FloatingActionButton) findViewById(R.id.FAB_main_newIncome);
        fab_newPerson = (FloatingActionButton) findViewById(R.id.FAB_main_manageProfile);

        textView_newExpense = (TextView) findViewById(R.id.textView_newExpense);
        textView_newIncome = (TextView) findViewById(R.id.textView_newIncome);
        textView_newPerson = (TextView) findViewById(R.id.textView_manageProfile);

        spinner_profiles = (Spinner) findViewById(R.id.spinner_profiles);

        button_setStartDate = (Button) findViewById(R.id.button_timeframe_startDate);
        button_setEndDate = (Button) findViewById(R.id.button_timeframe_endDate);

        textView_startDate = (TextView) findViewById(R.id.textView_startDate);
        textView_endDate = (TextView) findViewById(R.id.textView_endDate);

        //Toolbar setup (action menu)
        toolbar.inflateMenu(R.menu.toolbar_menu_main);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //setActionBar(toolbar);


        //Floating action button setup
        //addNew.setShadowRadius(4);
        //addNew.setHideAnimation(ActionButton.Animations.JUMP_TO_DOWN);
        //addNew.setShowAnimation(ActionButton.Animations.JUMP_FROM_DOWN);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FABMenuOpen) {
                    OpenFABMenu();
                } else {
                    CloseFABMenu();
                }
            }
        });

        fab_newExpense.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        fab_newExpense.setRippleColor(Color.GRAY);
        fab_newIncome.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        fab_newIncome.setRippleColor(Color.GRAY);
        fab_newPerson.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        fab_newPerson.setRippleColor(Color.GRAY);


        //Profiles Spinner Setup
        profile_adapter = new AdapterProfilesSpinner(this, R.layout.spinner_dropdown_title_white, ProfileManager.GetProfileNames());
        //profile_adapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_title_white, ProfileManager.GetProfileNames());
        profile_adapter.setDropDownViewResource(R.layout.spinner_dropdown_list_primary);
        spinner_profiles.setAdapter(profile_adapter);
        //OnClick Listener
        spinner_profiles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object si = spinner_profiles.getSelectedItem();
                if (si != null) {
                    if (ProfileManager.SelectProfile(si.toString())){
                        UpdateStartEndDate();
                        //profile_adapter.notifyDataSetChanged();
                    }
                    else{
                        Toast.makeText(ActivityMain.this, "Selected Profile could not be found.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Select current profile
        spinner_profiles.setSelection(ProfileManager.GetProfileIndex(ProfileManager.GetCurrentProfile()));


        final Profile pr = ProfileManager.GetCurrentProfile();

        //UpdateStartEndDate();

        button_setStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open date picker dialog
                LocalDate c = null;
                if (pr != null) { c = pr.GetStartTime(); }
                if (c == null) { c = new LocalDate(); }

                DatePickerDialog d = new DatePickerDialog(ActivityMain.this, datePicker, c.getYear(), c.getMonthOfYear() - 1, c.getDayOfMonth());
                d.show();
            }
        });

        button_setEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open date picker dialog
                LocalDate c = null;
                if (pr != null) { c = pr.GetEndTime(); }
                if (c == null) { c = new LocalDate(); }

                DatePickerDialog d = new DatePickerDialog(ActivityMain.this, datePicker2, c.getYear(), c.getMonthOfYear() - 1, c.getDayOfMonth());
                d.show();
            }
        });



        Button button_db = (Button)findViewById(R.id.button_db);
        if (button_db != null) {
            button_db.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent dbmanager = new Intent(ActivityMain.this, AndroidDatabaseManager.class);
                    startActivity(dbmanager);
                }
            });
        }

        //Hide floating action button when recyclerView is scrolled
        //recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        //    @Override
        //    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        //        super.onScrolled(recyclerView, dx, dy);
        //        if (dy > 10 && addNew.isShown()) { addNew.hide(); }
        //        else if (dy < 0 && addNew.isHidden()){addNew.show(); }
        //    }
        //});


        //Sort
        //Manager.Sort(Manager.SORT_METHODS.DATE_UP);

        //Permissions
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }


    //Temp
    //public void deleteDB(View v){
    //    ProfileManager.DeleteDatabase();
    //    ProfileManager.ClearAllObjects();
    //    UpdateProfileList(true);
    //}

    //Context menu
    public void showContextMenu(final View itemView)
    {
        //String items[] = {"Edit", "Delete"};

/*
        new AlertDialog.Builder(MainActivity.this)
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item)
                        {
                            case 0: //Edit
                                Intent intent = new Intent(MainActivity.this, NewTabActivity.class);
                                intent.putExtra("tab", TabManager.GetTab(recyclerView.getChildAdapterPosition(itemView)).toString());
                                startActivityForResult(intent, 1);
                                break;
                            case 1: //Delete
                                //Discard dialog
                                final Dialog discardDialog = new Dialog(MainActivity.this);
                                discardDialog.setContentView(R.layout.dialog_discard);
                                discardDialog.setTitle("Discard Tab?");

                                //Find components
                                final Button DiscardButton = (Button) discardDialog.findViewById(R.id.discard_button_discard);
                                final Button cancelButton = (Button) discardDialog.findViewById(R.id.discard_button_cancel);

                                //DiscardButton action
                                DiscardButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //Dimiss dialog
                                        discardDialog.dismiss();

                                        TabManager.DeleteTabByID(TabManager.GetTab(recyclerView.getChildAdapterPosition(itemView)).toString()); //Delete tab
                                        adapter.notifyItemRemoved(recyclerView.getChildAdapterPosition(itemView));
                                        TabManager.Sort(TabManager.SORT_METHODS.DATE_UP);
                                    }
                                });

                                //CancelButton action
                                cancelButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        discardDialog.dismiss();
                                    }
                                });


                                //Show the discard dialog
                                discardDialog.show();
                                break;
                            default:
                                dialog.cancel();
                                break;
                        }
                    }
                }).create().show();
                */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu_main, menu);
        return true;
    }

    //Toolbar button handling
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //super.onOptionsItemSelected(item);
        Boolean mIsLargeLayout = true; //TODO Handle this in values

        switch (item.getItemId())
        {
            case R.id.toolbar_action_settings: //Start settings activity
                Intent intent = new Intent(ActivityMain.this, ActivitySettings.class);
                //intent.putExtra("profile", pr.GetID());
                startActivityForResult(intent, 3);
                return true;
            /*
            case R.id.toolbar_action_manageprofiles:
                ProfileManager.OpenDialogFragment(this, new DialogFragmentManageProfiles(), mIsLargeLayout);
                return true;

            case R.id.toolbar_action_managepeople:
                ProfileManager.OpenDialogFragment(this, new DialogFragmentManagePeople(), mIsLargeLayout);
                return true;

            case R.id.toolbar_action_managecategories:
                ProfileManager.OpenDialogFragment(this, new DialogFragmentManageCategories(), mIsLargeLayout);

                return true;
            case R.id.toolbar_action_exportdatabase:
                ProfileManager.OpenDialogFragment(this, new DialogFragmentDatabaseExport(), mIsLargeLayout);
                return true;
            case R.id.toolbar_action_importdatabase:
                ProfileManager.OpenDialogFragment(this, new DialogFragmentDatabaseImport(), mIsLargeLayout);
                return true;
            */
            default:
                return false;
        }
    }


    //Return results from child activities
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Profile pr = null;
        if (data != null) {
            pr = ProfileManager.GetProfileByID(data.getIntExtra("profile", -1));
        }

        switch (requestCode) {
            case 0: //New expense
                if (resultCode == RESULT_OK) {
                    if (pr != null && data != null) {
                        Expense newExp = (Expense) data.getSerializableExtra("newExpense");

                        if (newExp != null) {
                            pr.AddExpense(newExp);
                        }
                    }
                }
                break;
            case 1: //New Income
                if (resultCode == RESULT_OK) {
                    if (pr != null && data != null) {
                        Income newInc = (Income) data.getSerializableExtra("newIncome");

                        if (newInc != null) {
                            pr.AddIncome(newInc);
                        }
                    }
                }
                break;
            case 3: //Refresh info
                UpdateStartEndDate();
                break;
        }
    }


    //Date picker dialog listener [DEBUG] Temporary for testing
    final DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            Profile pr = ProfileManager.GetCurrentProfile();
            if (pr != null) {
                //Set date
                pr.SetStartTime(new LocalDate(year, monthOfYear + 1, dayOfMonth));

                UpdateStartEndDate();

                //Update profile
                //ProfileManager.UpdateProfile(pr);
            }
        }
    };
    final DatePickerDialog.OnDateSetListener datePicker2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            Profile pr = ProfileManager.GetCurrentProfile();
            if (pr != null) {
                //Set date
                pr.SetEndTime(new LocalDate(year, monthOfYear + 1, dayOfMonth));

                UpdateStartEndDate();

                //Update profile
                //ProfileManager.UpdateProfile(pr);
            }
        }
    };

    public void UpdateStartEndDate(){
        Profile pr = ProfileManager.GetCurrentProfile();
        if (pr != null) {
            //Format button text
            if(textView_startDate != null) {
                if (pr.GetStartTime() != null) {
                    textView_startDate.setText(getString(R.string.time_start_format, pr.GetStartTime().toString(ProfileManager.simpleDateFormat)));
                }
                else {
                    textView_startDate.setText(R.string.time_start);
                }
            }
            if(textView_endDate != null) {
                if (pr.GetEndTime() != null) {
                    textView_endDate.setText(getString(R.string.time_end_format, pr.GetEndTime().toString(ProfileManager.simpleDateFormat)));
                }
                else {
                    textView_endDate.setText(R.string.time_end);
                }
            }
        }
    }

    public void UpdateProfileList(boolean selectCurrentProfile){
        if (spinner_profiles != null) {
            //Profile selected = ProfileManager.GetProfileByIndex(spinner_profiles.getSelectedItemPosition());

            //Profiles Spinner Setup
            //profile_adapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_title_white, ProfileManager.GetProfileNames());
            //profile_adapter.setDropDownViewResource(R.layout.spinner_dropdown_list_primary);
            //spinner_profiles.setAdapter(profile_adapter);
            profile_adapter.notifyDataSetChanged();

            //Select current profile
            //if (selectCurrentProfile) { spinner_profiles.setSelection(ProfileManager.GetProfileIndex(ProfileManager.GetCurrentProfile())); }
        }
    }
    public void SetSelection(int index){
        if (spinner_profiles != null){
            profile_adapter.notifyDataSetChanged();

            spinner_profiles.setSelection(index);
        }
    }



    public void NextMonth(View v){
        Profile pr = ProfileManager.GetCurrentProfile();
        if (pr != null){
            pr.TimePeriodPlus(1);
            UpdateStartEndDate();
        }

    }
    public void PrevMonth(View v){
        Profile pr = ProfileManager.GetCurrentProfile();
        if (pr != null) {
            pr.TimePeriodMinus(1);
            UpdateStartEndDate();
        }
    }
    public void ShowAll(View v){
        Profile pr = ProfileManager.GetCurrentProfile();
        if (pr != null) {
            pr.SetStartTime(null);
            pr.SetEndTime(null);
            UpdateStartEndDate();
            pr.CalculateTimeFrame();
        }
    }

    public int dptopx(int dp)
    {
        final float scale = ActivityMain.this.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    //FAB Menu
    public void OpenFABMenu()
    {
        //ProfileManager.scheduleNotification(ProfileManager.getNotification("Test Notification"), 1000);

        FABMenuOpen = true;
        RotateAnimation rot = new RotateAnimation(0, 45, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rot.setDuration(200);
        rot.setFillAfter(true);
        addNew.startAnimation(rot);

        Animation FABEntry = AnimationUtils.loadAnimation(ActivityMain.this, R.anim.fab_entry);

        fab_newExpense.startAnimation(FABEntry);
        fab_newExpense.setVisibility(View.VISIBLE);
        fab_newIncome.startAnimation(FABEntry);
        fab_newIncome.setVisibility(View.VISIBLE);
        fab_newPerson.startAnimation(FABEntry);
        //fab_newPerson.setVisibility(View.VISIBLE);

        textView_newExpense.setVisibility(View.VISIBLE);
        textView_newExpense.startAnimation(FABEntry);
        textView_newIncome.setVisibility(View.VISIBLE);
        textView_newIncome.startAnimation(FABEntry);
        //textView_newPerson.setVisibility(View.VISIBLE);
        //textView_newPerson.startAnimation(FABEntry);
    }
    public void CloseFABMenu()
    {
        FABMenuOpen = false;
        RotateAnimation rot = new RotateAnimation(45, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rot.setDuration(200);
        rot.setFillAfter(true);
        addNew.startAnimation(rot);

        Animation FABExit = AnimationUtils.loadAnimation(ActivityMain.this, R.anim.fab_exit);

        fab_newExpense.startAnimation(FABExit);
        fab_newExpense.setVisibility(View.INVISIBLE);
        fab_newIncome.startAnimation(FABExit);
        fab_newIncome.setVisibility(View.INVISIBLE);
        fab_newPerson.startAnimation(FABExit);
        //fab_newPerson.setVisibility(View.INVISIBLE);

        textView_newExpense.setVisibility(View.INVISIBLE);
        textView_newExpense.startAnimation(FABExit);
        textView_newIncome.setVisibility(View.INVISIBLE);
        textView_newIncome.startAnimation(FABExit);
        //textView_newPerson.setVisibility(View.INVISIBLE);
        //textView_newPerson.startAnimation(FABExit);
    }


    //Buttons
    //public void MainDefaultSettings(View v){
    //    ProfileManager.LoadDefaultSettings();
    //}
    public void MainIncomeClick(View v)
    {
        /*
        total += 1;
        income.setWeightSum(total);

        ImageButton b = new ImageButton(MainActivity.this);
        b.setBackgroundResource(R.drawable.white_background);
        b.setBackgroundColor(Color.BLUE);


        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(dptopx(20), LinearLayout.LayoutParams.WRAP_CONTENT);
        p.weight = 1;
        p.setMargins(dptopx(10), dptopx(3), dptopx(10), dptopx(3));

        b.setLayoutParams(p);


        income.addView(b);
        */
        Profile pr = ProfileManager.GetCurrentProfile();
        if (pr != null) {
            pr.CalculateTimeFrame();//Update timeframes

            //Start income (details) activity and send it the profile we clicked on
            Intent intent = new Intent(ActivityMain.this, ActivityDetailsIncome.class);
            intent.putExtra("profile", pr.GetID());
            startActivityForResult(intent, 3);
        }
        else {
            ProfileManager.Print("ERROR: Profile not found, could not start IncomeActivity");
        }
    }
    public void MainExpenseClick(View v)
    {
        Profile pr = ProfileManager.GetCurrentProfile();
        if (pr != null) {
            pr.CalculateTimeFrame();//Update timeframes

            //Start expense (details) activity and send it the profile we clicked on
            Intent intent = new Intent(ActivityMain.this, ActivityDetailsExpense.class);
            intent.putExtra("profile", pr.GetID());
            startActivityForResult(intent, 3);
        }
        else {
            ProfileManager.Print("ERROR: Profile not found, could not start ExpenseActivity");
        }
    }


    public void MainNewExpenseClick(View v){
        Profile pr = ProfileManager.GetCurrentProfile();
        if (pr != null) {
            pr.CalculateTimeFrame();
            Intent intent = new Intent(ActivityMain.this, ActivityNewExpense.class);
            intent.putExtra("profile", pr.GetID());
            startActivityForResult(intent, 0);
            CloseFABMenu();
        }
        else {
            ProfileManager.Print("ERROR: Profile not found, could not start NewExpenseActivity");
        }
    }

    public void MainNewIncomeClick(View v){
        Profile pr = ProfileManager.GetCurrentProfile();
        if (pr != null) {
            Intent intent = new Intent(ActivityMain.this, ActivityNewIncome.class);
            intent.putExtra("profile", pr.GetID());
            startActivityForResult(intent, 1);
            CloseFABMenu();
        }
        else {
            ProfileManager.Print("ERROR: Profile not found, could not start NewIncomeActivity");
        }
    }

    /*
    public void MainImportBackup(View v){
        new AlertDialog.Builder(this).setTitle(R.string.confirm_areyousure_deleteall)
            .setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ProfileManager.ImportDatabaseBackup();
                }})
            .setNegativeButton(R.string.confirm_no, null)
            .create().show();
    }
    */

}
