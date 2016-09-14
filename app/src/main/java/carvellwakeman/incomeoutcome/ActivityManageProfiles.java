package carvellwakeman.incomeoutcome;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;
import org.joda.time.LocalDate;
import org.joda.time.Period;

public class ActivityManageProfiles extends AppCompatActivity {
    Boolean menustate = true;
    Profile editingprofile = null;

    LocalDate start_date;
    LocalDate end_date;
    Period period;

    AdapterManageProfiles adapter;

    AppBarLayout appBarLayout;
    Toolbar toolbar;
    MenuItem button_save;

    FloatingActionButton button_new;
    Button button_startdate;
    Button button_enddate;
    CheckBox checkbox_override_enddate;

    TextInputLayout TIL;
    EditText editText_profilename;
    EditText editText_period;

    Spinner spinner_period;

    LinearLayout layout_edit;

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
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);
        AppBarLayoutExpanded(false);


        button_new = (FloatingActionButton) findViewById(R.id.FAB_dialogmpr_new);
        button_startdate = (Button) findViewById(R.id.button_dialogmpr_startdate);
        button_enddate = (Button) findViewById(R.id.button_dialogmpr_enddate);

        checkbox_override_enddate = (CheckBox) findViewById(R.id.checkbox_override_enddate);

        layout_edit = (LinearLayout) findViewById(R.id.linearLayout_dialogmpr_editprofile);

        editText_period = (EditText) findViewById(R.id.editText_profile_period);

        spinner_period = (Spinner) findViewById(R.id.spinner_profile_period);

        recyclerView_profiles = (RecyclerView) findViewById(R.id.recyclerView_dialogmpr_profiles);

        //Calculate the default period
        CalculatePeriod();

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
        ArrayAdapter arrAd = ArrayAdapter.createFromResource(this, R.array.period_array, R.layout.spinner_dropdown_title_white);
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


        //Title input
        TIL = (TextInputLayout)findViewById(R.id.TIL_dialogmpr_profilename);
        if (TIL != null) {
            TIL.setErrorEnabled(true);
            editText_profilename = TIL.getEditText();
        }

        //Configure toolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.inflateMenu(R.menu.toolbar_menu_save);
        toolbar.setTitle(R.string.title_manageprofiles);
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
                    if (!ProfileManager.getInstance().HasProfile(str)) {
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
                //Visibility
                button_enddate.setVisibility(View.GONE);
            }
        });

        //Hide floating action button when recyclerView is scrolled
        recyclerView_profiles.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 10 && button_new.isShown()) { button_new.hide(); }
                else if (dy < 0 && !button_new.isShown()){button_new.show(); }
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

        //End Date override
        checkbox_override_enddate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                button_enddate.setVisibility( (b ? View.VISIBLE : View.GONE) );
            }
        });

        ClearAddMenu();

    }


    @Override
    public void onBackPressed()
    {
        if (menustate){ super.onBackPressed(); }
        else { ClearAddMenu(); ToggleMenus(true); }
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
        switch (item.getItemId()) {
            case android.R.id.home:
                if (menustate) { finish(); }
                else {
                    ClearAddMenu();
                    ToggleMenus(true);
                }
                return true;
            case R.id.toolbar_save: //SAVE button
                if (start_date == null) { start_date = new LocalDate(); }

                if (!editText_profilename.getText().toString().equals("")) {
                    if (editingprofile != null) {//Make changes to an existing profile
                        //Name
                        editingprofile.SetName(editText_profilename.getText().toString());
                        //Date
                        editingprofile.SetStartTimeDontSave(start_date);
                        editingprofile.SetEndTimeDontSave(null);
                        editingprofile.SetPeriodDontSave(period);
                        if (end_date != null) { editingprofile.SetEndTimeDontSave(end_date); }
                        //Update
                        ProfileManager.getInstance().UpdateProfile(editingprofile);
                    }
                    else { //Add a new profile
                        //Create profile
                        Profile pr = new Profile(editText_profilename.getText().toString());
                        //Date
                        pr.SetStartTimeDontSave(start_date);
                        pr.SetEndTimeDontSave(null);
                        pr.SetPeriodDontSave(period);
                        if (end_date != null) { pr.SetEndTimeDontSave(end_date); }
                        //Add new
                        ProfileManager.getInstance().AddProfile(pr);
                        ProfileManager.getInstance().SelectProfile(pr);
                    }

                    adapter.notifyDataSetChanged();

                    ProfileManager.hideSoftKeyboard(this, editText_profilename);
                    ClearAddMenu();
                    ToggleMenus(true);
                }
                return true;
            default:
                return false;
        }
    }

    //Update positive button text
    public void SetSaveButtonEnabled(Boolean enabled){
        if (button_save != null) {
            button_save.setEnabled(enabled);
            if (button_save.getIcon() != null) button_save.getIcon().setAlpha((enabled ? 255 : 130));
        }
    }

    //Edit profile
    public void EditProfile(final String id, DialogFragmentManagePPC dialogFragment){
        Profile pr = ProfileManager.getInstance().GetProfileByID(Integer.valueOf(id));
        if (pr != null) {
            editingprofile = pr;

            //Open add new layout
            ToggleMenus(false);

            //Set title to edit
            toolbar.setTitle(R.string.title_editprofile);


            //Load profile information into add new profile settings
            editText_profilename.setText(pr.GetName());
            start_date = pr.GetStartTime();
            //end_date = pr.GetEndTime();
            period = pr.GetPeriod();
            UpdateDates();
            UpdatePeriod();

            //Visibility
            //button_enddate.setVisibility(View.VISIBLE);

            //Dismiss dialogfragment
            dialogFragment.dismiss();
        }
    }

    //Select profile
    public void SelectProfile(String id, final DialogFragmentManagePPC dialogFragment){
        Profile pr = ProfileManager.getInstance().GetProfileByID(Integer.valueOf(id));
        if (pr != null) {
            if (!ProfileManager.getInstance().SelectProfile(pr)) {ProfileManager.PrintUser(this, "Selected Profile could not be found.");}
            adapter.notifyDataSetChanged();
            dialogFragment.dismiss();
        }
    }

    //Delete profile
    public void RemoveProfile(String id, final DialogFragmentManagePPC dialogFragment){
        final Profile pr = ProfileManager.getInstance().GetProfileByID(Integer.valueOf(id));
        if (pr != null) {
            if (pr.GetTransactionsSize() > 0) {
                ProfileManager.OpenDialogFragment(this, DialogFragmentTransferTransaction.newInstance(this, pr), true); //TODO: Handle mIsLargeDisplay
            }
            else {
                new AlertDialog.Builder(this).setTitle(R.string.confirm_areyousure_deletesingle)
                        .setPositiveButton(R.string.action_deleteitem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ProfileManager.getInstance().RemoveProfile(pr);
                                adapter.notifyDataSetChanged();
                                dialogFragment.dismiss();
                                dialog.dismiss();
                            }})
                        .setNegativeButton(R.string.action_cancel, null).create().show();

            }
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
            if ( (name.equals("")) || (!name.equals("") && ProfileManager.getInstance().HasProfile(name))) {
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
        if (editingprofile != null) {
            Period pe = editingprofile.GetPeriod();

            int YEARS = pe.getYears();
            int MONTHS = pe.getMonths();
            int WEEKS = pe.getWeeks();
            int DAYS = pe.getDays();

            editText_period.setText(String.valueOf(Math.max(Math.max(YEARS, Math.max(MONTHS, Math.max(WEEKS, DAYS))), 1)));
            int index = (DAYS > 0 ? 0 : WEEKS > 0 ? 1 : MONTHS > 0 ? 2 : YEARS > 0 ? 3 : 0);
            spinner_period.setSelection(index);
        }
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
            //Set startdate
            start_date = new LocalDate(year, monthOfYear + 1, dayOfMonth);
            UpdateDates();
            CheckCanSave();
        }
    };
    DatePickerDialog.OnDateSetListener datePicker2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            //Set end date
            end_date = new LocalDate(year, monthOfYear + 1, dayOfMonth);
            UpdateDates();
            CheckCanSave();
        }
    };


    //Expand and retract sub menus
    public void ToggleMenus(boolean editList){
        menustate = editList;

        //Enable edit layout
        //layout_edit.setVisibility( (edit ? View.VISIBLE : View.GONE) );
        //Disable add layout
        //layout_add.setVisibility( (edit ? View.GONE : View.VISIBLE) );
        AppBarLayoutExpanded(!editList);

        //Enable add new button
        button_new.setVisibility( (editList ? View.VISIBLE : View.GONE) );
        //Disable save button
        button_save.setVisible(!editList);
        //Set title
        toolbar.setTitle( (editList ? R.string.title_manageprofiles : R.string.title_addnewprofile) );
        //Set back button
        //toolbar.setNavigationIcon( (edit ? R.drawable.ic_clear_white_24dp : R.drawable.ic_arrow_back_white_24dp) );
    }

    //Clear add profile menu
    public void ClearAddMenu(){
        start_date = null;
        end_date = null;
        period = null;

        UpdateDates();

        checkbox_override_enddate.setChecked(false);

        editText_profilename.setText("");
        editText_period.setText("1");
        spinner_period.setSelection(2);

        editingprofile = null;
    }

    public void AppBarLayoutExpanded(boolean expanded){
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
        lp.height = (expanded ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) getResources().getDimension(R.dimen.toolbar_size));
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