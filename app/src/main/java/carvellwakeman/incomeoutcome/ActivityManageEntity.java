package carvellwakeman.incomeoutcome;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
import android.widget.LinearLayout;

public abstract class ActivityManageEntity<T extends BaseEntity> extends AppCompatActivity{

     public enum MENU_STATE
    {
        SELECT,
        EDIT,
        ADD,
        ADDNEW,
        VIEW,
        ADDNEWSELECT // Special case for adding an entity and then selecting one
    }

    MENU_STATE menuState = MENU_STATE.VIEW;
    T editingEntity;

    AdapterManageEntity adapter;

    AppBarLayout appBarLayout;
    android.support.v7.widget.Toolbar toolbar;
    MenuItem button_save;

    FloatingActionButton button_new;

    TextInputLayout TIL;
    EditText editText_name;

    LinearLayout edit_layout;

    NpaLinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;


    public ActivityManageEntity() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);

        button_new = (FloatingActionButton) findViewById(R.id.FAB_AME);

        edit_layout = (LinearLayout) findViewById(R.id.linearLayout_edit_AME);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_AME);

        TIL = (TextInputLayout) findViewById(R.id.TIL_name_AME);
        editText_name = TIL.getEditText();


        // Close menus
        AppBarLayoutExpanded(false);

        // Configure toolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.inflateMenu(R.menu.toolbar_menu_save);
        setSupportActionBar(toolbar);

        // Edit name field
        TIL.setErrorEnabled(true);

        editText_name.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = editText_name.getText().toString();

                if (str.equals("")) {
                    TIL.setError(getString(R.string.tt_enter_title));
                }
                else{
                    if (GetEntity() == null) {
                        TIL.setError("");
                    } else {
                        TIL.setError(getString(R.string.tt_already_exists));
                    }
                }

                SetSaveButtonEnabled(CanSave());
            }

            @Override public void afterTextChanged(Editable s) {}
        });


        //Set adapter (Done by children)
        //adapter = new AdapterManageEntity(this);
        //recyclerView.setAdapter(adapter);

        //LinearLayoutManager for RecyclerView
        linearLayoutManager = new NpaLinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);


        //Button listeners
        button_new.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // If select state, new state should be addselect
                if (menuState == MENU_STATE.SELECT){
                    menuState = MENU_STATE.ADDNEWSELECT;
                } else {
                    menuState = MENU_STATE.ADD;
                }

                OpenAddMenu();
            }
        });

        // Hide add button when recyclerView is scrolled
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 10 && button_new.isShown()) { button_new.hide(); }
                else if (dy < 0 && !button_new.isShown() && menuState != MENU_STATE.ADDNEW && menuState != MENU_STATE.SELECT){button_new.show(); }
            }
        });

        Intent intent = getIntent();
        if (intent.getBooleanExtra("addnew", false)){
            menuState = MENU_STATE.ADDNEW;
            edit_layout.setVisibility(View.GONE);
        }
        if (intent.getBooleanExtra("select", false)){
            if (menuState == MENU_STATE.ADDNEW){
                menuState = MENU_STATE.ADDNEWSELECT;
            } else {
                menuState = MENU_STATE.SELECT;
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_save, menu);
        button_save = menu.findItem(R.id.toolbar_save);
        button_save.setVisible(false);

        // Open menus as necessary
        if (menuState == MENU_STATE.ADDNEW || menuState == MENU_STATE.ADDNEWSELECT){
            OpenAddMenu();
        }
        if (menuState == MENU_STATE.SELECT){
            OpenSelectMode();
        }
        return true;
    }

    @Override
    public void onBackPressed() { BackAction(); }

    // Toolbar button handling
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home: // Back Button
                BackAction();
                return true;
            case R.id.toolbar_save: // SAVE button
                SaveAction();

                // Add new and then select short circuit
                if (menuState == MENU_STATE.ADDNEWSELECT){
                    menuState = MENU_STATE.SELECT;

                    edit_layout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);

                    CloseSubMenus();
                    OpenSelectMode();

                    return true;
                }

                // Add New short circuit
                if (menuState == MENU_STATE.ADDNEW) {
                    Intent intent = new Intent();
                    intent.putExtra("entity", editingEntity.GetID());
                    setResult(1, intent);
                    finish();
                }

                adapter.notifyDataSetChanged();
                BackAction();
                return true;
            default:
                return false;
        }
    }


    // Check if the user is allowed to save
    public boolean CanSave(){
        String str = editText_name.getText().toString();
        return !str.equals("") && GetEntity() == null;
    }


    // Update positive button text
    public void SetSaveButtonEnabled(boolean enabled){
        if (button_save != null) {
            button_save.setEnabled(enabled);
            //if (button_save.getIcon() != null) button_save.getIcon().setAlpha((enabled ? 255 : 130)); // Done in selector and styles now
        }
    }


    // Get entity
    public abstract T GetEntity();

    // Edit entity
    public abstract void EditEntity(final Integer id, DialogFragmentManagePPC dialogFragment);

    // Delete entity
    public abstract void DeleteEntity(final Integer id, final DialogFragmentManagePPC dialogFragment);

    // Select entity
    public abstract void SelectEntity(T entity);

    // Save entity
    public void SaveAction(){
        ClearSubMenuData();
    }


    // Back button / toolbar back button action
    public void BackAction(){
        ClearSubMenuData();

        if (menuState == MENU_STATE.ADD | menuState == MENU_STATE.EDIT) {
            menuState = MENU_STATE.VIEW;
            CloseSubMenus();
        } else {
            finish();
        }
    }


    // Expand and retract sub menus
    public void OpenEditMenu(){
        AppBarLayoutExpanded(true);

        //Show add new button
        button_new.setVisibility( View.VISIBLE );

        //Show save button
        button_save.setVisible(true);
    }

    public void OpenAddMenu(){
        AppBarLayoutExpanded(true);

        //Focus on text input
        editText_name.requestFocus();
        Helper.showSoftKeyboard(this, editText_name);

        //Hide add new button
        button_new.setVisibility( View.GONE );

        //Show save button
        button_save.setVisible(true);
    }

    public void OpenSelectMode(){
        //Hide add new button
        //button_new.setVisibility(View.GONE);

        //Hide save button
        button_save.setVisible(false);
    }

    public void CloseSubMenus(){
        AppBarLayoutExpanded(false);

        //Show add new button
        button_new.setVisibility( View.VISIBLE );

        //Hide save button
        button_save.setVisible(false);

        //Hide soft keyboard
        Helper.hideSoftKeyboard(this, editText_name);
    }

    public void ClearSubMenuData(){
        editingEntity = null;
        editText_name.setText("");
    }

    // Sub menu app bar
    public void AppBarLayoutExpanded(boolean expanded){
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
        lp.height = (expanded ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) getResources().getDimension(R.dimen.toolbar_size));
    }

}
