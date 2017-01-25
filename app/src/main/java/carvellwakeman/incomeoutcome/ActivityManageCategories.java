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

public class ActivityManageCategories extends AppCompatActivity {
    Boolean menustate = true;
    Category editingCategory;

    AdapterManageCategories adapter;

    AppBarLayout appBarLayout;
    android.support.v7.widget.Toolbar toolbar;
    MenuItem button_save;

    FloatingActionButton button_new;


    DiscreteSeekBar seekBar_red;
    DiscreteSeekBar seekBar_green;
    DiscreteSeekBar seekBar_blue;

    ImageView imageView_colorindicator;
    TextInputLayout TIL;
    EditText editText_name;

    LinearLayout layout_edit;
    //LinearLayout layout_add;

    NpaLinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;


    public ActivityManageCategories() {}


    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managecategories);
        //public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.activity_managecategories, container, false);
        //view.setBackgroundColor(Color.WHITE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);
        AppBarLayoutExpanded(false);

        button_new = (FloatingActionButton) findViewById(R.id.FAB_dialogmc_new);

        seekBar_red = (DiscreteSeekBar) findViewById(R.id.seekBar_dialogcat_red);
        seekBar_green = (DiscreteSeekBar) findViewById(R.id.seekBar_dialogcat_green);
        seekBar_blue = (DiscreteSeekBar) findViewById(R.id.seekBar_dialogcat_blue);

        imageView_colorindicator = (ImageView) findViewById(R.id.imageView_dialogcat);

        layout_edit = (LinearLayout) findViewById(R.id.linearLayout_dialog_editcategory);
        //layout_add = (LinearLayout) findViewById(R.id.linearLayout_dialog_newcategory);

        recyclerView = (RecyclerView) findViewById(R.id.dialog_recyclerView_categories);

        TIL = (TextInputLayout) findViewById(R.id.TIL_dialog_categoryname);


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
        toolbar.setTitle(R.string.title_editcategory);
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
                    if (CategoryManager.getInstance().GetCategory(str) == null) {
                        CheckCanSave();
                        TIL.setError("");
                    }
                    else{ CheckCanSave(); TIL.setError("Category already exists"); }
                }
                else{ CheckCanSave(); TIL.setError("Enter a title"); }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        //Color bars
        seekBar_red.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                SetIndicatorColor(GetSeekbarColor());
            }
            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                CheckCanSave();
            }
        });
        seekBar_green.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                SetIndicatorColor(GetSeekbarColor());
            }
            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                CheckCanSave();
            }
        });
        seekBar_blue.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                SetIndicatorColor(GetSeekbarColor());
            }
            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                CheckCanSave();
            }
        });


        //Set adapter
        adapter = new AdapterManageCategories(this);
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


        LayoutInflater inflater = getLayoutInflater();

        //Default categories button
        Card DefaultCategories = new Card(this, inflater, layout_edit, 0);
        Setting loadDefCat = new Setting(inflater, R.drawable.ic_database_plus_white_24dp, getString(R.string.title_settings_defaultcategories), getString(R.string.subtitle_settings_defaultcategories),
                new View.OnClickListener() { @Override public void onClick(View v) {
                    CategoryManager.getInstance().RemoveAllCategories();
                    DatabaseManager.getInstance().deleteTableContent(DatabaseManager.TABLE_SETTINGS_CATEGORIES);
                    CategoryManager.getInstance().LoadDefaultCategories();
                    for (Category cat : CategoryManager.getInstance().GetCategories()) { DatabaseManager.getInstance().insertSetting(cat, true); }
                    adapter.notifyDataSetChanged();
                }}
        );
        DefaultCategories.AddView(loadDefCat.getView());


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
                String newCategory = editText_name.getText().toString();

                if (!newCategory.equals("")) {
                    //New category
                    if (editingCategory == null){
                        editingCategory = new Category(newCategory, GetColor());
                    } else { //Update category
                        editingCategory.SetTitle(newCategory);
                        editingCategory.SetColor(GetColor());

                        //Update budget transactions
                        for (Budget budget : BudgetManager.getInstance().GetBudgets()) {
                            for (new_Transaction transaction : budget.GetTransactions(new_Transaction.TRANSACTION_TYPE.Expense)){

                                //Update category
                                if (transaction.GetCategory().equals(editingCategory.GetTitle())) {
                                    transaction.SetCategory(newCategory);
                                }

                                //Update database
                                DatabaseManager.getInstance().insert(budget, transaction, true);
                            }
                        }
                    }

                    //Add or update old category
                    CategoryManager.getInstance().AddCategory(editingCategory);
                    DatabaseManager.getInstance().insertSetting(editingCategory, true);

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

    //Get color from the three seekbars
    public int GetSeekbarColor(){
        return Color.argb(255, (int)(((double)seekBar_red.getProgress()/100.0)*255), (int)(((double)seekBar_green.getProgress()/100.0)*255), (int)(((double)seekBar_blue.getProgress()/100.0)*255));
    }

    //Check if the user is allowed to save
    public void CheckCanSave()
    {
        String name = editText_name.getText().toString();

        editingCategory = CategoryManager.getInstance().GetCategory(name);

        if (editingCategory != null) {
            if (editingCategory.GetColor() == GetSeekbarColor() && //Check color
                    (name.equals("") || (!name.equals("") && editingCategory.GetTitle().equals(name))) //Check if name is the same
            ) { SetSaveButtonEnabled(false); }
            else { SetSaveButtonEnabled(true); }
        }
        else {
            if (name.equals("")) { SetSaveButtonEnabled(false); }
            else { SetSaveButtonEnabled(true); }
        }
    }


    //Update positive button text
    public void SetSaveButtonEnabled(Boolean enabled){
        if (button_save != null) {
            button_save.setEnabled(enabled);
            if (button_save.getIcon() != null) button_save.getIcon().setAlpha((enabled ? 255 : 130));
        }
    }

    //Edit category
    public void EditCategory(final String id, DialogFragmentManagePPC dialogFragment){
        Category cr = CategoryManager.getInstance().GetCategory(Integer.valueOf(id));
        if (cr != null) {
            editingCategory = cr;

            //Open add new layout
            ToggleMenus(false);
            //Set title to edit
            toolbar.setTitle(R.string.title_editcategory);

            //Load information
            editText_name.setText(cr.GetTitle());

            int red = Color.red(cr.GetColor());
            int green = Color.green(cr.GetColor());
            int blue = Color.blue(cr.GetColor());

            seekBar_red.setProgress((int) ((red / 255.0) * 100));
            seekBar_green.setProgress((int) ((green / 255.0) * 100));
            seekBar_blue.setProgress((int) ((blue / 255.0) * 100));

            SetIndicatorColor(cr.GetColor());

            CheckCanSave();

            //Dismiss dialogfragment
            dialogFragment.dismiss();
        }
    }

    //Delete category
    public void DeleteCategory(final String id, final DialogFragmentManagePPC dialogFragment)
    {
        final Category cr = CategoryManager.getInstance().GetCategory(Integer.valueOf(id));
        if (cr != null) {
            new AlertDialog.Builder(this).setTitle(R.string.confirm_areyousure_deletesingle).setPositiveButton(R.string.action_deleteitem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CategoryManager.getInstance().RemoveCategory(cr);

                    DatabaseManager.getInstance().removeCategorySetting(cr.GetTitle());

                    adapter.notifyDataSetChanged();
                    dialogFragment.dismiss();
                    dialog.dismiss();
                }
            }).setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }).create().show();
        }
    }

    public int GetColor(){
        return Color.argb(255, (int)(((double)seekBar_red.getProgress()/100.0)*255), (int)(((double)seekBar_green.getProgress()/100.0)*255), (int)(((double)seekBar_blue.getProgress()/100.0)*255));
    }


    //Expand and retract sub menus
    public void ToggleMenus(Boolean editList){
        menustate = editList;

        //Color preview button
        //imageView_colorindicator.setVisibility( (edit ? View.GONE : View.VISIBLE) );
        //Enable edit layout
        //layout_edit.setVisibility( (editList ? View.VISIBLE : View.GONE) );
        //Disable add layout
        //layout_add.setVisibility( (edit ? View.GONE : View.VISIBLE) );
        AppBarLayoutExpanded(!editList);

        //Enable add new button
        button_new.setVisibility( (editList ? View.VISIBLE : View.GONE) );
        //Disable save button
        button_save.setVisible(!editList);
        //Set title
        toolbar.setTitle( (editList ? R.string.title_managecategories : R.string.title_addnewcategory) );
        //Set back button
        //toolbar.setNavigationIcon( (edit ? R.drawable.ic_clear_white_24dp : R.drawable.ic_arrow_back_white_24dp) );
    }

    public void ClearAddMenu(){
        editingCategory = null;

        //Reset old state
        editText_name.setText("");
        seekBar_red.setProgress(50);
        seekBar_green.setProgress(50);
        seekBar_blue.setProgress(50);
    }


    public void SetIndicatorColor(int color){ imageView_colorindicator.setColorFilter(color); }

    public void AppBarLayoutExpanded(boolean expanded){
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
        lp.height = (expanded ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) getResources().getDimension(R.dimen.toolbar_size));
    }

}