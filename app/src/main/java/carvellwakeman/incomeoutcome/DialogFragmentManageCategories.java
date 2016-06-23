package carvellwakeman.incomeoutcome;


import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class DialogFragmentManageCategories extends DialogFragment {
    Boolean menustate = true;
    Category old_category;

    AdapterManageCategories adapter;

    TextView textView_title;

    Button button_positive;
    Button button_negative;
    Button button_back;

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


    public DialogFragmentManageCategories() {}


    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_managecategories, container, false);
        view.setBackgroundColor(Color.WHITE);

        textView_title = (TextView) view.findViewById(R.id.textView_dialogcat_title);

        button_positive = (Button) view.findViewById(R.id.button_dialogcat_positive);
        button_negative = (Button) view.findViewById(R.id.button_dialogcat_negative);
        button_back = (Button) view.findViewById(R.id.button_dialogcat_back);

        seekBar_red = (DiscreteSeekBar) view.findViewById(R.id.seekBar_dialogcat_red);
        seekBar_green = (DiscreteSeekBar) view.findViewById(R.id.seekBar_dialogcat_green);
        seekBar_blue = (DiscreteSeekBar) view.findViewById(R.id.seekBar_dialogcat_blue);

        imageView_colorindicator = (ImageView) view.findViewById(R.id.imageView_dialogcat);

        layout_edit = (LinearLayout) view.findViewById(R.id.linearLayout_dialog_editcategory);
        layout_add = (LinearLayout) view.findViewById(R.id.linearLayout_dialog_newcategory);

        recyclerView = (RecyclerView) view.findViewById(R.id.dialog_recyclerView_categories);

        TIL = (TextInputLayout)view.findViewById(R.id.TIL_dialog_categoryname);


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
                        SetPositiveButtonEnabled(true);
                        TIL.setError("");
                    }
                    else{ SetPositiveButtonEnabled(false); TIL.setError("Category already exists"); }
                }
                else{ SetPositiveButtonEnabled(false); TIL.setError("Enter a title"); }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        //Color bars
        seekBar_red.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                SetIndicatorColor(Color.argb(255, (int)(((double)seekBar_red.getProgress()/100.0)*255), (int)(((double)seekBar_green.getProgress()/100.0)*255), (int)(((double)seekBar_blue.getProgress()/100.0)*255)));
                SetPositiveButtonEnabled(true);
            }
            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
            }
        });
        seekBar_green.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                SetIndicatorColor(Color.argb(255, (int)(((double)seekBar_red.getProgress()/100.0)*255), (int)(((double)seekBar_green.getProgress()/100.0)*255), (int)(((double)seekBar_blue.getProgress()/100.0)*255)));
                SetPositiveButtonEnabled(true);
            }
            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
            }
        });
        seekBar_blue.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                SetIndicatorColor(Color.argb(255, (int)(((double)seekBar_red.getProgress()/100.0)*255), (int)(((double)seekBar_green.getProgress()/100.0)*255), (int)(((double)seekBar_blue.getProgress()/100.0)*255)));
                SetPositiveButtonEnabled(true);
            }
            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
            }
        });


        //Set profiles adapter
        adapter = new AdapterManageCategories(this);
        recyclerView.setAdapter(adapter);

        //LinearLayoutManager for RecyclerView
        linearLayoutManager = new NpaLinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(NpaLinearLayoutManager.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(linearLayoutManager);


        //Button listeners
        button_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menustate){ //New
                    ToggleMenus(false);
                } else { //Add
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

                    //Dismiss dialog
                    dismiss();
                }
            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleMenus(true);
            }
        });


        //Close fragment/dialog
        button_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });



        // Inflate the layout to use as dialog or embedded fragment
        return view;
    }

    //Edit category
    public void EditCategory(Category category){
        old_category = category;

        //Open add new layout
        ToggleMenus(false);
        //Set title to edit
        textView_title.setText(R.string.title_editcategory);
        //Set positive button to "save"
        button_positive.setText(R.string.action_save);

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

    //Update positive button text
    public void SetPositiveButtonEnabled(Boolean enabled){
        button_positive.setEnabled(enabled);
    }


    //Expand and retract sub menus
    public void ToggleMenus(Boolean edit){
        menustate = !menustate;

        if (edit){
            SetPositiveButtonEnabled(true);

            layout_edit.setVisibility(View.VISIBLE);
            layout_add.setVisibility(View.GONE);

            imageView_colorindicator.setVisibility(View.GONE);

            textView_title.setText(R.string.title_editcategory);

            button_positive.setText(R.string.action_new);
            button_back.setVisibility(View.GONE);
        }
        else{
            editText_categoryname.setText("");

            layout_edit.setVisibility(View.GONE);
            layout_add.setVisibility(View.VISIBLE);

            imageView_colorindicator.setVisibility(View.VISIBLE);

            textView_title.setText(R.string.title_newcategory);

            button_positive.setText(R.string.action_add);
            button_back.setVisibility(View.VISIBLE);
        }
    }


    public void SetIndicatorColor(int color){
        imageView_colorindicator.setColorFilter(color);
    }



    /** The system calls this only when creating the layout in a dialog. */
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
}