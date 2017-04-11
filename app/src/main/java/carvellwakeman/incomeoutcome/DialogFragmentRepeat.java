package carvellwakeman.incomeoutcome;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import org.joda.time.LocalDate;

public class DialogFragmentRepeat extends DialogFragment
{
    ActivityNewTransaction _parent;
    TimePeriod _timePeriod;

    //Dates
    LocalDate dateUntil;

    //Adapters
    ArrayAdapter<CharSequence> repeatAdapter;
    ArrayAdapter<CharSequence> repeatUntilAdapter;

    //Sections
    LinearLayout linearLayout_repeatSpinner;
        Spinner spinner_repeatFrequency;
    RelativeLayout relativeLayout_repeateveryn;
        EditText editText_repeateveryn;
        TextView textView_every;
    RelativeLayout relativeLayout_weekly;
        CheckBox checkBox_mon, checkBox_tues, checkBox_wed, checkBox_thur, checkBox_fri, checkBox_sat, checkBox_sun;
    RelativeLayout relativeLayout_repeatuntil;
        Spinner spinner_repeatUntil;
        EditText editText_repeatNumberTimes;
        Button button_repeatUntil;
        TextView textView_repeatNumberTimes;

    //Done button
    Button button_positive;


    static DialogFragmentRepeat newInstance(ActivityNewTransaction parent, TimePeriod tp) {
        DialogFragmentRepeat fg = new DialogFragmentRepeat();
        fg._parent = parent;
        fg._timePeriod = new TimePeriod(tp);
        return fg;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_repeat, container, false);
        view.setBackgroundColor(Color.WHITE);

        //Inflate the layout to use as dialog or embedded fragment
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        //Today
        dateUntil = new LocalDate();

        //Done button
        button_positive = (Button) view.findViewById(R.id.button_dialogrep_positive);
        button_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Repeat Until null date case
                if (_timePeriod.GetRepeatUntil()==RepeatUntil.DATE && _timePeriod.GetRepeatUntilDate() == null){
                    _timePeriod.SetRepeatUntilDate(_timePeriod.GetDate());
                }
                switch(_timePeriod.GetRepeatFrequency()){
                    case WEEKLY:
                        if (!AllDaysOff()) {
                            _timePeriod.SetDayOfWeek(0, GetValMon());
                            _timePeriod.SetDayOfWeek(1, GetValTues());
                            _timePeriod.SetDayOfWeek(2, GetValWed());
                            _timePeriod.SetDayOfWeek(3, GetValThur());
                            _timePeriod.SetDayOfWeek(4, GetValFri());
                            _timePeriod.SetDayOfWeek(5, GetValSat());
                            _timePeriod.SetDayOfWeek(6, GetValSun());


                        }
                        break;
                    case MONTHLY:
                        if (_timePeriod.GetDate() != null){
                            _timePeriod.SetRepeatDayOfMonth(_timePeriod.GetDate().getDayOfMonth());
                        }

                        break;
                    case YEARLY:
                        if (_timePeriod.GetDate() != null){
                            _timePeriod.SetDateOfYear(_timePeriod.GetDate());
                        }

                        break;
                    default:
                        break;
                }

                //Sent timeperiod back to parent
                Intent intent = new Intent();
                intent.putExtra("timeperiod", _timePeriod);
                _parent.onActivityResult(0,0,intent);
                dismiss();
            }
        });


        //Views
        linearLayout_repeatSpinner = (LinearLayout) view.findViewById(R.id.linearLayout_repeatspinner);
        spinner_repeatFrequency = (Spinner) view.findViewById(R.id.spinner_repeatFrequency);
        relativeLayout_repeateveryn = (RelativeLayout) view.findViewById(R.id.relativeLayout_repeat_everyn);
        editText_repeateveryn = (EditText) view.findViewById(R.id.editText_repeateveryn);
        textView_every = (TextView) view.findViewById(R.id.textView_repeatType);
        relativeLayout_weekly = (RelativeLayout) view.findViewById(R.id.relativeLayout_repeat_weekly);
        checkBox_mon = (CheckBox) view.findViewById(R.id.checkBox_repeatWeekly_mon);
        checkBox_tues = (CheckBox) view.findViewById(R.id.checkBox_repeatWeekly_tues);
        checkBox_wed = (CheckBox) view.findViewById(R.id.checkBox_repeatWeekly_wed);
        checkBox_thur = (CheckBox) view.findViewById(R.id.checkBox_repeatWeekly_thur);
        checkBox_fri = (CheckBox) view.findViewById(R.id.checkBox_repeatWeekly_fri);
        checkBox_sat = (CheckBox) view.findViewById(R.id.checkBox_repeatWeekly_sat);
        checkBox_sun = (CheckBox) view.findViewById(R.id.checkBox_repeatWeekly_sun);
        relativeLayout_repeatuntil = (RelativeLayout) view.findViewById(R.id.relativeLayout_repeat_until);
        spinner_repeatUntil = (Spinner) view.findViewById(R.id.spinner_repeatUntil);
        editText_repeatNumberTimes = (EditText) view.findViewById(R.id.editText_repeatNumberOccurences);
        button_repeatUntil = (Button) view.findViewById(R.id.button_repeatUntil);
        textView_repeatNumberTimes = (TextView) view.findViewById(R.id.textView_repeatnumbertimes);


        //Checkboxes
        checkBox_mon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { button_positive.setEnabled(!AllDaysOff()); }
        });
        checkBox_tues.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { button_positive.setEnabled(!AllDaysOff()); }
        });
        checkBox_wed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { button_positive.setEnabled(!AllDaysOff()); }
        });
        checkBox_thur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { button_positive.setEnabled(!AllDaysOff()); }
        });
        checkBox_fri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { button_positive.setEnabled(!AllDaysOff()); }
        });
        checkBox_sat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { button_positive.setEnabled(!AllDaysOff()); }
        });
        checkBox_sun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { button_positive.setEnabled(!AllDaysOff()); }
        });

        //Adapters & Spinners
        repeatAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.repeat_array, R.layout.spinner_dropdown_title);
        repeatAdapter.setDropDownViewResource(R.layout.spinner_dropdown_list_primary);
        repeatUntilAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.repeat_until, R.layout.spinner_dropdown_title);
        repeatUntilAdapter.setDropDownViewResource(R.layout.spinner_dropdown_list_primary);

        spinner_repeatFrequency.setAdapter(repeatAdapter);
        spinner_repeatFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Hide soft keyboard
                Helper.hideSoftKeyboard(_parent, view);

                relativeLayout_repeateveryn.setVisibility(View.VISIBLE);
                relativeLayout_weekly.setVisibility(View.GONE);
                relativeLayout_repeatuntil.setVisibility(View.VISIBLE);

                _timePeriod.SetRepeatFrequency(_timePeriod.GetRepeatFrequencyFromIndex(position));

                switch (position) {
                    case 0: //Repeat - Never
                        relativeLayout_repeateveryn.setVisibility(View.GONE);
                        relativeLayout_weekly.setVisibility(View.GONE);
                        relativeLayout_repeatuntil.setVisibility(View.GONE);
                        break;
                    case 1: //Repeat - Daily
                        textView_every.setText(R.string.repeat_day);
                        break;
                    case 2: //Repeat - Weekly
                        relativeLayout_weekly.setVisibility(View.VISIBLE);
                        textView_every.setText(R.string.repeat_week);
                        break;
                    case 3: //Repeat - Monthly
                        textView_every.setText(R.string.repeat_month);
                        break;
                    case 4: //Repeat - Yearly
                        textView_every.setText(R.string.repeat_year);
                        break;
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinner_repeatUntil.setAdapter(repeatUntilAdapter);
        spinner_repeatUntil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (_timePeriod != null) {
                    //Hide soft keyboard
                    Helper.hideSoftKeyboard(_parent, view);

                    _timePeriod.SetRepeatUntil(_timePeriod.GetRepeatUntilFromIndex(position));

                    switch (position) {
                        case 0: //Repeat - Forever
                            editText_repeatNumberTimes.setVisibility(View.GONE);
                            button_repeatUntil.setVisibility(View.GONE);
                            textView_repeatNumberTimes.setVisibility(View.GONE);
                            _timePeriod.SetRepeatANumberOfTimes(1);
                            _timePeriod.SetRepeatUntilDate(null);
                            editText_repeatNumberTimes.setText(R.string.format_one);

                            break;
                        case 1: //Repeat - Until a date
                            editText_repeatNumberTimes.setVisibility(View.GONE);
                            button_repeatUntil.setVisibility(View.VISIBLE);
                            textView_repeatNumberTimes.setVisibility(View.GONE);
                            _timePeriod.SetRepeatANumberOfTimes(1);
                            editText_repeatNumberTimes.setText(R.string.format_one);

                            break;
                        case 2: //Repeat - A number of events
                            editText_repeatNumberTimes.setVisibility(View.VISIBLE);
                            button_repeatUntil.setVisibility(View.GONE);
                            textView_repeatNumberTimes.setVisibility(View.VISIBLE);
                            try {
                                _timePeriod.SetRepeatANumberOfTimes(Integer.parseInt(editText_repeatNumberTimes.getText().toString()));
                            } catch (Exception ex) {}

                            _timePeriod.SetRepeatUntilDate(null);
                            break;
                    }
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        //Repeat Every N
        editText_repeateveryn.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (_timePeriod != null) {
                    try {
                        int n = Integer.parseInt(String.valueOf(s));
                        SetEveryNTitle(Math.max(n, 1));
                        _timePeriod.SetRepeatEveryN(Math.max(n, 1));
                    }
                    catch (NumberFormatException ex) { ex.printStackTrace(); }
                }
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        //Repeat Until
        editText_repeatNumberTimes.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (_timePeriod != null) {
                    try {
                        int n = Integer.parseInt(String.valueOf(s));
                        if (n == 1) { textView_repeatNumberTimes.setText(R.string.repeat_event); }
                        else { textView_repeatNumberTimes.setText(R.string.repeat_events); }
                        _timePeriod.SetRepeatANumberOfTimes(Math.max(n, 1));
                    }
                    catch (NumberFormatException ex) { ex.printStackTrace(); }
                }
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        button_repeatUntil.setText(dateUntil.toString(Helper.getString(R.string.date_format)));
        button_repeatUntil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide soft keyboard
                Helper.hideSoftKeyboard(_parent, v);

                //Open date picker dialog
                if (dateUntil == null) { dateUntil = new LocalDate(); }
                DatePickerDialog d = new DatePickerDialog(getActivity(), datePicker, dateUntil.getYear(), dateUntil.getMonthOfYear()-1, dateUntil.getDayOfMonth());
                d.show();
            }
        });


    }

    @Override
    public void onStart(){
        super.onStart();

        //Time Period
        SetTimePeriod(_timePeriod);
    }



    //Time Period
    public void SetTimePeriod(TimePeriod tp){
        _timePeriod = tp;

        if (_timePeriod != null) {

            //Spinner repeat
            spinner_repeatFrequency.setSelection(_timePeriod.GetRepeatFrequencyIndex(_timePeriod.GetRepeatFrequency()));

            //Switch case for repeat types
            switch (_timePeriod.GetRepeatFrequency()) {
                case NEVER:
                    break;
                case DAILY:
                    break;
                case WEEKLY:
                    SetValMon(_timePeriod.GetDayOfWeek(0));
                    SetValTues(_timePeriod.GetDayOfWeek(1));
                    SetValWed(_timePeriod.GetDayOfWeek(2));
                    SetValThur(_timePeriod.GetDayOfWeek(3));
                    SetValFri(_timePeriod.GetDayOfWeek(4));
                    SetValSat(_timePeriod.GetDayOfWeek(5));
                    SetValSun(_timePeriod.GetDayOfWeek(6));
                    break;
                default:
            }

            //Repeat Every n

            textView_every.setText(String.valueOf(_timePeriod.GetRepeatEveryN()));
            SetEveryN(_timePeriod.GetRepeatEveryN());

            //Spinner repeat until
            spinner_repeatUntil.setSelection(_timePeriod.GetRepeatUntilIndex(_timePeriod.GetRepeatUntil()), false);

            switch (_timePeriod.GetRepeatUntil()) {
                case FOREVER:
                    dateUntil = null;
                    editText_repeatNumberTimes.setText(R.string.format_one);
                    break;
                case DATE:
                    dateUntil = _timePeriod.GetRepeatUntilDate();
                    break;
                case TIMES:
                    dateUntil = null;
                    editText_repeatNumberTimes.setText(String.valueOf(_timePeriod.GetRepeatANumberOfTimes()));
                    break;
            }

            SetRepeatUntilButtonTitle();
        }
    }

    //Repeat Until Date picker
    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            //Set date
            dateUntil = new LocalDate(year, monthOfYear+1, dayOfMonth);
            SetRepeatUntilButtonTitle();
            _timePeriod.SetRepeatUntilDate(dateUntil);
            _timePeriod.SetRepeatUntil(_timePeriod.GetRepeatUntilFromIndex(spinner_repeatUntil.getSelectedItemPosition()));
        }
    };

    //Weekly checkbox values
    public Boolean AllDaysOff() {  return (!GetValMon() && !GetValTues() && !GetValWed() && !GetValThur() && !GetValFri() && !GetValSat() && !GetValSun()); }
    public void SetValMon(Boolean val){ if (checkBox_mon!=null) { checkBox_mon.setChecked(val); } }
    public void SetValTues(Boolean val){if (checkBox_tues!=null) { checkBox_tues.setChecked(val); } }
    public void SetValWed(Boolean val){ if (checkBox_wed!=null) { checkBox_wed.setChecked(val); } }
    public void SetValThur(Boolean val){ if (checkBox_thur!=null) { checkBox_thur.setChecked(val); } }
    public void SetValFri(Boolean val){ if (checkBox_fri!=null) { checkBox_fri.setChecked(val); } }
    public void SetValSat(Boolean val){ if (checkBox_sat!=null) { checkBox_sat.setChecked(val); } }
    public void SetValSun(Boolean val){ if (checkBox_sun!=null) { checkBox_sun.setChecked(val); } }

    public Boolean GetValMon(){ return (checkBox_mon!=null && checkBox_mon.isChecked()); }
    public Boolean GetValTues(){ return checkBox_tues!=null && checkBox_tues.isChecked(); }
    public Boolean GetValWed(){ return checkBox_wed!=null && checkBox_wed.isChecked();}
    public Boolean GetValThur(){ return checkBox_thur!=null && checkBox_thur.isChecked();}
    public Boolean GetValFri(){ return checkBox_fri!=null && checkBox_fri.isChecked();}
    public Boolean GetValSat(){ return checkBox_sat!=null && checkBox_sat.isChecked();}
    public Boolean GetValSun(){ return checkBox_sun!=null && checkBox_sun.isChecked();}

    //Every N
    public void SetEveryN(int n){
        if (editText_repeateveryn != null) {
            //Lower clamp of value to 1
            editText_repeateveryn.setText(String.valueOf(n));
        }

        SetEveryNTitle(n);
    }

    public void SetEveryNTitle(int n){
        if (spinner_repeatFrequency != null) {
            switch (spinner_repeatFrequency.getSelectedItemPosition()) {
                case 1: //Repeat - Daily
                    if (n == 1) { textView_every.setText(R.string.repeat_day); }
                    else { textView_every.setText(R.string.repeat_days); }
                    break;
                case 2: //Repeat - Weekly
                    if (n == 1) { textView_every.setText(R.string.repeat_week); }
                    else { textView_every.setText(R.string.repeat_weeks); }
                    break;
                case 3: //Repeat - Monthly
                    if (n == 1) { textView_every.setText(R.string.repeat_month); }
                    else { textView_every.setText(R.string.repeat_months); }
                    break;
                case 4: //Repeat - Yearly
                    if (n == 1) { textView_every.setText(R.string.repeat_year); }
                    else { textView_every.setText(R.string.repeat_years); }
                    break;
            }

        }
    }


    public void SetRepeatUntilButtonTitle(){
        if (button_repeatUntil != null) {
            if (dateUntil == null){
                button_repeatUntil.setText(R.string.time_setdate);
            }
            else {
                button_repeatUntil.setText(dateUntil.toString(Helper.getString(R.string.date_format)));
            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();

        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //dialog.getWindow().setBackgroundDrawable(null);
        }
    }
}