package carvellwakeman.incomeoutcome;


import java.util.ArrayList;

public class Transaction implements java.io.Serializable
{
    private int _uniqueID;
    private int _parentID;

    private String _category;
    private String _sourcename;
    private String _description;

    private Double value;
    private Boolean staticValue;

    private TimePeriod when;

    private ArrayList<Integer> children;

    public Transaction()
    {
        _uniqueID = java.lang.System.identityHashCode(this);

        _parentID = 0;

        _sourcename = "";
        _category = "";
        _description = "";

        value = 0.0;
        staticValue = true;

        when = new TimePeriod();

        children = new ArrayList<>();
    }
    public Transaction(Transaction copy){
        this();

        _parentID = copy.GetID();

        _sourcename = copy.GetSourceName();
        _category = copy.GetCategory();
        _description = copy.GetDescription();

        value = copy.GetValue();
        staticValue = copy.GetStatic();

        when = copy.GetTimePeriod();

        children.addAll(copy.GetChildrenCopy());
    }
    public Transaction(Transaction copy, TimePeriod tp){
        this(copy);

        when = tp;
    }

    //Accessors
    public int GetID() { return _uniqueID; }

    public String GetSourceName() { return _sourcename; }
    public String GetCategory() { return _category; }
    public String GetDescription() { return _description; }

    public Double GetValue() { return value; }
    public Boolean GetStatic() { return staticValue; }
    public String GetValueFormatted() { return (staticValue ? "" : "~") + ProfileManager.currencyFormat.format(GetValue()); }

    public int GetParentID() { return _parentID; }
    public Boolean IsChild(){ return GetParentID() != 0; }
    public ArrayList<Integer> GetChildren() { return children; }
    public ArrayList<Integer> GetChildrenCopy() {
        ArrayList<Integer> newList = new ArrayList<>();
        for (Integer i : children){
            newList.add(i);
        }
        return newList;
    }

    public TimePeriod GetTimePeriod() { return when; }



    //Mutators
    public void SetID(int id){ _uniqueID = id; } //TODO Should not be used outside of loading
    public void SetParentID(int id){ _parentID = id; }
    public void SetSourceName(String name) { _sourcename = name; }
    public void SetCategory(String category) { _category = category; }
    public void SetDescription(String desc) { _description = desc; }
    public void SetValue(Double val){ value = val; }
    public void SetStatic(Boolean isStatic) { staticValue = isStatic; }
    public void SetTimePeriod(TimePeriod tp) { when = tp; }


    //Children management
    public void AddChild(Transaction child, boolean edited){
        children.add(child.GetID());
        //Blacklist child's date
        TimePeriod tp = child.GetTimePeriod();
        if (tp != null) { when.AddBlacklistDate(tp.GetDate(), edited); }
    }
    public void AddChild(int id){ children.add(id); }
    public void RemoveChild(int CID){ children.remove(CID); }
    public boolean HasChild(int CID){ return children.contains(CID); }


    //Clear all
    public void ClearAllObjects(){
        when = null;

        children.clear();
        children = null;
    }

}
