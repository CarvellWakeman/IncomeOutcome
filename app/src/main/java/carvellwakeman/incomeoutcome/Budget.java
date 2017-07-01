package carvellwakeman.incomeoutcome;


import org.joda.time.*;
import org.joda.time.format.PeriodFormat;
import java.util.*;
import carvellwakeman.incomeoutcome.TimePeriod.Repeat;


public class Budget implements java.io.Serializable, BaseEntity
{
    //Info
    private int _uniqueID;
    private String _name;

    private boolean _selected;


    //TimeFrame
    private LocalDate _startTime;
    private LocalDate _endTime;
    private Period _period;
    private Repeat _periodFrequency;

    //new_Transaction sources
    private ArrayList<new_Transaction> _transactions;

    public Budget(String name){
        _uniqueID = System.identityHashCode(this);
        _name = name;

        _transactions = new ArrayList<>();

        //Default period (1 month)
        _period = new Period(0,1,0,0,0,0,0,0);
        SetStartDate(LocalDate.now().withDayOfMonth(1)); //First day of month
        SetEndDate(LocalDate.now().withDayOfMonth(LocalDate.now().dayOfMonth().getMaximumValue())); //Last day of month
    }




    //Accessors
    public int GetID() { return _uniqueID; }
    public String GetName() { return _name; }
    public boolean GetSelected() { return _selected; }

    public LocalDate GetStartDate(){  return _startTime; }
    public LocalDate GetEndDate(){ return _endTime; }
    public Period GetPeriod() { return _period; }
    public Repeat GetPeriodFreqency() { return _periodFrequency; }


    //Mutators
    public void SetID(int id) { _uniqueID = id; } //Should not be used outside of loading
    public void SetName(String name) { _name = name; }
    public void SetSelected(boolean selected) { _selected = selected; }

    public void SetStartDate(LocalDate start) { _startTime =  start; }
    public void SetEndDate(LocalDate end) { _endTime = end; }

    public void SetPeriod(Period period){
        _period = period;

        //Set frequency
        if (period.getYears()>0){ _periodFrequency = Repeat.YEARLY; }
        else if (period.getMonths()>0) { _periodFrequency = Repeat.MONTHLY; }
        else if (period.getWeeks()>0) { _periodFrequency = Repeat.WEEKLY; }
        else if (period.getDays()>0) { _periodFrequency = Repeat.DAILY; }
        else { _periodFrequency = Repeat.NEVER; }

        //Set start and end dates to the precision of the period
        LocalDate T = TimePeriod.calcNearestDateInPeriod(GetStartDate(), period);
        SetStartDate(T);
    }

    public void MoveTimePeriod(int n) {
        if (GetStartDate() == null) { SetStartDate((new LocalDate()).withDayOfMonth(1)); }

        for (int i = 0; i < Math.abs(n); i++) {
            if (n > 0) { SetStartDate(GetStartDate().plus(GetPeriod())); }
            else { SetStartDate(GetStartDate().minus(GetPeriod())); }
        }


        //Subtract one day for months and years
        if (GetPeriodFreqency() == Repeat.MONTHLY || GetPeriodFreqency() == Repeat.YEARLY) {
            SetEndDate(GetStartDate().plus(GetPeriod()).minusDays(1));
        } else {
            SetEndDate(GetStartDate().plus(GetPeriod()));
        }

    }


    //Transactions
    public void AddTransaction(new_Transaction transaction) {
        _transactions.add(transaction);
        transaction.SetBudgetID(GetID());
    }

    public void RemoveTransaction(new_Transaction transaction) { _transactions.remove(transaction); }
    public void RemoveTransaction(int ID){
        for (int i = 0; i < _transactions.size(); i++) {
            if (_transactions.get(i).GetID() == ID){
                _transactions.remove(i);
            }
        }
    }
    public void RemoveAllTransactions(){
        for (int i = 0; i < _transactions.size(); i++) {
            _transactions.get(i).RemoveAllChildren();
        }

        _transactions.clear();
    }

    public new_Transaction GetTransaction(int ID){
        //Short circuit for invalid ID
        if (ID == -1){ return null; }

        for (int i = 0; i < _transactions.size(); i++) {
            if (_transactions.get(i).GetID() == ID) {
                return _transactions.get(i);
            }
        }
        return null;
    }
    public ArrayList<new_Transaction> GetAllTransactions() { return _transactions; }
    public ArrayList<new_Transaction> GetTransactions(new_Transaction.TRANSACTION_TYPE type) { return GetTransactions(null, null, type); }
    public ArrayList<new_Transaction> GetTransactionsInTimeframe(new_Transaction.TRANSACTION_TYPE type){ return GetTransactions(GetStartDate(), GetEndDate(), type); }
    public ArrayList<new_Transaction> GetTransactions(LocalDate startDate, LocalDate endDate, new_Transaction.TRANSACTION_TYPE type){
        ArrayList<new_Transaction> tmp = new ArrayList<>();
        for (int i = 0; i < _transactions.size(); i++) {
            tmp.addAll(_transactions.get(i).GetOccurrences(startDate, endDate, type));
        }
        Helper.Print(App.GetContext(), "GetTransactions");
        return tmp;
    }


    public int GetTransactionCount() { return _transactions.size(); }



    //Formatting
    public String GetDateFormatted()
    {
        if (_endTime != null && _startTime != null) { //||showAll  OR && !showAll   ???
            //Yearly
            if (_endTime.getDayOfYear() == _endTime.dayOfYear().getMaximumValue() && _startTime.getDayOfYear() == _startTime.dayOfYear().getMinimumValue()){
                return _startTime.toString(Helper.getString(R.string.date_format_justyear));
            }
            //Monthly
            else if (_startTime.getMonthOfYear()==_endTime.getMonthOfYear() &&  _endTime.getDayOfMonth() == _endTime.dayOfMonth().getMaximumValue() && _startTime.getDayOfMonth() == _startTime.dayOfMonth().getMinimumValue()) {
                return _startTime.toString(Helper.getString(R.string.date_format_noday));
            }

            //Seasonally (Winter)
            else if (_startTime.getMonthOfYear()==DateTimeConstants.DECEMBER && _startTime.getDayOfMonth() == 1 &&
                    _endTime.getMonthOfYear()==DateTimeConstants.FEBRUARY && _endTime.getDayOfMonth() == _endTime.dayOfMonth().getMaximumValue()){
                return Helper.getString(R.string.time_winter) + _startTime.getYear() + "-" + _endTime.getYear();
            }
            //Seasonally (Spring)
            else if (_startTime.getMonthOfYear()==DateTimeConstants.MARCH && _startTime.getDayOfMonth() == 1 &&
                    _endTime.getMonthOfYear()==DateTimeConstants.MAY && _endTime.getDayOfMonth() == _endTime.dayOfMonth().getMaximumValue()){
                return Helper.getString(R.string.time_spring) + _startTime.getYear();
            }
            //Seasonally (Summer)
            else if (_startTime.getMonthOfYear()==DateTimeConstants.JUNE && _startTime.getDayOfMonth() == 1 &&
                    _endTime.getMonthOfYear()==DateTimeConstants.AUGUST && _endTime.getDayOfMonth() == _endTime.dayOfMonth().getMaximumValue()){
                return Helper.getString(R.string.time_summer) + _startTime.getYear();
            }
            //Seasonally (Fall)
            else if (_startTime.getMonthOfYear()==DateTimeConstants.SEPTEMBER && _startTime.getDayOfMonth() == 1 &&
                    _endTime.getMonthOfYear()==DateTimeConstants.NOVEMBER && _endTime.getDayOfMonth() == _endTime.dayOfMonth().getMaximumValue()){
                return Helper.getString(R.string.time_fall) + _startTime.getYear();
            }

            //Same Day
            else if (_startTime.equals(_endTime)) {
                return _startTime.toString(Helper.getString(R.string.date_format));
            }

            else { //Default
                return _startTime.toString(Helper.getString(R.string.date_format_short)) + " - " + _endTime.toString(Helper.getString(R.string.date_format_short));
            }
        }
        else {
            if (_endTime == null && _startTime != null){ //&&!showAll
                return Helper.getString(R.string.time_started) + " " + _startTime.toString(Helper.getString(R.string.date_format));
            }
            else {
                return Helper.getString(R.string.misc_all);
            }
        }
    }

    public String GetPeriodFormatted(){
        if (_period != null){
            return Helper.getString(R.string.repeat_occurevery) + " "+ _period.toString(PeriodFormat.wordBased(App.GetLocale()));
        }
        return "No Period";
    }
}
