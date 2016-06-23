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
    //TimePeriod
    TimePeriod timePeriod;

    //Dates
    LocalDate date;
    LocalDate dateUntil;
    LocalDate dateOfYear;


    boolean noInfiniteLoopPlease = true;


    //Adapters
    ArrayAdapter<CharSequence> repeatAdapter;
    ArrayAdapter<CharSequence> repeatUntilAdapter;
    //ArrayAdapter<String> blacklistAdapter;
    AdapterBlacklistDates blacklistAdapter;


    //Fragments (Replaced with reusable views for compatibility)
    RelativeLayout relativeLayout_fragments;

    CardView card_blacklist_dates;

    TextView textView_every;
    TextView textView_repeatType;
    EditText editText_repeatFrequency;

        //Weekly
        CheckBox checkBox_mon, checkBox_tues, checkBox_wed, checkBox_thur, checkBox_fri, checkBox_sat, checkBox_sun;

        //Monthly
        EditText editText_dayOfMonth;
        TextView textView_dayOfMonthWarning;

        //Yearly
        Button button_monthDayOfYear;



    //FragmentManager fragmentManager;
    //FragmentTransaction fragmentTransaction;

    //RepeatDaily fragment_daily;
    //RepeatWeekly fragment_weekly;
    //RepeatMonthly fragment_monthly;
    //RepeatYearly fragment_yearly;




    //Views
    Spinner spinner_repeatFrequency;
    Spinner spinner_repeatUntil;

    CheckBox checkbox_repeat;

    EditText editText_repeatNumberTimes;

    Button button_setDate;
    Button button_repeatUntilDate;

    TextView textView_repeatUntilTimes;

    RecyclerView recyclerView_blacklistDates;


    public FragmentTimePeriod() { }

    //Accessors
    public TimePeriod GetTimePeriod(){
        //TimePeriod null check
        if (timePeriod == null) { timePeriod = new TimePeriod(); }

        //Date null check
        if (date == null) { date = LocalDate.now(); }

        //Set date to the selected date
        timePeriod.SetDate(date);

        //Was repeat selected?
        if (checkbox_repeat.isChecked()){

            //Repeat Type spinner
            switch (spinner_repeatFrequency.getSelectedItemPosition())
            {
                case 0: //Repeat - Never
                    timePeriod.SetRepeatFrequency(Repeat.NEVER);
                    //Set date to the selected date
                    //timePeriod.SetDate( date );
                    break;
                case 1: //Repeat - Daily
                    timePeriod.SetRepeatFrequency(Repeat.DAILY);
                    //Set date to the selected date
                    //timePeriod.SetDate( date );
                    break;
                case 2: //Repeat - Weekly
                    timePeriod.SetRepeatFrequency(Repeat.WEEKLY);
                    if (AllDaysOff()){
                        Toast.makeText(getActivity(), "Check at least 1 day of the week to repeat on.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        timePeriod.SetDayOfWeek(0, GetValMon());
                        timePeriod.SetDayOfWeek(1, GetValTues());
                        timePeriod.SetDayOfWeek(2, GetValWed());
                        timePeriod.SetDayOfWeek(3, GetValThur());
                        timePeriod.SetDayOfWeek(4, GetValFri());
                        timePeriod.SetDayOfWeek(5, GetValSat());
                        timePeriod.SetDayOfWeek(6, GetValSun());

                        //Set date to nearest Mon/tues/wed/thur/fri/sat/sun to the selected date
                        //if (GetValMon() && date.getDayOfWeek() <= 1) { timePeriod.SetDate( TimePeriod.calcNextDayOfWeek(date, 1) ); }
                        //else if (GetValTues() && date.getDayOfWeek() <= 2) { timePeriod.SetDate( TimePeriod.calcNextDayOfWeek(date, 2) ); }
                        //else if (GetValWed() && date.getDayOfWeek() <= 3) { timePeriod.SetDate( TimePeriod.calcNextDayOfWeek(date, 3) ); }
                        //else if (GetValThur() && date.getDayOfWeek() <= 4) { timePeriod.SetDate( TimePeriod.calcNextDayOfWeek(date, 4) ); }
                        //else if (GetValFri() && date.getDayOfWeek() <= 5) { timePeriod.SetDate( TimePeriod.calcNextDayOfWeek(date, 5) ); }
                        //else if (GetValSat() && date.getDayOfWeek() <= 6) { timePeriod.SetDate( TimePeriod.calcNextDayOfWeek(date, 6) ); }
                        //else if (GetValSun() && date.getDayOfWeek() <= 7) { timePeriod.SetDate( TimePeriod.calcNextDayOfWeek(date, 7) ); }
                        //else { timePeriod.SetDate(date); }
                    }
                    break;
                case 3: //Repeat - Monthly
                    timePeriod.SetRepeatFrequency(Repeat.MONTHLY);

                    timePeriod.SetRepeatDayOfMonth(Integer.parseInt(editText_dayOfMonth.getText().toString()));

                    //Set date to the nearest DayOfMonth to the selected date
                    //timePeriod.SetDate( TimePeriod.calcNextDayOfMonth(date, timePeriod.GetDayOfMonth()) );
                    break;
                case 4: //Repeat - Yearly
                    timePeriod.SetRepeatFrequency(Repeat.YEARLY);

                    timePeriod.SetDateOfYear(dateOfYear);

                    //Set date to the nearest DateOfYear to the selected date
                    //timePeriod.SetDate(TimePeriod.calcNextDateOfYear(date, timePeriod.GetDateOfYear()));
                    break;
            }
            timePeriod.SetRepeatEveryN(GetEveryN());

            //Repeat Until spinner
            switch (spinner_repeatUntil.getSelectedItemPosition()) {
                case 0: //Repeat - forever
                    timePeriod.SetRepeatUntil(RepeatUntil.FOREVER);
                    timePeriod.SetRepeatUntilDate(null);
                    timePeriod.SetRepeatANumberOfTimes(1);
                    break;
                case 1: //Repeat - Until a date
                    timePeriod.SetRepeatUntil(RepeatUntil.DATE);
                    timePeriod.SetRepeatUntilDate(dateUntil);
                    timePeriod.SetRepeatANumberOfTimes(1);
                    break;
                case 2: //Repeat - A number of times
                    timePeriod.SetRepeatUntil(RepeatUntil.TIMES);
                    timePeriod.SetRepeatUntilDate(null);
                    timePeriod.SetRepeatANumberOfTimes( Math.max(Integer.parseInt(editText_repeatNumberTimes.getText().toString()), 1) );
                    break;
            }
        } else {
            //Reset repeat frequency
            timePeriod.SetRepeatFrequency(Repeat.NEVER);
            //Set date to the selected date
            //timePeriod.SetDate( date );
        }


        return timePeriod;
    }

    //Mutators
    public void SetTimePeriod(TimePeriod tp){
        timePeriod = tp;

        if (timePeriod != null) {
            if (getView() != null) {

                //Set date
                date = timePeriod.GetDate();
                if (date != null) {
                    button_setDate.setText(date.toString(ProfileManager.simpleDateFormat));
                }

                if (timePeriod.GetRepeatFrequency() != Repeat.NEVER) { //If no repeat is set, set the date normally
                    //Repeat Checkbox
                    checkbox_repeat.setChecked(true);

                    //VISIBILITY changes
                    spinner_repeatFrequency.setVisibility(View.VISIBLE);

                    //Spinner repeat type
                    spinner_repeatFrequency.setSelection(timePeriod.GetRepeatFrequencyIndex(timePeriod.GetRepeatFrequency()));

                    //Switch case for repeat types
                    switch (timePeriod.GetRepeatFrequency()) {
                        case NEVER:
                            //Do nothing
                            break;
                        case DAILY:
                            break;
                        case WEEKLY:
                            SetValMon(timePeriod.GetDayOfWeek(0));
                            SetValTues(timePeriod.GetDayOfWeek(1));
                            SetValWed(timePeriod.GetDayOfWeek(2));
                            SetValThur(timePeriod.GetDayOfWeek(3));
                            SetValFri(timePeriod.GetDayOfWeek(4));
                            SetValSat(timePeriod.GetDayOfWeek(5));
                            SetValSun(timePeriod.GetDayOfWeek(6));
                            break;
                        case MONTHLY:
                            editText_dayOfMonth.setText(String.valueOf(Math.min(Math.max(timePeriod.GetRepeatDayOfMonth(), 1), 31)));
                            break;
                        case YEARLY:
                            if (dateOfYear == null) { dateOfYear = new LocalDate(); }
                            dateOfYear = timePeriod.GetDateOfYear();
                            button_monthDayOfYear.setText(dateOfYear.toString(ProfileManager.simpleDateFormatNoYear));
                            break;
                        default:
                    }
                    SetEveryN(timePeriod.GetRepeatEveryN());

                    //Sub fragment selection
                    _set_repeatFrequencySelection(spinner_repeatFrequency.getSelectedItemPosition());

                    //Reset repeatEveryN to 1 if the repeat Frequency is changed
                    //if (timePeriod.GetRepeatEveryN() != 1){ SetEveryN(1); }


                    //Spinner repeat until
                    spinner_repeatUntil.setSelection(timePeriod.GetRepeatUntilIndex(timePeriod.GetRepeatUntil()), false);

                    switch (timePeriod.GetRepeatUntil()) {
                        case FOREVER:
                            dateUntil = null;
                            button_repeatUntilDate.setText("Repeat Until");
                            editText_repeatNumberTimes.setText("1");

                            editText_repeatNumberTimes.setVisibility(View.GONE);
                            textView_repeatUntilTimes.setVisibility(View.GONE);
                            button_repeatUntilDate.setVisibility(View.GONE);
                            break;
                        case DATE:
                            dateUntil = timePeriod.GetRepeatUntilDate();
                            editText_repeatNumberTimes.setText("1");
                            if (dateUntil != null) { button_repeatUntilDate.setText(dateUntil.toString(ProfileManager.simpleDateFormat)); }

                            editText_repeatNumberTimes.setVisibility(View.GONE);
                            textView_repeatUntilTimes.setVisibility(View.GONE);
                            button_repeatUntilDate.setVisibility(View.VISIBLE);
                            break;
                        case TIMES:
                            dateUntil = null;
                            editText_repeatNumberTimes.setText(String.valueOf(timePeriod.GetRepeatANumberOfTimes()));

                            editText_repeatNumberTimes.setVisibility(View.VISIBLE);
                            textView_repeatUntilTimes.setVisibility(View.VISIBLE);
                            button_repeatUntilDate.setVisibility(View.GONE);
                            break;
                    }
                }

                // Blacklist dates
                //Set listview adapter
                blacklistAdapter = new AdapterBlacklistDates(getActivity(), timePeriod);
                recyclerView_blacklistDates.setAdapter(blacklistAdapter);

                //LinearLayoutManager for RecyclerView
                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                llm.setOrientation(NpaLinearLayoutManager.VERTICAL);
                llm.scrollToPosition(0);
                recyclerView_blacklistDates.setLayoutManager(llm);

                //blacklistAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, timePeriod.GetBlacklistDatesString());
                /*listView_blacklistDates.setAdapter(blacklistAdapter);
                listView_blacklistDates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String items[] = {"Remove"};
                        final int pos = position;
                        new android.support.v7.app.AlertDialog.Builder(getActivity()).setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item) {
                                    case 0: //Delete
                                        timePeriod.removeBlacklistDate(timePeriod.getBlacklistDate(pos));

                                        blacklistAdapter.clear();
                                        blacklistAdapter.addAll(timePeriod.GetBlacklistDatesString());
                                        break;
                                    default:
                                        dialog.cancel();
                                        break;
                                }
                            }
                        }).create().show();
                    }
                });
                */
            }
        }
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_time_period, container, false);

        //Dates/Formats
        date = new LocalDate();
        dateUntil = new LocalDate();
        dateOfYear = new LocalDate();


        //Blacklist dates list
        card_blacklist_dates = (CardView) myFragmentView.findViewById(R.id.card_newExpense_blacklist);
        recyclerView_blacklistDates = (RecyclerView) myFragmentView.findViewById(R.id.recyclerView_blacklistDates);


        //Shared between repeat types
        relativeLayout_fragments = (RelativeLayout) myFragmentView.findViewById(R.id.relativeLayout_fragmentRepeat);
        textView_every = (TextView) myFragmentView.findViewById(R.id.textView_every);
        textView_repeatType = (TextView) myFragmentView.findViewById(R.id.textView_repeatType);
        editText_repeatFrequency = (EditText) myFragmentView.findViewById(R.id.editText_repeatFrequency);

        //Repeat weekly
        checkBox_mon = (CheckBox) myFragmentView.findViewById(R.id.checkBox_repeatWeekly_mon);
        checkBox_tues = (CheckBox) myFragmentView.findViewById(R.id.checkBox_repeatWeekly_tues);
        checkBox_wed = (CheckBox) myFragmentView.findViewById(R.id.checkBox_repeatWeekly_wed);
        checkBox_thur = (CheckBox) myFragmentView.findViewById(R.id.checkBox_repeatWeekly_thur);
        checkBox_fri = (CheckBox) myFragmentView.findViewById(R.id.checkBox_repeatWeekly_fri);
        checkBox_sat = (CheckBox) myFragmentView.findViewById(R.id.checkBox_repeatWeekly_sat);
        checkBox_sun = (CheckBox) myFragmentView.findViewById(R.id.checkBox_repeatWeekly_sun);

        //Repeat Monthly
        editText_dayOfMonth = (EditText) myFragmentView.findViewById(R.id.editText_dayOfMonth);
        textView_dayOfMonthWarning = (TextView) myFragmentView.findViewById(R.id.textview_dayOfMonthWarning);

        //Repeat Yearly
        button_monthDayOfYear = (Button) myFragmentView.findViewById(R.id.button_monthDayOfYear);

        //Enable/disable repeating
        checkbox_repeat = (CheckBox) myFragmentView.findViewById(R.id.checkBox_repeat);

        //Repeat frequency and until
        spinner_repeatFrequency = (Spinner) myFragmentView.findViewById(R.id.spinner_repeatFrequency);
        spinner_repeatUntil = (Spinner) myFragmentView.findViewById(R.id.spinner_repeatUntil);

        //Repeat Until # of times
        editText_repeatNumberTimes = (EditText) myFragmentView.findViewById(R.id.editText_repeatNumberOccurences);
        textView_repeatUntilTimes = (TextView) myFragmentView.findViewById(R.id.textView_repeatUntilTimes);


        //Repeat toggle
        checkbox_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkbox_repeat.isChecked()) { //Not Checked
                    //Spinner visibility
                    spinner_repeatFrequency.setVisibility( View.GONE );
                    spinner_repeatUntil.setVisibility( View.GONE );

                    //Spinner repeatUntil sub 'fragments' visibility
                    button_repeatUntilDate.setVisibility( View.GONE );
                    editText_repeatNumberTimes.setVisibility( View.GONE );
                    textView_repeatUntilTimes.setVisibility(  View.GONE );

                    //Spinner repeatFrequency sub 'fragments' visibility
                    relativeLayout_fragments.setVisibility( View.GONE );
                    toggleRepeatShared(View.GONE);
                    toggleRepeatWeekly( View.GONE );
                    toggleRepeatMonthly( View.GONE );
                    toggleRepeatYearly( View.GONE );
                }
                else //Checked
                {
                    //Spinner visibility
                    spinner_repeatFrequency.setVisibility( View.VISIBLE );
                    spinner_repeatUntil.setVisibility( View.VISIBLE );

                    //Repeat Frequency selection (Set visibility of 'fragments')
                    relativeLayout_fragments.setVisibility( View.VISIBLE );
                    _set_repeatFrequencySelection(spinner_repeatFrequency.getSelectedItemPosition());
                }
            }
        });

        //Date buttons
        button_setDate = (Button) myFragmentView.findViewById(R.id.button_newExpense_date);
        button_setDate.setText(date.toString(ProfileManager.simpleDateFormat));
        button_setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide soft keyboard
                ProfileManager.hideSoftKeyboard(getActivity(), v);

                //Open date picker dialog
                if (date == null) { date = new LocalDate(); }
                new DatePickerDialog(getActivity(), datePicker, date.getYear(), date.getMonthOfYear()-1, date.getDayOfMonth()).show();
            }
        });

        button_repeatUntilDate = (Button) myFragmentView.findViewById(R.id.button_repeatUntil);
        button_repeatUntilDate.setText(dateUntil.toString(ProfileManager.simpleDateFormat));
        button_repeatUntilDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide soft keyboard
                ProfileManager.hideSoftKeyboard(getActivity(), v);

                //Open date picker dialog
                if (dateUntil == null) { dateUntil = new LocalDate(); }
                DatePickerDialog d = new DatePickerDialog(getActivity(), datePicker2, dateUntil.getYear(), dateUntil.getMonthOfYear()-1, dateUntil.getDayOfMonth());
                d.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        spinner_repeatUntil.setSelection(0, true);
                    }
                });
                d.show();
            }
        });

        button_monthDayOfYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide soft keyboard
                ProfileManager.hideSoftKeyboard(getActivity(), v);

                //Open date picker dialog
                if (dateOfYear == null) { dateOfYear = new LocalDate(); }
                DatePickerDialog d = new DatePickerDialog(getActivity(), datePicker3, dateOfYear.getYear(), dateOfYear.getMonthOfYear()-1, dateOfYear.getDayOfMonth());
                d.show();
            }
        });


        //Repeat spinner (visibility)
        repeatAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.repeat_array, R.layout.spinner_dropdown_title);
        repeatAdapter.setDropDownViewResource(R.layout.spinner_dropdown_list_primary);
        spinner_repeatFrequency.setAdapter(repeatAdapter);
        spinner_repeatFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _set_repeatFrequencySelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        //Repeat until spinner
        repeatUntilAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.repeat_until, R.layout.spinner_dropdown_title);
        repeatUntilAdapter.setDropDownViewResource(R.layout.spinner_dropdown_list_primary);
        spinner_repeatUntil.setAdapter(repeatUntilAdapter);
        //RepeatUntil spinner listener
        spinner_repeatUntil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0: //Repeat - Forever
                        dateUntil = null;
                        editText_repeatNumberTimes.setText("1");

                        editText_repeatNumberTimes.setVisibility(View.GONE);
                        textView_repeatUntilTimes.setVisibility(View.GONE);
                        button_repeatUntilDate.setVisibility(View.GONE);
                        break;
                    case 1: //Repeat - Until a date
                        editText_repeatNumberTimes.setVisibility(View.GONE);
                        textView_repeatUntilTimes.setVisibility(View.GONE);
                        button_repeatUntilDate.setVisibility(View.VISIBLE);
                        break;
                    case 2: //Repeat - A number of times
                        editText_repeatNumberTimes.setVisibility(View.VISIBLE);
                        textView_repeatUntilTimes.setVisibility(View.VISIBLE);
                        button_repeatUntilDate.setVisibility(View.GONE);
                        //(new NumberDialog()).show(fragmentManager, "numberPicker");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        //EditText dayOfMonth clamping
        editText_dayOfMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (noInfiniteLoopPlease){
                    try {
                        int n = Integer.parseInt(String.valueOf(s));

                        //Clamp editText_dayOfMonth between 1 and 31
                        if (n > 31 || n < 1) {
                            noInfiniteLoopPlease = false;
                            editText_dayOfMonth.setText(String.valueOf(Math.min(Math.max(n, 1), 31)));
                        }

                        //Day Of Month warning label
                        textView_dayOfMonthWarning.setVisibility( (n >= 29 ? View.VISIBLE : View.GONE) );
                    }
                    catch (NumberFormatException ex){}
                }
                else {
                    noInfiniteLoopPlease = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });


        // Inflate the layout for this fragment (After this fragment has been created)
        return myFragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //Set Time period (After fragment created)
        SetTimePeriod(timePeriod);
    }


    //Date picker dialog listener
    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            //Set date
            date = new LocalDate(year, monthOfYear+1, dayOfMonth);

            //Format button text
            if (date != null && button_setDate != null) { button_setDate.setText(date.toString(ProfileManager.simpleDateFormat)); }
        }
    };
    DatePickerDialog.OnDateSetListener datePicker2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            //Set date
            dateUntil = new LocalDate(year, monthOfYear+1, dayOfMonth);

            //Format button text
            if (dateUntil != null && button_repeatUntilDate != null) { button_repeatUntilDate.setText(dateUntil.toString(ProfileManager.simpleDateFormat)); }
        }
    };
    DatePickerDialog.OnDateSetListener datePicker3 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            //Set date
            dateOfYear = new LocalDate(year, monthOfYear+1, dayOfMonth);

            //Format button text
            if (dateOfYear != null && button_monthDayOfYear != null) { button_monthDayOfYear.setText(dateOfYear.toString(ProfileManager.simpleDateFormatNoYear)); }
        }
    };


    //Accessors
    public int GetEveryN(){
        if (editText_repeatFrequency != null){
            //Lower clamp of value to 1
            return Math.max(Integer.parseInt(editText_repeatFrequency.getText().toString()), 1);
        }
        else { return 0; }
    }

    public Boolean AllDaysOff() {  return (!GetValMon() && !GetValTues() && !GetValWed() && !GetValThur() && !GetValFri() && !GetValSat() && !GetValSun()); }
    public Boolean GetValMon(){ return (checkBox_mon!=null && checkBox_mon.isChecked()); }
    public Boolean GetValTues(){ if (checkBox_tues!=null) { return checkBox_tues.isChecked(); } else { return false; } }
    public Boolean GetValWed(){ if (checkBox_wed!=null) { return checkBox_wed.isChecked(); } else { return false; } }
    public Boolean GetValThur(){ if (checkBox_thur!=null) { return checkBox_thur.isChecked(); } else { return false; } }
    public Boolean GetValFri(){ if (checkBox_fri!=null) { return checkBox_fri.isChecked(); } else { return false; } }
    public Boolean GetValSat(){ if (checkBox_sat!=null) { return checkBox_sat.isChecked(); } else { return false; } }
    public Boolean GetValSun(){ if (checkBox_sun!=null) { return checkBox_sun.isChecked(); } else { return false; } }

    //public int GetMonthOfYear(){
    //    if (numberPicker_month != null){
    //        return numberPicker_month.getValue();
    //    }
    //    else { return 0; }
    //}
    //public int GetDayOfMonth(){
    //    if (numberPicker_day != null){
    //        return numberPicker_day.getValue();
    //    }
    //    else { return 0; }
    //}


    //Mutator
    //private void _set_numberPicker_day(Calendar cal){
    //    numberPicker_day.setMaxValue(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    //    numberPicker_day.setMinValue(cal.getActualMinimum(Calendar.DAY_OF_MONTH));
    //}

    private void _set_repeatFrequencySelection(int position){

        switch (position) {
            case 0: //Repeat - Never
                spinner_repeatUntil.setVisibility(View.GONE);
                button_repeatUntilDate.setVisibility(View.GONE);
                relativeLayout_fragments.setVisibility(View.GONE);

                toggleRepeatShared(View.GONE);
                toggleRepeatWeekly(View.GONE);
                toggleRepeatMonthly(View.GONE);
                toggleRepeatYearly(View.GONE);
                break;
            case 1: //Repeat - Daily
                textView_repeatType.setText(R.string.repeat_days);
                relativeLayout_fragments.setVisibility(View.VISIBLE);
                spinner_repeatUntil.setVisibility(View.VISIBLE);
                button_repeatUntilDate.setVisibility( (spinner_repeatUntil.getSelectedItemPosition() == 1 ? View.VISIBLE : View.GONE) );

                toggleRepeatShared(View.VISIBLE);
                toggleRepeatWeekly(View.GONE);
                toggleRepeatMonthly(View.GONE);
                toggleRepeatYearly(View.GONE);
                break;
            case 2: //Repeat - Weekly
                textView_repeatType.setText(R.string.repeat_weeks);
                relativeLayout_fragments.setVisibility(View.VISIBLE);
                spinner_repeatUntil.setVisibility(View.VISIBLE);
                button_repeatUntilDate.setVisibility( (spinner_repeatUntil.getSelectedItemPosition() == 1 ? View.VISIBLE : View.GONE) );

                toggleRepeatShared(View.VISIBLE);
                toggleRepeatWeekly(View.VISIBLE);
                toggleRepeatMonthly(View.GONE);
                toggleRepeatYearly(View.GONE);
                break;
            case 3: //Repeat - Monthly
                textView_repeatType.setText(R.string.repeat_months);
                relativeLayout_fragments.setVisibility(View.VISIBLE);
                spinner_repeatUntil.setVisibility(View.VISIBLE);
                button_repeatUntilDate.setVisibility( (spinner_repeatUntil.getSelectedItemPosition() == 1 ? View.VISIBLE : View.GONE) );

                toggleRepeatShared(View.VISIBLE);
                toggleRepeatWeekly(View.GONE);
                toggleRepeatYearly(View.GONE);
                toggleRepeatMonthly(View.VISIBLE);
                break;
            case 4: //Repeat - Yearly
                textView_repeatType.setText(R.string.repeat_years);
                relativeLayout_fragments.setVisibility(View.VISIBLE);
                spinner_repeatUntil.setVisibility(View.VISIBLE);
                button_repeatUntilDate.setVisibility( (spinner_repeatUntil.getSelectedItemPosition() == 1 ? View.VISIBLE : View.GONE) );

                toggleRepeatShared(View.VISIBLE);
                toggleRepeatWeekly(View.GONE);
                toggleRepeatMonthly(View.GONE);
                toggleRepeatYearly(View.VISIBLE);
                break;
        }
    }

    public void SetValMon(Boolean val){ if (checkBox_mon!=null) { checkBox_mon.setChecked(val); } }
    public void SetValTues(Boolean val){if (checkBox_tues!=null) { checkBox_tues.setChecked(val); } }
    public void SetValWed(Boolean val){ if (checkBox_wed!=null) { checkBox_wed.setChecked(val); } }
    public void SetValThur(Boolean val){ if (checkBox_thur!=null) { checkBox_thur.setChecked(val); } }
    public void SetValFri(Boolean val){ if (checkBox_fri!=null) { checkBox_fri.setChecked(val); } }
    public void SetValSat(Boolean val){ if (checkBox_sat!=null) { checkBox_sat.setChecked(val); } }
    public void SetValSun(Boolean val){ if (checkBox_sun!=null) { checkBox_sun.setChecked(val); } }

    public void SetEveryN(int n){
        if (editText_repeatFrequency != null) {
            //Lower clamp of value to 1
            editText_repeatFrequency.setText(String.valueOf(Math.max(n, 1)));
        }
    }


    //Hide/Show repeat 'fragments'
    private void toggleRepeatShared(int v) {
        textView_every.setVisibility(v);
        textView_repeatType.setVisibility(v);
        editText_repeatFrequency.setVisibility(v);
        card_blacklist_dates.setVisibility(v);
    }
    private void toggleRepeatWeekly(int v) {
        checkBox_mon.setVisibility(v);
        checkBox_tues.setVisibility(v);
        checkBox_wed.setVisibility(v);
        checkBox_thur.setVisibility(v);
        checkBox_fri.setVisibility(v);
        checkBox_sat.setVisibility(v);
        checkBox_sun.setVisibility(v);
    }
    private void toggleRepeatMonthly(int v) {
        editText_dayOfMonth.setVisibility(v);
    }
    private void toggleRepeatYearly(int v) {
        button_monthDayOfYear.setVisibility(v);
    }
}
