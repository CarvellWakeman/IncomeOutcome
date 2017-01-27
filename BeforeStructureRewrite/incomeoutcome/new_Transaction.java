package carvellwakeman.incomeoutcome;


import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.util.*;


public class new_Transaction implements java.io.Serializable
{
    public static String ME = "You";

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

    private String _category;
    private String _source;
    private String _description;

    private Double _value;

    private TimePeriod _when;

    private ArrayList<Integer> _children;

    //Expense type only
    private String _paidBy;
    private HashMap<String,Double> _split;

    private LocalDate _paidBack;


    public new_Transaction() { this(TRANSACTION_TYPE.Expense); } //Default constructor, assuming type will be changed using SetType()
    public new_Transaction(TRANSACTION_TYPE ttype)
    {
        _type = ttype;

        _uniqueID = System.identityHashCode(this);

        _parentID = 0;

        _source = "";
        _category = "";
        _description = "";

        _value = 0.0;

        _when = new TimePeriod();

        _children = new ArrayList<>();

        //Expense only
        _paidBy = "";
        _split = new HashMap<>();

        _paidBack = null;
    }
    public new_Transaction(new_Transaction copy){
        this(copy.GetType());

        _parentID = copy.GetID();

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
    //public new_Transaction(new_Transaction copy, TimePeriod tp){
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
            for (String s : input.split(ProfileManager.getString(R.string.item_delimiter))) {
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
    public ArrayList<Integer> GetChildren() { return _children; }
    public String GetChildrenFormatted(){
        String childrenString = "";
        for (Integer id : _children){
            childrenString += id;
            if (_children.indexOf(id) != _children.size()-1) { childrenString += ProfileManager.getString(R.string.item_delimiter); }
        }
        return childrenString;
    }


    //Accessors
    public new_Transaction.TRANSACTION_TYPE GetType() { return _type; }
    public int GetID() { return _uniqueID; }
    public int GetParentID() { return _parentID; }

    public Double GetValue() { return _value; }
    public Double GetSplit(String person) { if (_split.containsKey(person)) { return _split.get(person); } else { return 0.00d; } }
    public HashMap<String, Double> GetSplitArray() { return _split; }
    public Double GetDebt(String personA, String personB) {
        if (GetPaidBy().equals(personB) && GetPaidBack() == null) {
            return GetSplit(personA);
        } else {
            return 0.00d;
        }
    }

    public String GetPaidBy() { return _paidBy; }
    public LocalDate GetPaidBack() { return _paidBack; }

    public String GetSource() { return _source; }
    public String GetDescription() { return _description; }
    public String GetCategory() { return _category; }

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
    public void SetID(Integer ID) { _uniqueID = ID; }
    public void SetParentID(Integer ID) { _parentID = ID; }

    public void SetValue(Double value){ _value = value; }
    public void SetSplit(String person, Double value) { _split.put(person, value); }

    public void SetPaidBy(String person) { _paidBy = person; }
    public void SetPaidBack(LocalDate paidBack) { _paidBack = paidBack; }

    public void SetSource(String source) { _source = source; }
    public void SetDescription(String description) { _description = description; }
    public void SetCategory(String category) { _category = category; }

    public void SetTimePeriod(TimePeriod timePeriod) { _when = timePeriod; }

}