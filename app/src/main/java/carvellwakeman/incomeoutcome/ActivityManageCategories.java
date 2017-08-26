package carvellwakeman.incomeoutcome;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.*;
import android.widget.*;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class ActivityManageCategories extends ActivityManageEntity<Category> {

    DiscreteSeekBar seekBar_red;
    DiscreteSeekBar seekBar_green;
    DiscreteSeekBar seekBar_blue;

    ImageView imageView_colorindicator;


    public ActivityManageCategories() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_managecategories);
        super.onCreate(savedInstanceState);


        seekBar_red = (DiscreteSeekBar) findViewById(R.id.seekBar_dialogcat_red);
        seekBar_green = (DiscreteSeekBar) findViewById(R.id.seekBar_dialogcat_green);
        seekBar_blue = (DiscreteSeekBar) findViewById(R.id.seekBar_dialogcat_blue);

        imageView_colorindicator = (ImageView) findViewById(R.id.imageView_dialogcat);

        toolbar.setTitle(R.string.title_managecategories);


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
                SetSaveButtonEnabled(CanSave());
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
                SetSaveButtonEnabled(CanSave());
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
                SetSaveButtonEnabled(CanSave());
            }
        });


        //Set adapter
        adapter = new AdapterManageCategories(this);
        recyclerView.setAdapter(adapter);


        LayoutInflater inflater = getLayoutInflater();

        //Default categories button
        Card DefaultCategories = new Card(this, inflater, edit_layout, 0);
        Setting loadDefCat = new Setting(inflater, R.drawable.ic_database_plus_white_24dp, getString(R.string.title_settings_defaultcategories), getString(R.string.subtitle_settings_defaultcategories),
                new View.OnClickListener() { @Override public void onClick(View v) {
                    recyclerView.setVisibility(View.VISIBLE);

                    //Hide soft keyboard
                    Helper.hideSoftKeyboard(ActivityManageCategories.this, v);

                    //Close sub menus
                    CloseSubMenus();

                    // Select short circuit
                    if (menuState == MENU_STATE.ADDNEWSELECT){
                        menuState = MENU_STATE.SELECT;
                        OpenSelectMode();
                    }

                    //Replace categories with defaults
                    CategoryManager.getInstance().RemoveAllCategories();
                    DatabaseManager.getInstance(ActivityManageCategories.this).deleteTableContent(DatabaseManager.TABLE_SETTINGS_CATEGORIES);
                    CategoryManager.getInstance().LoadDefaultCategories();
                    for (Category cat : CategoryManager.getInstance().GetCategories()) { DatabaseManager.getInstance(ActivityManageCategories.this).insertSetting(cat, true); }
                    adapter.notifyDataSetChanged();
                }}
        );
        DefaultCategories.AddView(loadDefCat.getView());

        Intent intent = getIntent();
        if (intent.getBooleanExtra("addnew", false)){
            edit_layout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }


    //Check if the user is allowed to save
    @Override
    public boolean CanSave() {
        // Check if color is different
        return super.CanSave() || (editingEntity != null && editingEntity.GetColor() != GetSeekbarColor());
    }

    // Get category
    @Override
    public Category GetEntity(){
        return CategoryManager.getInstance().GetCategory(editText_name.getText().toString());
    }

    //Edit category
    @Override
    public void EditEntity(final Integer id, DialogFragmentManageBPC dialogFragment){
        Category cr = CategoryManager.getInstance().GetCategory(id);
        if (cr != null) {
            menuState = MENU_STATE.EDIT;

            editingEntity = cr;

            //Open add new layout
            OpenEditMenu();

            //Load information
            editText_name.setText(cr.GetTitle());

            int red = Color.red(cr.GetColor());
            int green = Color.green(cr.GetColor());
            int blue = Color.blue(cr.GetColor());

            seekBar_red.setProgress((int) ((red / 255.0) * 100));
            seekBar_green.setProgress((int) ((green / 255.0) * 100));
            seekBar_blue.setProgress((int) ((blue / 255.0) * 100));

            SetIndicatorColor(cr.GetColor()); //Already done by seekbar listeners

            //Disable save button
            SetSaveButtonEnabled(false);

            //Dismiss dialogfragment
            dialogFragment.dismiss();
        }
    }

    //Delete category
    @Override
    public void DeleteEntity(final Integer id, final DialogFragmentManageBPC dialogFragment) {
        final Category cr = CategoryManager.getInstance().GetCategory(id);
        if (cr != null) {
            new AlertDialog.Builder(this).setTitle(R.string.confirm_areyousure_deletesingle).setPositiveButton(R.string.action_deleteitem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                // Update transactions using this category
                BudgetManager bm = BudgetManager.getInstance();
                DatabaseManager dm = DatabaseManager.getInstance(ActivityManageCategories.this);

                for (Budget b : bm.GetBudgets()){
                    for (Transaction t : b.GetAllTransactions()){
                        if (t.GetCategory() == cr.GetID()){
                            t.SetCategory(Category.Deleted.GetID());
                            dm.insert(t, true);
                        }
                    }
                }

                CategoryManager.getInstance().RemoveCategory(cr);
                DatabaseManager.getInstance(ActivityManageCategories.this).removeCategorySetting(cr);

                adapter.notifyDataSetChanged();
                dialogFragment.dismiss();
                dialog.dismiss();
                }
            }).setNegativeButton(R.string.action_cancel, null)
            .create().show();
        }
    }

    // Select category
    @Override
    public void SelectEntity(Category cat){
        Intent intent = new Intent();
        intent.putExtra("entity", cat.GetID());
        setResult(1, intent);
        finish();
    }

    // Save category
    @Override
    public void SaveAction(){
        String newCategory = editText_name.getText().toString();

        if (!newCategory.equals("")) {
            //New category
            if (editingEntity == null){
                editingEntity = new Category(newCategory, GetSeekbarColor());
            } else { //Update category
                editingEntity.SetTitle(newCategory);
                editingEntity.SetColor(GetSeekbarColor());
            }

            //Add or update old category
            CategoryManager.getInstance().AddCategory(editingEntity);
            DatabaseManager.getInstance(ActivityManageCategories.this).insertSetting(editingEntity, new Runnable() {
                @Override public void run() { adapter.notifyDataSetChanged(); }
            }, true);
        }

        // Add New short circuit
        if (menuState == MENU_STATE.ADDNEW) {
            Intent intent = new Intent();
            intent.putExtra("category", editingEntity.GetID());
            setResult(1, intent);
            finish();
        }
    }


    //Expand and retract sub menus
    @Override
    public void OpenEditMenu(){
        super.OpenEditMenu();

        //Set title
        toolbar.setTitle( R.string.title_editcategory );
    }

    @Override
    public void OpenAddMenu(){
        super.OpenAddMenu();

        //Set title
        toolbar.setTitle( R.string.title_addnewcategory );
    }

    @Override
    public void OpenSelectMode(){
        super.OpenSelectMode();

        //Set title
        toolbar.setTitle( R.string.title_selectcategory );
    }

    @Override
    public void CloseSubMenus(){
        super.CloseSubMenus();

        //Set title
        toolbar.setTitle( R.string.title_managecategories );
    }


    // Category unique fields
    public int GetSeekbarColor(){
        int red =   (int) ( ((double)seekBar_red.getProgress() / 100.0) * 255);
        int green = (int) ( ((double)seekBar_green.getProgress() / 100.0) * 255);
        int blue =  (int) ( ((double)seekBar_blue.getProgress() / 100.0) * 255);
        return Color.argb(255, red, green, blue);
    }
    public void SetIndicatorColor(int color){ imageView_colorindicator.setColorFilter(color); }

}