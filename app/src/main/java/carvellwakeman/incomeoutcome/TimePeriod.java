package carvellwakeman.incomeoutcome;

import org.joda.time.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class TimePeriod implements java.io.Serializable, BaseEntity
{
    enum Repeat{
        NEVER,
        DAILY,
        WEEKLY,
        MONTHLY,
        YEARLY
    }
    enum RepeatUntil{
        FOREVER,
        DATE,
        TIMES
    }

    private int _uniqueID;

    //Date of occurence (Null if repeatUntil is not Repeat.NEVER)
    private LocalDate date;
    private LocalDate _firstOccurenceDate;

    //Frequency of repeat (Never, Daily, Weekly, Monthly, Yearly)
    private Repeat repeatFrequency;

    //Repeat until (Forever, a date, a number of times)
    private RepeatUntil repeatUntil;
        private int repeatUntilTimes;
        private LocalDate repeatUntilDate;

    //All
    private int repeatEveryN;

    //Weekly
    private Boolean[] repeatDayOfWeek;

    //Monthly (and/or) yearly (This variable is reused depending on context, monthly or yearly time period repeat
    private int repeatDayOfMonth;

    //Yearly
    private LocalDate dateOfYear;


    // Blacklist dates
    private ArrayList<BlacklistDate> blacklistDates;
    private ArrayList<BlacklistDate> _blacklistDatesQueue;


    public TimePeriod()
    {
        _uniqueID = System.identityHashCode(this);

        //Dates
        date = LocalDate.now();
        _firstOccurenceDate = null;

        //Frequency of repeat (Never, Daily, Weekly, Monthly, Yearly)
        repeatFrequency = Repeat.NEVER;

        //Repeat until (Forever, a date, a number of times)
        repeatUntil = RepeatUntil.FOREVER;
        repeatUntilTimes = 0;
        repeatUntilDate = null;

        //Daily, weekly, monthly, yearly
        repeatEveryN = 1;

        //Weekly
        repeatDayOfWeek = new Boolean[7];
        repeatDayOfWeek[0] = repeatDayOfWeek[1] = repeatDayOfWeek[2] = repeatDayOfWeek[3] = repeatDayOfWeek[4] = repeatDayOfWeek[5] = repeatDayOfWeek[6] = false; //Set all days of the week to be unused

        //Monthly
        repeatDayOfMonth = 1;

        //Yearly
        dateOfYear = null;


        // Blacklist dates
        blacklistDates = new ArrayList<>();
        _blacklistDatesQueue = new ArrayList<>();

        CalculateFirstOccurrence();
    }
    public TimePeriod(TimePeriod copy)
    {
        //Dates
        date = copy.GetDate();
        _firstOccurenceDate = copy.GetFirstOccurrence();

        //Frequency of repeat (Never, Daily, Weekly, Monthly, Yearly)
        repeatFrequency = copy.GetRepeatFrequency();

        //Repeat until (Forever, a date, a number of times)
        repeatUntil = copy.GetRepeatUntil();
        repeatUntilTimes = copy.GetRepeatANumberOfTimes();
        repeatUntilDate = copy.GetRepeatUntilDate();

        //Daily, weekly, monthly, yearly
        repeatEveryN = copy.GetRepeatEveryN();

        //Weekly
        repeatDayOfWeek = copy.repeatDayOfWeek;

        //Monthly
        repeatDayOfMonth = copy.GetRepeatDayOfMonth();

        //Yearly
        dateOfYear = copy.GetDateOfYear();

        // Blacklist dates
        blacklistDates = new ArrayList<>();
        _blacklistDatesQueue = new ArrayList<>();
        blacklistDates.addAll(copy.GetBlacklistDates());
    }
    public TimePeriod(LocalDate _date)
    {
        this();
        date = _date;
    }


    public int GetID() { return _uniqueID; }
    public void SetID(int ID){ _uniqueID = ID; }


    //Blacklist
    public void AddBlacklistDate(LocalDate date, Boolean edited){ blacklistDates.add(new BlacklistDate(date, edited)); }
    public void FlushBlacklistDateQueue(){
        if (_blacklistDatesQueue !=  null) {
            for (int i = 0; i < _blacklistDatesQueue.size(); i++) {
                //Remove objects from queue
                blacklistDates.remove(_blacklistDatesQueue.get(i));
            }
        }
    }
    public void QueueBlacklistDateRemoval(LocalDate date) {
        if (blacklistDates != null) {
            for (int i = 0; i < blacklistDates.size(); i++) {
                if (blacklistDates.get(i).date.compareTo(date) == 0) {
                    //Queue blacklist date for deletion
                    _blacklistDatesQueue.add(blacklistDates.get(i));
                }
            }
        }
    }
    public void RemoveBlacklistDate(LocalDate date) {
        QueueBlacklistDateRemoval(date);
        FlushBlacklistDateQueue();
    }
    //public void ClearBlacklistDates(){ blacklistDates.clear(); }
    public void ClearBlacklistQueue() { if (_blacklistDatesQueue != null){ _blacklistDatesQueue.clear(); } }

    public String GetBlacklistDatesSaving(){
        String str = "";

        if (blacklistDates != null) {
            for (int i = 0; i < blacklistDates.size(); i++) {
                str += blacklistDates.get(i).date.toString(Helper.getString(R.string.date_format_saving)) + "|" + (blacklistDates.get(i).edited ? 1 : 0) + ",";
            }
            //Remove last comma
            if (str.length() > 0) { str = str.substring(0, str.length() - 1); }
        }

        return str;
    }

    public ArrayList<BlacklistDate> GetBlacklistDates() { return blacklistDates; }
    public BlacklistDate GetBlacklistDate(int index){
        if (blacklistDates!=null && index >= 0 && blacklistDates.size() > 0){ return blacklistDates.get(index); }
        return null;
    }
    public int GetBlacklistDatesCount() {
        if (blacklistDates!=null){ return blacklistDates.size(); }
        return -1;
    }
    public int GetBlacklistDatesCountWithoutQueue() {
        if (blacklistDates != null && _blacklistDatesQueue != null) { return blacklistDates.size() - _blacklistDatesQueue.size(); }
        return -1;
    }


    public String GetBlacklistDateString(int index) { //*Excluding _blacklistDatesQueue
        if (blacklistDates!=null) {
            if (blacklistDates.get(index) != null) {
                if (!_blacklistDatesQueue.contains(blacklistDates.get(index))) {
                    return blacklistDates.get(index).date.toString(Helper.getString(R.string.date_format)) + (blacklistDates.get(index).edited ? " (edited)" : " (deleted)");
                }
            }
        }

        return "";
    }


    //Helpers
    public static LocalDate dateMin(LocalDate date1, LocalDate date2){
        if (date2 == null || ( date1 != null && date1.compareTo(date2) <= 0 ) ) { return date1; }
        else { return date2; }
    }
    public static LocalDate dateMax(LocalDate date1, LocalDate date2){
        if (date2 == null || ( date1 != null && date1.compareTo(date2) >= 0 ) ) { return date1; }
        else { return date2; }
    }

    public static LocalDate calcFirstDayOfWeek(Boolean[] dayOfWeek, LocalDate date){
        if (dayOfWeek[0]) { return calcNextDayOfWeek(date, 1); }
        else if (dayOfWeek[1] && date.getDayOfWeek() <= 2) { return calcNextDayOfWeek(date, 2); }
        else if (dayOfWeek[2] && date.getDayOfWeek() <= 3) { return calcNextDayOfWeek(date, 3); }
        else if (dayOfWeek[3] && date.getDayOfWeek() <= 4) { return calcNextDayOfWeek(date, 4); }
        else if (dayOfWeek[4] && date.getDayOfWeek() <= 5) { return calcNextDayOfWeek(date, 5); }
        else if (dayOfWeek[5] && date.getDayOfWeek() <= 6) { return calcNextDayOfWeek(date, 6); }
        else if (dayOfWeek[6] && date.getDayOfWeek() <= 7) { return calcNextDayOfWeek(date, 7); }
        else { return date; }
    }

    public static LocalDate calcNextDayOfWeek(LocalDate date, int dayOfWeek) {
        if (date != null) {
            return (date.getDayOfWeek() <= dayOfWeek) ? date.withDayOfWeek(dayOfWeek) : date.plusWeeks(1).withDayOfWeek(dayOfWeek);
        }
        return null;
    }
    public static LocalDate calcPrevDayOfWeek(LocalDate date, int dayOfWeek) {
        if (date != null) {
            if (date.getDayOfWeek() < dayOfWeek) {
                return date.minusWeeks(1).withDayOfWeek(dayOfWeek);
            }
            else { return date.withDayOfWeek(dayOfWeek); }
        }
        return null;
    }

    public static LocalDate calcNextDayOfMonth(LocalDate date, int dayOfMonth) {
        if (date != null) {
            dayOfMonth = Math.min(30,dayOfMonth); // Added after 'org.joda.time.IllegalFieldValueException: Value 31 for dayOfMonth must be in the range [1,30]', is it necessary though?
            return (date.getDayOfMonth() <= dayOfMonth) ? date.withDayOfMonth(dayOfMonth) : date.plusMonths(1).withDayOfMonth(dayOfMonth);
        }
        return null;
    }
    public static LocalDate calcPrevDayOfMonth(LocalDate date, int dayOfMonth) {
        if (date != null) {
            return (date.getDayOfMonth() >= dayOfMonth) ? date.withDayOfMonth(dayOfMonth) : date.minusMonths(1).withDayOfMonth(dayOfMonth);
        }
        return null;
    }

    public static LocalDate calcNextDateOfYear(LocalDate date, LocalDate dateInYear){
        if (date != null && dateInYear != null) {
            return (date.compareTo(dateInYear) <= 0 ? dateInYear : dateInYear.plusYears(1));
        }
        return null;
    }
    public static LocalDate calcPrevDateOfYear(LocalDate date, LocalDate dateInYear){
        if (date != null && dateInYear != null) {
            return (date.compareTo(dateInYear) >= 0 ? dateInYear : dateInYear.minusYears(1));
        }
        return null;
    }
    public static LocalDate calcNearestDateInPeriod(LocalDate date, Period per){
        if (per != null && date != null) {
            LocalDate ret = new LocalDate(date);

            if (per.getYears() != 0) {
                return ret.yearOfEra().roundFloorCopy().minusYears(ret.getYearOfEra() % per.getYears());
            }
            else if (per.getMonths() != 0) {
                return ret.monthOfYear().roundFloorCopy().minusMonths((ret.getMonthOfYear() - 1) % per.getMonths());
            }
            else if (per.getWeeks() != 0) {
                return ret.weekOfWeekyear().roundFloorCopy().minusWeeks((ret.getWeekOfWeekyear() - 1) % per.getWeeks());
            }
            else if (per.getDays() != 0) {
                return ret.dayOfMonth().roundFloorCopy().minusDays((ret.getDayOfMonth() - 1) % per.getDays());
            }
            return ret.dayOfYear().roundCeilingCopy().minusDays(ret.getDayOfYear() % per.getMillis());
        }
        return null;
    }


    //Repeating events calculations
    private ArrayList<LocalDate> timeFrame_add_events(Period event_period, LocalDate initialEvent, LocalDate event_start, LocalDate event_end){
        ArrayList<LocalDate> event_occurrences = new ArrayList<>();
        int event_occurrences_count = 0;
        LocalDate currentEvent = initialEvent;
        //Helper.Print(App.GetContext(), "event_start:" + event_start.toString(Helper.getString(R.string.date_format)));
        //Helper.Print(App.GetContext(), "event_end:" + event_end.toString(Helper.getString(R.string.date_format)));
        //Helper.Print(App.GetContext(), "initialEvent:" + initialEvent.toString(Helper.getString(R.string.date_format)));

        //Null check for event period
        if (event_start != null && event_end != null) {

            //Loop until the event passes event_end
            while (currentEvent.compareTo(event_end) <= 0) {

                //Restrict number of occurrences to repeatUntilTimes
                if ((repeatUntil == RepeatUntil.TIMES && event_occurrences_count < repeatUntilTimes) || repeatUntil != RepeatUntil.TIMES) {
                    //Special case for weekly repeats
                    if (repeatFrequency == Repeat.WEEKLY) {
                        //Mon-Sun loop
                        for (int i = 1; i < 8; i++) {
                            //Mon-Sun (i) was selected to repeat on
                            if (repeatDayOfWeek[i - 1]) {
                                LocalDate dow = calcNextDayOfWeek(currentEvent, i);
                                //Mon-Sun (i) of current week is after timeFrame_start and before timeFrame_end
                                if (dow.compareTo(event_start) >= 0 && dow.compareTo(event_end) <= 0) {

                                    if (blacklistDates.size() == 0) {
                                        event_occurrences.add(dow);
                                    }
                                    else {
                                        // Blacklist dates check
                                        for (int ii = 0; ii < blacklistDates.size(); ii++) {
                                            if (dow.compareTo(blacklistDates.get(ii).date) == 0) {
                                                break;
                                            }

                                            if (ii == blacklistDates.size() - 1) {
                                                event_occurrences.add(dow);
                                            }
                                        }
                                    }
                                }
                                event_occurrences_count++;
                            }
                        }
                    }
                    else {
                        // currentEvent is within the start and end dates
                        if (currentEvent.compareTo(event_start) >= 0 && currentEvent.compareTo(event_end) <= 0) {

                            // Blacklist dates check
                            if (blacklistDates.size() == 0) {
                                event_occurrences.add(currentEvent);
                                //ProfileManager.Print("currentEvent2:" + currentEvent.toString(ProfileManager.simpleDateFormat));
                            }
                            else {
                                for (int ii = 0; ii < blacklistDates.size(); ii++) {
                                    if (currentEvent.compareTo(blacklistDates.get(ii).date) == 0) {
                                        break;
                                    }

                                    if (ii == blacklistDates.size() - 1) {
                                        event_occurrences.add(currentEvent);
                                        //ProfileManager.Print("currentEvent1:" + currentEvent.toString(ProfileManager.simpleDateFormat));
                                    }
                                }
                            }
                        }
                        event_occurrences_count++;
                    }
                }

                //Increment event by time_period
                currentEvent = currentEvent.plus(event_period);

            }
        }
        else { //No start or end, just add the parent event
            event_occurrences.add(initialEvent);
        }

        //Sort event_occurrences by date before returning, this is a bugfix for the repeatWeekly code returning a Monday when MWF are repeating and the event starts on a day > Monday.
        Collections.sort(event_occurrences, new Comparator<LocalDate>() {
            @Override
            public int compare(LocalDate date1, LocalDate date2) { return  date1.compareTo(date2); }
        });


        return event_occurrences;
    }

    public ArrayList<LocalDate> GetOccurrencesWithin(LocalDate timeFrame_start, LocalDate timeFrame_end)
    {
        //Occurrences arrayList, will hold dates of all occurrences of this TimePeriod between timeFrame_start and timeFrame_end
        ArrayList<LocalDate> occurrences = new ArrayList<>();

        //Event time section dates
        LocalDate event_start = date; //dateMax(date, timeFrame_start);
        LocalDate event_end = timeFrame_end;
        LocalDate initialEvent = (event_start!=null ? event_start : date);
        //Null start_time =(set to first occurrence of event)       ^^^^
        //Null end_time = impossible, because there could be infinite occurrences


        //Set event_start & initialEvent depending on repeatFrequency: NEVER (Short-Circuit return), WEEKLY, MONTHLY, and YEARLY
        switch(repeatFrequency){
            case NEVER:
                if (date != null) {
                    if (timeFrame_start == null && timeFrame_end != null && date.compareTo(timeFrame_end) <= 0 ||
                            timeFrame_start != null && timeFrame_end == null && date.compareTo(timeFrame_start) >= 0 ||
                            timeFrame_start != null && timeFrame_end != null && date.compareTo(timeFrame_start) >= 0 && date.compareTo(timeFrame_end) <= 0){
                        occurrences.add(date);
                    }
                }
                return occurrences;
            case WEEKLY:
                event_start = calcFirstDayOfWeek(repeatDayOfWeek, event_start);
                initialEvent = event_start;
                break;
            case MONTHLY:
                event_start = calcNextDayOfMonth(event_start, repeatDayOfMonth);
                initialEvent = event_start;
                break;
            case YEARLY:
                event_start = calcNextDateOfYear(event_start, dateOfYear);
                initialEvent = event_start;
                break;
        }


        //Event Period setup
        int YEARS  = repeatEveryN * (repeatFrequency==Repeat.YEARLY  ? 1 : 0);
        int MONTHS = repeatEveryN * (repeatFrequency==Repeat.MONTHLY ? 1 : 0);
        int WEEKS  = repeatEveryN * (repeatFrequency==Repeat.WEEKLY  ? 1 : 0);
        int DAYS   = repeatEveryN * (repeatFrequency==Repeat.DAILY   ? 1 : 0);

        // REPEAT_FREQUENCY * REPEAT_EVERY_N
        Period event_period = new Period(YEARS, MONTHS, WEEKS, DAYS, 0, 0, 0, 0);


        //Set event_end to either the timeframe_end or the repeatUntil date, whichever comes first (Allows for null repeatUntilDate)
        event_end = dateMin(timeFrame_end, repeatUntilDate);

        //Set event_start to either timeframe_start or event_start, whichever comes last
        event_start = dateMax(timeFrame_start, event_start);

        //Add events to occurrences
        occurrences.addAll(timeFrame_add_events(event_period, initialEvent, event_start, event_end));

        //Return list of occurrences
        return occurrences;
    }

    public void CalculateFirstOccurrence(){
        ArrayList<LocalDate> occurrences = GetOccurrencesWithin(date, null);
        if (occurrences.size() > 0) {
            _firstOccurenceDate = occurrences.get(0);
            //ProfileManager.Print("FirstOccurrence:" + _firstOccurenceDate.toString(ProfileManager.simpleDateFormat));
        }
    }


    //Accessors
    public LocalDate GetDate() { return date; }
    public LocalDate GetFirstOccurrence() { CalculateFirstOccurrence(); if (_firstOccurenceDate == null ) { return date; } else { return _firstOccurenceDate; } }

    public Repeat GetRepeatFrequency() { return repeatFrequency; }
        public Boolean DoesRepeat() { return GetRepeatFrequency() != Repeat.NEVER; }
    public int GetRepeatFrequencyIndex(Repeat freq) { return freq.ordinal(); }
    public Repeat GetRepeatFrequencyFromIndex(int index) { return Repeat.values()[index]; }

    public RepeatUntil GetRepeatUntil() { return repeatUntil; }
    public int GetRepeatUntilIndex(RepeatUntil until) { return until.ordinal(); }
    public RepeatUntil GetRepeatUntilFromIndex(int index) { return RepeatUntil.values()[index]; }

    public int GetRepeatANumberOfTimes() { return repeatUntilTimes; }
    public LocalDate GetRepeatUntilDate() { return repeatUntilDate; }
    public int GetRepeatEveryN() { return repeatEveryN; }

    public String GetRepeatDayOfWeekBinary() {
        return (repeatDayOfWeek[0] ? "1" : "0")
                + (repeatDayOfWeek[1] ? "1" : "0")
                + (repeatDayOfWeek[2] ? "1" : "0")
                + (repeatDayOfWeek[3] ? "1" : "0")
                + (repeatDayOfWeek[4] ? "1" : "0")
                + (repeatDayOfWeek[5] ? "1" : "0")
                + (repeatDayOfWeek[6] ? "1" : "0");
    }

    public Boolean GetDayOfWeek(int day) { return repeatDayOfWeek[day]; }
    public int GetRepeatDayOfMonth() { return repeatDayOfMonth; }
    public LocalDate GetDateOfYear() { return dateOfYear; }


    //Mutators
    public void SetRepeatDayOfWeekFromBinary(String str) { //0 - Monday, 6 - Sunday
        repeatDayOfWeek[0] = str.length() >= 1 && str.charAt(0) == '1';
        repeatDayOfWeek[1] = str.length() >= 2 && str.charAt(1) == '1';
        repeatDayOfWeek[2] = str.length() >= 3 && str.charAt(2) == '1';
        repeatDayOfWeek[3] = str.length() >= 4 && str.charAt(3) == '1';
        repeatDayOfWeek[4] = str.length() >= 5 && str.charAt(4) == '1';
        repeatDayOfWeek[5] = str.length() >= 6 && str.charAt(5) == '1';
        repeatDayOfWeek[6] = str.length() >= 7 && str.charAt(6) == '1';
    }
    public void SetDate(LocalDate newDate) { date = newDate; }
    //public void SetFirstOccurrence(LocalDate newDate) { _firstOccurenceDate = newDate; }
    public void SetRepeatFrequency(Repeat freq) { repeatFrequency = freq; }
    public void SetRepeatUntil(RepeatUntil until) { repeatUntil = until; }
    public void SetRepeatANumberOfTimes(int times) { repeatUntilTimes = times; }
    public void SetRepeatUntilDate(LocalDate repUntDate) { repeatUntilDate = repUntDate; }
    public void SetRepeatEveryN(int n) { repeatEveryN = n; }
    public void SetDayOfWeek(int day, Boolean val) { if (day >= 0 && day <= 6) { repeatDayOfWeek[day] = val; } }
    public void SetRepeatDayOfMonth(int val) { repeatDayOfMonth = val; }
    public void SetDateOfYear(LocalDate newDate) { dateOfYear = newDate; }


    //Formatting
    public String GetDateFormatted()
    {
        if (date != null) { return date.toString(Helper.getString(R.string.date_format)); }
        else { return Helper.getString(R.string.time_nodate); }
    }
    public String GetRepeatUntilDateFormatted()
    {
        if (repeatUntilDate != null) { return repeatUntilDate.toString(Helper.getString(R.string.date_format)); }
        else { return ""; }
    }

    public String GetRepeatFrequencyFormatted(Repeat type){ return type.toString(); }
    public String GetEveryNFormatted(Repeat type){
        if (repeatEveryN == 1){
            return " " + GetRepeatFrequencyFormatted(type);
        }
        else {
            switch (type) {
                case NEVER:
                    return "";
                case DAILY:
                    return " " + Helper.getString(R.string.repeat_everylower) + " " + String.valueOf(repeatEveryN) + " " + Helper.getString(R.string.repeat_days);
                case WEEKLY:
                    return " " + Helper.getString(R.string.repeat_everylower) + " " + String.valueOf(repeatEveryN) + " " + Helper.getString(R.string.repeat_weeks);
                case MONTHLY:
                    return " " + Helper.getString(R.string.repeat_everylower) + " " + String.valueOf(repeatEveryN) + " " + Helper.getString(R.string.repeat_months);
                case YEARLY:
                    return " " + Helper.getString(R.string.repeat_everylower) + " " + String.valueOf(repeatEveryN) + " " + Helper.getString(R.string.repeat_years);
                default:
                    return "";
            }
        }
    }
    public String GetRepeatDaysOfWeek() {
        String str = Helper.getString(R.string.misc_on)

                + (repeatDayOfWeek[0] ? " " + Helper.getString(R.string.repeat_mon) + "," : "")
                + (repeatDayOfWeek[1] ? " " + Helper.getString(R.string.repeat_tue) + "," : "")
                + (repeatDayOfWeek[2] ? " " + Helper.getString(R.string.repeat_wed) + "," : "")
                + (repeatDayOfWeek[3] ? " " + Helper.getString(R.string.repeat_thur) + "," : "")
                + (repeatDayOfWeek[4] ? " " + Helper.getString(R.string.repeat_fri) + "," : "")
                + (repeatDayOfWeek[5] ? " " + Helper.getString(R.string.repeat_sat) + "," : "")
                + (repeatDayOfWeek[6] ? " " + Helper.getString(R.string.repeat_sun) + "" : "");
        //Remove last comma
        if (str.charAt(str.length()-1) == ','){ str = str.substring(0, str.length()-1); }
        return str;
    }

    public String GetDayOfMonthFormatted(){ return Helper.getString(R.string.misc_onday) + " " + String.valueOf(repeatDayOfMonth); }
    public String GetRepeatYear(){ return Helper.getString(R.string.misc_on) + " " + dateOfYear.toString(Helper.getString(R.string.date_format_noyear)); }

    public String GetRepeatString(){
        //Short-Circuit if repeat type is NEVER
        if (repeatFrequency == Repeat.NEVER) { return ""; }

        String tense = Helper.getString(date.compareTo(LocalDate.now()) <= 0 ? R.string.time_started : R.string.time_starts);
        String repeatTypeString = tense + " " + GetDateFormatted() + "\n" + Helper.getString(R.string.repeats) + " " + GetEveryNFormatted(GetRepeatFrequency());

        //Repeat Frequency
        switch (repeatFrequency){
            case WEEKLY:
                repeatTypeString += " " + GetRepeatDaysOfWeek();
                break;
            case MONTHLY:
                repeatTypeString += " " +GetDayOfMonthFormatted();
                break;
            case YEARLY:
                repeatTypeString += " " +GetRepeatYear();
                break;
        }

        //Repeat Until
        switch (repeatUntil)
        {
            case FOREVER:
                repeatTypeString += ", " + Helper.getString(R.string.repeat_forever);
                break;
            case DATE:
                repeatTypeString += ", " + Helper.getString(R.string.until) + " " + GetRepeatUntilDateFormatted();
                break;
            case TIMES:
                repeatTypeString += ", " + GetRepeatANumberOfTimes() + " " + Helper.getString(R.string.repeat_events);
                break;
        }

        //Return
        return repeatTypeString;
    }

    public String GetRepeatStringShort(){
        //Short-Circuit if repeat type is NEVER
        if (repeatFrequency == Repeat.NEVER) { return Helper.getString(R.string.repeat_never); }

        String repeatTypeString = Helper.getString(R.string.repeats) + GetEveryNFormatted(GetRepeatFrequency());

        //Repeat Frequency
        switch (repeatFrequency){
            case WEEKLY:
                repeatTypeString += " " + GetRepeatDaysOfWeek();
                break;
            case MONTHLY:
                repeatTypeString += " " + GetDayOfMonthFormatted();
                break;
            case YEARLY:
                repeatTypeString += " " + GetRepeatYear();
                break;
        }

        //Repeat Until
        switch (repeatUntil)
        {
            case FOREVER:
                repeatTypeString += ", " + Helper.getString(R.string.repeat_forever);
                break;
            case DATE:
                repeatTypeString += ", " + Helper.getString(R.string.until) + " " + GetRepeatUntilDateFormatted();
                break;
            case TIMES:
                repeatTypeString += "; " + GetRepeatANumberOfTimes() + " " + Helper.getString(R.string.repeat_events);
                break;
        }

        //Return
        return repeatTypeString;
    }




    //Equals
    @Override
    public boolean equals(Object o){
        if (o.getClass() == TimePeriod.class) {
            TimePeriod tp = (TimePeriod) o;

            if (!tp.GetDate().equals(this.GetDate())) { return false; }
            if (!tp.GetFirstOccurrence().equals(this.GetFirstOccurrence())) { return false; }

            if (!tp.GetRepeatFrequency().equals(this.GetRepeatFrequency())) { return false; }

            if (!tp.GetRepeatUntil().equals(this.GetRepeatUntil())) { return false; }
            if (tp.GetRepeatANumberOfTimes() != this.GetRepeatANumberOfTimes()) { return false; }
            if (!tp.GetRepeatUntilDate().equals(this.GetRepeatUntilDate())) { return false; }
            if (tp.GetRepeatEveryN() != this.GetRepeatEveryN()) { return false; }

            if (!tp.GetRepeatDayOfWeekBinary().equals(this.GetRepeatDayOfWeekBinary())) { return false; }
            if (tp.GetRepeatDayOfMonth() != this.GetRepeatDayOfMonth()) { return false; }

            if (!tp.GetDateOfYear().equals(this.GetDateOfYear())) { return false; }

            if (!tp.GetBlacklistDatesSaving().equals(this.GetBlacklistDatesSaving())) { return false; }


            return true;
        }

        return false;
    }

}


