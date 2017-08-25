package carvellwakeman.incomeoutcome;


import android.content.Context;
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

    //Transaction sources
    private ArrayList<Transaction> _transactions;

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
    public void AddTransaction(Transaction transaction) {
        _transactions.add(transaction);
        transaction.SetBudgetID(GetID());
    }

    public void RemoveTransaction(Transaction transaction) { RemoveTransaction(transaction.GetID()); }
    public void RemoveTransaction(int ID){
        for (int i = 0; i < _transactions.size(); i++) {
            if (_transactions.get(i).GetID() == ID){
                TimePeriod tp = _transactions.get(i).GetTimePeriod();
                if (tp != null){
                    for (BlacklistDate bd : tp.GetBlacklistDates()){
                        RemoveTransaction(bd.transactionID);
                    }
                }

                _transactions.remove(i);
            }
        }
    }
    public void RemoveAllTransactions(){
        _transactions.clear();
    }

    public Transaction GetTransaction(int ID){
        //Short circuit for invalid ID
        if (ID == -1){ return null; }

        for (int i = 0; i < _transactions.size(); i++) {
            if (_transactions.get(i).GetID() == ID) {
                return _transactions.get(i);
            }
        }
        return null;
    }
    public ArrayList<Transaction> GetAllTransactions() { return _transactions; }
    public ArrayList<Transaction> GetTransactions(Transaction.TRANSACTION_TYPE type) {
        ArrayList<Transaction> temp = new ArrayList<>();
        for (Transaction t : _transactions){
            if (t.GetType() == type){ temp.add(t); }
        }
        return temp;
    }
    public ArrayList<Transaction> GetTransactionsInTimeframe(Context context, Transaction.TRANSACTION_TYPE type){ return GetTransactions(context, GetStartDate(), GetEndDate(), type, Helper.SORT_METHODS.DATE_ASC, null); }
    public ArrayList<Transaction> GetTransactionsInTimeframe(Context context, Transaction.TRANSACTION_TYPE type, Helper.SORT_METHODS sort, HashMap<Helper.FILTER_METHODS, String>  filters){
        return GetTransactions(context, GetStartDate(), GetEndDate(), type, sort, filters);
    }
    public ArrayList<Transaction> GetTransactions(Context context, LocalDate startDate, LocalDate endDate, Transaction.TRANSACTION_TYPE type, final Helper.SORT_METHODS sort, HashMap<Helper.FILTER_METHODS, String>  filters){
        Helper.Log(context, "Budget", "GetTransactions");

        ArrayList<Transaction> ret = new ArrayList<>();
        ArrayList<Transaction> occurrences;
        for (Transaction tran : _transactions){
            occurrences = tran.GetOccurrences(startDate, endDate, type);

            // Filtering
            if (filters != null && filters.size() > 0){
                CategoryManager cm = CategoryManager.getInstance();
                PersonManager pm = PersonManager.getInstance();

                for (Transaction occ : occurrences) {
                    int filtersMet = 0;
                    for (HashMap.Entry<Helper.FILTER_METHODS, String> entry : filters.entrySet()){
                        switch (entry.getKey()){
                            case CATEGORY:
                                if (cm.GetCategory(occ.GetCategory()).GetTitle().equals(filters.get(Helper.FILTER_METHODS.CATEGORY))){ filtersMet++; }
                                break;
                            case SOURCE:
                                if (occ.GetSource().equals(filters.get(Helper.FILTER_METHODS.SOURCE))){ filtersMet++; }
                                break;
                            case PAIDBY:
                                if (pm.GetPerson(occ.GetPaidBy()).GetName().equals(filters.get(Helper.FILTER_METHODS.PAIDBY))){ filtersMet++; }
                                break;
                            case SPLITWITH:
                                // Not split short circuit
                                if (filters.get(Helper.FILTER_METHODS.SPLITWITH).equals(context.getString(R.string.tt_not_split)) && !occ.IsSplit()) {
                                    filtersMet++;
                                    break;
                                } else {
                                    for (Integer id : occ.GetSplitArray().keySet()) {
                                        if (pm.GetPerson(id).GetName().equals(filters.get(Helper.FILTER_METHODS.SPLITWITH))) {
                                            filtersMet++;
                                            break;
                                        }
                                    }
                                }
                                break;
                            case PAIDBACK:
                                if (occ.GetPaidBack() != null && filters.get(Helper.FILTER_METHODS.PAIDBACK).equals(context.getString(R.string.confirm_yes)) ) { filtersMet++; }
                                else if (occ.GetPaidBack() == null && filters.get(Helper.FILTER_METHODS.PAIDBACK).equals(context.getString(R.string.confirm_no)) ) { filtersMet++; }
                                break;
                        }
                    }
                    // All filters met, add
                    if (filtersMet == filters.size()){
                        ret.add(occ);
                    }

                }
            } else {
                ret.addAll(occurrences);
            }
        }

        // Sorting
        if (sort != null) {
            Collections.sort(ret, new Comparator<Transaction>() {
                @Override public int compare(Transaction t1, Transaction t2) { return t1.SortCompare(t2, sort); }
            });
        }

        return ret;
    }


    public int GetTransactionCount() { return _transactions.size(); }



    //Formatting
    public String GetDateFormatted(Context context)
    {
        if (_endTime != null && _startTime != null) { //||showAll  OR && !showAll   ???
            //Yearly
            if (_endTime.getDayOfYear() == _endTime.dayOfYear().getMaximumValue() && _startTime.getDayOfYear() == _startTime.dayOfYear().getMinimumValue()){
                return _startTime.toString(context.getString(R.string.date_format_justyear));
            }
            //Monthly
            else if (_startTime.getMonthOfYear()==_endTime.getMonthOfYear() &&  _endTime.getDayOfMonth() == _endTime.dayOfMonth().getMaximumValue() && _startTime.getDayOfMonth() == _startTime.dayOfMonth().getMinimumValue()) {
                return _startTime.toString(context.getString(R.string.date_format_noday));
            }

            //Seasonally (Winter)
            else if (_startTime.getMonthOfYear()==DateTimeConstants.DECEMBER && _startTime.getDayOfMonth() == 1 &&
                    _endTime.getMonthOfYear()==DateTimeConstants.FEBRUARY && _endTime.getDayOfMonth() == _endTime.dayOfMonth().getMaximumValue()){
                return context.getString(R.string.time_winter) + _startTime.getYear() + "-" + _endTime.getYear();
            }
            //Seasonally (Spring)
            else if (_startTime.getMonthOfYear()==DateTimeConstants.MARCH && _startTime.getDayOfMonth() == 1 &&
                    _endTime.getMonthOfYear()==DateTimeConstants.MAY && _endTime.getDayOfMonth() == _endTime.dayOfMonth().getMaximumValue()){
                return context.getString(R.string.time_spring) + _startTime.getYear();
            }
            //Seasonally (Summer)
            else if (_startTime.getMonthOfYear()==DateTimeConstants.JUNE && _startTime.getDayOfMonth() == 1 &&
                    _endTime.getMonthOfYear()==DateTimeConstants.AUGUST && _endTime.getDayOfMonth() == _endTime.dayOfMonth().getMaximumValue()){
                return context.getString(R.string.time_summer) + _startTime.getYear();
            }
            //Seasonally (Fall)
            else if (_startTime.getMonthOfYear()==DateTimeConstants.SEPTEMBER && _startTime.getDayOfMonth() == 1 &&
                    _endTime.getMonthOfYear()==DateTimeConstants.NOVEMBER && _endTime.getDayOfMonth() == _endTime.dayOfMonth().getMaximumValue()){
                return context.getString(R.string.time_fall) + _startTime.getYear();
            }

            //Same Day
            else if (_startTime.equals(_endTime)) {
                return _startTime.toString(context.getString(R.string.date_format));
            }

            else { //Default
                return _startTime.toString(context.getString(R.string.date_format_short)) + " - " + _endTime.toString(context.getString(R.string.date_format_short));
            }
        }
        else {
            if (_endTime == null && _startTime != null){ //&&!showAll
                return context.getString(R.string.time_started) + " " + _startTime.toString(context.getString(R.string.date_format));
            }
            else {
                return context.getString(R.string.misc_all);
            }
        }
    }

    public String GetPeriodFormatted(Context context){
        if (_period != null){
            return context.getString(R.string.repeat_occurevery) + " "+ _period.toString(PeriodFormat.wordBased(App.GetLocale()));
        }
        return "No Period";
    }
}
