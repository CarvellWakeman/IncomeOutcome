package carvellwakeman.incomeoutcome;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.*;
import android.widget.*;
import org.joda.time.LocalDate;
import org.joda.time.Period;

public class ActivityManageBudgets extends AppCompatActivity {
    Boolean menustate = true;
    Budget editingBudget = null;

    Period period;

    AdapterManageBudgets adapter;

    AppBarLayout appBarLayout;
    Toolbar toolbar;
    MenuItem button_save;

    FloatingActionButton button_new;
    //Button button_startdate;
    //Button button_enddate;
    //CheckBox checkbox_override_enddate;

    TextInputLayout TIL;
    EditText editText_name;
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
        setContentView(R.layout.activity_managebudgets);
        //view.setBackgroundColor(Color.WHITE);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);
        AppBarLayoutExpanded(false);


        button_new = (FloatingActionButton) findViewById(R.id.FAB_dialogmpr_new);
        //button_startdate = (Button) findViewById(R.id.button_dialogmpr_startdate);
        //button_enddate = (Button) findViewById(R.id.button_dialogmpr_enddate);

        //checkbox_override_enddate = (CheckBox) findViewById(R.id.checkbox_override_enddate);

        layout_edit = (LinearLayout) findViewById(R.id.linearLayout_dialogmpr_editbudget);

        editText_period = (EditText) findViewById(R.id.editText_profile_period);

        spinner_period = (Spinner) findViewById(R.id.spinner_profile_period);

        recyclerView_profiles = (RecyclerView) findViewById(R.id.recyclerView_dialogmpr_budgets);

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
            editText_name = TIL.getEditText();
        }

        //Configure toolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.inflateMenu(R.menu.toolbar_menu_save);
        toolbar.setTitle(R.string.title_managebudgets);
        setSupportActionBar(toolbar);
        //button_save = toolbar.getMenu().findItem(R.id.toolbar_save);
        //button_save.setVisible(false);


        //Set profiles adapter
        adapter = new AdapterManageBudgets(this);
        recyclerView_profiles.setAdapter(adapter);

        //LinearLayoutManager for RecyclerView
        linearLayoutManager = new NpaLinearLayoutManager(this);
        recyclerView_profiles.setLayoutManager(linearLayoutManager);


        editText_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = editText_name.getText().toString();

                if (!str.equals("")) {
                    if (BudgetManager.getInstance().GetBudget(str) == null) {
                        TIL.setError("");
                    }
                    else{ TIL.setError("Budget already exists"); }
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
                OpenAddMenu();
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

    }

    //Option to automatically open the add menu
    @Override public void onWindowFocusChanged(boolean hasFocus){
        Intent intent = getIntent();
        if (intent.getBooleanExtra("addnew", false)){ OpenAddMenu(); }
    }


    @Override
    public void onBackPressed() { BackAction(); }

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
            case android.R.id.home: //Back button
                BackAction();
                return true;
            case R.id.toolbar_save: //SAVE button

                String newBudget = editText_name.getText().toString();

                if (!newBudget.equals("")) {
                    //New
                    if (editingBudget == null){
                        //Create budget
                        editingBudget = new Budget(newBudget);
                        //Date and Period
                        editingBudget.SetStartDate(new LocalDate());
                        editingBudget.SetEndDate(null);
                        editingBudget.SetPeriod(period);
                    } else { //Update
                        //Name
                        editingBudget.SetName(newBudget);
                        //Period
                        editingBudget.SetPeriod(period);
                    }

                    //Add or update old person
                    BudgetManager.getInstance().AddBudget(editingBudget);
                    DatabaseManager.getInstance().insertSetting(editingBudget, true);

                    adapter.notifyDataSetChanged();

                   CloseSubMenus();
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

    //Edit budget
    public void EditBudget(final Integer id, DialogFragmentManagePPC dialogFragment){
        Budget br = BudgetManager.getInstance().GetBudget(id);
        if (br != null) {
            editingBudget = br;

            //Open add new layout
            OpenEditMenu();

            //Set title to edit
            toolbar.setTitle(R.string.title_editbudget);


            //Load profile information into add new profile settings
            editText_name.setText(br.GetName());
            //start_date = br.GetStartDate();
            //end_date = pr.GetEndTime();
            period = br.GetPeriod();
            UpdatePeriod();

            //Disiable save button
            SetSaveButtonEnabled(false);

            //Dismiss dialogfragment
            dialogFragment.dismiss();
        }
    }

    //Select profile
    public void SelectBudget(Integer id, final DialogFragmentManagePPC dialogFragment){
        Budget br = BudgetManager.getInstance().GetBudget(id);
        if (br != null) {
            BudgetManager.getInstance().SetSelectedBudget(br);
            for (Budget b : BudgetManager.getInstance().GetBudgets()){
                DatabaseManager.getInstance().insertSetting(b, true);
            }

            adapter.notifyDataSetChanged();
            if (dialogFragment != null) { dialogFragment.dismiss(); }
        }
    }

    //Delete budget
    public void RemoveBudget(Integer id, final DialogFragmentManagePPC dialogFragment){
        final Budget br = BudgetManager.getInstance().GetBudget(id);
        if (br != null) {
            if (br.GetTransactionCount() > 0 && BudgetManager.getInstance().GetBudgetCount() > 1) {
                Helper.OpenDialogFragment(this, DialogFragmentTransferTransaction.newInstance(this, dialogFragment, br), true); //TODO: Handle mIsLargeDisplay
            }
            else {
                new AlertDialog.Builder(this).setTitle(R.string.confirm_areyousure_deletetransactions)
                        .setPositiveButton(R.string.action_deleteitem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (new_Transaction t : br.GetAllTransactions()){ DatabaseManager.getInstance().remove(t); }
                                DatabaseManager.getInstance().removeBudgetSetting(br);
                                BudgetManager.getInstance().RemoveBudget(br);
                                //Select a new budget if the one being deleted was the selected one
                                if (br.GetSelected()) { BudgetManager.getInstance().SetSelectedBudget(BudgetManager.getInstance().GetBudgets().get(0)); }

                                adapter.notifyDataSetChanged();

                                dialogFragment.dismiss();
                                dialog.dismiss();
                            }})
                        .setNegativeButton(R.string.action_cancel, null).create().show();

            }


        }
    }

    //Check if the user is allowed to save
    public void CheckCanSave() {
        String name = editText_name.getText().toString();

        if (name.equals("")) {
            SetSaveButtonEnabled(false);
        } else {
            if (BudgetManager.getInstance().GetBudget(name) != null) {
                Period pe = editingBudget.GetPeriod();
                SetSaveButtonEnabled(!pe.equals(period)); //Check if color is different
            } else {
                SetSaveButtonEnabled(true);
            }
        }
    }



    //Update period
    public void UpdatePeriod(){
        if (editingBudget != null) {
            Period pe = editingBudget.GetPeriod();
            if (pe != null) {
                int YEARS = pe.getYears();
                int MONTHS = pe.getMonths();
                int WEEKS = pe.getWeeks();
                int DAYS = pe.getDays();

                editText_period.setText(String.valueOf(Math.max(Math.max(YEARS, Math.max(MONTHS, Math.max(WEEKS, DAYS))), 1)));
                int index = (DAYS > 0 ? 0 : WEEKS > 0 ? 1 : MONTHS > 0 ? 2 : YEARS > 0 ? 3 : 0);
                spinner_period.setSelection(index);
            }
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

    //Back button / toolbar back button action
    public void BackAction(){
        if (!menustate) { finish(); }
        else { CloseSubMenus(); }
    }


    //Expand and retract sub menus
    public void OpenEditMenu(){
        AppBarLayoutExpanded(true);

        //Show add new button
        button_new.setVisibility( View.VISIBLE );

        //Show save button
        button_save.setVisible(true);

        //Set title
        toolbar.setTitle( R.string.title_editbudget );
    }

    public void OpenAddMenu(){
        AppBarLayoutExpanded(true);

        //Clear old data
        ClearSubMenuData();

        //Focus on text input
        editText_name.requestFocus();
        Helper.showSoftKeyboard(ActivityManageBudgets.this, editText_name);

        //Hide add new button
        button_new.setVisibility( View.GONE );

        //Show save button
        button_save.setVisible(true);

        //Set title
        toolbar.setTitle( R.string.title_addnewbudget );
    }

    public void CloseSubMenus(){
        AppBarLayoutExpanded(false);

        //Show add new button
        button_new.setVisibility( View.VISIBLE );

        //Set title
        toolbar.setTitle( R.string.title_managebudgets );

        //Hide save button
        button_save.setVisible(false);

        //Hide soft keyboard
        Helper.hideSoftKeyboard(ActivityManageBudgets.this, editText_name);
    }

    public void ClearSubMenuData(){
        period = null;

        editText_name.setText("");
        editText_period.setText("1");
        spinner_period.setSelection(2);

        editingBudget = null;
    }

    public void AppBarLayoutExpanded(boolean expanded){
        menustate = expanded;

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