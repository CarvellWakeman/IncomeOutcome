package carvellwakeman.incomeoutcome;


import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.app.DialogFragment;
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
    Category old_category;

    AdapterManageCategories adapter;

    android.support.v7.widget.Toolbar toolbar;
    MenuItem button_save;

    FloatingActionButton button_new;


    DiscreteSeekBar seekBar_red;
    DiscreteSeekBar seekBar_green;
    DiscreteSeekBar seekBar_blue;

    ImageView imageView_colorindicator;
    TextInputLayout TIL;
    EditText editText_categoryname;

    LinearLayout layout_edit;
    LinearLayout layout_add;

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

        button_new = (FloatingActionButton) findViewById(R.id.FAB_dialogmc_new);

        seekBar_red = (DiscreteSeekBar) findViewById(R.id.seekBar_dialogcat_red);
        seekBar_green = (DiscreteSeekBar) findViewById(R.id.seekBar_dialogcat_green);
        seekBar_blue = (DiscreteSeekBar) findViewById(R.id.seekBar_dialogcat_blue);

        imageView_colorindicator = (ImageView) findViewById(R.id.imageView_dialogcat);

        layout_edit = (LinearLayout) findViewById(R.id.linearLayout_dialog_editcategory);
        layout_add = (LinearLayout) findViewById(R.id.linearLayout_dialog_newcategory);

        recyclerView = (RecyclerView) findViewById(R.id.dialog_recyclerView_categories);

        TIL = (TextInputLayout) findViewById(R.id.TIL_dialog_categoryname);


        //Configure toolbar
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
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
        //button_save = toolbar.getMenu().findItem(R.id.toolbar_save);
        //button_save.setVisible(false);
        //SetSaveButtonEnabled(false);


        TIL.setErrorEnabled(true);
        editText_categoryname = TIL.getEditText();

        editText_categoryname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = editText_categoryname.getText().toString();

                if (!str.equals("")) {
                    if (!ProfileManager.HasCategory(str)) {
                        SetSaveButtonEnabled(true);
                        TIL.setError("");
                    }
                    else{ SetSaveButtonEnabled(false); TIL.setError("Category already exists"); }
                }
                else{ SetSaveButtonEnabled(false); TIL.setError("Enter a title"); }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        //Color bars
        seekBar_red.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                SetIndicatorColor(Color.argb(255, (int)(((double)seekBar_red.getProgress()/100.0)*255), (int)(((double)seekBar_green.getProgress()/100.0)*255), (int)(((double)seekBar_blue.getProgress()/100.0)*255)));
            }
            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                SetSaveButtonEnabled(true);
            }
        });
        seekBar_green.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                SetIndicatorColor(Color.argb(255, (int)(((double)seekBar_red.getProgress()/100.0)*255), (int)(((double)seekBar_green.getProgress()/100.0)*255), (int)(((double)seekBar_blue.getProgress()/100.0)*255)));
            }
            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                SetSaveButtonEnabled(true);
            }
        });
        seekBar_blue.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                SetIndicatorColor(Color.argb(255, (int)(((double)seekBar_red.getProgress()/100.0)*255), (int)(((double)seekBar_green.getProgress()/100.0)*255), (int)(((double)seekBar_blue.getProgress()/100.0)*255)));
            }
            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                SetSaveButtonEnabled(true);
            }
        });


        //Set profiles adapter
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
                String str = editText_categoryname.getText().toString();

                if (old_category != null) {
                    String old = old_category.GetTitle();

                    old_category.SetTitle(str);
                    old_category.SetColor(GetColor());

                    //Update old category
                    ProfileManager.UpdateCategory(old, old_category);
                }
                else {
                    //Add new category
                    ProfileManager.AddCategory(str, GetColor());
                }

                //finish dialog
                finish();
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

    //Edit category
    public void EditCategory(Category category){
        old_category = category;

        //Open add new layout
        ToggleMenus(false);
        //Set title to edit
        toolbar.setTitle(R.string.title_editcategory);

        //Load information
        editText_categoryname.setText(category.GetTitle());

        int red = Color.red(category.GetColor());
        int green = Color.green(category.GetColor());
        int blue = Color.blue(category.GetColor());

        seekBar_red.setProgress( (int)((red / 255.0)*100) );
        seekBar_green.setProgress( (int)((green / 255.0)*100) );
        seekBar_blue.setProgress( (int)((blue / 255.0)*100) );

        SetIndicatorColor(category.GetColor());
    }

    public int GetColor(){
        return Color.argb(255, (int)(((double)seekBar_red.getProgress()/100.0)*255), (int)(((double)seekBar_green.getProgress()/100.0)*255), (int)(((double)seekBar_blue.getProgress()/100.0)*255));
    }


    //Expand and retract sub menus
    public void ToggleMenus(Boolean edit){
        menustate = !menustate;

        //Reset old state
        editText_categoryname.setText("");
        seekBar_red.setProgress(50);
        seekBar_green.setProgress(50);
        seekBar_blue.setProgress(50);

        //Color preview button
        //imageView_colorindicator.setVisibility( (edit ? View.GONE : View.VISIBLE) );
        //Enable edit layout
        layout_edit.setVisibility( (edit ? View.VISIBLE : View.GONE) );
        //Disable add layout
        layout_add.setVisibility( (edit ? View.GONE : View.VISIBLE) );
        //Enable add new button
        button_new.setVisibility( (edit ? View.VISIBLE : View.GONE) );
        //Disable save button
        button_save.setVisible(!edit);
        //Set title
        toolbar.setTitle( (edit ? R.string.title_editcategory : R.string.title_addnewcategory) );
        //Set back button
        toolbar.setNavigationIcon( (edit ? R.drawable.ic_clear_white_24dp : R.drawable.ic_arrow_back_white_24dp) );
    }


    public void SetIndicatorColor(int color){
        imageView_colorindicator.setColorFilter(color);
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