package carvellwakeman.incomeoutcome;


import java.util.*;

import android.content.Context;
import org.joda.time.*;
import org.joda.time.format.PeriodFormat;

public class Profile implements java.io.Serializable
{
    //Info
    private int _uniqueID;
    private String name;


    //TimeFrame
    private boolean _showAll;
    private LocalDate _startTime;
    private LocalDate _endTime;
    private Period _period;
    private Repeat _periodFrequency;

    //Transaction sources
    private ArrayList<Transaction> Transactions;
    private ArrayList<Transaction> Transactions_timeFrame;

    //Totals
    private HashMap<String, Transaction> TransactionTotals;


    //Sorting & Filtering
    private ProfileManager.SORT_METHODS sortMethod;
    private ProfileManager.FILTER_METHODS filterMethod;
    private Comparator<Transaction> sortComparator;
    private Object filterData;


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
        _showAll = false;

        //Transactions
        for (Transaction tr : Transactions){
            tr.ClearAllObjects();
        }
        Transactions.clear();
        Transactions_timeFrame.clear();

        //Totals
        TransactionTotals.clear();
    }

    public void RemoveAll(Context ac){
        for (int i = 0; i < Transactions.size(); i++){
            RemoveTransaction(ac, Transactions.get(i), true);
        }
    }

    //Accessors
    public int GetID() { return _uniqueID; }
    public String GetName() { return name; }

    public LocalDate GetStartTime(){  return _startTime; }
    public LocalDate GetEndTime(){ return _endTime; }
    public Period GetPeriod() { return _period; }
    public Repeat GetPeriodFreqency() { return _periodFrequency; }
    public boolean GetShowAll() { return _showAll; }

    //public int GetIncomeSourcesSize() { return IncomeSources.size(); }
    //public int GetExpenseSourcesSize() { return ExpenseSources.size(); }
    public int GetTransactionsSize() { return Transactions.size(); }

    //public int GetIncomeSourcesInTimeFrameSize() { return _IncomeSources_timeFrame.size(); }
    //public int GetExpenseSourcesInTimeFrameSize() { return _ExpenseSources_timeFrame.size(); }
    public int GetTransactionsInTimeFrameSize() { return Transactions_timeFrame.size(); }

    public String GetPeriodFormatted(){
        if (_period != null){
            return ProfileManager.getString(R.string.repeat_occurevery) + " "+ _period.toString(PeriodFormat.wordBased(App.GetLocale()));
        }
        return "No Period";
    }

    public String GetDateFormatted()
    {
        if (_endTime != null && _startTime != null && !_showAll) {
            if (_endTime.getDayOfYear() == _endTime.dayOfYear().getMaximumValue() && _startTime.getDayOfYear() == _startTime.dayOfYear().getMinimumValue()){ //Yearly
                return _startTime.toString(ProfileManager.simpleDateFormatJustYear);
            }
            else if (_startTime.getMonthOfYear()==_endTime.getMonthOfYear() &&  _endTime.getDayOfMonth() == _endTime.dayOfMonth().getMaximumValue() && _startTime.getDayOfMonth() == _startTime.dayOfMonth().getMinimumValue()) { //Monthly
                return _startTime.toString(ProfileManager.simpleDateFormatNoDay);
            }


            else if (_startTime.getMonthOfYear()==DateTimeConstants.DECEMBER && _startTime.getDayOfMonth() == 1 &&
                    _endTime.getMonthOfYear()==DateTimeConstants.FEBRUARY && _endTime.getDayOfMonth() == _endTime.dayOfMonth().getMaximumValue()){ //Seasonally (Winter)
                return ProfileManager.getString(R.string.time_winter) + _startTime.getYear() + "-" + _endTime.getYear();
            }
            else if (_startTime.getMonthOfYear()==DateTimeConstants.MARCH && _startTime.getDayOfMonth() == 1 &&
                    _endTime.getMonthOfYear()==DateTimeConstants.MAY && _endTime.getDayOfMonth() == _endTime.dayOfMonth().getMaximumValue()){ //Seasonally (Spring)
                return ProfileManager.getString(R.string.time_spring) + _startTime.getYear();
            }
            else if (_startTime.getMonthOfYear()==DateTimeConstants.JUNE && _startTime.getDayOfMonth() == 1 &&
                    _endTime.getMonthOfYear()==DateTimeConstants.AUGUST && _endTime.getDayOfMonth() == _endTime.dayOfMonth().getMaximumValue()){ //Seasonally (Summer)
                return ProfileManager.getString(R.string.time_summer) + _startTime.getYear();
            }
            else if (_startTime.getMonthOfYear()==DateTimeConstants.SEPTEMBER && _startTime.getDayOfMonth() == 1 &&
                    _endTime.getMonthOfYear()==DateTimeConstants.NOVEMBER && _endTime.getDayOfMonth() == _endTime.dayOfMonth().getMaximumValue()){ //Seasonally (Fall)
                return ProfileManager.getString(R.string.time_fall) + _startTime.getYear();
            }

            else if (_startTime.equals(_endTime)) {
                return _startTime.toString(ProfileManager.simpleDateFormat);
            }

            else { //Default
                return _startTime.toString(ProfileManager.simpleDateFormatShort) + " - " + _endTime.toString(ProfileManager.simpleDateFormatShort);
            }
        }
        else {
            if (_endTime == null && _startTime != null && !_showAll){
                return ProfileManager.getString(R.string.time_started) + _startTime.toString(ProfileManager.simpleDateFormat);
            }
            else {
                return ProfileManager.getString(R.string.misc_all);
            }
        }
    }
    //public int GetTotalsSize() { return _totals.size(); }
    //public Expense GetTotalValueAtKey(OtherPerson op){ return _totals.get(op); }
    //public OtherPerson GetTotalKeyAtIndex(int index) { return _totals.keySet().toArray(new OtherPerson[_totals.keySet().size()])[index]; }


    //Mutators
    public void SetID(int id){ _uniqueID = id; } //TODO Should not be used outside of loading
    public void SetName(String newName){ name = newName; }
    public void SetStartTimeDontSave(LocalDate start){
        _startTime = start;
        //Set end time if it does not exist
        if (_startTime != null && GetEndTime() == null) {
            SetEndTimeDontSave(_startTime.plus(GetPeriod()));
            SetEndTimeDontSave(GetEndTime().minusDays(1));
        }
    }
    public void SetStartTime(Context ac, LocalDate start){ //[EXCLUDE] Removing DBInsertSetting so that the database is not updated many times for one profile
        SetStartTimeDontSave(start);

        ProfileManager.getInstance().DBInsertSetting(ac, this, true);
    }
    public void SetEndTimeDontSave(LocalDate end){ _endTime = end; }
    public void SetEndTime(Context ac, LocalDate end){
        SetEndTimeDontSave(end);
        ProfileManager.getInstance().DBInsertSetting(ac, this, true);
    }
    public void SetPeriodDontSave(Period period){
        _period = period;
        //Set frequency
        if (period.getYears()>0){ _periodFrequency = Repeat.YEARLY; }
        else if (period.getMonths()>0) { _periodFrequency = Repeat.MONTHLY; }
        else if (period.getWeeks()>0) { _periodFrequency = Repeat.WEEKLY; }
        else if (period.getDays()>0) { _periodFrequency = Repeat.DAILY; }
        else { _periodFrequency = Repeat.NEVER; }
        //Set start and end dates to the precision of the period
        LocalDate T = TimePeriod.calcNearestDateInPeriod(period, GetStartTime());
        SetStartTimeDontSave(T);
        //SetEndTime(TimePeriod.calcNearestDateInPeriod(period, GetEndTime()));
    }
    public void SetPeriod(Context ac, Period period){
        SetPeriodDontSave(period);
        ProfileManager.getInstance().DBInsertSetting(ac, this, true);
    }

    public void TimePeriodPlus(Context ac, int n){
        if (GetStartTime() == null) { SetStartTime(ac, (new LocalDate()).withDayOfMonth(1) ); }

        for (int i = 0; i < n; i++) {
            SetStartTime(ac, GetStartTime().plus(GetPeriod()));
        }

        SetEndTime(ac, GetStartTime().plus(GetPeriod()));

        //Subtract one day for months
        if (GetPeriodFreqency()==Repeat.MONTHLY || GetPeriodFreqency()==Repeat.YEARLY) { SetEndTime(ac, GetEndTime().minusDays(1)); }

    }
    public void TimePeriodMinus(Context ac, int n){
        if (GetStartTime() == null) { SetStartTime(ac, (new LocalDate()).withDayOfMonth(1) ); }

        for (int i = 0; i < n; i++) {
            SetStartTime(ac, GetStartTime().minus(GetPeriod()));
        }

        SetEndTime(ac, GetStartTime().plus(GetPeriod()));
        //Subtract one day for months
        if (GetPeriodFreqency()==Repeat.MONTHLY || GetPeriodFreqency()==Repeat.YEARLY) { SetEndTime(ac, GetEndTime().minusDays(1)); }
    }
    public void SetShowAll(boolean shouldShowAll){ _showAll = shouldShowAll; }


    //Transaction management
    public void AddTransactionDontSave(Transaction transaction) { Transactions.add(transaction); }
    public void AddTransaction(Context ac, Transaction transaction) { AddTransactionDontSave(transaction); ProfileManager.getInstance().DBInsertTransaction(ac, this, transaction, false);}
    public void RemoveTransaction(Context ac, Transaction transaction, boolean deleteChildren) {
        ProfileManager.getInstance().DBRemoveTransaction(ac, transaction);

        if (transaction != null) {
            if (deleteChildren) {
                for (int id : transaction.GetChildren()) {
                    RemoveTransaction(ac, GetTransaction(id), deleteChildren);
                }
            }
            //Remove transaction from it's parent as a child
            if (transaction.GetParentID() != 0){
                Transaction parent = GetTransaction(transaction.GetParentID());
                if (parent != null){
                    parent.RemoveChild(transaction.GetID());
                    UpdateTransaction(ac, parent);
                }
            }
            //Remove expense from profile
            Transactions.remove(transaction);
        }
    }
    public void RemoveTransaction(Context ac, int id, boolean deleteChildren) { RemoveTransaction(ac, GetTransaction(id), deleteChildren); }

    private void UpdateTransaction(Context ac, Transaction transaction, Profile profile) {
        ProfileManager.getInstance().DBInsertTransaction(ac, profile, transaction, true);
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
                profile.RemoveTransaction(ac, transaction, false);
            }
        }
    }
    public void UpdateTransaction(Context ac, Transaction transaction) { UpdateTransaction(ac, transaction, this); }

    public void CloneTransaction(Context ac, Transaction oldTr, Transaction newTr){
        AddTransaction(ac, newTr);

        //Set child relationship
        oldTr.AddChild(newTr, true);
        newTr.SetParentID(oldTr.GetID());

        UpdateTransaction(ac, newTr);
        UpdateTransaction(ac, oldTr);
    }

    public void UpdateOtherPerson(Context ac, String old, String name) {
        for (Transaction tr : Transactions){
            if (tr.GetSplitWith() != null && tr.GetSplitWith().equals(old)) {
                tr.SetSplitValue(name, tr.GetSplitValue());
                UpdateTransaction(ac, tr);
            }
        }
    }

    public void UpdateCategory(Context ac, String old, String name) {
        for (Transaction tr : Transactions){
            if (tr.GetCategory().equals(old)) {
                tr.SetCategory(name);
                UpdateTransaction(ac, tr);
            }
        }
    }

    public void UpdatePaidBackInTimeFrame(Context ac, LocalDate paidBack, boolean override){
        ArrayList<Transaction> tempList = new ArrayList<>();
        for (Transaction tr : Transactions_timeFrame) { tempList.add(tr); }

        //ProfileManager.Print("Size:" + tempList.size());
        for (int i = 0; i < tempList.size(); i++){
            Transaction tr = tempList.get(i);
            //ProfileManager.Print(ex.GetID() + " : " + tr.GetCategory());

            if (tr.GetType() == Transaction.TRANSACTION_TYPE.Expense) {
                //Child or Parent that doesn't repeat : Set paid back unless it already exists or override
                if (GetTransaction(tr.GetID()) != null) {
                    if (tr.IsChild() || tr.GetTimePeriod() != null && !tr.GetTimePeriod().DoesRepeat()) {
                        //ProfileManager.Print("Option 1");
                        if (tr.GetPaidBack() == null || override) {
                            tr.SetPaidBack(paidBack);
                            UpdateTransaction(ac, tr);
                        }
                    }
                    else { //Parent that does repeat : clone expense to avoid affecting children
                        //ProfileManager.Print("Option 2");
                        Transaction newTr = new Transaction(tr);
                        newTr.SetTimePeriod(new TimePeriod(tr.GetTimePeriod().GetDate()));
                        newTr.SetPaidBack(paidBack);
                        newTr.RemoveChildren();
                        CloneTransaction(ac, tr, newTr);
                    }
                }
                else { //Not independent transaction : CloneExpense and set paid back
                    //ProfileManager.Print("Option 3");
                    Transaction newTr = new Transaction(tr);
                    newTr.SetPaidBack(paidBack);
                    newTr.RemoveChildren();
                    CloneTransaction(ac, GetTransaction(tr.GetParentID()), newTr);
                }
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
    public Transaction CalculatePeriodTotalBetweenDates(Context ac){
        Transaction nt = new Transaction();

        for (int i = 0; i < Transactions_timeFrame.size(); i++) {
            Transaction tr = Transactions_timeFrame.get(i);

            if (tr.GetType() == Transaction.TRANSACTION_TYPE.Expense) {
                nt.SetValue(nt.GetValue() - tr.GetMyCost());
            }
            else if (tr.GetType() == Transaction.TRANSACTION_TYPE.Income) {
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
        String key = null;
        String me = ProfileManager.getString(R.string.format_me);

        Transaction curr;

        //ProfileManager.Print(App.GetContext(), "ActivityType(" + ttype.toString() + ") keyType(" + keyType + ")");
        //ProfileManager.Print(App.GetContext(), "CalculateTotalsTTS: " + Transactions_timeFrame.size());
        for (int i = 0; i < Transactions_timeFrame.size(); i++) {
            Transaction next = Transactions_timeFrame.get(i);

            //Find key type
            if (keyType==0) { //Split With
                key = next.GetSplitWith();
            } else if (keyType==1) { //Source name
                key = next.GetSourceName();
            } else if (keyType==2) { //Category
                key = next.GetCategory();
            }


            //Find transaction by key if it's been found before in the loop, or make a new one
            curr = TransactionTotals.get(key);
            //ProfileManager.Print(App.GetContext(), "Curr:" + curr + " Key: " + key);
            if (curr == null && key != null) {
                Transaction nt = new Transaction( ttype );
                TransactionTotals.put(key, nt);//TODO Avoid creating a new transaction, try to just sum up the value

                curr = nt;
                //ProfileManager.Print(App.GetContext(), activityType + " - CreateNewTransaction");
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
    public HashMap<String, Transaction> GetTransactionTotals() { return TransactionTotals; }
    public Set<String> GetTotalsKeySet(){ return TransactionTotals.keySet(); }
    public Collection<Transaction> GetTotalsValueSet(){ return TransactionTotals.values(); }

    //Transfer
    public void TransferTransaction(Context ac, Transaction transaction, Profile moveTo){
        UpdateTransaction(ac, transaction, moveTo);
        moveTo.AddTransactionDontSave(transaction);
        RemoveTransaction(ac, transaction, true);
    }
    public void TransferAllTransactions(Context ac, Profile moveTo){
        for (int i = 0; i < Transactions.size(); i++){
            TransferTransaction(ac, Transactions.get(i), moveTo);
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
    public int paidbySort(Transaction t1, Transaction t2){ return t1.GetWhoPaid().compareTo(t2.GetWhoPaid()); }
    public int categorySort(Transaction t1, Transaction t2){ return  t1.GetCategory().compareTo(t2.GetCategory()); }
    public int sourceSort(Transaction t1, Transaction t2){ return  t1.GetSourceName().compareTo(t2.GetSourceName()); }
    public void SetSortMethod(ProfileManager.SORT_METHODS method)
    {
        if (method != null) {
            sortMethod = method;

            switch (sortMethod) {
                case DATE_UP:
                    sortComparator = new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) { return dateSort(t1, t2); }
                    };
                    break;
                case DATE_DOWN:
                    sortComparator = new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) { return dateSort(t2, t1); }
                    };
                    break;
                case COST_UP:
                    sortComparator = new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) { return valueSort(t1, t2); }
                    };
                    break;
                case COST_DOWN:
                    sortComparator = new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) { return valueSort(t2, t1); }
                    };
                    break;
                case CATEGORY_UP:
                    sortComparator = new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) { return categorySort(t1, t2); }
                    };
                    break;
                case CATEGORY_DOWN:
                    sortComparator = new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) { return categorySort(t2, t1); }
                    };
                    break;
                case SOURCE_UP:
                    sortComparator = new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) { return sourceSort(t1, t2); }
                    };
                    break;
                case SOURCE_DOWN:
                    sortComparator = new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) { return sourceSort(t2, t1); }
                    };
                    break;
                case PAIDBY_UP:
                    sortComparator = new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) { return paidbySort(t1, t2); }
                    };
                    break;
                case PAIDBY_DOWN:
                    sortComparator = new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) { return paidbySort(t2, t1); }
                    };
                    break;
                default:
                    sortComparator = new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction t1, Transaction t2) { return dateSort(t1, t2); }
                    };
                    break;
            }
        }
    }

    //Filter
    public ProfileManager.FILTER_METHODS GetFilterMethod() { return filterMethod; }
    public Object GetFilterData() { return filterData; }
    public void SetFilterMethod(ProfileManager.FILTER_METHODS method, Object data){
        if (method != null) {
            filterMethod = method;
            filterData = data;

            //ArrayList<Transaction> temp = new ArrayList<>();
            //temp.addAll(Transactions_timeFrame);

            //if (filterMethod == ProfileManager.FILTER_METHODS.NONE) {
                //Reset time frame
                //CalculateTimeFrame(activityType);
                //Calculate totals
                //CalculateTotalsInTimeFrame(activityType, (activityType == 0 ? 0 : 1));
                //return;
            //}
            /*
            for (int i = 0; i < temp.size(); i++) {
                switch (filterMethod) {
                    case CATEGORY:
                        if (filterData == null || temp.get(i).GetCategory() == null) {
                            Transactions_timeFrame.clear();
                            break;
                        }
                        if (filterData != null && temp.get(i).GetCategory() != null && !temp.get(i).GetCategory().equalsIgnoreCase(String.valueOf(filterData))) {
                            Transactions_timeFrame.remove(temp.get(i));
                        }
                        break;
                    case SOURCE:
                        if (filterData == null || temp.get(i).GetSourceName() == null) {
                            Transactions_timeFrame.clear();
                            break;
                        }
                        if (filterData != null && temp.get(i).GetSourceName() != null && !temp.get(i).GetSourceName().equalsIgnoreCase(String.valueOf(filterData))) {
                            Transactions_timeFrame.remove(temp.get(i));
                        }
                        break;
                    case PAIDBY:
                        if (filterData == null || temp.get(i).GetSplitWith() == null) {
                            Transactions_timeFrame.clear();
                            break;
                        }
                        if (filterData != null && temp.get(i).GetSplitWith() != null && !temp.get(i).GetSplitWith().equalsIgnoreCase(String.valueOf(filterData))) {
                            Transactions_timeFrame.remove(temp.get(i));
                        }
                        break;
                }
            }
*/
            //Calculate totals
            //CalculateTotalsInTimeFrame(activityType, (activityType == 0 ? 0 : 1));
        }
    }


    //Calculate the expenses and income sources that are within the timeframe provided
    public void CalculateTimeFrame(Integer activityType) {
        Transactions_timeFrame.clear();

        TimePeriod tp;
        ArrayList<LocalDate> occ = null;

        //Add transactions within the time period to the timeframe array
        for (int i = 0; i < Transactions.size(); i++) {
            if (activityType == null || Transactions.get(i).GetType().ordinal() == activityType) {

                tp = Transactions.get(i).GetTimePeriod();
                if (tp != null) {

                    LocalDate start = _startTime;
                    LocalDate end = _endTime;
                    if (_showAll) {
                        start = tp.GetDate();
                        end = LocalDate.now();
                    }


                    if (start!=null && end!=null) {
                        occ = tp.GetOccurrencesWithin(start, end);

                        for (int ii = 0; ii < occ.size(); ii++) {
                            //Add transaction to temp array
                            if (Transactions.get(i).GetTimePeriod() != null && Transactions.get(i).GetTimePeriod().GetDate() != null) {
                                //If transaction is not a ghost occurrence of the repeating transaction (IE: A real transaction)
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
                    }
                    else { //Add All
                        Transactions_timeFrame.add(Transactions.get(i));
                    }
                }


            }
        }


        //Sorting and filtering - This is a separate loop so that the sorting and filtering still work even when ShowAll is active (Above code is two separate loops in two if statements)
        ArrayList<Transaction> temp = new ArrayList<>(Transactions_timeFrame);
        for (int i = 0; i < temp.size(); i++) {
            Transaction tran = temp.get(i);
            //Apply Filtering
            if (filterMethod != null) {
                switch (filterMethod) {
                    default:
                    case NONE:
                        break;
                    case CATEGORY:
                        if (filterData != null) {
                            if (tran.GetCategory() != null) {
                                if (!tran.GetCategory().equalsIgnoreCase(String.valueOf(filterData))) {
                                    Transactions_timeFrame.remove(tran);
                                }
                            }
                            else { Transactions_timeFrame.remove(tran); }
                        }
                        break;
                    case SOURCE:
                        if (filterData != null) {
                            if (tran.GetSourceName() != null) {
                                if (!tran.GetSourceName().equalsIgnoreCase(String.valueOf(filterData))) {
                                    Transactions_timeFrame.remove(tran);
                                }
                            }
                            else { Transactions_timeFrame.remove(tran); }
                        }
                        break;
                    case PAIDBY:
                        if (filterData != null) {
                            if (tran.GetSplitWith() != null) {
                                if (String.valueOf(filterData).equalsIgnoreCase(ProfileManager.getString(R.string.format_me))) {
                                    if (!tran.GetIPaid()) {
                                        Transactions_timeFrame.remove(tran);
                                    }
                                }
                                else if (tran.GetSplitWith().equalsIgnoreCase(String.valueOf(filterData))) {
                                    if (tran.GetIPaid()) {
                                        Transactions_timeFrame.remove(tran);
                                    }
                                }
                                else { Transactions_timeFrame.remove(tran); }
                            }
                            else {
                                if (!String.valueOf(filterData).equalsIgnoreCase(ProfileManager.getString(R.string.format_me)) || !tran.GetIPaid()) {
                                    Transactions_timeFrame.remove(tran);
                                }
                            }
                        }
                        break;
                    case SPLITWITH:
                        if (filterData != null) {
                            if (tran.GetSplitWith() != null) {
                                if (!tran.GetSplitWith().equalsIgnoreCase(String.valueOf(filterData))) {
                                    Transactions_timeFrame.remove(tran);
                                }
                            }
                            else { Transactions_timeFrame.remove(tran); }
                        }
                        break;
                }
            }

            //Apply sorting (Much simpler than filtering)
            if (sortComparator != null && sortMethod != null) {
                Collections.sort(Transactions_timeFrame, sortComparator);
            }
        }


    }
}
