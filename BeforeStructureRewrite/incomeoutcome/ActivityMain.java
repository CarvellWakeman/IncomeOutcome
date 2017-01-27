package carvellwakeman.incomeoutcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;


public class ActivityMain extends AppCompatActivity
{
    ProfileManager profileManager;

    ArrayList<Integer> toolbar_menus;

    Toolbar toolbar;

    int _profileID;
    Profile _profile;

    CardVersus versusCard;
    CardTransaction expensesCard;
    CardTransaction incomeCard;

    Button button_suggestaddprofile;

    RelativeLayout relativeLayout_period;
    ImageView button_nextPeriod;
    ImageView button_prevPeriod;
    CheckBox checkbox_showall;

    LinearLayout progress_loadingData;

    ProfileManager.CallBack sortFilterCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the profile manager
        profileManager = ProfileManager.getInstance();
        profileManager.initialize(this, new ProfileManager.CallBack() {
            @Override public void call() {
                if (versusCard!=null){ versusCard.getBase().setVisibility(View.VISIBLE); }
                if (incomeCard!=null){ incomeCard.getBase().setVisibility(View.VISIBLE); }
                if (expensesCard!=null){ expensesCard.getBase().setVisibility(View.VISIBLE); }
                if (progress_loadingData!=null){ progress_loadingData.setVisibility(View.GONE); }
                if (relativeLayout_period!=null){ relativeLayout_period.setVisibility(View.VISIBLE); }

                RefreshOverview();
            }
        });

        //Sort filter callback
        sortFilterCallBack = new ProfileManager.CallBack() { @Override public void call() {
            RefreshOverview();
        }};

        //Toolbar menus
        toolbar_menus = new ArrayList<>();
        toolbar_menus.add(R.menu.submenu_settings);
        toolbar_menus.add(R.menu.submenu_filter_expense);
        toolbar_menus.add(R.menu.submenu_paidback);

        //Set our activity's data
        _profile = profileManager.GetCurrentProfile();
        if (_profile != null) {
            _profileID = _profile.GetID();
        }
        //Find Views
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        button_suggestaddprofile = (Button) findViewById(R.id.button_suggest_addprofile);

        button_suggestaddprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityMain.this, ActivityManageProfiles.class));
            }
        });

        //Period management
        relativeLayout_period = (RelativeLayout) findViewById(R.id.relativeLayout_toolbarPeriod);
        relativeLayout_period.setVisibility(View.GONE);
        button_nextPeriod = (ImageView) findViewById(R.id.button_nextPeriod);
        button_prevPeriod = (ImageView) findViewById(R.id.button_prevPeriod);
        checkbox_showall = (CheckBox) findViewById(R.id.checkbox_showall);

        //Loading
        progress_loadingData = (LinearLayout) findViewById(R.id.progress_database_loading);

        button_nextPeriod.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (_profile != null){
                    checkbox_showall.setChecked(false);
                    _profile.TimePeriodPlus(ActivityMain.this, 1);
                    RefreshOverview();
                }
            }
        });
        button_prevPeriod.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (_profile != null){
                    checkbox_showall.setChecked(false);
                    _profile.TimePeriodMinus(ActivityMain.this, 1);
                    RefreshOverview();
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
                    RefreshOverview();
                }
            }
        });


        //Configure toolbar
        //toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        //toolbar.setNavigationOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { onBackPressed(); } });
        for(int m : toolbar_menus){ toolbar.inflateMenu(m); }
        setSupportActionBar(toolbar);
        //ToolbarTitleUpdate();


        //Card inflater
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.overview_layout);


        //Cards
        versusCard = new CardVersus(insertPoint, 0, _profileID, this, inflater, R.layout.card_versus);
        expensesCard = new CardTransaction(insertPoint, 1, _profileID, 0, 2, getString(R.string.header_expenses_summary), this, inflater, R.layout.card_transaction);
        incomeCard = new CardTransaction(insertPoint, 2, _profileID, 1, 1, getString(R.string.header_income_summary), this, inflater, R.layout.card_transaction);

        versusCard.getBase().setVisibility(View.GONE);
        expensesCard.getBase().setVisibility(View.GONE);
        incomeCard.getBase().setVisibility(View.GONE);

        //versusCard.insert(insertPoint, 0);
        //expensesCard.insert(insertPoint, 1);
        //incomeCard.insert(insertPoint, 2);

        //Ask for permissions
        //ProfileManager.OpenDialogFragment(this, DialogFragmentPermissionReasoning.newInstance(this, new int[]{ R.string.tt_permission_writestorage1 }, new int[]{ R.string.tt_permission_writestorage2 }, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }), true);

        //Check app version for update, display changelog
        if (!App.GetVersion(this).equals(App.GetLastVersion())){
            App.SetLastVersion(this);
            ProfileManager.OpenDialogFragment(this, DialogFragmentChangelog.newInstance(), true);
        }

    }

    @Override
    public void onStart(){
        super.onStart();
        
        //RefreshOverview();
    }

    @Override
    public void onResume(){
        super.onResume();

        if (_profile != null) {
            checkbox_showall.setChecked(_profile.GetShowAll());

            RefreshOverview();
        }

        //Sort and filter bubbles
        if (_profile != null) {
            SortFilterOptions.DisplayFilter(this, _profile.GetFilterMethod(), _profile.GetFilterData(), sortFilterCallBack);
            SortFilterOptions.DisplaySort(this, ProfileManager.SORT_METHODS.DATE_DOWN, sortFilterCallBack); //Sorting is irrelevant on main activity
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        for(int m : toolbar_menus){ getMenuInflater().inflate(m, menu); }

        return true;
    }


    //Toolbar button handling
    Intent intent;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {

            case android.R.id.home:
                intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                return true;
            case R.id.toolbar_action_settings: //Start settings activity
                intent = new Intent(ActivityMain.this, ActivitySettings.class);
                startActivityForResult(intent, 0);
                return true;
            case R.id.toolbar_paidback: //Expense only
                ProfileManager.OpenDialogFragment(ActivityMain.this, DialogFragmentPaidBack.newInstance(ActivityMain.this, new ProfileManager.CallBack() { @Override public void call() {
                    RefreshOverview();
                }}, _profile), true);
                return true;
        }

        SortFilterOptions.Run(this, _profile, item, sortFilterCallBack);

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
        if (data != null) {}

        //Refresh graphs if we return to this page. Not the most efficient, but simple
        RefreshOverview();
    }


    //Refresh overview
    public void RefreshOverview(){
        //Suggest the user add a profile if none exist
        if (profileManager.GetCurrentProfile()==null){
            versusCard.getBase().setVisibility(View.GONE);
            expensesCard.getBase().setVisibility(View.GONE);
            incomeCard.getBase().setVisibility(View.GONE);
            button_suggestaddprofile.setVisibility(View.VISIBLE);
            progress_loadingData.setVisibility(View.GONE);
            relativeLayout_period.setVisibility(View.GONE);
        }
        else {
            versusCard.getBase().setVisibility(View.VISIBLE);
            expensesCard.getBase().setVisibility(View.VISIBLE);
            incomeCard.getBase().setVisibility(View.VISIBLE);
            button_suggestaddprofile.setVisibility(View.GONE);
            relativeLayout_period.setVisibility(View.VISIBLE);
        }


        _profile = profileManager.GetCurrentProfile();
        if (_profile != null) {
            _profileID = _profile.GetID();

            versusCard.SetProfileID(_profileID);
            expensesCard.SetProfileID(_profileID);
            incomeCard.SetProfileID(_profileID);

            versusCard.SetData();
            expensesCard.SetData();
            incomeCard.SetData();
        }

        ToolbarTitleUpdate();
    }

    //Toolbar title update
    public void ToolbarTitleUpdate(){
        if (getSupportActionBar() != null) {
            if (_profile != null) {
                getSupportActionBar().setTitle(_profile.GetName() + " " + getString(R.string.title_overview));
                getSupportActionBar().setSubtitle(_profile.GetDateFormatted());
            }
            else {
                getSupportActionBar().setTitle(R.string.title_overview);
                getSupportActionBar().setSubtitle("");
            }
        }
    }

}