package carvellwakeman.incomeoutcome;


import org.joda.time.*;
import java.util.*;


public class Budget implements java.io.Serializable
{
    //Info
    private int _uniqueID;
    private String _name;


    //TimeFrame
    private LocalDate _startTime;
    private LocalDate _endTime;
    private Period _period;
    private Repeat _periodFrequency;

    //Transaction sources
    private ArrayList<new_Transaction> Transactions;

    public Budget(String name){
        _uniqueID = System.identityHashCode(this);
        _name = name;

        Transactions = new ArrayList<>();

        //Default period (1 month)
        _period = new Period(0,1,0,0,0,0,0,0);
        SetStartDate(LocalDate.now().withDayOfMonth(1));
        SetEndDate(LocalDate.now().withDayOfMonth(31));
    }




    //Accessors
    public int GetID() { return _uniqueID; }
    public String GetName() { return _name; }

    public LocalDate GetStartDate(){  return _startTime; }
    public LocalDate GetEndDate(){ return _endTime; }
    public Period GetPeriod() { return _period; }
    public Repeat GetPeriodFreqency() { return _periodFrequency; }


    //Mutators
    public void SetID(int id) { _uniqueID = id; } //Should not be used outside of loading
    public void SetName(String name) { _name = name; }

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
        LocalDate T = TimePeriod.calcNearestDateInPeriod(period, GetStartDate());
        SetStartDate(T);
    }

    public void MoveTimePeriod(int n){
        if (GetStartDate() == null) { SetStartDate((new LocalDate()).withDayOfMonth(1) ); }

        for (int i = 0; i < Math.abs(n); i++) {
            if (n > 0) { SetStartDate(GetStartDate().plus(GetPeriod())); }
            else { SetStartDate(GetStartDate().minus(GetPeriod())); }
        }

        SetEndDate(GetStartDate().plus(GetPeriod()));

        //Subtract one day for months and years
        if (GetPeriodFreqency()==Repeat.MONTHLY || GetPeriodFreqency()==Repeat.YEARLY) { SetEndDate(GetEndDate().minusDays(1)); }

    }


    //Transactions
    public void AddTransaction(new_Transaction transaction) { Transactions.add(transaction); }

    public void RemoveTransaction(new_Transaction transaction) { Transactions.remove(transaction); }
    public void RemoveTransaction(int ID){
        for (int i = 0; i < Transactions.size(); i++) {
            if (Transactions.get(i).GetID() == ID){
                Transactions.remove(i);
            }
        }
    }
    public void RemoveAllTransactions(){ Transactions.clear(); }

    public new_Transaction GetTransaction(int ID){
        for (int i = 0; i < Transactions.size(); i++) {
            if (Transactions.get(i).GetID() == ID) {
                return Transactions.get(i);
            }
        }
        return null;
    }
    public ArrayList<new_Transaction> GetTransactions(new_Transaction.TRANSACTION_TYPE type) { return GetTransactions(null, null, type); }
    public ArrayList<new_Transaction> GetTransactions(LocalDate startDate, LocalDate endDate, new_Transaction.TRANSACTION_TYPE type){
        ArrayList<new_Transaction> l = new ArrayList<>();
        for (int i = 0; i < Transactions.size(); i++) {
            l.addAll(Transactions.get(i).GetOccurrences(startDate, endDate, type));
        }
        return l;
    }

    public int GetTransactionCount() { return Transactions.size(); }

}
