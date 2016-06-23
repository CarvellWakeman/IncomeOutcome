package carvellwakeman.incomeoutcome;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import org.joda.time.LocalDate;


public class RepeatYearly extends Fragment {
    int EveryN;
    int MoY;
    int DoM;

    EditText input;

    NumberPicker pick_month;
    NumberPicker pick_day;

    public RepeatYearly() {
        EveryN = 0;

    }


    //Accessor
    public int GetEveryN(){
        if (input != null){
            //Lower clamp of value to 1
            return Math.max(Integer.parseInt(input.getText().toString()), 1);
        }
        else { return 0; }
    }
    public int GetMonthOfYear(){
        if (pick_month != null){
            return pick_month.getValue();
        }
        else { return 0; }
    }
    public int GetDayOfMonth(){
        if (pick_day != null){
            return pick_day.getValue();
        }
        else { return 0; }
    }

    //Mutator
    public void SetEveryN(int n){
        EveryN = n;
        if (input != null) {
            //Lower clamp of value to 1
            input.setText(String.valueOf(Math.max(n, 1)));
        }
    }
    public void SetMonthOfYear(int n){
        MoY = n;
        if (pick_month != null) {
            pick_month.setValue(n);
        }
    }
    public void SetDayOfMonth(int n){
        DoM = n;
        if (pick_day != null) {
            pick_day.setValue(n);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_repeat_yearly, container, false);
        input = (EditText) myFragmentView.findViewById(R.id.editText_repeatYearly);
        pick_month = (NumberPicker) myFragmentView.findViewById(R.id.numberPicker_month);
        pick_day = (NumberPicker) myFragmentView.findViewById(R.id.numberPicker_day);
        pick_day.setWrapSelectorWheel(false);
        pick_day.setMaxValue(31); //Jan default
        pick_day.setMinValue(1);
        pick_day.setWrapSelectorWheel(false);

        final LocalDate cal = new LocalDate();
        String[] mnths = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        pick_month.setMaxValue(11);
        pick_month.setMinValue(0);
        pick_month.setDisplayedValues(mnths);
        pick_month.setWrapSelectorWheel(false);
        pick_month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                cal.set(LocalDate.MONTH, pick_month.getValue());

                pick_day.setMaxValue(cal.getActualMaximum(LocalDate.DAY_OF_MONTH));
                pick_day.setMinValue(cal.getActualMinimum(LocalDate.DAY_OF_MONTH));
            }
        });

        //Set values to begin with
        SetEveryN(EveryN);
        SetMonthOfYear(MoY);
        SetDayOfMonth(DoM);

        // Inflate the layout for this fragment
        return myFragmentView;
    }

}
