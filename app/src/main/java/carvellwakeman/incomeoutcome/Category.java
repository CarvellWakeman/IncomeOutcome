package carvellwakeman.incomeoutcome;

public class Category implements BaseEntity
{
    private int _uniqueID;
    private String _title;
    private int _color;


    public Category(String title, int color){
        _uniqueID = java.lang.System.identityHashCode(this);

        _title = title;
        _color = color;
    }


    //Accessors
    public String GetTitle(){ return _title; }
    public int GetColor() { return _color; }
    public int GetID() { return _uniqueID; }

    //Modifiers
    public void SetTitle(String title){ _title = title; }
    public void SetColor(int color){ _color = color; }
    public void SetID(int ID){ _uniqueID = ID; }


}
