package carvellwakeman.incomeoutcome;

import android.graphics.Color;
import java.util.ArrayList;

public class OtherPersonManager
{
    static OtherPersonManager instance = new OtherPersonManager();

    //Categories
    private ArrayList<OtherPerson> _otherPeople;

    //Constructor and Init
    private OtherPersonManager(){}
    static OtherPersonManager getInstance(){ return instance; }
    public void initialize(){
        _otherPeople = new ArrayList<>();
    }


    //Management
    public OtherPerson AddOtherPerson(OtherPerson person) {
        if (person != null) {
            OtherPerson cat = GetOtherPerson(person.GetID());
            if (cat != null) { //Update
                cat.SetName(person.GetName());
            } else { //Add new
                _otherPeople.add(person);
            }
        }
        return person;
    }
    public OtherPerson AddOtherPerson(String name){
        return AddOtherPerson(new OtherPerson(name));
    }

    public void RemoveOtherPerson(OtherPerson person) { _otherPeople.remove(person); }
    public void RemoveOtherPerson(String title) {
        for (int i = 0; i < _otherPeople.size(); i++) {
            if (_otherPeople.get(i).GetName().equals(title)) {
                RemoveOtherPerson(_otherPeople.get(i));
            }
        }
    }
    public void RemoveAllOtherPeople() { _otherPeople.clear(); }

    //Get other person by index
    public OtherPerson GetOtherPerson(int ID){
        for (OtherPerson p : _otherPeople) {
            if (p.GetID() == ID) { return p; }
        }
        return null;
    }
    public OtherPerson GetOtherPerson(String title) {
        for (OtherPerson p : _otherPeople) {
            if (p.GetName().equals(title)) { return p; }
        }
        return null;
    }

    public ArrayList<OtherPerson> GetOtherPeople() { return _otherPeople; }
    public ArrayList<String> GetOtherPeopleNames(){
        ArrayList<String> arr = new ArrayList<>();
        for (OtherPerson p : _otherPeople) {
            arr.add(p.GetName());
        }
        return arr;
    }

    public int GetOtherPeopleCount() { return _otherPeople.size(); }

}
