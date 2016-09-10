package carvellwakeman.incomeoutcome;


import org.joda.time.LocalDate;
import java.util.ArrayList;


public class Transaction implements java.io.Serializable
{
    //Transaction type
    public enum TRANSACTION_TYPE
    {
        Expense,
        Income
    }
    private TRANSACTION_TYPE type;

    //Transaction IDs
    private int _uniqueID;
    private int _parentID;

    private String _category;
    private String _sourcename;
    private String _description;

    private Double value;
    private boolean staticValue;

    private TimePeriod when;

    private ArrayList<Integer> children;

    //Expense type only
    private boolean IPaid;
    private String splitWith;
    private Double splitValue;

    private LocalDate paidBack;


    public Transaction() { this(TRANSACTION_TYPE.Expense); } //Default constructor, assuming type will be changed using SetType()
    public Transaction(TRANSACTION_TYPE ttype)
    {
        type = ttype;

        _uniqueID = java.lang.System.identityHashCode(this);

        _parentID = 0;

        _sourcename = "";
        _category = "";
        _description = "";

        value = 0.0;
        staticValue = true;

        when = new TimePeriod();

        children = new ArrayList<>();

        //Expense only
        IPaid = true;
        splitWith = null;
        splitValue = 0.0;

        paidBack = null;
    }
    public Transaction(Transaction copy){
        this(copy.GetType());

        _parentID = copy.GetID();

        _sourcename = copy.GetSourceName();
        _category = copy.GetCategory();
        _description = copy.GetDescription();

        value = copy.GetValue();
        staticValue = copy.GetStatic();

        when = copy.GetTimePeriod();

        children.addAll(copy.GetChildrenCopy());

        //Expense only
        IPaid = copy.GetIPaid();
        splitWith = copy.GetSplitWith();
        splitValue = copy.GetSplitValue();

        paidBack = copy.GetPaidBack();
    }
    public Transaction(Transaction copy, TimePeriod tp){
        this(copy);

        when = tp;
    }

    //Accessors
    public TRANSACTION_TYPE GetType() { return type; }
    public int GetID() { return _uniqueID; }

    public String GetSourceName() { return _sourcename; }
    public String GetCategory() { return _category; }
    public String GetDescription() { return _description; }

    //Total Transaction value
    public Double GetValue() { return value; }
    public boolean GetStatic() { return staticValue; }
    public String GetValueFormatted() { return (staticValue ? "" : "~") + ProfileManager.currencyFormat.format(GetValue()); }

    public int GetParentID() { return _parentID; }
    public boolean IsChild(){ return GetParentID() != 0;
    }
    public ArrayList<Integer> GetChildren() { return children; }
    public ArrayList<Integer> GetChildrenCopy() {
        ArrayList<Integer> newList = new ArrayList<>();
        for (Integer i : children){
            newList.add(i);
        }
        return newList;
    }
    public String GetChildrenFormatted(){
        String childrenString = "";
        for (Integer id : children){
            childrenString += id;
            if (children.indexOf(id) != children.size()-1) { childrenString += ProfileManager.getString(R.string.item_delimiter); }
        }
        return childrenString;
    }

    public TimePeriod GetTimePeriod() { return when; }


    //EXPENSE ONLY Accessors
    public String GetSplitWith() { return splitWith; }

    //Partition of transaction that was split with another person
    public Double GetSplitValue() { return splitValue; }
    //My partition of the split transaction
    public Double GetMySplitValue() { return GetValue() - GetSplitValue(); }

    //The money I owe OtherPerson
    public Double GetMyDebt() { if (!GetIPaid() && !IsPaidBack()) { return GetMySplitValue(); } else { return 0.0d; } }
    //The money OtherPerson owes me
    public Double GetSplitDebt() { if (GetIPaid() && !IsPaidBack()) { return GetSplitValue(); } else { return 0.0d; } }

    //The money I spend on this transaction, depends on whether or not OtherPerson has paid me for their partition
    public Double GetMyCost() { if ( GetIPaid() && GetSplitWith() != null || GetSplitWith() == null) {
        if (IsPaidBack()){ return GetMySplitValue(); } else { return GetValue(); }
    } else { return GetMyDebt(); } }
    //The money OtherPerson has spent on this transaction, depends on whether or not I have paid them back
    public Double GetSplitCost() { if ( !GetIPaid() && GetSplitWith() != null) {
        if (IsPaidBack()){ return GetSplitValue(); } else { return GetValue(); }
    } else { return GetSplitDebt(); } }

    public Double GetTotalZeroWeighted() {
        //ProfileManager.Print("SplitWith:" + GetSplitWith());
        //ProfileManager.Print("MyCost:" + GetMyCost());
        //ProfileManager.Print("SplitCost:" + GetSplitCost());
        //ProfileManager.Print("ZWCost:" + (GetMyCost() - GetSplitCost()));
        return GetMyDebt() - GetSplitDebt();
    }

    public Double GetMySplitPercentage() { if (GetValue() > 0) { return 1 - (GetSplitValue() / GetValue()); } else { return 0.0; } }
    public Double GetOtherSplitPercentage() { if (GetValue() > 0) { return (GetSplitValue() / GetValue()); } else { return 0.0; }   }

    public String GetSplitValueFormatted() { return ProfileManager.currencyFormat.format(GetSplitValue()); }
    public String GetMySplitValueFormatted() { return ProfileManager.currencyFormat.format(GetMySplitValue()); }

    public LocalDate GetPaidBack() { return paidBack; }
    public boolean IsPaidBack() { return !(paidBack == null); }
    public String GetPaidBackFormatted() { return "Paid Back " + GetPaidBack().toString(ProfileManager.simpleDateFormat); }
    public boolean GetIPaid() { return IPaid; }



    //Mutators
    public void SetType(TRANSACTION_TYPE ttype){ type = ttype; }
    public void SetID(int id){ _uniqueID = id; } //TODO Should not be used outside of loading
    public void SetParentID(int id){ _parentID = id; }
    public void SetSourceName(String name) { _sourcename = name; }
    public void SetCategory(String category) { _category = category; }
    public void SetDescription(String desc) { _description = desc; }
    public void SetValue(Double val){ value = val; }
    public void SetStatic(boolean isStatic) { staticValue = isStatic; }
    public void SetTimePeriod(TimePeriod tp) { when = tp; }


    //EXPENSE ONLY Mutators
    public void SetSplitValue(String name, Double val) {
        if (name.equals("")){ splitWith = null; } else { splitWith = name; }
        splitValue = val;
    }
    public void SetIPaid(boolean paid) { IPaid = paid; }
    public void SetPaidBack(LocalDate date) { paidBack = date; }


    //Children management
    public void AddChild(Transaction child, boolean edited){
        children.add(child.GetID());
        //Blacklist child's date
        TimePeriod tp = child.GetTimePeriod();
        if (tp != null) { when.AddBlacklistDate(tp.GetDate(), edited); }
    }
    public void AddChild(Integer id){ children.add(id); }
    public void AddChildrenFromFormattedString(String input){
        if (input != null && !input.equals("")) {
            for (String s : input.split(ProfileManager.getString(R.string.item_delimiter))) {
                if (!s.equals("")) { try { AddChild(Integer.valueOf(s)); } catch (Exception e) { e.printStackTrace(); } }
            }
        }
    }
    public void RemoveChildren() { children.clear(); }
    public void RemoveChild(Integer CID){ if (children.size() > 0) { children.remove(CID); } }
    public boolean HasChild(Integer CID){ return children.contains(CID); }


    //Clear all
    public void ClearAllObjects(){
        when = null;

        children.clear();
        children = null;
    }


    //Equals
    public boolean isSimilarChildOf(Transaction tr){
        if (tr != null) {

            if (!tr.GetType().equals(this.GetType())) { return false; }
            //if (tr.GetID() != this.GetID()) { return false; } //No ID check, if the ID is the same then we can just check the ID instead of doing deepEquals

            if (!tr.GetSourceName().equals(this.GetSourceName())) { return false; }
            if (!tr.GetCategory().equals(this.GetCategory())) { return false; }
            if (!tr.GetDescription().equals(this.GetDescription())) { return false; }

            if (!tr.GetValue().equals(this.GetValue())) { return false; }
            if (tr.GetStatic() != this.GetStatic()) { return false; }
            //if (tr.GetParentID() != this.GetParentID()) { return false; }

            //if (!tr.GetChildrenFormatted().equals(this.GetChildrenFormatted())) { return false; }
            //if (!tr.GetTimePeriod().equals(this.GetTimePeriod())) { return false; }

            //EXPENSE ONLY
            if (tr.GetSplitWith() != null && this.GetSplitWith() != null && !tr.GetSplitWith().equals(this.GetSplitWith())) { return false; }
            if (!tr.GetSplitValue().equals(this.GetSplitValue())) { return false; }
            if (tr.GetPaidBack() != this.GetPaidBack() || tr.GetPaidBack() != null && this.GetPaidBack() != null && tr.GetPaidBack().compareTo(this.GetPaidBack()) != 0) { return false; }
            if (tr.GetIPaid() != this.GetIPaid()) { return false; }

            return true;
        }

        return false;
    }

}
