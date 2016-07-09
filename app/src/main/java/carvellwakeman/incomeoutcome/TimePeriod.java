package carvellwakeman.incomeoutcome;

import org.joda.time.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

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

public class TimePeriod implements java.io.Serializable
{
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
        //Dates
        date = null;
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
        blacklistDates = copy.GetBlacklistDates();
    }
    public TimePeriod(LocalDate _date)
    {
        this();
        date = _date;
    }


    //Blacklist
    public void AddBlacklistDate(LocalDate date, Boolean edited){ blacklistDates.add(new BlacklistDate(date, edited)); }
    public void RemoveBlacklistDateQueue(){
        for (int i = 0; i < _blacklistDatesQueue.size(); i++) {
            //Remove objects from queue
            ProfileManager.Print("BlacklistDate " + _blacklistDatesQueue.get(i).date.toString(ProfileManager.simpleDateFormat) + " Removed");
            blacklistDates.remove(_blacklistDatesQueue.get(i));
        }
    }
    public void RemoveBlacklistDate(LocalDate date) {
        ProfileManager.Print("BlacklistDate " + date.toString(ProfileManager.simpleDateFormat) + " queued for removal on save");
        for (int i = 0; i < blacklistDates.size(); i++) {
            if (blacklistDates.get(i).date.compareTo(date) == 0) {
                //Queue blacklist date for deletion
                _blacklistDatesQueue.add(blacklistDates.get(i));
            }
        }
    }
    //public void ClearBlacklistDates(){ blacklistDates.clear(); }
    public void ClearBlacklistQueue() { _blacklistDatesQueue.clear(); }

    public String GetBlacklistDatesSaving(){
        String str = "";
        for (int i = 0; i < blacklistDates.size(); i++) {
            str += blacklistDates.get(i).date.toString(ProfileManager.simpleDateFormatSaving) + "|" + (blacklistDates.get(i).edited ? 1 : 0) + ",";
        }
        //Remove last comma
        if (str.length() > 0) { str = str.substring(0, str.length()-1); }
        return str;
    }

    public ArrayList<BlacklistDate> GetBlacklistDates() { return blacklistDates; }
    public BlacklistDate GetBlacklistDate(int index){ if (index >= 0 && blacklistDates.size() > 0){ return blacklistDates.get(index); } else { return null; } }

    public int GetBlacklistDatesCount() { return blacklistDates.size(); }
    public int GetBlacklistDatesCountWithoutQueue() { return blacklistDates.size() - _blacklistDatesQueue.size(); }

    public String GetBlacklistDateString(int index) { //*Excluding _blacklistDatesQueue
        if (blacklistDates.get(index) != null){
            if (!_blacklistDatesQueue.contains(blacklistDates.get(index))){
                return blacklistDates.get(index).date.toString(ProfileManager.simpleDateFormat) + (blacklistDates.get(index).edited ? " (edited)" : " (deleted)");
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
        if (dayOfWeek[0] && date.getDayOfWeek() <= 1) { return calcNextDayOfWeek(date, 1); }
        else if (dayOfWeek[1] && date.getDayOfWeek() <= 2) { return calcNextDayOfWeek(date, 2); }
        else if (dayOfWeek[2] && date.getDayOfWeek() <= 3) { return calcNextDayOfWeek(date, 3); }
        else if (dayOfWeek[3] && date.getDayOfWeek() <= 4) { return calcNextDayOfWeek(date, 4); }
        else if (dayOfWeek[4] && date.getDayOfWeek() <= 5) { return calcNextDayOfWeek(date, 5); }
        else if (dayOfWeek[5] && date.getDayOfWeek() <= 6) { return calcNextDayOfWeek(date, 6); }
        else if (dayOfWeek[6] && date.getDayOfWeek() <= 7) { return calcNextDayOfWeek(date, 7); }
        else { return date; }
    }

    public static LocalDate calcNextDayOfWeek(LocalDate date, int dayOfWeek) {
        return (date.getDayOfWeek() <= dayOfWeek) ? date.withDayOfWeek(dayOfWeek) : date.plusWeeks(1).withDayOfWeek(dayOfWeek);
    }
    public static LocalDate calcPrevDayOfWeek(LocalDate date, int dayOfWeek) {
        if (date.getDayOfWeek() < dayOfWeek) { return date.minusWeeks(1).withDayOfWeek(dayOfWeek);
        } else { return date.withDayOfWeek(dayOfWeek); }
    }

    public static LocalDate calcNextDayOfMonth(LocalDate date, int dayOfMonth) {
        return (date.getDayOfMonth() <= dayOfMonth) ? date.withDayOfMonth(dayOfMonth) : date.plusMonths(1).withDayOfMonth(dayOfMonth);
    }
    public static LocalDate calcPrevDayOfMonth(LocalDate date, int dayOfMonth) {
        return (date.getDayOfMonth() >= dayOfMonth) ? date.withDayOfMonth(dayOfMonth) : date.minusMonths(1).withDayOfMonth(dayOfMonth);
    }

    public static LocalDate calcNextDateOfYear(LocalDate date, LocalDate dateInYear){
        return (date.compareTo(dateInYear) <= 0 ? dateInYear : dateInYear.plusYears(1));
    }
    public static LocalDate calcPrevDateOfYear(LocalDate date, LocalDate dateInYear){
        return (date.compareTo(dateInYear) >= 0 ? dateInYear : dateInYear.minusYears(1));
    }


    //Repeating events calculations
    private ArrayList<LocalDate> timeFrame_add_events(Period event_period, LocalDate initialEvent, LocalDate event_start, LocalDate event_end){
        ArrayList<LocalDate> event_occurrences = new ArrayList<>();
        int event_occurrences_count = 0;
        LocalDate currentEvent = initialEvent;
        //ProfileManager.Print("event_start:" + event_start.toString(ProfileManager.simpleDateFormat));
        //ProfileManager.Print("event_end:" + event_end.toString(ProfileManager.simpleDateFormat));
        //ProfileManager.Print("initialEvent:" + initialEvent.toString(ProfileManager.simpleDateFormat));

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
        //Null start_time (set to first occurrence of event)        ^^^^
        //Null end_time impossible, because there could be infinite occurrences


        //Set event_start & initialEvent depending on repeatFrequence: NEVER (Short-Circuit return), WEEKLY, MONTHLY, and YEARLY
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
    public LocalDate GetFirstOccurrence() { if (_firstOccurenceDate == null ) { return date; } else { return _firstOccurenceDate; } }

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
    public void SetRepeatDayOfWeekFromBinary(String str) {
        repeatDayOfWeek[0] = str.length() >= 1 && str.charAt(0) == '1';
        repeatDayOfWeek[1] = str.length() >= 2 && str.charAt(1) == '1';
        repeatDayOfWeek[2] = str.length() >= 3 && str.charAt(2) == '1';
        repeatDayOfWeek[3] = str.length() >= 4 && str.charAt(3) == '1';
        repeatDayOfWeek[4] = str.length() >= 5 && str.charAt(4) == '1';
        repeatDayOfWeek[5] = str.length() >= 6 && str.charAt(5) == '1';
        repeatDayOfWeek[6] = str.length() >= 7 && str.charAt(6) == '1';
    }
    public void SetDate(LocalDate newDate) { date = newDate; CalculateFirstOccurrence();}
    //public void SetFirstOccurrence(LocalDate newDate) { _firstOccurenceDate = newDate; }
    public void SetRepeatFrequency(Repeat freq) { repeatFrequency = freq; CalculateFirstOccurrence();}
    public void SetRepeatUntil(RepeatUntil until) { repeatUntil = until; CalculateFirstOccurrence();}
    public void SetRepeatANumberOfTimes(int times) { repeatUntilTimes = times; CalculateFirstOccurrence();}
    public void SetRepeatUntilDate(LocalDate repUntDate) { repeatUntilDate = repUntDate; CalculateFirstOccurrence();}
    public void SetRepeatEveryN(int n) { repeatEveryN = n; CalculateFirstOccurrence();}
    public void SetDayOfWeek(int day, Boolean val) { if (day >= 0 && day <= 6) { repeatDayOfWeek[day] = val; } CalculateFirstOccurrence();}
    public void SetRepeatDayOfMonth(int val) { repeatDayOfMonth = val; CalculateFirstOccurrence();}
    public void SetDateOfYear(LocalDate newDate) { dateOfYear = newDate; CalculateFirstOccurrence();}


    //Formatting
    public String GetDateFormatted()
    {
        if (date != null) { return date.toString(ProfileManager.simpleDateFormat); }
        else { return "No Date"; }
    }
    public String GetRepeatUntilDateFormatted()
    {
        if (repeatUntilDate != null) { return repeatUntilDate.toString(ProfileManager.simpleDateFormat); }
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
                    return " every " + String.valueOf(repeatEveryN) + " days";
                case WEEKLY:
                    return " every " + String.valueOf(repeatEveryN) + " weeks";
                case MONTHLY:
                    return " every " + String.valueOf(repeatEveryN) + " months";
                case YEARLY:
                    return " every " + String.valueOf(repeatEveryN) + " years";
                default:
                    return "";
            }
        }
    }
    public String GetRepeatDaysOfWeek() {
        String str = " on" + (repeatDayOfWeek[0] ? " Mon," : "")
                + (repeatDayOfWeek[1] ? " Tues," : "")
                + (repeatDayOfWeek[2] ? " Wed," : "")
                + (repeatDayOfWeek[3] ? " Thur," : "")
                + (repeatDayOfWeek[4] ? " Fri," : "")
                + (repeatDayOfWeek[5] ? " Sat," : "")
                + (repeatDayOfWeek[6] ? " Sun" : "");
        //Remove last comma
        if (str.charAt(str.length()-1) == ','){ str = str.substring(0, str.length()-1); }
        return str;
    }

    public String GetDayOfMonthFormatted(){ return " on day " + String.valueOf(repeatDayOfMonth); }
    public String GetRepeatYear(){ return " on " + dateOfYear.toString(ProfileManager.simpleDateFormatNoYear); }

    public String GetRepeatString(Repeat type, RepeatUntil until){
        //Short-Circuit if repeat type is NEVER
        if (type == Repeat.NEVER) { return GetDateFormatted(); }

        String tense = (date.compareTo(LocalDate.now()) <= 0 ? "Started " : "Starts ");
        String repeatTypeString = tense + GetDateFormatted() + "\nRepeats" + GetEveryNFormatted(GetRepeatFrequency());

        //Repeat Frequency
        switch (type){
            case WEEKLY:
                repeatTypeString += GetRepeatDaysOfWeek();
                break;
            case MONTHLY:
                repeatTypeString += GetDayOfMonthFormatted();
                break;
            case YEARLY:
                repeatTypeString += GetRepeatYear();
                break;
        }

        //Repeat Until
        switch (until)
        {
            case FOREVER:
                repeatTypeString += ",\nforever";
                break;
            case DATE:
                repeatTypeString += ",\nuntil " + GetRepeatUntilDateFormatted();
                break;
            case TIMES:
                repeatTypeString += ",\n" + GetRepeatANumberOfTimes() + " times";
                break;
        }

        //Return
        return repeatTypeString;
    }

}


