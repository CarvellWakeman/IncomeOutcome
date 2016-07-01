package carvellwakeman.incomeoutcome;


import org.joda.time.LocalDate;

public class Expense extends Transaction
{
    private Boolean IPaid;
    private String splitWith;
    private Double splitValue;

    private LocalDate paidBack;

    public Expense()
    {
        super();

        IPaid = true;
        splitWith = null;
        splitValue = 0.0;

        paidBack = null;
    }
    public Expense(Expense copy){
        super(copy);

        IPaid = copy.GetIPaid();
        splitWith = copy.GetSplitWith();
        splitValue = copy.GetSplitValue();

        paidBack = copy.GetPaidBack();

    }
    public Expense(Expense copy, TimePeriod tp){
        super(copy, tp);

        IPaid = copy.GetIPaid();
        splitWith = copy.GetSplitWith();
        splitValue = copy.GetSplitValue();

        paidBack = copy.GetPaidBack();
    }


    //Accessors
    public String GetSplitWith() { return splitWith; }

    public Double GetSplitValue() { return splitValue; }
    public Double GetMySplitValue() { return GetValue() - GetSplitValue(); }

    public Double GetMyDebt() { if (!GetIPaid() && !IsPaidBack()) { return GetValue() - GetSplitValue(); } else { return 0.0d; } }
    public Double GetSplitDebt() { if (GetIPaid() && !IsPaidBack()) { return GetSplitValue(); } else { return 0.0d; } }

    public Double GetMySplitPercentage() { if (GetValue() > 0) { return 1 - (GetSplitValue() / GetValue()); } else { return 0.0; } }
    public Double GetOtherSplitPercentage() { if (GetValue() > 0) { return (GetSplitValue() / GetValue()); } else { return 0.0; }   }

    public String GetSplitValueFormatted() { return ProfileManager.currencyFormat.format(GetSplitValue()); }
    public String GetMySplitValueFormatted() { return ProfileManager.currencyFormat.format(GetMySplitValue()); }

    public LocalDate GetPaidBack() { return paidBack; }
    public boolean IsPaidBack() { return !(paidBack == null); }
    public String GetPaidBackFormatted() { return "Paid Back " + GetPaidBack().toString(ProfileManager.simpleDateFormat); }
    public Boolean GetIPaid() { return IPaid; }


    //Mutators
    public void SetSplitValue(String name, Double val) {
        if (name!=null && name.equals("")){ splitWith = null; } else { splitWith = name; }
        splitValue = val;
    }
    public void SetIPaid(Boolean paid) { IPaid = paid; }
    public void SetPaidBack(LocalDate date) { paidBack = date; }


    //Clear all
    public void ClearAllObjects(){
        //splitWith.ClearAll();

        super.ClearAllObjects();
    }

}
