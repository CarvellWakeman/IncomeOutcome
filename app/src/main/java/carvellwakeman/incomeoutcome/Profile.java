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
        CalculateTotalsInTimeFrame(0);
        CalculateTotalsInTimeFrame(1);
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


    //Income management
    /*
    public void AddIncome(Income income, boolean dontsave) { IncomeSources.add(income); }
    public void AddIncome(Income income) { AddIncome(income, true); CalculateTimeFrame(); ProfileManager.InsertIncomeDatabase(this, income, false);}
    public void RemoveIncomeDontSave(Income income, boolean deleteChildren) {
        if (income != null) {
            if (deleteChildren) {
                for (int id : income.GetChildren()) {
                    RemoveIncome(GetIncome(id), deleteChildren);
                }
            }
            IncomeSources.remove(income);
        }
    }
    public void RemoveIncome(Income income, boolean deleteChildren) { RemoveIncomeDontSave(income, deleteChildren); CalculateTimeFrame(); ProfileManager.RemoveIncomeDatabase(income); }
    public void RemoveIncome(int id, boolean deleteChildren) { RemoveIncome(GetIncome(id), deleteChildren); }

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
            if (TF != null && TF.IsChild()) {
                return GetIncome(TF.GetParentID());
            }
            else {
                return in;
            }
        }
        return null;
    }

    //Get Income total cost
    */


    //Expense management
    /*
    public void AddExpenseDontSave(Expense expense) { ExpenseSources.add(expense); }
    public void AddExpense(Expense expense) { AddExpenseDontSave(expense); CalculateTimeFrame(); ProfileManager.InsertExpenseDatabase(this, expense, false);}
    public void RemoveExpenseDontSave(Expense expense, boolean deleteChildren) {
        if (expense != null) {
            if (deleteChildren) {
                for (int id : expense.GetChildren()) {
                    RemoveExpense(GetExpense(id), deleteChildren);
                }
            }
            //Remove expense from it's parent as a child
            if (expense.GetParentID() != 0){
                Expense parent = GetExpense(expense.GetParentID());
                if (parent != null){
                    parent.RemoveChild(expense.GetID());
                    UpdateExpense(parent);
                }
            }
            //Remove expense from profile
            ExpenseSources.remove(expense);
        }
    }
    public void RemoveExpense(Expense expense, boolean deleteChildren) { RemoveExpenseDontSave(expense, deleteChildren); CalculateTimeFrame(); ProfileManager.RemoveExpenseDatabase(expense); }
    public void RemoveExpense(int id, boolean deleteChildren) { RemoveExpense(GetExpense(id), deleteChildren); }

    public void UpdateExpense(Expense expense) { UpdateExpense(expense, this); }
    private void UpdateExpense(Expense expense, Profile profile) { ProfileManager.InsertExpenseDatabase(profile, expense, true); }

    public void CloneExpense(Expense oldEx, Expense newEx){
        AddExpense(newEx);

        //Set child relationship
        oldEx.AddChild(newEx, true);
        newEx.SetParentID(oldEx.GetID());

        UpdateExpense(newEx);
        UpdateExpense(oldEx);
    }

    public void TransferExpense(Expense expense, Profile moveTo){
        UpdateExpense(expense, moveTo);
        moveTo.AddExpenseDontSave(expense);
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

    public void UpdatePaidBackInTimeFrame(LocalDate paidBack, boolean override){
        CalculateTimeFrame();

        ArrayList<Expense> tempList = new ArrayList<>();
        for (Expense ex : _ExpenseSources_timeFrame) { tempList.add(ex); }

        //ProfileManager.Print("Size:" + tempList.size());
        for (int i = 0; i < tempList.size(); i++){
            Expense ex = tempList.get(i);
            //ProfileManager.Print(ex.GetID() + " : " + ex.GetCategory());

            //Child or Parent that doesn't repeat : Set paid back unless it already exists or override
            if (GetExpense(ex.GetID()) != null){
                if (ex.IsChild() || ex.GetTimePeriod() != null && !ex.GetTimePeriod().DoesRepeat()) {
                    //ProfileManager.Print("Option 1");
                    if (ex.GetPaidBack() == null || override) {
                        ex.SetPaidBack(paidBack);
                        UpdateExpense(ex);
                    }
                } else { //Parent that does repeat : clone expense to avoid affecting children
                    //ProfileManager.Print("Option 2");
                    Expense newExp = new Expense(ex);
                    newExp.SetTimePeriod(new TimePeriod(ex.GetTimePeriod().GetDate()));
                    newExp.SetPaidBack(paidBack);
                    newExp.RemoveChildren();
                    CloneExpense(ex, newExp);
                }
            }
            else { //Not independent transaction : CloneExpense and set paid back
                //ProfileManager.Print("Option 3");
                Expense newExp = new Expense(ex);
                newExp.SetPaidBack(paidBack);
                newExp.RemoveChildren();
                CloneExpense(GetExpense(ex.GetParentID()), newExp);
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
            if (TF != null && TF.IsChild()) {
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

            if (ex.GetSplitWith()!=null) {
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
    */



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
        RemoveTransactionDontSave(transaction, deleteChildren);
        ProfileManager.RemoveTransactionDatabase(transaction); }
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

    //Get transaction total cost
    public HashMap<String, Transaction> GetTotals(){
        TransactionTotals.keySet().clear();
        TransactionTotals.values().clear();
        TransactionTotals.clear();

        Transaction temp;

        for (int i = 0; i < Transactions.size(); i++){
            Transaction tr = Transactions.get(i);

            temp = TransactionTotals.get( tr.GetSplitWith() );
            if ( temp == null ){
                TransactionTotals.put( tr.GetSplitWith(), new Transaction(tr.GetType()));
                temp = TransactionTotals.get( tr.GetSplitWith() );
            }

            // Sum up values
            temp.SetValue(temp.GetValue() + tr.GetMyDebt());
            temp.SetSplitValue(tr.GetSplitWith(), temp.GetSplitValue() + tr.GetSplitDebt());
        }

        return TransactionTotals;
    }

    public void CalculateTotalsInTimeFrame(int activityType){
        TransactionTotals.keySet().clear();
        TransactionTotals.values().clear();
        TransactionTotals.clear();

        Transaction temp;

        if (activityType == 0) { //Expenses
            for (int i = 0; i < Transactions_timeFrame.size(); i++) {
                Transaction tr = Transactions_timeFrame.get(i);

                temp = TransactionTotals.get(tr.GetSplitWith());
                if (temp == null && tr.GetSplitWith() != null) {
                    TransactionTotals.put(tr.GetSplitWith(), new Transaction(tr.GetType()));
                    temp = TransactionTotals.get(tr.GetSplitWith());
                }

                //Sum up values
                if (temp != null){
                    temp.SetValue(temp.GetValue() + tr.GetMyDebt());
                    temp.SetSplitValue(tr.GetSplitWith(), temp.GetSplitValue() + tr.GetSplitDebt());
                }
            }
        }
        else if (activityType == 1){ //Income
            for (int i = 0; i < Transactions_timeFrame.size(); i++) {
                Transaction tr = Transactions_timeFrame.get(i);

                temp = TransactionTotals.get(tr.GetSourceName());
                if (temp == null) {
                    TransactionTotals.put(tr.GetSourceName(), new Transaction(Transaction.TRANSACTION_TYPE.Income));//TODO Avoid creating a new transaction, try to just sum up the value
                    temp = TransactionTotals.get(tr.GetSourceName());
                }

                // Sum up values
                temp.SetValue(temp.GetValue() + tr.GetValue());
            }
        }

        //return TransactionTotals;
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

    public void Filter(ProfileManager.FILTER_METHODS method, Object filterData, int activityType){
        filterMethod = method;
        ArrayList<Transaction> temp = new ArrayList<>();
        temp.addAll(Transactions_timeFrame);

        if (filterMethod == ProfileManager.FILTER_METHODS.NONE){
            //Reset time frame
            CalculateTimeFrame(activityType);
            //Calculate totals
            CalculateTotalsInTimeFrame(activityType);
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
        CalculateTotalsInTimeFrame(activityType);
    }



    //Calculate the expenses and income sources that are within the timeframe provided
    public void CalculateTimeFrame(int activityType)
    {
        Transactions_timeFrame.clear();

        TimePeriod tp;
        ArrayList<LocalDate> occ = null;

        if (_startTime!= null && _endTime != null) {

            //Add transactions within the time period to the timeframe array
            for (int i = 0; i < Transactions.size(); i++) {
                if (Transactions.get(i).GetType().ordinal() == activityType) {
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
