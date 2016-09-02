package carvellwakeman.incomeoutcome;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.*;
import android.app.DialogFragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;

import static carvellwakeman.incomeoutcome.R.id.frameLayout;

public class ActivityManagePeople extends AppCompatActivity {
    Boolean menustate = true;
    String old_otherperson;

    AdapterManagePeople adapter;

    AppBarLayout appBarLayout;
    Toolbar toolbar;

    MenuItem button_save;

    FloatingActionButton button_new;

    TextInputLayout TIL;
    EditText editText_personname;

    LinearLayout layout_edit;

    NpaLinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managepeople);
        //public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.dialog_managepeople, container, false);
        //view.setBackgroundColor(Color.WHITE);


        toolbar = (Toolbar) findViewById(R.id.toolbar);

        appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);
        AppBarLayoutExpanded(false);

        //appBarLayout.setExpanded(false, false);
        //appBarLayout.setActivated(false);
        //CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
        //lp.height = (int) getResources().getDimension(R.dimen.toolbar_size);

        button_new = (FloatingActionButton) findViewById(R.id.FAB_dialogmp_new);

        layout_edit = (LinearLayout) findViewById(R.id.linearLayout_dialog_editperson);

        recyclerView = (RecyclerView) findViewById(R.id.dialog_recyclerView_people);

        TIL = (TextInputLayout)findViewById(R.id.TIL_dialog_personname);


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
        toolbar.setTitle(R.string.title_managepeople);
        setSupportActionBar(toolbar);
        //button_save = toolbar.getMenu().findItem(R.id.toolbar_save);
        //button_save.setVisible(false);


        TIL.setErrorEnabled(true);
        editText_personname = TIL.getEditText();

        editText_personname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = editText_personname.getText().toString();

                if (!str.equals("")) {
                    if (!ProfileManager.HasOtherPerson(str)) {
                        SetSaveButtonEnabled(true);
                        TIL.setError("");
                    }
                    else{ SetSaveButtonEnabled(false); TIL.setError("Person already exists"); }
                }
                else{ SetSaveButtonEnabled(false); TIL.setError("Enter a name"); }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        //Set profiles adapter
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
                String str = editText_personname.getText().toString();

                //Update other person
                ProfileManager.UpdateOtherPerson(old_otherperson, str);

                //Delete old person if they exist (For editing)
                ProfileManager.RemoveOtherPerson(old_otherperson);

                //Add new person (Edit or new)
                ProfileManager.AddOtherPerson(str);

                //Dismiss dialog
                finish();

                return true;
            default:
                return false;
        }
    }


    //Edit profile
    public void EditPerson(String name, final DialogFragmentManagePPC dialogFragment){
        old_otherperson = name;

        //Open add new layout
        ToggleMenus(false);
        //Set title to edit
        toolbar.setTitle(R.string.title_editperson);

        //Load information
        editText_personname.setText(name);

        //Dismiss fragment
        dialogFragment.dismiss();
    }

    public void DeletePerson(final String name, final DialogFragmentManagePPC dialogFragment){
        if (name != null) {
            new AlertDialog.Builder(this).setTitle(R.string.confirm_areyousure_deletesingle)
                    .setPositiveButton(R.string.action_deleteitem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProfileManager.RemoveOtherPerson(name);
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

        editText_personname.setText("");

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


    public void AppBarLayoutExpanded(boolean expanded){
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
        lp.height = (expanded ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) getResources().getDimension(R.dimen.toolbar_size));
    }


    /* The system calls this only when creating the layout in a dialog.
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