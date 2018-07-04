package carvellwakeman.incomeoutcome.data;

import carvellwakeman.incomeoutcome.models.Person;

import java.util.ArrayList;

public class PersonManager
{
    private static PersonManager instance = new PersonManager();

    //Categories
    private ArrayList<Person> _people;

    //Constructor and Init
    private PersonManager(){}
    public static PersonManager getInstance(){ return instance; }
    public void initialize(){
        _people = new ArrayList<>();
    }


    //Management
    public Person AddPerson(Person person) {
        if (person != null) {
            Person cat = GetPerson(person.GetID());
            if (cat != null) { //Update
                cat.SetName(person.GetName());
            } else { //Add new
                _people.add(person);
            }
        }
        return person;
    }
    public Person AddPerson(String name){
        return AddPerson(new Person(name));
    }

    public void RemovePerson(Person person) { _people.remove(person); }
    public void RemovePerson(String title) {
        for (int i = 0; i < _people.size(); i++) {
            if (_people.get(i).GetName().equals(title)) {
                RemovePerson(_people.get(i));
            }
        }
    }
    public void RemoveAllPeople() { _people.clear(); }

    //Get person by index
    public Person GetPerson(int ID){
        //Override for "ME"
        if (ID == Person.Me.GetID()){ return Person.Me; }
        // Short circuit for deleted person
        if (ID == Person.Deleted.GetID()){ return Person.Deleted; }

        for (Person p : _people) {
            if (p.GetID() == ID) { return p; }
        }
        return null;
    }
    public Person GetPerson(String title) {
        for (Person p : _people) {
            if (p.GetName().equals(title)) { return p; }
        }
        return null;
    }

    public ArrayList<Person> GetPeople() { return _people; }
    public ArrayList<String> GetPeopleNames(){
        ArrayList<String> arr = new ArrayList<>();
        for (Person p : _people) {
            arr.add(p.GetName());
        }
        return arr;
    }

    public int GetPeopleCount() { return _people.size(); }

}
