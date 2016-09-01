package carvellwakeman.incomeoutcome;


import java.util.*;

import android.database.sqlite.SQLiteDatabase;
import org.joda.time.*;

public class Profile implements java.io.Serializable
{
    //Info
    private int _uniqueID;
    private String name;


    //TimeFrame
    private LocalDate _startTime;
    private LocalDate _endTime;
    private Period _period;

    //Income sources
    //private ArrayList<Income> IncomeSources;
    //private ArrayList<Income> _IncomeSources_timeFrame;

    //Expenses sources
    //private ArrayList<Expense> ExpenseSources;
    //private ArrayList<Expense> _ExpenseSources_timeFrame;

    //Transaction sources
    private ArrayList<Transaction> Transactions;
    private ArrayList<Transaction> Transactions_timeFrame;

    //Sorting & Filtering
    private ProfileManager.SORT_METHODS sortMethod;
    private ProfileManager.FILTER_METHODS filterMethod;


    //Totals
    //protected HashMap<String, Expense> _ExpenseTotals;
    //protected HashMap<String, Income> _IncomeTotals;
    protected HashMap<String, Transaction> TransactionTotals;



    public Profile(String _name){
        _uniqueID = java.lang.System.identityHashCode(this);
        name = _name;

        //IncomeSources = new ArrayList<>();
        //ExpenseSources = new ArrayList<>();
        Transactions = new ArrayList<>();

        //_IncomeSources_timeFrame = new ArrayList<>();
        //_ExpenseSources_timeFrame = new ArrayList<>();
        Transactions_timeFrame = new ArrayList<>();

        //_ExpenseTotals = new HashMap<>();
        //_IncomeTotals = new HashMap<>();
        TransactionTotals = new HashMap<>();

        //GetTotalCostPerPersonInTimeFrame();
        //GetTotalIncomePerSourceInTimeFrame();

        //TODO: Necessary?
        //CalculateTotalsInTimeFrame(0);
        //CalculateTotalsInTimeFrame(1);
    }

    public void ClearAllObjects(){
        _startTime = null;
        _endTime = null;

        //Income sources
       //for (Income in : IncomeSources){
        //    in.ClearAllObjects();
        //}
        //IncomeSources.clear();
        //_IncomeSources_timeFrame.clear();

        //Expenses sources
        //for (Expense ex : ExpenseSources){
        //    ex.ClearAllObjects();
        //}
        //ExpenseSources.clear();
        //_ExpenseSources_timeFrame.clear();

        //Transactions
        for (Transaction tr : Transactions){
            tr.ClearAllObjects();
        }
        Transactions.clear();
        Transactions_timeFrame.clear();


        //Totals
        TransactionTotals.clear();
        //_ExpenseTotals.clear();
        //_IncomeTotals.clear();
    }

    public void RemoveAll(){
        //for (int i = 0; i < ExpenseSources.size(); i++){
        //    RemoveExpenseDontSave(ExpenseSources.get(i), true);
        //}
        //for (int i = 0; i < IncomeSources.size(); i++){
        //    RemoveIncomeDontSave(IncomeSources.get(i), true);
        //}
        for (int i = 0; i < Transactions.size(); i++){
            RemoveTransactionDontSave(Transactions.get(i), true);
        }
    }

    //Accessors
    public int GetID() { return _uniqueID; }
    public String GetName() { return name; }

    public LocalDate GetStartTime(){  return _startTime; }
    public LocalDate GetEndTime(){ return _endTime; }
    public Period GetPeriod() { return _period; }

    //public int GetIncomeSourcesSize() { return IncomeSources.size(); }
    //public int GetExpenseSourcesSize() { return ExpenseSources.size(); }
    public int GetTransactionsSize() { return Transactions.size(); }

    //public int GetIncomeSourcesInTimeFrameSize() { return _IncomeSources_timeFrame.size(); }
    //public int GetExpenseSourcesInTimeFrameSize() { return _ExpenseSources_timeFrame.size(); }
    public int GetTransactionsInTimeFrameSize() { return Transactions_timeFrame.size(); }

    public String GetDateFormatted()
    {
        if (_endTime != null && _startTime != null) {
            if (_endTime.getDayOfYear() == _endTime.dayOfYear().getMaximumValue() && _startTime.getDayOfYear() == _startTime.dayOfYear().getMinimumValue()){
                return _startTime.toString(ProfileManager.simpleDateFormatJustYear);
            }
            else if (_endTime.getDayOfMonth() == _endTime.dayOfMonth().getMaximumValue() && _startTime.getDayOfMonth() == _startTime.dayOfMonth().getMinimumValue()) {
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
    public void SetStartTimeDontSave(LocalDate start){
        _startTime = start;
    }
    public void SetStartTime(LocalDate start){ //[EXCLUDE] Removing insertSettingDatabase so that the database is not updated many times for one profile
        SetStartTimeDontSave(start);
        if (GetStartTime() != null) {
            SetEndTimeDontSave(GetStartTime().plus(GetPeriod()));
            SetEndTimeDontSave(GetEndTime().minusDays(1));
        }
        ProfileManager.InsertSettingDatabase(this, true);
    }
    public void SetEndTimeDontSave(LocalDate end){ _endTime = end; }
    public void SetEndTime(LocalDate end){
        SetEndTimeDontSave(end);
        ProfileManager.InsertSettingDatabase(this, true);
    }
    public void SetPeriodDontSave(Period period){
        _period = period;
    }
    public void SetPeriod(Period period){
        SetPeriodDontSave(period);
        ProfileManager.InsertSettingDatabase(this, true);
    }

    public void TimePeriodPlus(int n){
        if (GetStartTime() == null) { SetStartTime( (new LocalDate()).withDayOfMonth(1) ); }

        for (int i = 0; i < n; i++) {
            SetStartTime(GetStartTime().plus(GetPeriod()));
        }
    }
    public void TimePeriodMinus(int n){
        if (GetStartTime() == null) { SetStartTime( (new LocalDate()).withDayOfMonth(1) ); }

        for (int i = 0; i < n; i++) {
            SetStartTime(GetStartTime().minus(GetPeriod()));
        }
    }


    //Transaction management
    public void AddTransactionDontSave(Transaction transaction) { Transactions.add(transaction); }
    public void AddTransaction(Transaction transaction) { AddTransactionDontSave(transaction); ProfileManager.InsertTransactionDatabase(this, transaction, false);}
    public void RemoveTransactionDontSave(Transaction transaction, boolean deleteChildren) {
        if (transaction != null) {
            if (deleteChildren) {
                for (int id : transaction.GetChildren()) {
                    RemoveTransaction(GetTransaction(id), deleteChildren);
                }
            }
            //Remove transaction from it's parent as a child
            if (transaction.GetParentID() != 0){
                Transaction parent = GetTransaction(transaction.GetParentID());
                if (parent != null){
                    parent.RemoveChild(transaction.GetID());
                    UpdateTransaction(parent);
                }
            }
            //Remove expense from profile
            Transactions.remove(transaction);
        }
    }
    public void RemoveTransaction(Transaction transaction, boolean deleteChildren) {
        ProfileManager.RemoveTransactionDatabase(transaction);
        RemoveTransactionDontSave(transaction, deleteChildren);
    }
    public void RemoveTransaction(int id, boolean deleteChildren) { RemoveTransaction(GetTransaction(id), deleteChildren); }

    private void UpdateTransaction(Transaction transaction, Profile profile) {
        ProfileManager.InsertTransactionDatabase(profile, transaction, true);
        //Check if transaction has a parent and is exactly like its parent, and if so, remove its date from the parent's blacklist and delete it
        Transaction parent = null;
        if (transaction.GetParentID() > 0){ parent = GetTransaction(transaction.GetParentID()); }
        if (parent != null){
            if (transaction.isSimilarChildOf(parent) ) {
                //ProfileManager.Print("Special case, transaction just like parent, remove blacklist date ("+transaction.GetTimePeriod().GetDateFormatted()+") and delete transaction");

                //Remove blacklist date
                TimePeriod p_tp = parent.GetTimePeriod();
                TimePeriod tp = transaction.GetTimePeriod();
                if (tp != null && p_tp != null){
                    p_tp.RemoveBlacklistDate(tp.GetDate());
                }
                //Remove transaction child from its parent
                parent.RemoveChild(transaction.GetID());
                //Remove transaction
                profile.RemoveTransaction(transaction, false);
            }
        }
    }
    public void UpdateTransaction(Transaction transaction) { UpdateTransaction(transaction, this); }

    public void CloneTransaction(Transaction oldTr, Transaction newTr){
        AddTransaction(newTr);

        //Set child relationship
        oldTr.AddChild(newTr, true);
        newTr.SetParentID(oldTr.GetID());

        UpdateTransaction(newTr);
        UpdateTransaction(oldTr);
    }

    public void UpdateOtherPerson(String old, String name) {
        for (Transaction tr : Transactions){
            if (tr.GetSplitWith().equals(old)) {
                tr.SetSplitValue(name, tr.GetSplitValue());
                UpdateTransaction(tr);
            }
        }
    }

    public void UpdateCategory(String old, String name) {
        for (Transaction tr : Transactions){
            if (tr.GetCategory().equals(old)) {
                tr.SetCategory(name);
                UpdateTransaction(tr);
            }
        }
    }

    public void UpdatePaidBackInTimeFrame(LocalDate paidBack, boolean override){
        ArrayList<Transaction> tempList = new ArrayList<>();
        for (Transaction tr : Transactions_timeFrame) { tempList.add(tr); }

        //ProfileManager.Print("Size:" + tempList.size());
        for (int i = 0; i < tempList.size(); i++){
            Transaction tr = tempList.get(i);
            //ProfileManager.Print(ex.GetID() + " : " + tr.GetCategory());

            //Child or Parent that doesn't repeat : Set paid back unless it already exists or override
            if (GetTransaction(tr.GetID()) != null){
                if (tr.IsChild() || tr.GetTimePeriod() != null && !tr.GetTimePeriod().DoesRepeat()) {
                    //ProfileManager.Print("Option 1");
                    if (tr.GetPaidBack() == null || override) {
                        tr.SetPaidBack(paidBack);
                        UpdateTransaction(tr);
                    }
                } else { //Parent that does repeat : clone expense to avoid affecting children
                    //ProfileManager.Print("Option 2");
                    Transaction newTr = new Transaction(tr);
                    newTr.SetTimePeriod(new TimePeriod(tr.GetTimePeriod().GetDate()));
                    newTr.SetPaidBack(paidBack);
                    newTr.RemoveChildren();
                    CloneTransaction(tr, newTr);
                }
            }
            else { //Not independent transaction : CloneExpense and set paid back
                //ProfileManager.Print("Option 3");
                Transaction newTr = new Transaction(tr);
                newTr.SetPaidBack(paidBack);
                newTr.RemoveChildren();
                CloneTransaction(GetTransaction(tr.GetParentID()), newTr);
            }
        }
    }

    //Get transaction at index
    public Transaction GetTransactionAtIndex(int index) { if (index >= 0 && Transactions.size() > 0){ return Transactions.get(index); } else { return null; } }
    public Transaction GetTransactionAtIndexInTimeFrame(int index) { if (index >= 0 && Transactions_timeFrame.size() > 0){ return Transactions_timeFrame.get(index); } else { return null; } }

    //Get transaction by ID
    public Transaction GetTransaction(int id) {
        for (int i = 0; i < Transactions.size(); i++) {
            if (Transactions.get(i).GetID() == id) {
                return Transactions.get(i);
            }
        }
        return null;
    }
    public Transaction GetTransactionInTimeFrame(int id) {
        for (int i = 0; i < Transactions_timeFrame.size(); i++) {
            if (Transactions_timeFrame.get(i).GetID() == id) {
                return Transactions_timeFrame.get(i);
            }
        }
        return null;
    }
    public Transaction GetParentTransactionFromTimeFrameTransaction(Transaction transaction){
        if (transaction != null) {
            Transaction TF = GetTransactionInTimeFrame(transaction.GetID());
            if (TF != null && TF.IsChild()) {
                return GetTransaction(TF.GetParentID());
            }
            else {
                return transaction;
            }
        }
        return null;
    }

    //Get period total cost between two dates
    public Transaction CalculatePeriodTotalBetweenDates(){
        Transaction nt = new Transaction();

        for (int i = 0; i < Transactions_timeFrame.size(); i++){
            Transaction tr = Transactions_timeFrame.get(i);

            if (tr.GetType() == Transaction.TRANSACTION_TYPE.Expense){
                nt.SetValue(nt.GetValue() - tr.GetValue());
            }
            else if (tr.GetType() == Transaction.TRANSACTION_TYPE.Income){
                nt.SetValue(nt.GetValue() + tr.GetValue());
            }
        }
        nt.SetTimePeriod(new TimePeriod(GetStartTime()));

        return nt;
    }

    ///ActivityType///
    ///0 - Calculate expenses///
    ///1 - Calculate income///
    ///KeyType///
    ///0 - SplitWith (Map of transactions by person)
    ///1 - Source (Map by source)
    ///2 - Category (Map by category)
    public HashMap<String, Transaction> CalculateTotalsInTimeFrame(int activityType, int keyType){ return CalculateTotalsInTimeFrame(activityType, keyType, false); }
    public HashMap<String, Transaction> CalculateTotalsInTimeFrame(int activityType, int keyType, boolean includeMe){
        TransactionTotals.keySet().clear();
        TransactionTotals.values().clear();
        TransactionTotals.clear();

        Transaction.TRANSACTION_TYPE ttype = (activityType==0 ? Transaction.TRANSACTION_TYPE.Expense : Transaction.TRANSACTION_TYPE.Income);
        String key = "";
        String me = ProfileManager.getString(R.string.format_me);

        Transaction curr;

        for (int i = 0; i < Transactions_timeFrame.size(); i++) {
            Transaction next = Transactions_timeFrame.get(i);

            //Find key type
            switch(keyType){
                case 0: //Split With
                    key = next.GetSplitWith();
                    break;
                case 1: //Source name
                    key = next.GetSourceName();
                    break;
                case 2: //Category
                    key = next.GetCategory();
                    break;
            }

            //Find transaction by key if it's been found before in the loop, or make a new one
            curr = TransactionTotals.get(key);
            if (curr == null && key != null && !key.equals("")) {
                TransactionTotals.put(key, new Transaction( ttype ));//TODO Avoid creating a new transaction, try to just sum up the value

                curr = TransactionTotals.get(key);
            }


            //Add up value
            if (curr != null) {
                switch (keyType) {
                    case 0: //Split With
                        //My part of this transaction
                        if (includeMe){
                            //Special case for Split With, add an additional transaction for MY part of this split
                            if (TransactionTotals.get(me) == null){ TransactionTotals.put(me, new Transaction( ttype )); }

                            //curr.SetValue(curr.GetValue() + next.GetSplitCost());
                            //curr.SetSplitValue(next.GetSplitWith(), curr.GetSplitValue() + next.GetTotalZeroWeighted());

                            //TransactionTotals.get(me).SetValue(TransactionTotals.get(me).GetValue() + next.GetMyCost());
                            //TransactionTotals.get(me).SetSplitValue(me, 0.0d);


                            curr.SetValue(curr.GetValue() + next.GetSplitDebt());

                            TransactionTotals.get(me).SetValue(TransactionTotals.get(me).GetValue() + next.GetMyDebt());
                        }
                        else {
                            curr.SetValue(curr.GetValue() + next.GetMyDebt());
                            curr.SetSplitValue(next.GetSplitWith(), curr.GetSplitValue() + next.GetSplitDebt());
                        }
                        break;
                    case 2: //Category (same as source)
                    case 1: //Source name
                        if (activityType == 0) { //Expenses
                            if (next.GetSplitWith() != null) { //If Split expense
                                if (!next.GetIPaid()) { //Paid back, only include my debt
                                    curr.SetValue(curr.GetValue() + next.GetMyDebt());
                                } else {
                                    if (next.IsPaidBack()){ curr.SetValue(curr.GetValue() + next.GetMySplitValue()); }
                                    else { curr.SetValue(curr.GetValue() + next.GetValue()); }
                                }
                            }
                            else { curr.SetValue(curr.GetValue() + next.GetValue()); }
                        }
                        else if (activityType == 1) { //Income
                            curr.SetValue(curr.GetValue() + next.GetValue());
                        }
                        curr.SetSourceName(key);
                        curr.SetCategory(next.GetCategory());
                        break;
                }
            }
        }

        return new HashMap<>(TransactionTotals);
    }


    //Transfer
    public void TransferTransaction(Transaction transaction, Profile moveTo){
        UpdateTransaction(transaction, moveTo);
        moveTo.AddTransactionDontSave(transaction);
        RemoveTransaction(transaction, true);
    }
    public void TransferAllTransactions(Profile moveTo){
        for (int i = 0; i < Transactions.size(); i++){
            TransferTransaction(Transactions.get(i), moveTo);
        }
    }

    //Sort
    public ProfileManager.SORT_METHODS GetSortMethod(){ return sortMethod; }
    public int dateSort(Transaction t1, Transaction t2){
        TimePeriod tp1 = t1.GetTimePeriod();
        TimePeriod tp2 = t2.GetTimePeriod();
        if (tp1!=null && tp2!=null) {
            LocalDate td1 = tp1.GetDate();
            LocalDate td2 = tp2.GetDate();
            if (td1!=null && td2!=null) {
                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                return (int) Math.signum(Days.daysBetween(epoch, td1.toDateTime(new LocalTime())).getDays() - Days.daysBetween(epoch, td2.toDateTime(new LocalTime())).getDays());
            }
            return 0;
        }
        return 0;
    }
    public int valueSort(Transaction t1, Transaction t2){ return (int)Math.signum( (t1.GetValue()) - (t2.GetValue()) ); }
    public int paidbySort(Transaction t1, Transaction t2){ return (t1.GetIPaid() ? 1 : 0) - (t2.GetIPaid() ? 1 : 0); }
    public int categorySort(Transaction t1, Transaction t2){ return  t1.GetCategory().compareTo(t2.GetCategory()); }
    public int sourceSort(Transaction t1, Transaction t2){ return  t1.GetSourceName().compareTo(t2.GetSourceName()); }
    public void Sort(ProfileManager.SORT_METHODS method)
    {
        sortMethod = method;

        switch (sortMethod) {
            case DATE_UP:
                Collections.sort(Transactions_timeFrame, new Comparator<Transaction>() {
                    @Override public int compare(Transaction  t1, Transaction  t2) { return dateSort(t1, t2); }
                });
                break;
            case DATE_DOWN:
                Collections.sort(Transactions_timeFrame, new Comparator<Transaction>() {
                    @Override public int compare(Transaction  t1, Transaction  t2) { return dateSort(t2, t1); }
                });
                break;
            case COST_UP:
                Collections.sort(Transactions_timeFrame, new Comparator<Transaction>() {
                    @Override public int compare(Transaction  t1, Transaction  t2) { return valueSort(t1, t2); }
                });
                break;
            case COST_DOWN:
                Collections.sort(Transactions_timeFrame, new Comparator<Transaction>() {
                    @Override public int compare(Transaction  t1, Transaction  t2) { return valueSort(t2, t1); }
                });
                break;
            case CATEGORY_UP:
                Collections.sort(Transactions_timeFrame, new Comparator<Transaction>() {
                    @Override public int compare(Transaction  t1, Transaction  t2) { return categorySort(t1, t2); }
                });
                break;
            case CATEGORY_DOWN:
                Collections.sort(Transactions_timeFrame, new Comparator<Transaction>() {
                    @Override public int compare(Transaction  t1, Transaction  t2) { return categorySort(t2, t1); }
                });
                break;
            case SOURCE_UP:
                Collections.sort(Transactions_timeFrame, new Comparator<Transaction>() {
                    @Override public int compare(Transaction  t1, Transaction  t2) { return sourceSort(t1, t2); }
                });
                break;
            case SOURCE_DOWN:
                Collections.sort(Transactions_timeFrame, new Comparator<Transaction>() {
                    @Override public int compare(Transaction  t1, Transaction  t2) { return sourceSort(t2, t1); }
                });
                break;
            case PAIDBY_UP:
                Collections.sort(Transactions_timeFrame, new Comparator<Transaction>() {
                    @Override public int compare(Transaction  t1, Transaction  t2) { return paidbySort(t1, t2); }
                });
                break;
            case PAIDBY_DOWN:
                Collections.sort(Transactions_timeFrame, new Comparator<Transaction>() {
                    @Override public int compare(Transaction  t1, Transaction  t2) { return paidbySort(t2, t1); }
                });
                break;
        }
    }

    //Filter
    public void Filter(ProfileManager.FILTER_METHODS method, Object filterData, int activityType){
        filterMethod = method;
        ArrayList<Transaction> temp = new ArrayList<>();
        temp.addAll(Transactions_timeFrame);

        if (filterMethod == ProfileManager.FILTER_METHODS.NONE){
            //Reset time frame
            CalculateTimeFrame(activityType);
            //Calculate totals
            CalculateTotalsInTimeFrame(activityType, (activityType==0 ? 0 : 1));
            return;
        }

        for (int i = 0; i < temp.size(); i++) {
            switch (filterMethod) {
                case CATEGORY:
                    if (filterData == null || temp.get(i).GetCategory() == null) { Transactions_timeFrame.clear(); break; }
                    if (filterData != null && temp.get(i).GetCategory() != null && !temp.get(i).GetCategory().equalsIgnoreCase(String.valueOf(filterData))) {
                        Transactions_timeFrame.remove(temp.get(i));
                    }
                    break;
                case SOURCE:
                    if (filterData == null || temp.get(i).GetSourceName() == null) { Transactions_timeFrame.clear(); break; }
                    if (filterData != null && temp.get(i).GetSourceName() != null && !temp.get(i).GetSourceName().equalsIgnoreCase(String.valueOf(filterData))) {
                        Transactions_timeFrame.remove(temp.get(i));
                    }
                    break;
                case PAIDBY:
                    if (filterData == null || temp.get(i).GetSplitWith() == null) { Transactions_timeFrame.clear(); break; }
                    if (filterData != null && temp.get(i).GetSplitWith() != null && !temp.get(i).GetSplitWith().equalsIgnoreCase(String.valueOf(filterData))) {
                        Transactions_timeFrame.remove(temp.get(i));
                    }
                    break;
            }
        }

        //Calculate totals
        CalculateTotalsInTimeFrame(activityType, (activityType==0 ? 0 : 1));
    }


    //Calculate the expenses and income sources that are within the timeframe provided
    public void CalculateTimeFrame(Integer activityType)
    {
        Transactions_timeFrame.clear();

        TimePeriod tp;
        ArrayList<LocalDate> occ = null;

        if (_startTime!= null && _endTime != null) {

            //Add transactions within the time period to the timeframe array
            for (int i = 0; i < Transactions.size(); i++) {
                if (activityType == null || Transactions.get(i).GetType().ordinal() == activityType) {
                    tp = Transactions.get(i).GetTimePeriod();
                    if (tp != null) {
                        occ = tp.GetOccurrencesWithin(_startTime, _endTime);

                        for (int ii = 0; ii < occ.size(); ii++) {
                            //Add transaction to temp array
                            if (Transactions.get(i).GetTimePeriod() != null && Transactions.get(i).GetTimePeriod().GetDate() != null) {
                                if (Transactions.get(i).GetTimePeriod().GetDate().equals(occ.get(ii))) {
                                    Transactions_timeFrame.add(Transactions.get(i));
                                }
                                else {
                                    Transaction temp = new Transaction(Transactions.get(i), new TimePeriod(occ.get(ii)));
                                    temp.SetParentID(Transactions.get(i).GetID());
                                    Transactions_timeFrame.add(temp);
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

        }
        else{ //Add all transactions if start and end time are null
            for (Transaction tr : Transactions){
                if (tr.GetType().ordinal() == activityType) {
                    Transactions_timeFrame.add(tr);
                }
            }
        }
    }
}
