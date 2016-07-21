package carvellwakeman.incomeoutcome;

        import android.app.DatePickerDialog;
        import android.app.Dialog;
        import android.content.DialogInterface;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.app.DialogFragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.Window;
        import android.widget.*;
        import org.joda.time.LocalDate;

public class DialogFragmentPaidBack extends DialogFragment {

    ActivityDetailsTransaction _parent;
    Profile _profile;
    LocalDate date;

    RadioButton lastSelected;
    RadioButton radioButton_today;
    RadioButton radioButton_date;
    RadioButton radioButton_never;

    Button button_positive;
    Button button_negative;


    static DialogFragmentPaidBack newInstance(ActivityDetailsTransaction parent, Profile profile) {
        DialogFragmentPaidBack fg = new DialogFragmentPaidBack();
        fg._parent = parent;
        Bundle args = new Bundle();
        args.putSerializable("profile", profile);
        fg.setArguments(args);

        return fg;
    }

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_paidback, container, false);
        view.setBackgroundColor(Color.WHITE);

        _profile = (Profile)getArguments().getSerializable("profile");

        radioButton_today = (RadioButton) view.findViewById(R.id.radioButton_today);
        radioButton_date = (RadioButton) view.findViewById(R.id.radioButton_ondate);
        radioButton_never = (RadioButton) view.findViewById(R.id.radiobutton_never);

        button_positive = (Button) view.findViewById(R.id.button_dialogpb_positive);
        button_negative = (Button) view.findViewById(R.id.button_dialogpb_negative);

        lastSelected = radioButton_today;

        //Radio listeners
        radioButton_date.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lastSelected = radioButton_date;
                if (radioButton_date.isChecked()){
                    //Open date picker dialog
                    if (date == null) { date = new LocalDate(); }
                    DatePickerDialog d = new DatePickerDialog(getActivity(), datePicker, date.getYear(), date.getMonthOfYear()-1, date.getDayOfMonth());
                    d.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override  public void onCancel(DialogInterface dialog) { lastSelected.setChecked(true); }
                    });
                    d.show();
                }
                else {
                    radioButton_date.setText(R.string.time_ondate);
                }
            }
        });
        radioButton_today.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { lastSelected = radioButton_today; }
        });
        radioButton_never.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { lastSelected = radioButton_never; }
        });

        //Button listener
        button_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioButton_today.isChecked()){ //Today
                    _profile.UpdatePaidBackInTimeFrame(new LocalDate(), true);
                }
                else if (radioButton_date.isChecked()){ //A date
                    _profile.UpdatePaidBackInTimeFrame(date, true);
                }
                else if (radioButton_never.isChecked()){ //Never
                    _profile.UpdatePaidBackInTimeFrame(null, true);
                }

                _parent.elementsAdapter.notifyDataSetChanged();
                _parent.totalsAdapter.notifyDataSetChanged();
                _parent.UpdateAdapters();

                //_profile.CalculateTimeFrame(); //TODO Necessary? - Yes, necessary.
                _profile.CalculateTimeFrame(_parent.activityType);
                _profile.CalculateTotalsInTimeFrame(_parent.activityType);

                dismiss();
            }
        });


        //Close fragment/dialog
        button_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { dismiss(); }
        });

        // Inflate the layout to use as dialog or embedded fragment
        return view;
    }


    //Date picker dialog listener
    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            //Set date
            date = new LocalDate(year, monthOfYear+1, dayOfMonth);

            //Format button text
            if (radioButton_date != null) { radioButton_date.setText( String.format(getString(R.string.time_ondate_formatted), date.toString(ProfileManager.simpleDateFormat)) ); }
        }
    };


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