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

public class ActivityManageProfiles extends AppCompatActivity {
    Boolean menustate = true;
    Boolean editstate = false;
    Profile editingprofile = null;

    LocalDate start_date;
    LocalDate end_date;

    AdapterManageProfiles adapter;

    Toolbar toolbar;
    MenuItem button_save;

    FloatingActionButton button_new;
    Button button_startdate;
    Button button_enddate;

    TextInputLayout TIL;
    EditText editText_profilename;

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

        recyclerView_profiles = (RecyclerView) findViewById(R.id.recyclerView_dialogmpr_profiles);


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
                        SetSaveButtonEnabled(true);
                        TIL.setError("");
                    }
                    else{ SetSaveButtonEnabled(false); TIL.setError("Profile already exists"); }
                }
                else{ SetSaveButtonEnabled(false); TIL.setError("Enter a title"); }
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


        // Inflate the layout to use as dialog or embedded fragment
        //return view;
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
                            editingprofile.SetStartTime(start_date, true);
                            editingprofile.SetEndTime(end_date, true);
                            editingprofile.SetName(editText_profilename.getText().toString());

                            ProfileManager.UpdateProfile(editingprofile);

                            finish();
                        }
                    }
                }
                else{ //Add a new profile
                    if (!editText_profilename.getText().toString().equals("")){
                        Profile pr = new Profile(editText_profilename.getText().toString());
                        pr.SetStartTime(start_date, true);
                        pr.SetEndTime(end_date, true);

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
            UpdateDates();
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
        }else {
            button_enddate.setText(R.string.time_end);
        }

        //Save button state
        if (editingprofile != null){
            if (editingprofile.GetStartTime() != null && start_date != null) {
                if (editingprofile.GetStartTime().compareTo(start_date) != 0) { SetSaveButtonEnabled(true); }
            }
            if (editingprofile.GetEndTime() != null && end_date != null) {
                if (editingprofile.GetEndTime().compareTo(end_date) != 0) { SetSaveButtonEnabled(true); }
            }
        }

    }


    //Date pickers
    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            //Set date
            start_date = new LocalDate(year, monthOfYear + 1, dayOfMonth);
            UpdateDates();
        }
    };
    DatePickerDialog.OnDateSetListener datePicker2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            //Set date
            end_date = new LocalDate(year, monthOfYear + 1, dayOfMonth);
            UpdateDates();
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

        UpdateDates();

        editText_profilename.setText("");
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