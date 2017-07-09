package carvellwakeman.incomeoutcome;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import org.joda.time.LocalDate;
import org.joda.time.Period;

public class ActivityManageBudgets extends ActivityManageEntity<Budget> {

    Period period;

    EditText editText_period;

    Spinner spinner_period;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_managebudgets);
        super.onCreate(savedInstanceState);


        editText_period = (EditText) findViewById(R.id.editText_profile_period);

        spinner_period = (Spinner) findViewById(R.id.spinner_profile_period);


        //Calculate the default period
        CalculatePeriod();

        //Period type multiplier min value
        editText_period.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CalculatePeriod();
                SetSaveButtonEnabled(CanSave());
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
                SetSaveButtonEnabled(CanSave());
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        spinner_period.setSelection(2); // Months default

        toolbar.setTitle(R.string.title_managebudgets);

        //Set profiles adapter
        adapter = new AdapterManageBudgets(this);
        recyclerView.setAdapter(adapter);

    }


    //Check if the user is allowed to save
    @Override
    public boolean CanSave() {
        return super.CanSave() || (editingEntity != null && !editingEntity.GetPeriod().equals(period));
    }


    // Get category
    @Override
    public Budget GetEntity(){
        return BudgetManager.getInstance().GetBudget(editText_name.getText().toString());
    }

    //Edit budget
    @Override
    public void EditEntity(final Integer id, DialogFragmentManagePPC dialogFragment){
        Budget br = BudgetManager.getInstance().GetBudget(id);
        if (br != null) {
            menuState = MENU_STATE.EDIT;

            editingEntity = br;

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

    //Delete budget
    @Override
    public void DeleteEntity(Integer id, final DialogFragmentManagePPC dialogFragment){
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
                                for (Transaction t : br.GetAllTransactions()){ DatabaseManager.getInstance().remove(t); }
                                DatabaseManager.getInstance().removeBudgetSetting(br);
                                BudgetManager.getInstance().RemoveBudget(br);
                                //Select a new budget if the one being deleted was the selected one
                                if (br.GetSelected() && BudgetManager.getInstance().GetBudgetCount() > 0) { BudgetManager.getInstance().SetSelectedBudget(BudgetManager.getInstance().GetBudgets().get(0)); }

                                adapter.notifyDataSetChanged();

                                dialogFragment.dismiss();
                                dialog.dismiss();
                            }})
                        .setNegativeButton(R.string.action_cancel, null).create().show();

            }


        }
    }

    //Select budget (for return from activity)
    @Override
    public void SelectEntity(Budget budget){
        Intent intent = new Intent();
        intent.putExtra("entity", budget.GetID());
        setResult(1, intent);
        finish();
    }

    // Select budget (budget.Selected)
    public void SelectBudget(Integer id, final DialogFragmentManagePPC dialogFragment){
        Budget budget = BudgetManager.getInstance().GetBudget(id);
        if (budget != null) {
            // Set selected budget
            BudgetManager.getInstance().SetSelectedBudget(budget);

            // Updated other budges (they are not selected anymore)
            for (Budget b : BudgetManager.getInstance().GetBudgets()){
                DatabaseManager.getInstance().insertSetting(b, true);
            }

            adapter.notifyDataSetChanged();

            if (dialogFragment != null) { dialogFragment.dismiss(); }
        }
    }

    // Save budget
    @Override
    public void SaveAction(){
        String newBudget = editText_name.getText().toString();

        if (!newBudget.equals("")) {
            //New
            if (editingEntity == null){
                //Create budget
                editingEntity = new Budget(newBudget);
                //Date and Period
                editingEntity.SetStartDate(new LocalDate());
                editingEntity.SetEndDate(null);
                editingEntity.SetPeriod(period);
                editingEntity.MoveTimePeriod(0);
                //Active if it's the first
                if (BudgetManager.getInstance().GetBudgetCount() == 0){
                    editingEntity.SetSelected(true);
                }
            } else { //Update
                //Name
                editingEntity.SetName(newBudget);
                //Period
                editingEntity.SetPeriod(period);
            }

            //Add or update old budget
            BudgetManager.getInstance().AddBudget(editingEntity);
            DatabaseManager.getInstance().insertSetting(editingEntity, true);
        }

    }


    //Expand and retract sub menus
    @Override
    public void OpenEditMenu(){
        super.OpenEditMenu();

        //Set title
        toolbar.setTitle( R.string.title_editbudget );
    }

    @Override
    public void OpenAddMenu(){
        super.OpenAddMenu();

        //Set title
        toolbar.setTitle( R.string.title_addnewbudget );
    }

    @Override
    public void CloseSubMenus(){
        super.CloseSubMenus();

        //Set title
        toolbar.setTitle( R.string.title_managebudgets );
    }

    @Override
    public void ClearSubMenuData(){
        super.ClearSubMenuData();
        period = null;

        editText_period.setText("1");
        spinner_period.setSelection(2);
    }


    //Update period
    public void UpdatePeriod(){
        if (editingEntity != null) {
            Period pe = editingEntity.GetPeriod();
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
}