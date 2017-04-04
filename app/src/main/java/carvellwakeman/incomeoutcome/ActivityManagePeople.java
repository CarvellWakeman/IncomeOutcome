package carvellwakeman.incomeoutcome;


import android.content.DialogInterface;
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

public class ActivityManagePeople extends AppCompatActivity {
    Boolean menustate = true;
    Person editingPerson;

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);
        AppBarLayoutExpanded(false);

        button_new = (FloatingActionButton) findViewById(R.id.FAB_dialogmp_new);


        layout_edit = (LinearLayout) findViewById(R.id.linearLayout_dialog_editperson);

        recyclerView = (RecyclerView) findViewById(R.id.dialog_recyclerView_people);

        TIL = (TextInputLayout) findViewById(R.id.TIL_dialog_personname);


        //Configure toolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        //toolbar.setNavigationOnClickListener(new View.OnClickListener() { //Back button while editing closes editing menu (Does not exit activity)
        //    @Override  public void onClick(View v) { BackAction(); }
        //});
        toolbar.inflateMenu(R.menu.toolbar_menu_save);
        toolbar.setTitle(R.string.title_managepeople);
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
                    if (PersonManager.getInstance().GetPerson(str) == null) {
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
            public void onClick(View v) { OpenAddMenu(); }
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
        switch (item.getItemId())
        {
            case android.R.id.home: //Back Button
                BackAction();
                return true;
            case R.id.toolbar_save: //SAVE button
                String newPerson = editText_name.getText().toString();

                if (!newPerson.equals("")) {
                    //New
                    if (editingPerson == null){
                        editingPerson = new Person(newPerson);
                    } else { //Update
                        editingPerson.SetName(newPerson);
                    }

                    //Add or update old person
                    PersonManager.getInstance().AddPerson(editingPerson);
                    DatabaseManager.getInstance().insertSetting(editingPerson, true);

                    adapter.notifyDataSetChanged();

                    CloseSubMenus();
                }
                return true;
            default:
                return false;
        }
    }

    //Edit profile
    public void EditPerson(Integer id, final DialogFragmentManagePPC dialogFragment){

        editingPerson = PersonManager.getInstance().GetPerson(id);
        if (editingPerson != null){
            //Open edit layout
            OpenEditMenu();

            //Load information
            editText_name.setText(editingPerson.GetName());

            //Dismiss fragment
            dialogFragment.dismiss();
        }
    }

    public void DeletePerson(final Integer id, final DialogFragmentManagePPC dialogFragment){
        if (id != 0) {
            new AlertDialog.Builder(this).setTitle(R.string.confirm_areyousure_deletesingle)
                    .setPositiveButton(R.string.action_deleteitem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Person person = PersonManager.getInstance().GetPerson(id);
                            PersonManager.getInstance().RemovePerson(person);
                            DatabaseManager.getInstance().removePersonSetting(person);

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
        toolbar.setTitle( R.string.title_editperson );
    }

    public void OpenAddMenu(){
        AppBarLayoutExpanded(true);

        //Clear old data
        ClearSubMenuData();

        //Focus on text input
        editText_name.requestFocus();
        Helper.showSoftKeyboard(ActivityManagePeople.this, editText_name);

        //Hide add new button
        button_new.setVisibility( View.GONE );

        //Show save button
        button_save.setVisible(true);

        //Set title
        toolbar.setTitle( R.string.title_addnewperson );
    }

    public void CloseSubMenus(){
        AppBarLayoutExpanded(false);

        //Show add new button
        button_new.setVisibility( View.VISIBLE );

        //Set title
        toolbar.setTitle( R.string.title_managepeople );

        //Hide save button
        button_save.setVisible(false);

        //Hide soft keyboard
        Helper.hideSoftKeyboard(ActivityManagePeople.this, editText_name);
    }

    public void ClearSubMenuData(){
        editingPerson = null;

        editText_name.setText("");
    }


    public void AppBarLayoutExpanded(boolean expanded){
        menustate = expanded;

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
        lp.height = (expanded ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) getResources().getDimension(R.dimen.toolbar_size));
    }
}