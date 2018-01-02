package carvellwakeman.incomeoutcome;


import org.joda.time.LocalDate;

import java.util.*;


public class Transaction implements java.io.Serializable
{
    //Transaction type
    public enum TRANSACTION_TYPE
    {
        Expense,
        Income,
    }
    private TRANSACTION_TYPE _type;

    //Transaction IDs
    private int _uniqueID;
    private int _parentID;
    private int _budgetID;

    private int _category;
    private String _source;
    private String _description;

    private double _value;

    private TimePeriod _when;

    //private ArrayList<Integer> _children;

    public String debug_data = "";

    //Expense type only
    private int _paidBy; // -1 is 'you', else person ID
    private HashMap<Integer,Double> _split; //Split[($,me),($,p1),...]

    private LocalDate _paidBack;


    public Transaction() { this(TRANSACTION_TYPE.Expense); } //Default constructor, assuming type will be changed using SetType()
    public Transaction(int type){ this( type == 1 ? TRANSACTION_TYPE.Income : TRANSACTION_TYPE.Expense ); }
    public Transaction(TRANSACTION_TYPE ttype)
    {
        _type = ttype;

        _uniqueID = System.identityHashCode(this);
        _parentID = 0;
        _budgetID = 0;

        _source = "";
        _category = 0;
        _description = "";

        _value = 0.0;

        _when = new TimePeriod();

        //_children = new ArrayList<>();

        //Expense only
        _paidBy = Person.Me.GetID(); // Paid by you (-1)
        _split = new HashMap<>();

        _paidBack = null;
    }
    public Transaction(Transaction copy){
        this(copy.GetType());

        _parentID = copy.GetID();
        _budgetID = copy.GetBudgetID();

        _source = copy.GetSource();
        _category = copy.GetCategory();
        _description = copy.GetDescription();

        _value = copy.GetValue();

        _when = copy.GetTimePeriod();

        //_children.addAll(new ArrayList<>(copy.GetChildren()));

        //Expense only
        _paidBy = copy.GetPaidBy();
        _split = copy.GetSplitArray();

        _paidBack = copy.GetPaidBack();
    }
    //public Transaction(Transaction copy, TimePeriod tp){
    //    this(copy);
    //    _when = tp;
    //}


    //Children
    /*
    private void AddChild(int ID) {
        _children.add(ID);
    }
    public void AddChildrenFromFormattedString(String input){
        if (input != null && !input.equals("")) {
            for (String s : input.split(Helper.getString(R.string.item_delimiter))) {
                if (!s.equals("")) { try { AddChild(Integer.valueOf(s)); } catch (Exception e) { e.printStackTrace(); } }
            }
        }
    }
    public void RemoveChild(Transaction child){
        _children.remove(child.GetID());
        //Un-Blacklist child's date
        TimePeriod tp = child.GetTimePeriod();
        if (tp != null && GetTimePeriod() != null) { GetTimePeriod().RemoveBlacklistDate(tp.GetDate()); }
    }
    public void RemoveAllChildren(){
        _children.clear();
    }
    public ArrayList<Integer> GetChildren() { return _children; }
    public String GetChildrenFormatted(){
        String childrenString = "";
        for (int id : _children){
            childrenString += id;
            if (_children.indexOf(id) != _children.size()-1) { childrenString += Helper.getString(R.string.item_delimiter); }
        }
        return childrenString;
    }
    */


    //Accessors
    public Transaction.TRANSACTION_TYPE GetType() { return _type; }
    public int GetID() { return _uniqueID; }
    public int GetParentID() { return _parentID; }
    public int GetBudgetID() { return _budgetID; }

    public double GetValue() { return _value; }
    public double GetSplit(int personID) { if (_split.containsKey(personID)) { return _split.get(personID); } else { return 0.00d; } }
    public double GetSplitPercentage(int personID) { return Math.round( (GetSplit(personID) / GetValue()) * 100.0d); }
    public boolean IsSplit(){ return _split.size() > 1; }
    public HashMap<Integer, Double> GetSplitArray() { return _split; }
    public String GetSplitArrayString() {
        String t = "";
        for (Map.Entry<Integer, Double> split : _split.entrySet()){
            String name = String.valueOf(split.getKey());
            String value = String.valueOf(split.getValue());

            t += name + ":" + value + "|";

            //Helper.Log(App.GetContext(), "Tran", "GetSplit " + name + ":" + value);
        }
        if (t.length() > 0) {
            return t.substring(0, t.length() - 1); //Delete last delimeter if possible (endIndex is exclusive, add 1 to regular index subtraction
        }
        return "";
    }
    public double GetDebt(int personA, int personB) {
        if (GetPaidBy() == personB && GetPaidBack() == null && personA != personB) {
            return GetSplit(personA);
        } else {
            return 0.00d;
        }
    }

    public int GetPaidBy() { return _paidBy; }
    public LocalDate GetPaidBack() { return _paidBack; }

    public String GetSource() { return _source; }
    public String GetDescription() { return _description; }
    public int GetCategory() { return _category; }

    public TimePeriod GetTimePeriod() { return _when; }

    public ArrayList<Transaction> GetOccurrences(LocalDate startDate, LocalDate endDate, Transaction.TRANSACTION_TYPE type) {
        ArrayList<Transaction> occurrences = new ArrayList<>();

        // Check transaction type match
        if (GetType() == type) {

            // Valid TimePeriod
            TimePeriod tp = GetTimePeriod();
            if (tp != null && tp.GetDate() != null) {

                // Get Dates
                LocalDate start = (startDate != null ? startDate : tp.GetDate());
                LocalDate end = (endDate != null ? endDate : LocalDate.now());

                ArrayList<LocalDate> tp_dates = tp.GetOccurrencesWithin(start, end);

                //Helper.Print(App.GetContext(), "OGDate:" + tp.GetDate().toString(Helper.getString(R.string.date_format)));

                for (int ii = 0; ii < tp_dates.size(); ii++) {
                    //Helper.Print(App.GetContext(), "Occurrence:" + tp_dates.get(ii).toString(Helper.getString(R.string.date_format)));
                    //If we find THIS transaction in the list, add it (IE: The REAL slim shady)
                    boolean rep = tp.DoesRepeat();
                    if (tp.GetDate().equals(tp_dates.get(ii)) && !rep) {
                        occurrences.add(this);
                        //Helper.Print(App.GetContext(), "Add OG: " + this.GetParentID());
                    }
                    else { //Make instance transactions
                        Transaction temp = new Transaction(this);

                        // Add split to temp transaction
                        for (HashMap.Entry<Integer, Double> entry : GetSplitArray().entrySet()){
                            temp.SetSplit(entry.getKey(), entry.getValue());
                        }
                        temp.SetPaidBy(GetPaidBy());

                        // Set timeperiod to temp transaction
                        temp.SetTimePeriod(new TimePeriod(tp_dates.get(ii)));
                        temp.SetParentID(GetID());
                        occurrences.add(temp);

                        //Helper.Print(App.GetContext(), "Add GHOST: " + temp.GetParentID());
                    }
                }
            }
        }

        return occurrences;
    }


    //Mutators
    public void SetType(TRANSACTION_TYPE type) { _type = type; }
    public void SetID(int ID) { _uniqueID = ID; }
    public void SetParentID(int ID) { _parentID = ID; }
    public void SetBudgetID(int ID) { _budgetID = ID; }

    public void SetValue(double value){ _value = value; }
    public void SetSplit(int personID, double value) { _split.put(personID, value); }
    public void SetSplitFromArrayString(String splitString) {
        if (!splitString.equals("")) {
            //try {
            _split.clear();

            String[] splits = splitString.split("\\|");
            for (String split : splits){
                String[] name_value = split.split(":");
                int name = Integer.valueOf(name_value[0]);
                String value = name_value[1];
                //Helper.Print(App.GetContext(), "SetSplit " + name + ", " + value);
                SetSplit(name, Double.valueOf(value));
            }
            //} catch (Exception ex){
            //Helper.Print(App.GetContext(), "Error:" + ex.getMessage());
            //}
        }
    }

    public void SetPaidBy(int personID) { _paidBy = personID; }
    public void SetPaidBack(LocalDate paidBack) { _paidBack = paidBack; }

    public void SetSource(String source) {
        if (source==null){ _source=""; } else { _source = source; }
    }
    public void SetDescription(String description) {
        if (description==null){ _description=""; } else { _description = description; }
    }
    public void SetCategory(int categoryID) { _category = categoryID; }

    public void SetTimePeriod(TimePeriod timePeriod) { _when = timePeriod; }


    // Splits
    public boolean HasSplit(int id){ return _split.containsKey(id); }
    public void ClearSplit(){
        _split.clear();
    }
    public void RemoveSplit(int id){
        _split.remove(id);
    }


    // Sorting
    public int SortCompare(Transaction t, Helper.SORT_METHODS method) {
        CategoryManager cm = CategoryManager.getInstance();
        PersonManager pm = PersonManager.getInstance();

        switch (method) {
            case DATE_DSC:
                return GetTimePeriod().GetDate().compareTo(t.GetTimePeriod().GetDate());
            case DATE_ASC:
                return t.GetTimePeriod().GetDate().compareTo(GetTimePeriod().GetDate());

            case COST_DSC:
                return (int)Math.signum( (GetValue()) - (t.GetValue()) );
            case COST_ASC:
                return (int)Math.signum( (t.GetValue()) - (GetValue()) );

            case CATEGORY_DSC:
                return cm.GetCategory(GetCategory()).GetTitle().compareTo(cm.GetCategory(t.GetCategory()).GetTitle());
            case CATEGORY_ASC:
                return cm.GetCategory(t.GetCategory()).GetTitle().compareTo(cm.GetCategory(GetCategory()).GetTitle());

            case SOURCE_DSC:
                return GetSource().compareTo(t.GetSource());
            case SOURCE_ASC:
                return t.GetSource().compareTo(GetSource());

            case PAIDBY_DSC:
                return pm.GetPerson(GetPaidBy()).GetName().compareTo(pm.GetPerson(t.GetPaidBy()).GetName());
            case PAIDBY_ASC:
                return pm.GetPerson(t.GetPaidBy()).GetName().compareTo(pm.GetPerson(GetPaidBy()).GetName());

            default:
                return 0;
        }
    }

    // Equality
    public boolean shallowEquals(Transaction other){
        if (this._type != other._type) { return false; }
        //if (this._uniqueID != other._uniqueID) { return false; } // Don't compare IDs, that would be a deep equal, this is a shallow equal
        //if (this._parentID != other._parentID) { return false; }
        if (this._budgetID != other._budgetID) { return false; }
        if (this._category != other._category) { return false; }
        if (!this._source.equals(other._source)) { return false; }
        if (!this._description.equals(other._description)) { return false; }
        if (this._value != other._value) { return false; }
        //if (!this._when.shallowEquals(other._when)) { return false; } // Instance transactions will not share timeperiod similarity with their parent
        if (this._paidBy != other._paidBy) { return false; }
        if (this._split != other._split) { return false; }
        if (this._paidBack != null && other._paidBack != null) {
            if (!this._paidBack.equals(other._paidBack)){ return false; }
        } else if (this._paidBack == null ^ other._paidBack == null) { return false; }

        return true;
    }
}
