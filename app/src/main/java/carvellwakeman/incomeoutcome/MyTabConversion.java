package carvellwakeman.incomeoutcome;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import org.json.JSONArray;
import java.util.*;


public class MyTabConversion {

    private static String appDir = Environment.getExternalStorageDirectory().toString() + "/MyTabs/";;
    private static String tabsFilename = "Tabs.xml";

    public static String load(Context ac){
        if (Helper.isStoragePermissionGranted()){
            ArrayList<Tab> _tabs = new ArrayList<>();

            //Find MyTab file
            try {
                //Create a file that represents the directory we are saving in
                File tabsFile = new File(appDir, tabsFilename);

                //Create a reader
                BufferedReader reader = new BufferedReader(new FileReader(tabsFile));
                String line;

                while ((line = reader.readLine()) != null) {
                    //Create new JSONObject
                    JSONObject obj = new JSONObject(line);
                    Tab tab = new Tab(ac);
                    tab.SetJSON(obj);
                    //Add tab to _tabs
                    _tabs.add(tab);
                }

                //Close reader
                reader.close();
            }
            catch (FileNotFoundException ex) { return "File Not Found Exception"; }
            catch (JSONException ex) { return "JSONException"; }
            catch (IOException ex) { return "IOException"; }

            if (_tabs != null && _tabs.size() > 0) {
                //Create profile to house MyTab loaded objects
                Budget br = new Budget("MyTabData");
                if (_tabs.get(0) != null) {
                    br.SetStartDate(new LocalDate(_tabs.get(0).GetDateStart()));
                    br.SetEndDate(new LocalDate(_tabs.get(0).GetDateEnd()));
                    br.SetPeriod(new Period(0,1,0,0,0,0,0,0)); //1 Month
                }
                else { return "Bad Data Received"; }


                //Clear database and objects
                //ProfileManager.getInstance().ClearAllObjects();
                //ProfileManager.getInstance().DBDelete(ac);


                //Add new budget
                BudgetManager.getInstance().AddBudget(br);
                DatabaseManager.getInstance().insertSetting(br, false);
                //ProfileManager.getInstance().SelectProfile(br);

                //Convert between Tab<->MyTabTransaction objects into Profile<->Transaction objects
                for (Tab tab : _tabs) {
                    if (tab != null && tab._transactions != null) {
                        for (MyTabTransaction tran : tab._transactions) {
                            if (tran != null) {
                                new_Transaction NewTransaction = new new_Transaction();

                                //Mutators
                                NewTransaction.SetType(new_Transaction.TRANSACTION_TYPE.Expense);
                                NewTransaction.SetSource(tran.GetCompany());
                                if (CategoryManager.getInstance().GetCategory(tran.GetCategory()) != null) {
                                    Category cat = new Category(tran.GetCategory(), CategoryColors.getColor(tran.GetCategory()));
                                    CategoryManager.getInstance().AddCategory(cat);
                                    NewTransaction.SetCategory(cat.GetID());
                                }
                                NewTransaction.SetDescription(tran.GetDescription());
                                NewTransaction.SetValue(Double.parseDouble(Float.toString(tran.GetCost())));
                                NewTransaction.SetTimePeriod(new TimePeriod(new LocalDate(tran.GetDate())));

                                //EXPENSE ONLY Mutators

                                //Other person does not exist yet
                                if (PersonManager.getInstance().GetPerson(tab.GetPersonB()) == null) {
                                    Person person = PersonManager.getInstance().AddPerson(tab.GetPersonB());
                                    DatabaseManager.getInstance().insertSetting(person, false);

                                    if (tran.GetCostB() != 0.0f && !tran.GetPersonAPaid()){
                                        NewTransaction.SetSplit(person.GetID(), Double.parseDouble(Float.toString(tran.GetCostB())));
                                    }

                                }
                                Person person = PersonManager.getInstance().GetPerson(tab.GetPersonB());

                                NewTransaction.SetPaidBy( (tran.GetPersonAPaid() ? -1 : person.GetID()) );
                                NewTransaction.SetPaidBack((tab.GetDatePaid() == null ? null : new LocalDate(tab.GetDatePaid())));

                                br.AddTransaction(NewTransaction);
                                DatabaseManager.getInstance().insert(NewTransaction, false);
                            }
                        }
                    }
                }
            }
            else {
                return "No Tab Data Found";
            }
        }
        else {
            return "Storage Permission not granted";
        }

        return "";
    }
}

class CategoryColors {
    public static HashMap<String, Integer> categoryColors = new HashMap<>();

    static {
        categoryColors.put("Groceries", Color.argb(255, 0, 0, 255));
        categoryColors.put("Fast Food", Color.argb(255, 0, 20, 200));
        categoryColors.put("Restaurant", Color.argb(255, 50, 50, 150));
        categoryColors.put("Snacks", Color.argb(255, 50, 80, 150));
        categoryColors.put("Rent", Color.argb(255, 200, 0, 0));
        categoryColors.put("Mortgage", Color.argb(255, 180, 30, 0));
        categoryColors.put("ATM Withdrawal", Color.argb(255, 150, 50, 0));
        categoryColors.put("Electricity", Color.argb(255, 250, 255, 30));
        categoryColors.put("Sewer", Color.argb(255, 165, 165, 60));
        categoryColors.put("Water", Color.argb(255, 36, 174, 212));
        categoryColors.put("Garbage", Color.argb(255, 62, 105, 54));
        categoryColors.put("Internet", Color.argb(255, 50, 50, 150));
        categoryColors.put("Entertainment", Color.argb(255, 180, 255, 120));
        categoryColors.put("Gasoline", Color.argb(255, 150, 0, 150));
        categoryColors.put("Travel", Color.argb(255, 230, 50, 255));
        categoryColors.put("Vehicle Repair", Color.argb(255, 85, 0, 80));
        categoryColors.put("Office Supplies", Color.argb(255, 255, 180, 80));
        categoryColors.put("Home Supplies", Color.argb(255, 255, 100, 40));
        categoryColors.put("Kitchen Supplies", Color.argb(255, 255, 50, 20));
        categoryColors.put("Home Improvement", Color.argb(255, 50, 0, 255));
        categoryColors.put("Home Repair", Color.argb(255, 115, 80, 255));
        categoryColors.put("Pet", Color.argb(255, 200, 255, 200));
        categoryColors.put("Hobbies", Color.argb(255, 30, 200, 80));
        categoryColors.put("Second-Hand", Color.argb(255, 15, 140, 50));
        categoryColors.put("Clothing/Jewelry", Color.argb(255, 20, 200, 5));
        categoryColors.put("Gifts", Color.argb(255, 0, 255, 255));
        categoryColors.put("Medical", Color.argb(255, 130, 0, 40));
        categoryColors.put("Prescription", Color.argb(255, 180, 45, 50));
        categoryColors.put("Health & Beauty", Color.argb(255, 200, 20, 180));
        categoryColors.put("Other", Color.argb(255, 140, 140, 140));
    }
    public static int getColor(String name){
        if (categoryColors.get(name) != null){ return categoryColors.get(name); }
        else { return Helper.ColorFromString(name); }
    }
}

class Tab {
    //private String _name;
    private String _description;

    private String _personA;
    private String _personB;

    private Calendar _start;
    private Calendar _end;
    private Calendar _paidDate;

    public ArrayList<MyTabTransaction> _transactions;

    private boolean _paid;

    static HashMap<String, Integer> categoryColors;

    public Tab() {
        //_name = "";
        _description = "";
        _personA = "PersonA";
        _personB = "PersonB";

        _start = null;
        _end = null;
        _paidDate = null;

        _transactions = new ArrayList<>();

        _paid = false;
    }

    public Tab(Context c) {
        //_name = name;
        _description = "";
        _personA = "PersonA";
        _personB = "PersonB";

        _start = null;
        _end = null;
        _paidDate = null;

        _transactions = new ArrayList<>();

        _paid = false;
    }


    //Transaction addition/subtraction/info
    public int GetTransactionsCount() { return _transactions.size(); }

    //Add transaction
    public void AddTransaction(MyTabTransaction tran) {
        if (tran != null) {
            _transactions.add(tran);
        }
    }
    public void AddTransactions(List<MyTabTransaction> trans) {
        if (trans != null && trans.size() > 0) {
            _transactions.addAll(trans);
        }
    }

    //Delete transaction
    public void DeleteTransactionByID(String id) {
        if (id != null) {
            for (int i = 0; i < _transactions.size(); i++) {
                if (_transactions.get(i).toString().equals(id)) {
                    DeleteTransaction(_transactions.get(i));
                }
            }
        }
    }

    public void DeleteTransaction(MyTabTransaction tran) {
        if (tran != null) {
            _transactions.remove(tran);
        }
    }

    //Get Transaction at index
    public MyTabTransaction GetTransactionAtIndex(int index) {
        return _transactions.get(index);
    }

    public MyTabTransaction GetTransactionByID(String id) {
        for (int i = 0; i < _transactions.size(); i++) {
            if (_transactions.get(i).toString().equals(id)) {
                return _transactions.get(i);
            }
        }

        return null;
    }

    //Get Transaction Index
    public int GetTransactionIndex(MyTabTransaction transaction) {
        return _transactions.indexOf(transaction);
    }
    public int GetTransactionIndex(String id) {
        return GetTransactionIndex(GetTransactionByID(id));
    }


    //Accessors
    String GetDescription() { return _description; }
    String GetPersonA() { return _personA; }
    String GetPersonB() { return _personB; }
    boolean GetPaid() { return _paid; }
    Calendar GetDateStart() { return _start; }
    Calendar GetDateEnd() { return _end; }
    Calendar GetDatePaid() { return _paidDate; }



    //Mutators
    void SetDescription(String description) { _description = description; }
    void SetPersonA(String name) { _personA = name; }
    void SetPersonB(String name) { _personB = name; }
    void SetPaid(boolean paid) { _paid = paid; }
    void SetDateStart(Calendar start) {
        if (_end == null || start != null && start.before(_end)) {
            _start = start;
        }
        else if (start == null) { _start = null; }
    }
    void SetDateEnd(Calendar end) {
        if (end != null && end.after(_start)) { _end = end; }
        else if (end == null) { _end = null; }
    }
    void SetPaidDate(Calendar paidDate) { _paidDate = paidDate; }

    void SetJSON(JSONObject obj) {
        try {
            //Tab Description
            if (!obj.isNull("description")) { SetDescription(obj.getString("description")); }

            //Person A and B
            SetPersonA(obj.getString("personA"));
            SetPersonB(obj.getString("personB"));

            //Paid
            if (!obj.isNull("paid")) { SetPaid(obj.getBoolean("paid")); }

            //Paid Date
            Calendar paidDateJson = new GregorianCalendar();
            if (obj.has("paidDate") && !obj.isNull("paidDate")) {
                JSONObject subObjStart = obj.getJSONObject("paidDate");
                if (subObjStart != null) {
                    paidDateJson.set((int) subObjStart.get("year"), (int) subObjStart.get("month"), (int) subObjStart.get("day"));
                    SetPaidDate(paidDateJson);
                }
            }

            //Start Date
            Calendar startDateJson = new GregorianCalendar();
            if (obj.has("start") && !obj.isNull("start")) {
                JSONObject subObjStart = obj.getJSONObject("start");
                if (subObjStart != null) {
                    startDateJson.set((int) subObjStart.get("year"), (int) subObjStart.get("month"), (int) subObjStart.get("day"));
                }
            }
            SetDateStart(startDateJson);

            //End Date
            Calendar endDateJson = new GregorianCalendar();
            if (obj.has("end") && !obj.isNull("end")) {
                JSONObject subObjEnd = obj.getJSONObject("end");
                if (subObjEnd != null) {
                    endDateJson.set((int) subObjEnd.get("year"), (int) subObjEnd.get("month"), (int) subObjEnd.get("day"));
                    SetDateEnd(endDateJson);
                }
            }


            //Transactions
            JSONArray tranArr = obj.getJSONArray("transactions");
            if (tranArr != null) {
                for (int i = 0; i < tranArr.length(); i++) {
                    JSONObject tranObj = tranArr.getJSONObject(i);
                    MyTabTransaction t = new MyTabTransaction();

                    //Get Company, category, and description
                    t.SetCompany((String) tranObj.get("placeofpurchase"));
                    t.SetCategory((String) tranObj.get("category"));

                    t.SetDescription((String) tranObj.get("description"));

                    //Get Date Object
                    JSONObject subObjDate = tranObj.getJSONObject("date");
                    t.SetDate(Integer.valueOf(subObjDate.getString("year")), Integer.valueOf(subObjDate.getString("month")), Integer.valueOf(subObjDate.getString("day")));

                    //Get Cost
                    t.SetCost(Float.valueOf(tranObj.getString("totalCost")));

                    //Get Cost personA and B
                    t.SetCostA(Float.valueOf(tranObj.getString("costA")));
                    t.SetCostB(Float.valueOf(tranObj.getString("costB")));

                    //Get PersonAPaid (bool)
                    t.SetPersonAPaid((boolean) tranObj.get("personAPaid"));

                    //Add new transaction to arraylist
                    AddTransaction(t);
                }
            }
        }
        catch (JSONException ex) {
            //Toast.makeText(MainActivityContext, "Exception JSONException (In Tab)", Toast.LENGTH_LONG).show();
            Log.e("carvellwakeman.myTab", ex.toString());
        }
    }
}

class MyTabTransaction
{
    private String _category;
    private String _company;
    private String _description;

    private Calendar _date;

    private float _cost;
    private float _costA;
    private float _costB;
    private boolean _personAPaid;


    MyTabTransaction()
    {
        _company = "";
        _category = "";
        _description = "";

        _date = new GregorianCalendar();

        _cost = 0.0f;
        _costA = 0.0f;
        _costB = 0.0f;

        _personAPaid = true;
    }


    //Accessors
    public String GetCompany() { return _company; }
    public String GetCategory() { return _category; }
    public String GetDescription() { return _description; }
    public Calendar GetDate() { return _date; }

    public float GetCost() { return _cost; }
    public float GetCostA() { return _costA; }
    public float GetCostB() { return _costB; }

    //public float GetSplitA() { return (_cost == 0 ? _cost : _costA / _cost); }
    //public float GetSplitB() { return (_cost == 0 ? _cost : _costB / _cost); }
    public boolean GetPersonAPaid() { return _personAPaid; }


    //Mutators
    public void SetCompany(String company) { _company = company; }
    public void SetCategory(String category) { _category = category; }
    public void SetDescription(String desc) { _description = desc; }
    public void SetDate(int y, int m, int d) { if (_date != null) { _date.set(y, m, d); } }
    public void SetCost(float cost) { _cost = cost; }

    public void SetCostA(float cost) { _costA = cost; }
    public void SetCostB(float cost) { _costB = cost; }
    public void SetPersonAPaid(boolean personAPaid) { _personAPaid = personAPaid; }
}
