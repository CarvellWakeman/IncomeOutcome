package carvellwakeman.incomeoutcome;

import java.util.ArrayList;

public class PersonManager
{
    static PersonManager instance = new PersonManager();

    //Categories
    private ArrayList<Person> _People;

    //Constructor and Init
    private PersonManager(){}
    static PersonManager getInstance(){ return instance; }
    public void initialize(){
        _People = new ArrayList<>();
    }


    //Management
    public Person AddPerson(Person person) {
        if (person != null) {
            Person cat = GetPerson(person.GetID());
            if (cat != null) { //Update
                cat.SetName(person.GetName());
            } else { //Add new
                _People.add(person);
            }
        }
        return person;
    }
    public Person AddPerson(String name){
        return AddPerson(new Person(name));
    }

    public void RemovePerson(Person person) { _People.remove(person); }
    public void RemovePerson(String title) {
        for (int i = 0; i < _People.size(); i++) {
            if (_People.get(i).GetName().equals(title)) {
                RemovePerson(_People.get(i));
            }
        }
    }
    public void RemoveAllPeople() { _People.clear(); }

    //Get person by index
    public Person GetPerson(int ID){
        for (Person p : _People) {
            if (p.GetID() == ID) { return p; }
        }
        return null;
    }
    public Person GetPerson(String title) {
        for (Person p : _People) {
            if (p.GetName().equals(title)) { return p; }
        }
        return null;
    }

    public ArrayList<Person> GetPeople() { return _People; }
    public ArrayList<String> GetPeopleNames(){
        ArrayList<String> arr = new ArrayList<>();
        for (Person p : _People) {
            arr.add(p.GetName());
        }
        return arr;
    }

    public int GetPeopleCount() { return _People.size(); }

}
