package carvellwakeman.incomeoutcome;

import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import org.joda.time.LocalDate;


public class FragmentTimePeriod extends Fragment {

    //Dates
    TimePeriod _timePeriod;
    LocalDate startDate;

    AdapterBlacklistDates blacklistAdapter;


    Button button_setDate;
    Button button_setRepeat;

    RecyclerView recyclerView_blacklistDates;


    public FragmentTimePeriod() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_time_period, container, false);

        //Today's date
        if (startDate == null) { startDate = new LocalDate(); }
        SetDateButtonTitle();

        if (_timePeriod == null) { _timePeriod = new TimePeriod(startDate); }

        //Blacklist dates
        recyclerView_blacklistDates = (RecyclerView) myFragmentView.findViewById(R.id.recyclerView_blacklistDates);

        //Date button
        button_setDate = (Button) myFragmentView.findViewById(R.id.button_fragmenttp_date);
        button_setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide soft keyboard
                ProfileManager.hideSoftKeyboard(getActivity(), v);

                //Open date picker dialog
                if (startDate == null) { startDate = new LocalDate(); }
                new DatePickerDialog(getActivity(), datePicker, startDate.getYear(), startDate.getMonthOfYear()-1, startDate.getDayOfMonth()).show();
            }
        });

        //Repeat button
        button_setRepeat = (Button) myFragmentView.findViewById(R.id.button_fragmenttp_repeat);
        button_setRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragmentRepeat dfr = DialogFragmentRepeat.newInstance(FragmentTimePeriod.this, _timePeriod);
                ProfileManager.OpenDialogFragment(getActivity(), dfr, true);
            }
        });


        // Blacklist dates
        blacklistAdapter = new AdapterBlacklistDates(getActivity(), _timePeriod);
        recyclerView_blacklistDates.setAdapter(blacklistAdapter);

        //LinearLayoutManager for RecyclerView
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(NpaLinearLayoutManager.VERTICAL);
        llm.scrollToPosition(0);
        recyclerView_blacklistDates.setLayoutManager(llm);

        //Button titles
        SetDateButtonTitle();

        // Inflate the layout for this fragment (After this fragment has been created)
        return myFragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //Time Period
    public void SetTimePeriod(TimePeriod tp){
        _timePeriod = new TimePeriod(tp);
        startDate = tp.GetDate();
        SetDateButtonTitle();
    }
    public TimePeriod GetTimePeriod(){ return _timePeriod; }

    //Enabled
    public void SetEnabled(boolean enabled){
        button_setDate.setEnabled(enabled);
        button_setRepeat.setEnabled(enabled);
    }

    //Date picker dialog listener
    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
        //Set date
        startDate = new LocalDate(year, monthOfYear+1, dayOfMonth);

        //Time Period
        _timePeriod.SetDate(startDate);

        _timePeriod.SetRepeatDayOfMonth(1);
        _timePeriod.SetDateOfYear(null);

        switch(_timePeriod.GetRepeatFrequency()){
            case MONTHLY:
                _timePeriod.SetRepeatDayOfMonth(startDate.getDayOfMonth());
                break;
            case YEARLY:
                _timePeriod.SetDateOfYear(startDate);
                break;
        }

        //Format button text
        SetDateButtonTitle();
        }
    };


    public void SetDateButtonTitle(){
        if (button_setDate != null) { button_setDate.setText( startDate.toString(ProfileManager.simpleDateFormat) ); }
        if (button_setRepeat != null) { button_setRepeat.setText( _timePeriod.GetRepeatStringShort() ); }
    }

}
