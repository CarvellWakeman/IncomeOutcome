package carvellwakeman.incomeoutcome;

import java.util.ArrayList;

public class OtherPersonManager
{
    static OtherPersonManager instance = new OtherPersonManager();

    //Categories
    private ArrayList<String> _otherPersons;

    //Constructor and Init
    private OtherPersonManager(){}
    static OtherPersonManager getInstance(){ return instance; }
    public void initialize(){
        _otherPersons = new ArrayList<>();
    }


    //Management
    public void AddOtherPerson(String name) {
        if (name != null && !name.equals("")) {
            _otherPersons.add(name);
        }
    }

    public void RemoveOtherPerson(String name) { _otherPersons.remove(name); }

    public void RemoveAllOtherPerson() { _otherPersons.clear(); }

    public boolean HasOtherPerson(String name){ return _otherPersons.contains(name); }

    public ArrayList<String> GetOtherPersons() { return _otherPersons; }

    public ArrayList<String> GetOtherPeopleAndMe(){
        ArrayList<String> a = new ArrayList<>(_otherPersons);
        a.add(Helper.getString(R.string.format_me));
        return a;
    }


    public int GetOtherPersonCount() { return _otherPersons.size(); }

}
