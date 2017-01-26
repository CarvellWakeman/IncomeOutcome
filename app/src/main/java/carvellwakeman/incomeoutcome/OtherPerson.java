package carvellwakeman.incomeoutcome;

public class OtherPerson
{
    private int _uniqueID;
    private String _name;


    public OtherPerson(String name){
        _uniqueID = System.identityHashCode(this);

        _name = name;
    }


    //Accessors
    public String GetName(){ return _name; }
    public int GetID() { return _uniqueID; }

    //Modifiers
    public void SetName(String name){ _name = name; }
    public void SetID(int ID){ _uniqueID = ID; }

}
