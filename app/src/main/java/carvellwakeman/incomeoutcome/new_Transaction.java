package carvellwakeman.incomeoutcome;


import org.joda.time.LocalDate;

import java.util.*;


public class new_Transaction implements java.io.Serializable
{
    public static String ME = Helper.getString(R.string.format_me);

    //Transaction type
    public enum TRANSACTION_TYPE
    {
        Expense,
        Income
    }
    private TRANSACTION_TYPE _type;

    //Transaction IDs
    private int _uniqueID;
    private int _parentID;
    private int _budgetID;

    private int _category;
    private String _source;
    private String _description;

    private Double _value;

    private TimePeriod _when;

    private ArrayList<Integer> _children;

    //Expense type only
    private Integer _paidBy;
    private HashMap<Integer,Double> _split;

    private LocalDate _paidBack;


    public new_Transaction() { this(TRANSACTION_TYPE.Expense); } //Default constructor, assuming type will be changed using SetType()
    public new_Transaction(TRANSACTION_TYPE ttype)
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

        _children = new ArrayList<>();

        //Expense only
        _paidBy = 0;
        _split = new HashMap<>();

        _paidBack = null;
    }
    public new_Transaction(new_Transaction copy){
        this(copy.GetType());

        _parentID = copy.GetID();
        _budgetID = copy.GetBudgetID();

        _source = copy.GetSource();
        _category = copy.GetCategory();
        _description = copy.GetDescription();

        _value = copy.GetValue();

        _when = copy.GetTimePeriod();

        _children.addAll(new ArrayList<>(copy.GetChildren()));

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
    public void AddChild(new_Transaction child) {
        AddChild(child.GetID());
        child.SetParentID(GetID());
        //Blacklist child's date
        TimePeriod tp = child.GetTimePeriod();
        if (tp != null && GetTimePeriod() != null) { GetTimePeriod().AddBlacklistDate(tp.GetDate(), true); }}
    private void AddChild(Integer ID) {
        _children.add(ID);
    }
    public void AddChildrenFromFormattedString(String input){
        if (input != null && !input.equals("")) {
            for (String s : input.split(Helper.getString(R.string.item_delimiter))) {
                if (!s.equals("")) { try { AddChild(Integer.valueOf(s)); } catch (Exception e) { e.printStackTrace(); } }
            }
        }
    }
    public void RemoveChild(new_Transaction child){
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
        for (Integer id : _children){
            childrenString += id;
            if (_children.indexOf(id) != _children.size()-1) { childrenString += Helper.getString(R.string.item_delimiter); }
        }
        return childrenString;
    }


    //Accessors
    public new_Transaction.TRANSACTION_TYPE GetType() { return _type; }
    public int GetID() { return _uniqueID; }
    public int GetParentID() { return _parentID; }
    public int GetBudgetID() { return _budgetID; }

    public Double GetValue() { return _value; }
    public Double GetSplit(Integer person) { if (_split.containsKey(person)) { return _split.get(person); } else { return 0.00d; } }
    public HashMap<Integer, Double> GetSplitArray() { return _split; }
    public String GetSplitArrayString() {
        String t = "";
        for (Map.Entry<Integer, Double> split : _split.entrySet()){
            String name = String.valueOf(split.getKey());
            String value = String.valueOf(split.getValue());

            t += name + ":" + value + "|";

            //Helper.Print(App.GetContext(), "GetSplit " + name + ":" + value);
        }
        return (t.length() > 1 ? t.substring(0,t.length()-2) : t); //Delete last comma if possible
    }
    public Double GetDebt(Integer personA, Integer personB) {
        if (GetPaidBy() == personB && GetPaidBack() == null) {
            return GetSplit(personA);
        } else {
            return 0.00d;
        }
    }

    public Integer GetPaidBy() { return _paidBy; }
    public LocalDate GetPaidBack() { return _paidBack; }

    public String GetSource() { return _source; }
    public String GetDescription() { return _description; }
    public Integer GetCategory() { return _category; }

    public TimePeriod GetTimePeriod() { return _when; }

    public ArrayList<new_Transaction> GetOccurrences(LocalDate startDate, LocalDate endDate, new_Transaction.TRANSACTION_TYPE type) {
        ArrayList<new_Transaction> occurrences = new ArrayList<>();

        //Check transaction type match
        if (GetType() == type) {

            //Valid TimePeriod
            TimePeriod tp = GetTimePeriod();
            if (tp != null && tp.GetDate() != null) {

                //Get Dates
                LocalDate start = (startDate != null ? startDate : tp.GetDate());
                LocalDate end = (endDate != null ? endDate : LocalDate.now());

                ArrayList<LocalDate> tp_dates = tp.GetOccurrencesWithin(start, end);

                for (int ii = 0; ii < tp_dates.size(); ii++) {
                    //If we find THIS transaction in the list, add it (IE: The REAL slim shady)
                    if (tp.GetDate().equals(tp_dates.get(ii))) {
                        occurrences.add(this);
                    }
                    else { //Make ghost transactions
                        new_Transaction temp = new new_Transaction(this);
                        temp.SetTimePeriod(new TimePeriod(tp_dates.get(ii)));
                        temp.SetParentID(GetID());
                        occurrences.add(temp);
                    }
                }
            }
        }

        return occurrences;
    }


    //Mutators
    public void SetType(TRANSACTION_TYPE type) { _type = type; }
    public void SetID(Integer ID) { _uniqueID = ID; }
    public void SetParentID(Integer ID) { _parentID = ID; }
    public void SetBudgetID(Integer ID) { _budgetID = ID; }

    public void SetValue(Double value){ _value = value; }
    public void SetSplit(Integer person, Double value) { _split.put(person, value); }
    public void SetSplitFromArrayString(String splitString) {
        //try {
        _split.clear();
        //Helper.Print(App.GetContext(), "Input:" + splitString);

        String[] splits = splitString.split("\\|");
        for (String split : splits){
            String[] name_value = split.split(":");
            Integer name = Integer.valueOf(name_value[0]);
            String value = name_value[1];
            //Helper.Print(App.GetContext(), "SetSplit " + name + ", " + value);
            SetSplit(name, Double.valueOf(value));
        }
        //} catch (Exception ex){
            //Helper.Print(App.GetContext(), "Error:" + ex.getMessage());
        //}

    }

    public void SetPaidBy(Integer person) { _paidBy = person; }
    public void SetPaidBack(LocalDate paidBack) { _paidBack = paidBack; }

    public void SetSource(String source) { _source = source; }
    public void SetDescription(String description) { _description = description; }
    public void SetCategory(int categoryID) { _category = categoryID; }

    public void SetTimePeriod(TimePeriod timePeriod) { _when = timePeriod; }


    //Splits
    public void RemoveSplit(String person){
        _split.remove(person);
    }

}
