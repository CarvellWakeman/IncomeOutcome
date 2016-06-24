package carvellwakeman.incomeoutcome;


import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import android.database.sqlite.SQLiteDatabase;
import org.joda.time.LocalDate;

public class Profile implements java.io.Serializable
{
    //Info
    private int _uniqueID;
    private String name;


    //TimeFrame
    private LocalDate _startTime;
    private LocalDate _endTime;

    //Income sources
    private ArrayList<Income> IncomeSources;
    private ArrayList<Income> _IncomeSources_timeFrame;

    //Expenses sources
    private ArrayList<Expense> ExpenseSources;
    private ArrayList<Expense> _ExpenseSources_timeFrame;


    //Totals
    protected HashMap<String, Expense> _ExpenseTotals;
    protected HashMap<String, Income> _IncomeTotals;



    public Profile(String _name){
        _uniqueID = java.lang.System.identityHashCode(this);
        name = _name;

        IncomeSources = new ArrayList<>();
        ExpenseSources = new ArrayList<>();

        _IncomeSources_timeFrame = new ArrayList<>();
        _ExpenseSources_timeFrame = new ArrayList<>();

        _ExpenseTotals = new HashMap<>();
        _IncomeTotals = new HashMap<>();
        GetTotalCostPerPersonInTimeFrame();
        GetTotalIncomePerSourceInTimeFrame();
    }

    public void ClearAllObjects(){
        _startTime = null;
        _endTime = null;

        //Income sources
        for (Income in : IncomeSources){
            in.ClearAllObjects();
        }
        IncomeSources.clear();
        _IncomeSources_timeFrame.clear();

        //Expenses sources
        for (Expense ex : ExpenseSources){
            ex.ClearAllObjects();
        }
        ExpenseSources.clear();
        _ExpenseSources_timeFrame.clear();


        //Totals
        _ExpenseTotals.clear();
        _IncomeTotals.clear();
    }

    public void RemoveAll(){
        for (int i = 0; i < ExpenseSources.size(); i++){
            RemoveExpense(ExpenseSources.get(i));
        }
        for (int i = 0; i < IncomeSources.size(); i++){
            RemoveIncome(IncomeSources.get(i));
        }
    }

    //Accessors
    public int GetID() { return _uniqueID; }
    public String GetName() { return name; }

    public LocalDate GetStartTime(){ if (_startTime == null) { return new LocalDate(); } else { return _startTime; } }
    public LocalDate GetEndTime(){ if (_endTime == null) { return new LocalDate(); } else { return _endTime; }}

    public int GetIncomeSourcesSize() { return IncomeSources.size(); }
    public int GetExpenseSourcesSize() { return ExpenseSources.size(); }

    public int GetIncomeSourcesInTimeFrameSize() { return _IncomeSources_timeFrame.size(); }
    public int GetExpenseSourcesInTimeFrameSize() { return _ExpenseSources_timeFrame.size(); }

    public String GetDateFormatted()
    {
        if (_endTime != null && _startTime != null) {
            if (_endTime.getDayOfMonth() == _endTime.dayOfMonth().getMaximumValue() && _startTime.getDayOfMonth() == _startTime.dayOfMonth().getMinimumValue()) {
                return _startTime.toString(ProfileManager.simpleDateFormatNoDay);
            }
            else {
                return _startTime.toString(ProfileManager.simpleDateFormat) + " to " + _endTime.toString(ProfileManager.simpleDateFormat);
            }
        }
        else { return ""; }
    }
    //public int GetTotalsSize() { return _totals.size(); }
    //public Expense GetTotalValueAtKey(OtherPerson op){ return _totals.get(op); }
    //public OtherPerson GetTotalKeyAtIndex(int index) { return _totals.keySet().toArray(new OtherPerson[_totals.keySet().size()])[index]; }


    //Mutators
    public void SetID(int id){ _uniqueID = id; } //TODO Should not be used outside of loading
    public void SetName(String newName){ name = newName; }
    public void SetStartTime(LocalDate start, Boolean dontsave){ _startTime = start;  }
    public void SetStartTime(LocalDate start){ //[EXCLUDE] Removing insertSettingDatabase so that the database is not updated many times for one profile
        SetStartTime(start, true);
        ProfileManager.InsertSettingDatabase(this, true);
    }
    public void SetEndTime(LocalDate end, Boolean dontsave){ _endTime = end; }
    public void SetEndTime(LocalDate end){
        SetEndTime(end, true);
        ProfileManager.InsertSettingDatabase(this, true);
    }


    //Income management
    public void AddIncome(Income income, Boolean dontsave) { IncomeSources.add(income); }
    public void AddIncome(Income income) { AddIncome(income, true); CalculateTimeFrame(); ProfileManager.InsertIncomeDatabase(this, income, false);}
    public void RemoveIncome(Income income, Boolean dontsave) { IncomeSources.remove(income);  }
    public void RemoveIncome(Income income) { RemoveIncome(income, true); CalculateTimeFrame(); ProfileManager.RemoveIncomeDatabase(income); }
    public void RemoveIncome(int id, Boolean dontsave) { RemoveIncome(GetIncome(id), dontsave); }
    public void RemoveIncome(int id) { RemoveIncome(GetIncome(id)); }

    public void UpdateIncome(Income income) { UpdateIncome(income, this); }
    public void UpdateIncome(Income income, Profile profile) { ProfileManager.InsertIncomeDatabase(profile, income, true); }

    public void TransferIncome(Income income, Profile moveTo){
        UpdateIncome(income, moveTo);
        moveTo.AddIncome(income, true);
        RemoveIncome(income, true);
    }
    public void TransferAllIncome(Profile moveTo){
        for (Income in : IncomeSources){
            TransferIncome(in, moveTo);
        }
        CalculateTimeFrame();
    }

    //Get Income at index
    public Income GetIncomeAtIndex(int index) { if (index >= 0 && IncomeSources.size() > 0){ return IncomeSources.get(index); } else { return null; } }
    public Income GetIncomeAtIndexInTimeFrame(int index) { if (index >= 0 && _IncomeSources_timeFrame.size() > 0){ return _IncomeSources_timeFrame.get(index); } else { return null; } }

    //Get Income with ID
    public Income GetIncome(int id) {
        for (int i = 0; i < IncomeSources.size(); i++) {
            if (IncomeSources.get(i).GetID() == id) {
                return IncomeSources.get(i);
            }
        }
        return null;
    }
    public Income GetIncomeInTimeFrame(int id) {
        for (int i = 0; i < _IncomeSources_timeFrame.size(); i++) {
            if (_IncomeSources_timeFrame.get(i).GetID() == id) {
                return _IncomeSources_timeFrame.get(i);
            }
        }
        return null;
    }
    public Income GetParentIncomeFromTimeFrameIncome(Income in){
        if (in != null) {
            Income TF = GetIncomeInTimeFrame(in.GetID());
            if (TF != null && !TF.IsParent()) {
                return GetIncome(TF.GetParentID());
            }
            else {
                return in;
            }
        }
        return null;
    }

    //Get Income total cost



    //Expense management
    public void AddExpense(Expense expense, Boolean dontsave) { ExpenseSources.add(expense); }
    public void AddExpense(Expense expense) { AddExpense(expense, true); CalculateTimeFrame(); ProfileManager.InsertExpenseDatabase(this, expense, false);}
    public void RemoveExpense(Expense expense, Boolean dontsave) { ExpenseSources.remove(expense);  }
    public void RemoveExpense(Expense expense) { RemoveExpense(expense, true); CalculateTimeFrame(); ProfileManager.RemoveExpenseDatabase(expense); }
    public void RemoveExpense(int id, Boolean dontsave) { RemoveExpense(GetExpense(id), dontsave); }
    public void RemoveExpense(int id) { RemoveExpense(GetExpense(id)); }

    public void UpdateExpense(Expense expense) { UpdateExpense(expense, this); }
    public void UpdateExpense(Expense expense, Profile profile) { ProfileManager.InsertExpenseDatabase(profile, expense, true); }

    public void TransferExpense(Expense expense, Profile moveTo){
        UpdateExpense(expense, moveTo);
        moveTo.AddExpense(expense, true);
        RemoveExpense(expense, true);
    }
    public void TransferAllExpenses(Profile moveTo){
        for (int i = 0; i < ExpenseSources.size(); i++){
            TransferExpense(ExpenseSources.get(i), moveTo);
        }

        CalculateTimeFrame();
    }

    public void UpdateOtherPerson(String old, String name) {
        for (Expense ex : ExpenseSources){
            if (ex.GetSplitWith().equals(old)) {
                ex.SetSplitValue(name, ex.GetSplitValue());
                UpdateExpense(ex);
            }
        }
    }

    public void UpdateCategory(String old, String name) {
        //Update expenses
        for (Expense ex : ExpenseSources){
            if (ex.GetCategory().equals(old)) {
                ex.SetCategory(name);
                UpdateExpense(ex);
            }
        }
        //Update income
        for (Income in : IncomeSources){
            if (in.GetCategory().equals(old)) {
                in.SetCategory(name);
                UpdateIncome(in);
            }
        }
    }

    //Get Expense at index
    public Expense GetExpenseAtIndex(int index) { if (index >= 0 && ExpenseSources.size() > 0){ return ExpenseSources.get(index); } else { return null; } }
    public Expense GetExpenseAtIndexInTimeFrame(int index) { if (index >= 0 && _ExpenseSources_timeFrame.size() > 0){ return _ExpenseSources_timeFrame.get(index); } else { return null; } }

    //Get Expense with ID
    public Expense GetExpense(int id) {
        for (int i = 0; i < ExpenseSources.size(); i++) {
            if (ExpenseSources.get(i).GetID() == id) {
                return ExpenseSources.get(i);
            }
        }
        return null;
    }
    public Expense GetExpenseInTimeFrame(int id) {
        for (int i = 0; i < _ExpenseSources_timeFrame.size(); i++) {
            if (_ExpenseSources_timeFrame.get(i).GetID() == id) {
                return _ExpenseSources_timeFrame.get(i);
            }
        }
        return null;
    }
    public Expense GetParentExpenseFromTimeFrameExpense(Expense ex){
        if (ex != null) {
            Expense TF = GetExpenseInTimeFrame(ex.GetID());
            if (TF != null && !TF.IsParent()) {
                return GetExpense(TF.GetParentID());
            }
            else {
                return ex;
            }
        }
        return null;
    }


    public Double GetExpenseOwedBy(String name){
        Double total = 0.0;

        for (int i = 0; i < ExpenseSources.size(); i++){
            if (ExpenseSources.get(i).GetSplitWith().equals(name)){
                total += ExpenseSources.get(i).GetSplitValue();
            }
        }

        return total;
    }
    public Double GetExpenseIOweTo(String name){
        Double total = 0.0;

        for (int i = 0; i < ExpenseSources.size(); i++){
            if (ExpenseSources.get(i).GetSplitWith().equals(name)){
                total += ExpenseSources.get(i).GetMySplitValue();
            }
        }

        return total;
    }


    //Get Expense total cost
    public HashMap<String, Expense> GetTotalCostPerPerson(){
        _ExpenseTotals.clear();
        Expense temp;

        for (int i = 0; i < ExpenseSources.size(); i++){
            Expense ex = ExpenseSources.get(i);

            temp = _ExpenseTotals.get( ex.GetSplitWith() );
            if ( temp == null ){
                _ExpenseTotals.put( ex.GetSplitWith(), new Expense());
                temp = _ExpenseTotals.get( ex.GetSplitWith() );
            }

            // Sum up values
            temp.SetValue(temp.GetValue() + ex.GetMyDebt());
            temp.SetSplitValue(ex.GetSplitWith(), temp.GetSplitValue() + ex.GetSplitDebt());
        }

        return _ExpenseTotals;
    }
    public HashMap<String, Expense> GetTotalCostPerPersonInTimeFrame(){
        _ExpenseTotals.keySet().clear();
        _ExpenseTotals.values().clear();
        _ExpenseTotals.clear();

        Expense temp;

        for (int i = 0; i < _ExpenseSources_timeFrame.size(); i++){
            Expense ex = _ExpenseSources_timeFrame.get(i);

            if (!ex.GetSplitWith().equals("")) {
                temp = _ExpenseTotals.get(ex.GetSplitWith());
                if (temp == null) {
                    _ExpenseTotals.put(ex.GetSplitWith(), new Expense());
                    temp = _ExpenseTotals.get(ex.GetSplitWith());
                }

                // Sum up values
                temp.SetValue(temp.GetValue() + ex.GetMyDebt());
                temp.SetSplitValue(ex.GetSplitWith(), temp.GetSplitValue() + ex.GetSplitDebt());
            }
        }

        return _ExpenseTotals;
    }

    //Get Income total cost
    public HashMap<String, Income> GetTotalIncomePerSource(){
        _IncomeTotals.clear();
        Income temp;

        for (int i = 0; i < IncomeSources.size(); i++){
            Income in = IncomeSources.get(i);

            temp = _IncomeTotals.get( in.GetSourceName() );
            if ( temp == null ){
                _IncomeTotals.put( in.GetSourceName(), new Income());
                temp = _IncomeTotals.get( in.GetSourceName() );
            }

            // Sum up values
            temp.SetValue(temp.GetValue() + in.GetValue());
        }

        return _IncomeTotals;
    }
    public HashMap<String, Income> GetTotalIncomePerSourceInTimeFrame(){
        _IncomeTotals.keySet().clear();
        _IncomeTotals.values().clear();
        _IncomeTotals.clear();

        Income temp;

        for (int i = 0; i < _IncomeSources_timeFrame.size(); i++){
            Income in = _IncomeSources_timeFrame.get(i);

            temp = _IncomeTotals.get(in.GetSourceName());
            if (temp == null) {

                _IncomeTotals.put(in.GetSourceName(), new Income());
                temp = _IncomeTotals.get(in.GetSourceName());
            }

            // Sum up values
            temp.SetValue(temp.GetValue() + in.GetValue());
        }

        return _IncomeTotals;
    }


    //Transactions
    public void TransferTransaction(Transaction tran, Profile moveTo){
        boolean isExpense = ExpenseSources.contains(tran);

        if (isExpense){
            TransferExpense((Expense)tran, moveTo);
        }
        else{
            TransferIncome((Income)tran, moveTo);
        }

        CalculateTimeFrame();
    }
    public void TransferAllTransactions(Profile moveTo){
        TransferAllExpenses(moveTo);
        TransferAllIncome(moveTo);
    }


    //Calculate the expenses and income sources that are within the timeframe provided
    public void CalculateTimeFrame()
    {
        _IncomeSources_timeFrame.clear();
        _ExpenseSources_timeFrame.clear();

        TimePeriod tp;
        ArrayList<LocalDate> occ = null;

        //TODO NULL CHECKS!
        //Add income sources within the time period to the timeframe array
        for (int i = 0; i < IncomeSources.size(); i++){
            tp = IncomeSources.get(i).GetTimePeriod();
            occ = tp.GetOccurrencesWithin(_startTime, _endTime);

            for (int ii = 0; ii < occ.size(); ii++) {
                //Add income to temp array
                if ( IncomeSources.get(i).GetTimePeriod().GetDate().equals(occ.get(ii)) ) {
                    _IncomeSources_timeFrame.add( IncomeSources.get(i) );
                } else {
                    _IncomeSources_timeFrame.add(new Income(IncomeSources.get(i), new TimePeriod(occ.get(ii))));
                }
            }
        }


        //Add expenses within the time period to the timeframe array
        for (int i = 0; i < ExpenseSources.size(); i++){

            tp = ExpenseSources.get(i).GetTimePeriod();
            occ = tp.GetOccurrencesWithin(_startTime, _endTime);

            for (int ii = 0; ii < occ.size(); ii++) {
                //Add expense to temp array
                if ( ExpenseSources.get(i).GetTimePeriod().GetDate() != null) {
                    if (ExpenseSources.get(i).GetTimePeriod().GetDate().equals(occ.get(ii))) {
                        _ExpenseSources_timeFrame.add(ExpenseSources.get(i));
                        //if (ExpenseSources.get(i).IsParent()) {} else{ ProfileManager.Print("Special case");}
                    }
                    else {
                        _ExpenseSources_timeFrame.add(new Expense(ExpenseSources.get(i), new TimePeriod(occ.get(ii))));
                    }
                }
            }

            //Add expense if there are no repetitions of it
            //if (occ.size() == 1){
                //_ExpenseSources_timeFrame.add( ExpenseSources.get(i) );
            //}
        }
    }
}
