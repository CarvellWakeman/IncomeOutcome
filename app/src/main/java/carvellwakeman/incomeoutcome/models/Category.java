package carvellwakeman.incomeoutcome.models;

import android.graphics.Color;
import carvellwakeman.incomeoutcome.interfaces.BaseEntity;

public class Category implements BaseEntity
{
    private int _uniqueID;
    private String _title;
    private int _color;

    // Category that deleted categories default to
    public static Category Deleted;
    static {
        Deleted = new Category("[DELETED]", Color.argb(255, 0, 0, 0));
        Deleted.SetID(-1); // -1 for deleted category
    }

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
