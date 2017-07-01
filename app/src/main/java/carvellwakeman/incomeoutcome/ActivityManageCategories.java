package carvellwakeman.incomeoutcome;


import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.Random;

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


        LayoutInflater inflater = getLayoutInflater();

        //Default categories button
        Card DefaultCategories = new Card(this, inflater, edit_layout, 0);
        Setting loadDefCat = new Setting(inflater, R.drawable.ic_database_plus_white_24dp, getString(R.string.title_settings_defaultcategories), getString(R.string.subtitle_settings_defaultcategories),
                new View.OnClickListener() { @Override public void onClick(View v) {
                    //Hide soft keyboard
                    Helper.hideSoftKeyboard(ActivityManageCategories.this, v);

                    //Close sub menus
                    menuState = MENU_STATE.VIEW;
                    CloseSubMenus();

                    //Replace categories with defaults
                    CategoryManager.getInstance().RemoveAllCategories();
                    DatabaseManager.getInstance().deleteTableContent(DatabaseManager.TABLE_SETTINGS_CATEGORIES);
                    CategoryManager.getInstance().LoadDefaultCategories();
                    for (Category cat : CategoryManager.getInstance().GetCategories()) { DatabaseManager.getInstance().insertSetting(cat, true); }
                    adapter.notifyDataSetChanged();
                }}
        );
        DefaultCategories.AddView(loadDefCat.getView());

    }


    //Check if the user is allowed to save
    @Override
    public void CheckCanSave() {
        String name = editText_name.getText().toString();

        if (name.equals("")) {
            SetSaveButtonEnabled(false);
        } else {
            if (CategoryManager.getInstance().GetCategory(name) != null) {
                SetSaveButtonEnabled(editingEntity.GetColor() != GetSeekbarColor()); //Check if color is different
            } else {
                SetSaveButtonEnabled(true);
            }
        }
    }

    // Get category
    @Override
    public Category GetEntity(){
        return CategoryManager.getInstance().GetCategory(editText_name.getText().toString());
    }

    //Edit category
    @Override
    public void EditEntity(final Integer id, DialogFragmentManagePPC dialogFragment){
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
    public void DeleteEntity(final Integer id, final DialogFragmentManagePPC dialogFragment) {
        final Category cr = CategoryManager.getInstance().GetCategory(id);
        if (cr != null) {
            new AlertDialog.Builder(this).setTitle(R.string.confirm_areyousure_deletesingle).setPositiveButton(R.string.action_deleteitem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CategoryManager.getInstance().RemoveCategory(cr);

                    DatabaseManager.getInstance().removeCategorySetting(cr);

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
            DatabaseManager.getInstance().insertSetting(editingEntity, new CallBack() {
                @Override public void call() { adapter.notifyDataSetChanged(); }
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
    public void OpenEditMenu(){
        super.OpenEditMenu();

        //Set title
        toolbar.setTitle( R.string.title_editcategory );
    }

    public void OpenAddMenu(){
        super.OpenAddMenu();

        //Set title
        toolbar.setTitle( R.string.title_addnewcategory );
    }

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