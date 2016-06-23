package carvellwakeman.incomeoutcome;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.app.DialogFragment;
import android.support.v7.widget.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import org.joda.time.LocalDate;

public class DialogFragmentManageProfiles extends DialogFragment {
    Boolean menustate = true;
    Boolean editstate = false;
    Profile editingprofile = null;

    LocalDate start_date;
    LocalDate end_date;

    AdapterManageProfiles adapter;

    TextView textView_title;

    Button button_positive;
    Button button_negative;
    Button button_back;
    Button button_startdate;
    Button button_enddate;

    TextInputLayout TIL;
    EditText editText_profilename;

    LinearLayout layout_edit;
    LinearLayout layout_add;

    NpaLinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView_profiles;

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_manageprofiles, container, false);
        view.setBackgroundColor(Color.WHITE);

        textView_title = (TextView) view.findViewById(R.id.textView_dialogprofiles_title);

        button_positive = (Button) view.findViewById(R.id.button_dialogprofiles_positive);
        button_negative = (Button) view.findViewById(R.id.button_dialogprofiles_negative);
        button_back = (Button) view.findViewById(R.id.button_dialogprofiles_back);
        button_startdate = (Button) view.findViewById(R.id.dialog_button_startdate);
        button_enddate = (Button) view.findViewById(R.id.dialog_button_enddate);

        layout_edit = (LinearLayout) view.findViewById(R.id.linearLayout_dialog_editprofile);
        layout_add = (LinearLayout) view.findViewById(R.id.linearLayout_dialog_newprofile);

        recyclerView_profiles = (RecyclerView) view.findViewById(R.id.dialog_recyclerView_profiles);


        TIL = (TextInputLayout)view.findViewById(R.id.TIL_dialog_profilename);
        TIL.setErrorEnabled(true);
        editText_profilename = TIL.getEditText();


        //Set profiles adapter
        adapter = new AdapterManageProfiles(this);
        recyclerView_profiles.setAdapter(adapter);

        //LinearLayoutManager for RecyclerView
        linearLayoutManager = new NpaLinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(NpaLinearLayoutManager.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        recyclerView_profiles.setLayoutManager(linearLayoutManager);


        editText_profilename.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = editText_profilename.getText().toString();

                if (!str.equals("")) {
                    if (!ProfileManager.HasProfile(str)) {
                        SetPositiveButtonEnabled(true);
                        TIL.setError("");
                    }
                    else{ SetPositiveButtonEnabled(false); TIL.setError("Profile already exists"); }
                }
                else{ SetPositiveButtonEnabled(false); TIL.setError("Enter a title"); }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        //Button listeners
        button_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menustate){ //New
                    ToggleMenus(false);
                } else { //Add
                    if (editstate) { //Make changes to an existing profile
                        if (editingprofile != null){

                            if (!editText_profilename.getText().toString().equals("")){
                                editingprofile.SetStartTime(start_date, true);
                                editingprofile.SetEndTime(end_date, true);
                                editingprofile.SetName(editText_profilename.getText().toString());

                                ProfileManager.UpdateProfile(editingprofile);

                                dismiss();
                            }
                        }
                    }
                    else{ //Add a new profile
                        if (!editText_profilename.getText().toString().equals("")){
                            Profile pr = new Profile(editText_profilename.getText().toString());
                            pr.SetStartTime(start_date, true);
                            pr.SetEndTime(end_date, true);

                            ProfileManager.AddProfile(pr);

                            //Apparently updating the adapter from here doesn't work, so now I have to create a new one...
                            adapter.notifyDataSetChanged();

                            dismiss();
                        }
                    }
                }
            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleMenus(true);
            }
        });


        //Set start date
        button_startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open date picker dialog
                LocalDate c = null;
                if (start_date != null) { c = start_date; } else { c = new LocalDate(); }

                DatePickerDialog d = new DatePickerDialog(getActivity(), datePicker, c.getYear(), c.getMonthOfYear() - 1, c.getDayOfMonth());
                d.show();
            }
        });
        //Set end date
        button_enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open date picker dialog
                LocalDate c = null;
                if (end_date != null) { c = end_date; } else { c = new LocalDate(); }

                DatePickerDialog d = new DatePickerDialog(getActivity(), datePicker2, c.getYear(), c.getMonthOfYear() - 1, c.getDayOfMonth());
                d.show();
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

    //Update positive button text
    public void SetPositiveButtonEnabled(Boolean enabled){
        button_positive.setEnabled(enabled);
    }

    //Edit profile
    public void EditProfile(Profile pr){
        editingprofile = pr;
        //Open add new layout
        ToggleMenus(false);
        //Set title to edit
        textView_title.setText(R.string.title_editprofile);
        //Set positive button to "save"
        button_positive.setText(R.string.action_save);
        //Set edit state to true
        editstate = true;
        //Load profile information into add new profile settings
        if (pr != null) {
            editText_profilename.setText(pr.GetName());
            start_date = pr.GetStartTime();
            end_date = pr.GetEndTime();
            UpdateDates();
        }
    }

    //Update dates
    public void UpdateDates(){
        if (start_date != null) {
            button_startdate.setText(getActivity().getString(R.string.time_start_format, start_date.toString(ProfileManager.simpleDateFormat)));
        } else {
            button_startdate.setText(R.string.time_start);
        }
        if (end_date != null) {
            button_enddate.setText(getActivity().getString(R.string.time_end_format, end_date.toString(ProfileManager.simpleDateFormat)));
        }else {
            button_enddate.setText(R.string.time_end);
        }
    }


    //Date pickers
    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            //Set date
            start_date = new LocalDate(year, monthOfYear + 1, dayOfMonth);
            UpdateDates();
        }
    };
    DatePickerDialog.OnDateSetListener datePicker2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            //Set date
            end_date = new LocalDate(year, monthOfYear + 1, dayOfMonth);
            UpdateDates();
        }
    };


    //Expand and retract sub menus
    public void ToggleMenus(Boolean edit){
        menustate = !menustate;

        ClearAddMenu();



        if (edit){
            SetPositiveButtonEnabled(true);

            layout_edit.setVisibility(View.VISIBLE);
            layout_add.setVisibility(View.GONE);

            textView_title.setText(R.string.title_editprofile);

            button_positive.setText(R.string.action_new);
            button_back.setVisibility(View.GONE);
        }
        else{
            editstate = false;

            layout_edit.setVisibility(View.GONE);
            layout_add.setVisibility(View.VISIBLE);

            textView_title.setText(R.string.title_addnewprofile);

            button_positive.setText(R.string.action_add);
            button_back.setVisibility(View.VISIBLE);
        }

    }

    //Clear add profile menu
    public void ClearAddMenu(){
        start_date = null;
        end_date = null;

        UpdateDates();

        editText_profilename.setText("");
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