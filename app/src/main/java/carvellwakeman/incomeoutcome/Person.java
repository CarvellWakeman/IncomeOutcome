package carvellwakeman.incomeoutcome;

public class Person implements BaseEntity
{
    private int _uniqueID;
    private String _name;

    static Person Me;
    static Person Deleted;
    static {
        Me = new Person(Helper.getString(R.string.format_me));
        Me.SetID(-1); // -1 for you

        Deleted = new Person(Helper.getString(R.string.placeholder_deleted));
        Deleted.SetID(-2); // -2 for deleted person
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


