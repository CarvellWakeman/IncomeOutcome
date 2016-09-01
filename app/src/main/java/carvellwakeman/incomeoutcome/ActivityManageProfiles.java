package carvellwakeman.incomeoutcome;


import android.app.DatePickerDialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import org.joda.time.LocalDate;
import org.joda.time.Period;

public class ActivityManageProfiles extends AppCompatActivity {
    Boolean menustate = true;
    Boolean editstate = false;
    Profile editingprofile = null;

    LocalDate start_date;
    LocalDate end_date;
    Period period;

    AdapterManageProfiles adapter;
    AdapterProfilesSpinner profile_adapter;

    Toolbar toolbar;
    MenuItem button_save;

    FloatingActionButton button_new;
    Button button_startdate;
    Button button_enddate;

    TextInputLayout TIL;
    EditText editText_profilename;
    EditText editText_period;

    Spinner spinner_profiles;
    Spinner spinner_period;

    LinearLayout layout_edit;
    LinearLayout layout_add;

    NpaLinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView_profiles;

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manageprofiles);
        //view.setBackgroundColor(Color.WHITE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        button_new = (FloatingActionButton) findViewById(R.id.FAB_dialogmpr_new);
        button_startdate = (Button) findViewById(R.id.button_dialogmpr_startdate);
        button_enddate = (Button) findViewById(R.id.button_dialogmpr_enddate);

        layout_edit = (LinearLayout) findViewById(R.id.linearLayout_dialogmpr_editprofile);
        layout_add = (LinearLayout) findViewById(R.id.linearLayout_dialogmpr_newprofile);

        editText_period = (EditText) findViewById(R.id.editText_profile_period);

        spinner_period = (Spinner) findViewById(R.id.spinner_profile_period);

        recyclerView_profiles = (RecyclerView) findViewById(R.id.recyclerView_dialogmpr_profiles);

        //Period type multiplier min value
        editText_period.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CalculatePeriod();

                CheckCanSave();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        //Period type input
        ArrayAdapter arrAd = ArrayAdapter.createFromResource(this, R.array.period_array, R.layout.spinner_dropdown_title);
        arrAd.setDropDownViewResource(R.layout.spinner_dropdown_list_primary);
        spinner_period.setAdapter(arrAd);

        spinner_period.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CalculatePeriod();

                CheckCanSave();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });


        //Current profile selection
        spinner_profiles = (Spinner) findViewById(R.id.spinner_profiles);
        profile_adapter = new AdapterProfilesSpinner(this, R.layout.spinner_dropdown_title, ProfileManager.GetProfileNames());
        profile_adapter.setDropDownViewResource(R.layout.spinner_dropdown_list_primary);
        spinner_profiles.setAdapter(profile_adapter);
        spinner_profiles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object si = spinner_profiles.getSelectedItem();
                if (si != null) {
                    if (ProfileManager.SelectProfile(si.toString())){
                        //profile_adapter.notifyDataSetChanged();
                    }
                    else{
                        ProfileManager.Print("Selected Profile could not be found.");
                    }
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        //Select current profile
        spinner_profiles.setSelection(ProfileManager.GetProfileIndex(ProfileManager.GetCurrentProfile()));


        //Title input
        TIL = (TextInputLayout)findViewById(R.id.TIL_dialogmpr_profilename);
        if (TIL != null) {
            TIL.setErrorEnabled(true);
            editText_profilename = TIL.getEditText();
        }

        //Configure toolbar
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        toolbar.inflateMenu(R.menu.toolbar_menu_save);
        toolbar.setTitle(R.string.title_editprofile);
        setSupportActionBar(toolbar);
        //button_save = toolbar.getMenu().findItem(R.id.toolbar_save);
        //button_save.setVisible(false);




        //Set profiles adapter
        adapter = new AdapterManageProfiles(this);
        recyclerView_profiles.setAdapter(adapter);

        //LinearLayoutManager for RecyclerView
        linearLayoutManager = new NpaLinearLayoutManager(this);
        linearLayoutManager.setOrientation(NpaLinearLayoutManager.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        recyclerView_profiles.setLayoutManager(linearLayoutManager);


        editText_profilename.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = editText_profilename.getText().toString();
                if (!str.equals("")) {
                    if (!ProfileManager.HasProfile(str)) {
                        TIL.setError("");
                    }
                    else{ TIL.setError("Profile already exists"); }
                }
                else{ TIL.setError("Enter a title"); }

                CheckCanSave();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        //Button listeners
        button_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ProfileManager.Print("New Button Click");
                ToggleMenus(false);
            }
        });

        //Set start date
        button_startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open date picker dialog
                LocalDate c = null;
                if (start_date != null) { c = start_date; } else { c = new LocalDate(); }

                DatePickerDialog d = new DatePickerDialog(ActivityManageProfiles.this, datePicker, c.getYear(), c.getMonthOfYear() - 1, c.getDayOfMonth());
                d.show();
            }
        });
        //Set end date
        button_enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open date picker dialog
                LocalDate c = null;
                if (end_date != null) { c = end_date; } else { c = new LocalDate(); }

                DatePickerDialog d = new DatePickerDialog(ActivityManageProfiles.this, datePicker2, c.getYear(), c.getMonthOfYear() - 1, c.getDayOfMonth());
                d.show();
            }
        });

    }


    @Override
    public void onBackPressed()
    {
        if (menustate){ super.onBackPressed(); }
        else { ToggleMenus(true); }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_save, menu);
        button_save = menu.findItem(R.id.toolbar_save);
        button_save.setVisible(false);
        return true;
    }

    //Toolbar button handling
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (menustate) { finish(); }
                else { ToggleMenus(true); }
                return true;
            case R.id.toolbar_save: //SAVE button
                if (editstate) { //Make changes to an existing profile
                    if (editingprofile != null){

                        if (!editText_profilename.getText().toString().equals("")){
                            editingprofile.SetName(editText_profilename.getText().toString());

                            editingprofile.SetStartTimeDontSave(start_date);
                            editingprofile.SetEndTimeDontSave(end_date);
                            editingprofile.SetPeriodDontSave(period);

                            ProfileManager.UpdateProfile(editingprofile);

                            finish();
                        }
                    }
                }
                else{ //Add a new profile
                    if (!editText_profilename.getText().toString().equals("")){
                        Profile pr = new Profile(editText_profilename.getText().toString());
                        pr.SetStartTimeDontSave(start_date);
                        pr.SetEndTimeDontSave(end_date);
                        pr.SetPeriodDontSave(period);

                        ProfileManager.AddProfile(pr);

                        //Apparently updating the adapter from here doesn't work, so now I have to create a new one...
                        adapter.notifyDataSetChanged();

                        finish();
                    }
                }
                return true;
            default:
                return false;
        }
    }

    //Update positive button text
    public void SetSaveButtonEnabled(Boolean enabled){
        button_save.setEnabled(enabled);
        if (button_save.getIcon() != null) button_save.getIcon().setAlpha( (enabled ? 255 : 130) );
    }

    //Edit profile
    public void EditProfile(Profile pr){
        editingprofile = pr;
        //Open add new layout
        ToggleMenus(false);
        //Set title to edit
        toolbar.setTitle(R.string.title_editprofile);
        //Set edit state to true
        editstate = true;
        //Load profile information into add new profile settings
        if (pr != null) {
            editText_profilename.setText(pr.GetName());
            start_date = pr.GetStartTime();
            end_date = pr.GetEndTime();
            period = pr.GetPeriod();
            UpdateDates();
            UpdatePeriod();
        }
    }

    //Check if the user is allowed to save
    public void CheckCanSave()
    {
        String name = editText_profilename.getText().toString();

        if (editingprofile != null) {

            Period pe = editingprofile.GetPeriod();
            if (editingprofile.GetStartTime() != null && start_date != null && editingprofile.GetStartTime().compareTo(start_date) == 0 && //Check start date is same
                    editingprofile.GetEndTime() != null && end_date != null && editingprofile.GetEndTime().compareTo(end_date) == 0 && //Check end date is same
                    pe.equals(period) && //Check period is same
                    ((name.equals("")) || (!name.equals("") && editingprofile.GetName().equals(name)))) {
                SetSaveButtonEnabled(false);
            }
            else { SetSaveButtonEnabled(true); }
        }
        else {
            if ( (name.equals("")) || (!name.equals("") && ProfileManager.HasProfile(name))) {
                SetSaveButtonEnabled(false);
            }
            else { SetSaveButtonEnabled(true); }
        }
    }

    //Update dates
    public void UpdateDates(){
        if (start_date != null) {
            button_startdate.setText(getString(R.string.time_start_format, start_date.toString(ProfileManager.simpleDateFormat)));
        } else {
            button_startdate.setText(R.string.time_start);
        }
        if (end_date != null) {
            button_enddate.setText(getString(R.string.time_end_format, end_date.toString(ProfileManager.simpleDateFormat)));
        } else {
            button_enddate.setText(R.string.time_end);
        }
    }

    //Update period
    public void UpdatePeriod(){
        Period pe = editingprofile.GetPeriod();

        int YEARS = pe.getYears();
        int MONTHS = pe.getMonths();
        int WEEKS = pe.getWeeks();
        int DAYS = pe.getDays();

        editText_period.setText( String.valueOf( Math.max( Math.max(YEARS, Math.max(MONTHS, Math.max(WEEKS, DAYS) ) ), 1) ) );
        int index = (DAYS>0 ? 0 : WEEKS>0 ? 1 : MONTHS>0 ? 2 : YEARS>0 ? 3 : 0);
        spinner_period.setSelection(index);
    }

    //Calculate period
    public void CalculatePeriod(){
        try {
            Integer val = Integer.valueOf(editText_period.getText().toString());
            if (val <= 0) { val = 1; }

            //Event Period setup
            int DAYS   = val * (spinner_period.getSelectedItemPosition()==0  ? 1 : 0);
            int WEEKS  = val * (spinner_period.getSelectedItemPosition()==1  ? 1 : 0);
            int MONTHS = val * (spinner_period.getSelectedItemPosition()==2  ? 1 : 0);
            int YEARS  = val * (spinner_period.getSelectedItemPosition()==3  ? 1 : 0);
            if (period == null) { period = new Period(0, 0, 0, 0, 0, 0, 0, 0); }
            period = period.withYears(YEARS).withMonths(MONTHS).withWeeks(WEEKS).withDays(DAYS);

        } catch (Exception e) { e.printStackTrace(); }
    }


    //Date pickers
    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            //Set date
            start_date = new LocalDate(year, monthOfYear + 1, dayOfMonth);
            UpdateDates();
            CheckCanSave();
        }
    };
    DatePickerDialog.OnDateSetListener datePicker2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            //Set date
            end_date = new LocalDate(year, monthOfYear + 1, dayOfMonth);
            UpdateDates();
            CheckCanSave();
        }
    };


    //Expand and retract sub menus
    public void ToggleMenus(Boolean edit){
        menustate = !menustate;

        ClearAddMenu();

        //Enable edit layout
        layout_edit.setVisibility( (edit ? View.VISIBLE : View.GONE) );
        //Disable add layout
        layout_add.setVisibility( (edit ? View.GONE : View.VISIBLE) );
        //Enable add new button
        button_new.setVisibility( (edit ? View.VISIBLE : View.GONE) );
        //Disable save button
        button_save.setVisible(!edit);
        //Set title
        toolbar.setTitle( (edit ? R.string.title_editprofile : R.string.title_addnewprofile) );
        //Set back button
        toolbar.setNavigationIcon( (edit ? R.drawable.ic_clear_white_24dp : R.drawable.ic_arrow_back_white_24dp) );
    }

    //Clear add profile menu
    public void ClearAddMenu(){
        start_date = null;
        end_date = null;
        period = null;

        UpdateDates();

        editText_profilename.setText("");
        editText_period.setText("1");
        spinner_period.setSelection(2);
    }


    /*
    // The system calls this only when creating the layout in a dialog.
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.setCanceledOnTouchOutside(false); //Disable closing dialog by clicking outside of it
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //dialog.getWindow().setBackgroundDrawable(null);
        }
    }
    */
}