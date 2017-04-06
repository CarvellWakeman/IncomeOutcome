package carvellwakeman.incomeoutcome;

public class Person
{
    private int _uniqueID;
    private String _name;

    static Person Me;
    static {
        Me = new Person(Helper.getString(R.string.format_me));
        Me._uniqueID = 0;
    }

    public Person(String name){
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


