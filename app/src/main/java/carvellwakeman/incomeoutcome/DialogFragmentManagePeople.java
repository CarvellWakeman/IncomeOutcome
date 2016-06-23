package carvellwakeman.incomeoutcome;


import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;

public class DialogFragmentManagePeople extends DialogFragment {
    Boolean menustate = true;
    String old_otherperson;

    AdapterManagePeople adapter;

    TextView textView_title;

    Button button_positive;
    Button button_negative;
    Button button_back;

    TextInputLayout TIL;
    EditText editText_personname;

    LinearLayout layout_edit;
    LinearLayout layout_add;

    NpaLinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_managepeople, container, false);
        view.setBackgroundColor(Color.WHITE);

        textView_title = (TextView) view.findViewById(R.id.textView_dialogpeople_title);

        button_positive = (Button) view.findViewById(R.id.button_dialogpeople_positive);
        button_negative = (Button) view.findViewById(R.id.button_dialogpeople_negative);
        button_back = (Button) view.findViewById(R.id.button_dialogpeople_back);

        layout_edit = (LinearLayout) view.findViewById(R.id.linearLayout_dialog_editperson);
        layout_add = (LinearLayout) view.findViewById(R.id.linearLayout_dialog_newperson);

        recyclerView = (RecyclerView) view.findViewById(R.id.dialog_recyclerView_people);

        TIL = (TextInputLayout)view.findViewById(R.id.TIL_dialog_personname);


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
                        SetPositiveButtonEnabled(true);
                        TIL.setError("");
                    }
                    else{ SetPositiveButtonEnabled(false); TIL.setError("Person already exists"); }
                }
                else{ SetPositiveButtonEnabled(false); TIL.setError("Enter a name"); }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        //Set profiles adapter
        adapter = new AdapterManagePeople(this);
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
                    String str = editText_personname.getText().toString();

                    //Update other person
                    ProfileManager.UpdateOtherPerson(old_otherperson, str);

                    //Delete old person if they exist (For editing)
                    ProfileManager.RemoveOtherPerson(old_otherperson);

                    //Add new person (Edit or new)
                    ProfileManager.AddOtherPerson(str);

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

    //Edit profile
    public void EditPerson(String name){
        old_otherperson = name;

        //Open add new layout
        ToggleMenus(false);
        //Set title to edit
        textView_title.setText(R.string.title_editpeople);
        //Set positive button to "save"
        button_positive.setText(R.string.action_save);

        //Load information
        editText_personname.setText(name);
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

            textView_title.setText(R.string.title_editpeople);

            button_positive.setText(R.string.action_new);
            button_back.setVisibility(View.GONE);
        }
        else{
            editText_personname.setText("");

            layout_edit.setVisibility(View.GONE);
            layout_add.setVisibility(View.VISIBLE);

            textView_title.setText(R.string.title_addnewperson);

            button_positive.setText(R.string.action_add);
            button_back.setVisibility(View.VISIBLE);
        }
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