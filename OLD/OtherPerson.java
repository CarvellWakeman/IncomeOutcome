package carvellwakeman.incomeoutcome;

public class OtherPerson implements java.io.Serializable
{
    //Info
    private String _name;

    public OtherPerson(String name)
    {
        _name = name;
    }


    //Public accessors
    public String GetName() { return _name; }
    public double GetMoneyOwed(Profile profile){
        return profile.GetExpenseOwedBy(this);
    }

    //Public Mutators
    public void SetName(String name){ _name = name; }

    //Clear All
    public void ClearAll(){
        //Do nothing so far
    }

}
