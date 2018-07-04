package carvellwakeman.incomeoutcome.dialogs;

        import android.app.DatePickerDialog;
        import android.app.Dialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.app.DialogFragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.Window;
        import android.widget.*;
        import carvellwakeman.incomeoutcome.models.Budget;
        import carvellwakeman.incomeoutcome.R;
        import carvellwakeman.incomeoutcome.interfaces.RunnableParam;
        import org.joda.time.LocalDate;

public class DialogFragmentPaidBack extends DialogFragment
{
    Context _parent;
    RunnableParam _callBack;

    Budget _budget;
    LocalDate _date;

    RadioButton lastSelected;
    RadioButton radioButton_today;
    RadioButton radioButton_date;
    RadioButton radioButton_never;

    Button button_positive;
    Button button_negative;


    public static DialogFragmentPaidBack newInstance(Context parent, RunnableParam callBack, Budget budget) {
        DialogFragmentPaidBack fg = new DialogFragmentPaidBack();
        fg._callBack = callBack;
        fg._parent = parent;
        fg._budget = budget;
        return fg;
    }

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_paidback, container, false);
        view.setBackgroundColor(Color.WHITE);

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
                    if (_date == null) { _date = new LocalDate(); }
                    DatePickerDialog d = new DatePickerDialog(getActivity(), datePicker, _date.getYear(), _date.getMonthOfYear()-1, _date.getDayOfMonth());
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

            if (radioButton_today.isChecked()) { //Today
                _date = new LocalDate();
            }
            else if (radioButton_never.isChecked()) { //Never
                _date = null;
            }

            _callBack.run(_date);

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
            _date = new LocalDate(year, monthOfYear+1, dayOfMonth);
            //Format button text
            if (radioButton_date != null) { radioButton_date.setText( String.format(getString(R.string.time_ondate_formatted), _date.toString(_parent.getString(R.string.date_format))) ); }
        }
    };


    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}