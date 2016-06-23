package carvellwakeman.incomeoutcome;

public class Income extends Transaction
{


    public Income() { super(); }
    public Income(Income copy){ super(copy); }
    public Income(Income copy, TimePeriod tp){ super(copy, tp); }


    //Custom Accessors


    //Custom Formatted accessors


    //Custom Mutators
    public void ClearAllObjects(){
        //Custom clear


        super.ClearAllObjects();
    }


}
