package carvellwakeman.incomeoutcome;



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
    public TimePeriod GetTimePeriod() { return when; }

    public int GetParentID() { return _parentID; }

    public String GetValueFormatted() { return (staticValue ? "" : "~") + ProfileManager.currencyFormat.format(GetValue()); }

    // Formatted accessors
    public Boolean IsParent(){
        return GetParentID() == 0;
    }


    //Mutators
    public void SetID(int id){ _uniqueID = id; } //TODO Should not be used outside of loading
    public void SetParentID(int id){ _parentID = id; }
    public void SetSourceName(String name) { _sourcename = name; }
    public void SetCategory(String category) { _category = category; }
    public void SetDescription(String desc) { _description = desc; }
    public void SetValue(Double val){ value = val; }
    public void SetStatic(Boolean isStatic) { staticValue = isStatic; }
    public void SetTimePeriod(TimePeriod tp) { when = tp; }


    public void ClearAllObjects(){
        when = null;
    }

}
