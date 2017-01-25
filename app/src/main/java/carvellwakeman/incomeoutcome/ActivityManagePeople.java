package carvellwakeman.incomeoutcome;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.Map;

public class ActivityManagePeople extends AppCompatActivity {
    Boolean menustate = true;
    String editingPerson;

    AdapterManagePeople adapter;

    AppBarLayout appBarLayout;
    android.support.v7.widget.Toolbar toolbar;
    MenuItem button_save;

    FloatingActionButton button_new;
    
    TextInputLayout TIL;
    EditText editText_name;

    LinearLayout layout_edit;

    NpaLinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;


    public ActivityManagePeople() {}


    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managepeople);
        //public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.activity_managecategories, container, false);
        //view.setBackgroundColor(Color.WHITE);

        editingPerson = "";

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);
        AppBarLayoutExpanded(false);

        button_new = (FloatingActionButton) findViewById(R.id.FAB_dialogmp_new);


        layout_edit = (LinearLayout) findViewById(R.id.linearLayout_dialog_editperson);

        recyclerView = (RecyclerView) findViewById(R.id.dialog_recyclerView_people);

        TIL = (TextInputLayout) findViewById(R.id.TIL_dialog_personname);


        //Configure toolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menustate) { finish(); }
                else { ToggleMenus(true); }
            }
        });
        toolbar.inflateMenu(R.menu.toolbar_menu_save);
        toolbar.setTitle(R.string.title_editperson);
        setSupportActionBar(toolbar);


        TIL.setErrorEnabled(true);
        editText_name = TIL.getEditText();

        editText_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = editText_name.getText().toString();

                if (!str.equals("")) {
                    if (!OtherPersonManager.getInstance().HasOtherPerson(str)) {
                        SetSaveButtonEnabled(true);
                        TIL.setError("");
                    }
                    else{ SetSaveButtonEnabled(false); TIL.setError("Person already exists"); } //TODO: Put in strings
                }
                else{ SetSaveButtonEnabled(false); TIL.setError("Enter a name"); } //TODO: Put in strings
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });



        //Set adapter
        adapter = new AdapterManagePeople(this);
        recyclerView.setAdapter(adapter);

        //LinearLayoutManager for RecyclerView
        linearLayoutManager = new NpaLinearLayoutManager(this);
        linearLayoutManager.setOrientation(NpaLinearLayoutManager.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(linearLayoutManager);


        //Button listeners
        button_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleMenus(false);
            }
        });
        //Hide floating action button when recyclerView is scrolled
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 10 && button_new.isShown()) { button_new.hide(); }
                else if (dy < 0 && !button_new.isShown()){button_new.show(); }
            }
        });
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
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (menustate) { finish(); }
                else { ClearAddMenu(); ToggleMenus(true); }
                return true;
            case R.id.toolbar_save: //SAVE button
                String newPerson = editText_name.getText().toString();

                if (!newPerson.equals("")) {
                    //Add new
                    OtherPersonManager.getInstance().AddOtherPerson(newPerson);
                    DatabaseManager.getInstance().insertSetting(newPerson, true);

                    //Remove old person if editing
                    if (editingPerson != null && !editingPerson.equals("")){
                        OtherPersonManager.getInstance().RemoveOtherPerson(editingPerson);
                        DatabaseManager.getInstance().removePersonSetting(editingPerson);
                    }

                    //Update budget transactions
                    for (Budget budget : BudgetManager.getInstance().GetBudgets()) {
                        for (new_Transaction transaction : budget.GetTransactions(new_Transaction.TRANSACTION_TYPE.Expense)){

                            //Update split to new person
                            for (Map.Entry<String,Double> split : transaction.GetSplitArray().entrySet()){
                                if (split.getKey().equals(editingPerson)) {
                                    Helper.Print(this, "Update " + transaction.GetSource() + " Split " + split.getKey() + "(" + String.valueOf(split.getValue()) + ")" + " TO " + newPerson);
                                    transaction.SetSplit(newPerson, split.getValue());
                                    transaction.RemoveSplit(editingPerson);
                                    Helper.Print(this, "Updated TO: " + transaction.GetSplitArrayString());
                                }
                            }
                            if (transaction.GetPaidBy().equals(editingPerson)){
                                Helper.Print(this, "Update " + transaction.GetSource() + " PaidBy " + transaction.GetPaidBy() + " TO " + newPerson);
                                transaction.SetPaidBy(newPerson);
                                Helper.Print(this, "Updated TO: " + transaction.GetPaidBy());
                            }

                            //Update database
                            DatabaseManager.getInstance().insert(budget, transaction, true);
                            Helper.Print(this, "Database entry updated for: " + transaction.GetSource());
                        }
                    }

                    adapter.notifyDataSetChanged();

                    ClearAddMenu();
                    ToggleMenus(true);
                    Helper.hideSoftKeyboard(this, editText_name);
                }
                return true;
            default:
                return false;
        }
    }

    //Edit profile
    public void EditPerson(String name, final DialogFragmentManagePPC dialogFragment){
        editingPerson = name;

        //Open add new layout
        ToggleMenus(false);
        //Set title to edit
        toolbar.setTitle(R.string.title_editperson);

        //Load information
        editText_name.setText(name);

        //Dismiss fragment
        dialogFragment.dismiss();
    }

    public void DeletePerson(final String name, final DialogFragmentManagePPC dialogFragment){
        if (name != null) {
            new AlertDialog.Builder(this).setTitle(R.string.confirm_areyousure_deletesingle)
                    .setPositiveButton(R.string.action_deleteitem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            OtherPersonManager.getInstance().RemoveOtherPerson(name);
                            DatabaseManager.getInstance().removePersonSetting(name);

                            adapter.notifyDataSetChanged();
                            dialogFragment.dismiss();
                            dialog.dismiss();
                        }})
                    .setNegativeButton(R.string.action_cancel, null).create().show();

        }
    }

    //Update positive button text
    public void SetSaveButtonEnabled(Boolean enabled){
        if (button_save != null) {
            button_save.setEnabled(enabled);
            if (button_save.getIcon() != null) button_save.getIcon().setAlpha((enabled ? 255 : 130));
        }
    }


    //Expand and retract sub menus
    public void ToggleMenus(Boolean edit){
        menustate = edit;

        //Enable edit layout
        //layout_edit.setVisibility( (edit ? View.VISIBLE : View.GONE) );
        //Disable add layout
        //TIL.setVisibility( (edit ? View.GONE : View.VISIBLE) );
        AppBarLayoutExpanded(!edit);

        //Enable add new button
        button_new.setVisibility( (edit ? View.VISIBLE : View.GONE) );
        //Disable save button
        button_save.setVisible(!edit);
        //Set title
        toolbar.setTitle( (edit ? R.string.title_managepeople : R.string.title_addnewperson) );
        //Set back button
        //toolbar.setNavigationIcon( (edit ? R.drawable.ic_clear_white_24dp : R.drawable.ic_arrow_back_white_24dp) );
    }

    public void ClearAddMenu(){
        editingPerson = "";

        editText_name.setText("");
    }


    public void AppBarLayoutExpanded(boolean expanded){
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
        lp.height = (expanded ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) getResources().getDimension(R.dimen.toolbar_size));
    }
}