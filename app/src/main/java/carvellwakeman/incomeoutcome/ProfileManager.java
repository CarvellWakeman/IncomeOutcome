package carvellwakeman.incomeoutcome;


import android.app.*;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;

import android.support.design.widget.CollapsingToolbarLayout;

import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class ProfileManager
{
    static ProfileManager instance = new ProfileManager();

    //Categories
    private ArrayList<Category> _categories;
    //private static ArrayList<String> _categories;
    //private static HashMap<String, Integer> _categoryColors;

    //Profiles
    private int _currentProfileID;
    private ArrayList<Profile> _profiles;

    //Other people
    private ArrayList<String> _otherPeople;

    //Prefs
    //private static String tabsFilename;
    //private static String tabsErrorFilename;

    //Formats
    static DecimalFormat decimalFormat = new DecimalFormat("#.###");
    static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(); //new DecimalFormat("¤#.###");
    //public static DateFormat simpleDateFormat;
    //simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
    static String simpleDateFormat = "MMMM dd, yyyy"; //May 12, 2016
    //simpleDateFormatNoYear = new SimpleDateFormat("MMMM dd", Locale.US);
    static String simpleDateFormatJustYear = "yyyy"; //2016
    static String simpleDateFormatNoYear = "MMMM dd"; //May 12
    static String simpleDateFormatNoDay = "MMMM, yyyy"; //May, 2016
    static String simpleDateFormatShortNoDay = "MMM, yyyy"; //Apr, 2016
    static String simpleDateFormatShort = "MMM dd, yy"; //Apr, 2016
    static String simpleDateFormatSaving = "MM-dd-yyyy"; //05-12-2016

    static String simplePeriodFormat = "MM-dd-yyyy"; //05-12-2016


    //Context
    //private ActivityMain MainActivityInstance;
    //private Context MainActivityContext;

    //File I/O
    private DatabaseHelper databaseHelper;

    //private static boolean LoadError = false;


    //Sort and filter methods
    enum SORT_METHODS
    {
        DATE_UP,
        COST_UP,
        PAIDBY_UP,
        CATEGORY_UP,
        SOURCE_UP,

        DATE_DOWN,
        COST_DOWN,
        PAIDBY_DOWN,
        CATEGORY_DOWN,
        SOURCE_DOWN
    }

    enum FILTER_METHODS
    {
        NONE,
        DATE,
        COST,
        PAIDBY,
        SPLITWITH,
        CATEGORY,
        SOURCE
    }

    //Constructor and Init
    private ProfileManager(){}
    static ProfileManager getInstance(){ return instance; }

    public void initialize(final Context ac, final CallBack databaseLoadingCallback)
    {
        //MainActivityInstance = ac;
        //MainActivityContext = ac.getBaseContext();

        _currentProfileID = -1;

        _profiles = new ArrayList<>();
        //_categories = new LinkedHashMap<String, List<String>>();
        _categories = new ArrayList<>();
        //_categoryColors = new HashMap<>();
        _otherPeople = new ArrayList<>();

        //Initialize database helper
        databaseHelper = new DatabaseHelper(ac);


        //Load from database
        //databaseHelper.loadSettings();
        //databaseHelper.loadTransactions();
        GenericAsyncTask.RunDBTask(
            new CallBack() {
            @Override
            public void call() { databaseHelper.loadSettings(); }},

            new CallBack() {
            @Override public void call() {
                GenericAsyncTask.RunDBTask(new CallBack() {
                    @Override public void call() { databaseHelper.loadTransactions(); }
                }, databaseLoadingCallback);
            }
        });

        //(new DatabaseBackgroundHelper()).execute(8); //databaseHelper.loadTransactions();
        //(new DatabaseBackgroundHelper()).execute(9); //databaseHelper.loadTransactions();
        //databaseHelper.loadExpenses();
        //databaseHelper.loadIncome();

        //Load default categories if database is empty
        if (databaseHelper.isTableEmpty(DatabaseHelper.TABLE_SETTINGS_CATEGORIES) && databaseHelper.isTableEmpty(DatabaseHelper.TABLE_TRANSACTIONS)){
            LoadDefaultCategories(ac);
        }

        //Try to create the database (if it doesn't exist)
        databaseHelper.TryCreateDatabase();
    }


    public void LoadDefaultCategories(Context ac){
        //Try create database
        //ClearCategories();
        //if (databaseHelper != null){
            //databaseHelper.DeleteDB();
            //(new DatabaseBackgroundHelper()).execute(15); //databaseHelper.DeleteDB()
            //databaseHelper.TryCreateDatabase();
        //}


        //[DEBUG] Testing structures
        //LocalDate c1 = LocalDate.now().withDayOfMonth(1);
        //LocalDate c2 = LocalDate.now().withDayOfMonth(LocalDate.now().dayOfMonth().getMaximumValue());

        //Profile p1 = new Profile("Monthly Budget");
        //p1.SetStartTimeDontSave(c1);
        //p1.SetEndTimeDontSave(c2);
        //p1.SetPeriodDontSave(new Period(0,1,0,0,0,0,0,0)); //Monthly default period

        //Profile p2 = new Profile("Empty Profile");

        //AddProfile(p1);
        //AddProfile(p2);
        //SelectProfile(p1);

        //DEBUG other people
        //AddOtherPerson("Sabrina");
        //AddOtherPerson("Zach Homen");
        //AddOtherPerson("Mom");



        //Default initial category
        //AddCategory(MainActivityContext.getString(R.string.select_category), 0, true);

        //Default _categories
        AddCategory(ac, "Groceries", Color.argb(255, 0, 0, 255));
        AddCategory(ac, "Fast Food", Color.argb(255, 0, 20, 200));
        AddCategory(ac, "Restaurant", Color.argb(255, 50, 50, 150));
        AddCategory(ac, "Snacks", Color.argb(255, 50, 80, 150));

        AddCategory(ac, "Rent", Color.argb(255, 200, 0, 0));
        AddCategory(ac, "Mortgage", Color.argb(255, 180, 30, 0));
        AddCategory(ac, "ATM Withdrawal", Color.argb(255, 150, 50, 0));

        AddCategory(ac, "Electricity", Color.argb(255, 250, 255, 30));
        AddCategory(ac, "Sewer", Color.argb(255, 165, 165, 60));
        AddCategory(ac, "Water", Color.argb(255, 36, 174, 212));
        AddCategory(ac, "Garbage", Color.argb(255, 62, 105, 54));
        AddCategory(ac, "Internet", Color.argb(255, 50, 50, 150));
        AddCategory(ac, "Entertainment", Color.argb(255, 180, 255, 120));

        AddCategory(ac, "Gasoline", Color.argb(255, 150, 0, 150));
        AddCategory(ac, "Travel", Color.argb(255, 230, 50, 255));
        AddCategory(ac, "Vehicle", Color.argb(255, 85, 0, 80));

        AddCategory(ac, "Office Supplies", Color.argb(255, 255, 180, 80));
        AddCategory(ac, "Home Supplies", Color.argb(255, 255, 100, 40));
        AddCategory(ac, "Kitchen Supplies", Color.argb(255, 255, 50, 20));
        AddCategory(ac, "Home Improvement", Color.argb(255, 50, 0, 255));
        AddCategory(ac, "Home Repair", Color.argb(255, 115, 80, 255));
        AddCategory(ac, "Pet", Color.argb(255, 200, 255, 200));

        AddCategory(ac, "Hobbies", Color.argb(255, 30, 200, 80));
        AddCategory(ac, "Second-Hand", Color.argb(255, 15, 140, 50));
        AddCategory(ac, "Clothing/Jewelry", Color.argb(255, 20, 200, 5));

        AddCategory(ac, "Gifts", Color.argb(255, 0, 255, 255));

        AddCategory(ac, "Medical", Color.argb(255, 130, 0, 40));
        AddCategory(ac, "Prescription", Color.argb(255, 180, 45, 50));
        AddCategory(ac, "Health & Beauty", Color.argb(255, 100, 0, 200));
        AddCategory(ac, "Personal", Color.argb(255, 216, 66, 216));

        AddCategory(ac, "Other", Color.argb(255, 140, 140, 140));

        //MainActivityInstance.UpdateProfileList(false);
        //MainActivityInstance.SetSelection(GetProfileIndex(GetCurrentProfile()));

        //Success
        Print(ac, "Default categories loaded");
    }


    //Universal print
    public static void Print(Context c, String msg){ if (c!=null && isDebugMode(c)) { Toast.makeText(c, msg, Toast.LENGTH_SHORT).show(); } }
    public static void PrintLong(Context c, String msg){ if (c!=null && isDebugMode(c)) { Toast.makeText(c, msg, Toast.LENGTH_LONG).show(); } }
    public static void PrintUser(Context c, String msg){ if (c!=null){ Toast.makeText(c, msg, Toast.LENGTH_SHORT).show(); } }
    public static void Log(Context c, String cat, String msg){ if (c!=null && isDebugMode(c)) { Log.e(cat, msg); } }
    /*
    static int ret = 0;
    public static int AlertDialog(String title, String positive, String negative){
        AlertDialog.Builder b = new AlertDialog.Builder(MainActivityInstance);
        b.setTitle(title);
        b.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) { ret = 1; } });
        b.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) { ret = 0; } });
        b.create().show();

        return ret;
    }
    */

    //Dialog fragment manager
    public static void OpenDialogFragment(Activity caller, DialogFragment fragment, boolean openAsDialogFragment){
        if (openAsDialogFragment) { // The device is using a large layout, so show the fragment as a dialog
            FragmentTransaction ft = caller.getFragmentManager().beginTransaction();
            Fragment prev = caller.getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            fragment.show(caller.getFragmentManager(), "dialog");
            //FragmentTransaction ft = fragmentManager.beginTransaction();
            //ft.add(fragment, null);
            //ft.commitAllowingStateLoss();
        } else { // The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = caller.getFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(android.R.id.content, fragment).addToBackStack(null).commit();
        }
    }

    //Profile Management

    //Add profile
    public void AddProfileDontSave(Profile profile) {
        if (profile != null && _profiles != null) {
            _profiles.add(profile);
            //MainActivityInstance.UpdateProfileList(false);
        }
    }
    public void AddProfile(Context ac, Profile profile) {
        if (profile != null) { AddProfileDontSave(profile); DBInsertSetting(ac, profile, true); }
    }

    public void UpdateProfile(Context ac, Profile profile) {
        DBInsertSetting(ac, profile, true);
        //MainActivityInstance.UpdateProfileList(false); //INFINITE LOOP
    }

    //Delete profile
    public void RemoveProfile(Context ac, Profile profile)
    {
        if (_profiles != null) {
            boolean reselect = false;
            reselect = (profile == GetCurrentProfile());

            if (profile != null && _profiles != null) {
                profile.RemoveAll(ac);
                _profiles.remove(profile);
                DBRemoveSettingProfile(ac, profile);
                //Print("DeleteProfile");
            }
            if (reselect) { SelectProfile(ac, GetProfileByIndex(0)); }

            //MainActivityInstance.UpdateProfileList(false);
            //MainActivityInstance.SetSelection(GetProfileIndex(GetCurrentProfile()));
        }
    }
    public void RemoveProfileByID(Context ac, String id) {
        if (id != null && !id.equals("") && _profiles != null) {
            for (int i = 0; i < _profiles.size(); i++) {
                if (_profiles.get(i).toString().equals(id)) {
                    RemoveProfile(ac, _profiles.get(i));
                    break;
                }
            }
        }
    }
    public void RemoveAllProfilesAndTransactions(Context ac){
        if (_profiles != null) {
            for (int i = 0; i < _profiles.size(); i++) {
                _profiles.get(i).RemoveAll(ac);
                //_profiles.get(i).ClearAllObjects();
            }

            ArrayList<Profile> temp = new ArrayList<>();
            temp.addAll(_profiles);
            for (int i = 0; i < temp.size(); i++) {
                RemoveProfile(ac, temp.get(i));
            }

            ClearProfiles();
        }
    }


    //Get Profile at index
    public Profile GetProfileByIndex(int index) {
        if (GetProfileCount() >= index+1 && index >= 0 && _profiles != null) {
            return _profiles.get(index);
        }
        return null;
    }

    //Get Profile with ID
    public Profile GetProfileByID(int id) {
        if (_profiles != null) {
            for (int i = 0; i < _profiles.size(); i++) {
                if (_profiles.get(i).GetID() == id) {
                    return GetProfileByIndex(i);
                }
            }
        }
        return null;
    }

    //Get Profile with name
    public Profile GetProfileByName(String name){
        if (_profiles != null) {
            for (int i = 0; i < _profiles.size(); i++) {
                if (_profiles.get(i).GetName().equals(name)) {
                    return GetProfileByIndex(i);
                }
            }
        }
        return null;
    }

    //Get Profile Index
    public int GetProfileIndex(Profile profile) {
        if (profile != null && _profiles != null) {
            return _profiles.indexOf(profile);
        }
        return -1;
    }

    //Select Profile
    public void SelectProfileDontSave(Profile profile){
        _currentProfileID = profile.GetID();
    }
    public boolean SelectProfile(Context ac, Profile profile){
        if (profile != null && _profiles != null) {
            Profile old = GetCurrentProfile();

            //Update new profile to be active
            SelectProfileDontSave(profile);

            //Insert setting into database
            DBInsertSetting(ac, profile, true);

            //Update old profile to be unselected
            if (_profiles.size() > 0 && old != null) {
                DBInsertSetting(ac, old, true);
            }
            return _currentProfileID >= 0;
        }
        return false;
    }
    public boolean SelectProfile(Context ac, String name){
        if (_profiles != null) {
            for (int i = 0; i < _profiles.size(); i++) {
                if (_profiles.get(i).GetName().equals(name)) {
                    SelectProfile(ac, _profiles.get(i));
                    return true;
                }
            }
        }
        return false;
    }

    //Get Current Profile
    public Profile GetCurrentProfile(){
        if (GetProfileCount() > 0 && _currentProfileID != -1){
            return GetProfileByID(_currentProfileID);
        }

        return null;
    }

    //Get count
    public int GetProfileCount() {
        if (_profiles != null) {
            return _profiles.size();
        }
        return -1;
    }

    //Get profiles array
    public ArrayList<Profile> GetProfiles(){
        if (_profiles != null) {
            return _profiles;
        }
        return null;
    }

    //Get String array of profile names
    public ArrayList<String> GetProfileNames(){
        if (_profiles != null) {
            ArrayList<String> ar = new ArrayList<>();

            for (int i = 0; i < _profiles.size(); i++) {
                ar.add(_profiles.get(i).GetName());
            }

            return ar;
        }
        return null;
    }

    public boolean HasProfile(String profile){
        if (_profiles != null) {
            for (int i = 0; i < _profiles.size(); i++) {
                if (_profiles.get(i).GetName().equals(profile)) {
                    return true;
                }
            }
        }
        return false;
    }

    //Clear profiles
    public void ClearProfiles(){
        if (_profiles != null && _profiles.size() > 0) {
            for (Profile pr : _profiles){
                pr.ClearAllObjects();
            }
            _profiles.clear();
            _currentProfileID = -1;
        }
    }




    //Other People
    //Add other person
    public void AddOtherPersonDontSave(String name) {
        if (!name.equals("") && _otherPeople != null) {
            _otherPeople.add(name);
        }
    }
    public void AddOtherPerson(Context ac, String name){
        AddOtherPersonDontSave(name);
        DBInsertSetting(ac, name, true);
    }

    //Remove other person
    public void RemoveOtherPerson(Context ac, String name){
        if (_otherPeople != null) {
            for (int i = 0; i < _otherPeople.size(); i++) {
                if (_otherPeople.get(i).equals(name)) {
                    _otherPeople.remove(i);
                    DBRemoveSettingPerson(ac, name);
                }
            }
        }
    }

    public void RemoveAllPeople(Context ac){
        if (_otherPeople != null) {
            ArrayList<String> temp = new ArrayList<>();
            temp.addAll(_otherPeople);

            for (int i = 0; i < temp.size(); i++) {
                RemoveOtherPerson(ac, temp.get(i));
            }

            ClearOtherPeople();
        }
    }

    //Get other person by index
    public String GetOtherPersonByIndex(int idx){
        if (_otherPeople != null) {
            return _otherPeople.get(idx);
        }
        return null;
    }

    //Get other person by name
    public boolean HasOtherPerson(String name){
        if (_otherPeople != null) {
            for (int i = 0; i < _otherPeople.size(); i++) {
                if (_otherPeople.get(i).equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    //Get array of OtherPeople objects
    public ArrayList<String> GetOtherPeople(){
        return _otherPeople;
    }
    public ArrayList<String> GetOtherPeopleIncludingMe(){
        if (_otherPeople != null) {
            ArrayList<String> temp = new ArrayList<>();
            temp.addAll(_otherPeople);
            temp.add(getString(R.string.format_me));
            return temp;
        }
        return null;
    }

    //Get count of other people
    public int GetOtherPeopleCount(){
        if (_otherPeople != null) {
            return _otherPeople.size();
        }
        return -1;
    }

    //Clear other people
    public void ClearOtherPeople(){
        if (_otherPeople != null) {_otherPeople.clear();}
    }

    //Update other person
    public void UpdateOtherPerson(Context ac, String old, String name){
        if (_profiles != null) {
            for (Profile pr : _profiles) {
                pr.UpdateOtherPerson(ac, old, name);
            }
        }
    }



    //Categories
    //Add category
    public void AddCategoryDontSave(Category category) {
        if (category != null && _categories != null) {
            _categories.add(category);
        }
    }
    public void AddCategory(Context ac, Category category){
        AddCategoryDontSave(category);
        DBInsertSetting(ac, category, true);
    }
    public void AddCategory(Context ac, String name, int color){
        AddCategory(ac, new Category(name, color));
    }

    //Remove category
    public void RemoveCategory(Context ac, Category category) {
        if (_categories != null) {
            _categories.remove(category);
            DBRemoveSettingCategory(ac, category.GetTitle());
        }
    }
    public void RemoveCategory(Context ac, String title){
        if (_categories != null) {
            for (int i = 0; i < _categories.size(); i++) {
                if (_categories.get(i).GetTitle().equals(title)) {
                    RemoveCategory(ac, _categories.get(i));
                }
            }
        }
    }
    public void RemoveAllCategories(Context ac){
        if (_categories != null) {
            ArrayList<Category> temp = new ArrayList<>();
            temp.addAll(_categories);

            for (int i = 0; i < temp.size(); i++) {
                RemoveCategory(ac, temp.get(i));
            }

            ClearCategories();
        }
    }

    //Get category by index
    public Boolean HasCategory(String category){
        if (_categories != null) {
            for (int i = 0; i < _categories.size(); i++) {
                if (_categories.get(i).GetTitle().equals(category)) {
                    return true;
                }
            }
        }
        return false;
    }
    public Category GetCategory(String category)
    {
        if (_categories != null) {
            for (int i = 0; i < _categories.size(); i++) {
                if (_categories.get(i).GetTitle().equals(category)) {
                    return _categories.get(i);
                }
            }
        }
        return null;
    }
    public Category GetCategoryByIndex(int idx){
        if (_categories != null) {
            return _categories.get(idx);
        }
        return null;
    }

    public int GetCategoryIndex(String title) {
        if (_categories != null) {
            return _categories.indexOf(GetCategory(title));
        }
        return -1;
    }

    //Get array of OtherPeople objects
    public ArrayList<Category> GetCategories(){
        return _categories;
    }
    public ArrayList<String> GetCategoriesString(){
        if (_categories != null) {
            ArrayList<String> arr = new ArrayList<>();
            for (Category c : _categories) {
                arr.add(c.GetTitle());
            }
            return arr;
        }
        return null;
    }

    public int GetCategoriesCount() {
        if (_categories != null) {
            return _categories.size();
        }
        return -1;
    }


    //Get ArrayList<String> of _categories
    //public static ArrayList<Category> GetCategories(){
    //    return _categories;
    //}
    public ArrayList<String> GetCategoryTitles(){
        if (_categories != null) {
            ArrayList<String> arr = new ArrayList<>();
            arr.add(0, getString(R.string.select_category));

            for (Category cat : _categories) {
                arr.add(cat.GetTitle());
            }


            return arr;
        }
        return null;
    }

    //Clear _categories
    public void ClearCategories(){
        if (_categories != null) {_categories.clear();}
    }

    //Update category
    public void UpdateCategory(Context ac, String old, Category category){
        if (_profiles != null) {
            for (Profile pr : _profiles) {
                pr.UpdateCategory(ac, old, category.GetTitle());
            }
            DBInsertSetting(ac, category, true);
        }
    }

    //Clear All
    public void ClearAllObjects(){
        ClearProfiles();
        ClearOtherPeople();
        ClearCategories();
    }



    //Getting string resources from static contexts
    public static String getString(int resourceID){ return App.GetResources().getString(resourceID); }
    public static int getColor(int resourceID){ return App.GetResources().getColor(resourceID); }
    //public int getDrawbleResourceID(Context c, String title) { return App.GetResources().getIdentifier(title, "drawable", c.getPackageName()); }
    public static Drawable getDrawable(int resourceID) { return App.GetResources().getDrawable(resourceID); }
    //public static Drawable getDrawable(int resourceID){ return MainActivityInstance.getResources().getDrawable(resourceID); }

    public static void setRefreshToolbarEnable(CollapsingToolbarLayout collapsingToolbarLayout, boolean refreshToolbarEnable) {
        try {
            Field field = CollapsingToolbarLayout.class.getDeclaredField("mRefreshToolbar");
            field.setAccessible(true);
            field.setBoolean(collapsingToolbarLayout, refreshToolbarEnable);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //Date conversion for loading
    public static LocalDate ConvertDateFromString(String str){
        if (str != null && str.length() != 0) {
            try {
                DateTimeFormatter dtf = DateTimeFormat.forPattern(simpleDateFormatSaving);
                return dtf.parseLocalDate(str);
            }
            catch (IllegalArgumentException ex) {
                //Print("Could not parse localdate (" + str + ")");
                ex.printStackTrace();
            }
        }
        return null;
    }

    //Hide Soft Keyboard
    public static void hideSoftKeyboard(Activity act, View v)
    {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    //Color parsing
    public static int ColorFromString(String str){
        //Hash the string
        str = String.format("%X", str.hashCode());
        //Minimum string length = 6
        while (str.length() < 6) { str = '0' + str; }

        //Convert Hex string into int
        int coli = (int)Long.parseLong(str, 16);
        int r = (coli >> 16) & 0xFF;
        int g = (coli >> 8) & 0xFF;
        int b = (coli) & 0xFF;

        return Color.argb(255,r,g,b);
    }


    //Permissions
    public static boolean isStoragePermissionGranted(Context ac) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (PermissionChecker.checkSelfPermission(ac, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("PERMISSIONS","External Write Storage permission is granted");
                return true;
            } else {
                Log.v("PERMISSIONS", "External Write Storage permission is not granted");
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("PERMISSIONS","Permission is granted by default");
            return true;
        }
    }

    //DEBUG
    public static boolean isDebugMode(Context ac){
        return (0 != (ac.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }


    //External Database Management
    public DatabaseHelper GetDatabaseHelper() { return databaseHelper; }
    public void DBInsertSetting(Context ac, final Category category, final boolean tryupdate){
        if (isStoragePermissionGranted(ac) && databaseHelper != null) {
            if ( category != null ){ //|| databaseHelper.insertSetting(category, tryupdate) == -1
                //Print("Error inserting category into database");
                GenericAsyncTask.RunDBTask(new CallBack() {
                    @Override public void call() { databaseHelper.insertSetting(category, tryupdate); }
                });
                //(new DatabaseBackgroundHelper()).execute(2, category, tryupdate);
            }
        }
    }
    public void DBInsertSetting(Context ac, final String name, final boolean tryupdate){
        if (isStoragePermissionGranted(ac) && databaseHelper != null) {
            if (!name.equals("")) { //|| databaseHelper.insertSetting(name, tryupdate) == -1
                //Print("Error inserting person into database");
                //(new DatabaseBackgroundHelper()).execute(3, name, tryupdate);
                GenericAsyncTask.RunDBTask(new CallBack() {
                    @Override
                    public void call() { databaseHelper.insertSetting(name, tryupdate); }
                });
            }
        }
    }
    public void DBInsertSetting(Context ac, final Profile profile, final boolean tryupdate){
        if (isStoragePermissionGranted(ac) && databaseHelper != null) {
            if (profile != null) { //|| databaseHelper.insertSetting(profile, tryupdate) == -1
                //Print("Error inserting profile into database");
                //(new DatabaseBackgroundHelper()).execute(4, profile, tryupdate);
                GenericAsyncTask.RunDBTask(new CallBack() {
                    @Override
                    public void call() { databaseHelper.insertSetting(profile, tryupdate); }
                });
            }
        }
    }

    public void DBInsertTransaction(Context ac, final Profile pr, final Transaction tr, final boolean tryupdate){
        if (isStoragePermissionGranted(ac) && databaseHelper != null) {
            if (pr != null && tr != null) {
                //Print("Transaction " + (tryupdate ? "updated" : "inserted into database") );
                //(new DatabaseBackgroundHelper()).execute(5, pr, tr, tryupdate);
                GenericAsyncTask.RunDBTask(new CallBack() {
                    @Override
                    public void call() { databaseHelper.insert(pr, tr, tryupdate); }
                });
            }
            else { }//Print("Error inserting transaction into database"); }
        }
    }



    public void DBRemoveSettingCategory(Context ac, final String category){
        if (isStoragePermissionGranted(ac) && databaseHelper != null) {
            if (!category.equals("")) { //databaseHelper.removeCategorySetting(category)
                //Print("Category removed from database");
                GenericAsyncTask.RunDBTask(new CallBack() {
                    @Override
                    public void call() { databaseHelper.removeCategorySetting(category); }
                });
                //(new DatabaseBackgroundHelper()).execute(10, category);
            }
            else { }//Print("Error removing category from database"); }
        }
    }
    public void DBRemoveSettingPerson(Context ac, final String person){
        if (isStoragePermissionGranted(ac) && databaseHelper != null) {
            if (!person.equals("")) { //databaseHelper.removeSettingPerson(person)
                //Print("Person removed from database");
                //(new DatabaseBackgroundHelper()).execute(11, person);
                GenericAsyncTask.RunDBTask(new CallBack() {
                    @Override
                    public void call() { databaseHelper.removePersonSetting(person); }
                });
            }
            else { }//Print("Error removing person from database"); }
        }
    }
    public void DBRemoveSettingProfile(Context ac, final Profile pr){
        if (isStoragePermissionGranted(ac) && databaseHelper != null) {
            if (pr != null) { //databaseHelper.removeProfileSetting(pr)
                //Print("Profile removed from database");
                //(new DatabaseBackgroundHelper()).execute(12, pr);
                GenericAsyncTask.RunDBTask(new CallBack() {
                    @Override
                    public void call() { databaseHelper.removeProfileSetting(pr); }
                });
            }
            else { }//Print("Error removing profile from database"); }
        }
    }

    public void DBRemoveTransaction(Context ac, final Transaction tr){
        if (isStoragePermissionGranted(ac) && databaseHelper != null) {
            if (tr != null) { //databaseHelper.remove(tr)
                //Print("Transaction removed from database");
                //(new DatabaseBackgroundHelper()).execute(13, tr);
                GenericAsyncTask.RunDBTask(new CallBack() {
                    @Override
                    public void call() { databaseHelper.remove(tr); }
                });
            }
            else { }//Print("Error removing transaction from database"); }
        }
    }
    public void DBDelete(Context ac){
        if (isStoragePermissionGranted(ac) && databaseHelper != null) {
            //databaseHelper.DeleteDB();
            GenericAsyncTask.RunDBTask(new CallBack() {
                @Override
                public void call() { databaseHelper.DeleteDB(); }
            });
            //(new DatabaseBackgroundHelper()).execute(15);
        }
    }
    public void DBDeleteTransactionsAndProfiles(Context ac){
        if (isStoragePermissionGranted(ac) && databaseHelper != null) {
            //(new DatabaseBackgroundHelper()).execute(16);
            GenericAsyncTask.RunDBTask(new CallBack() {
                @Override
                public void call() {
                    databaseHelper.DeleteTransactions();
                    databaseHelper.DeleteProfiles();
                }
            });
        }
    }

    public void DBExport(Context ac, final String str){
        if (isStoragePermissionGranted(ac) && databaseHelper != null) {
            if (!str.equals("")) {
                //databaseHelper.DBExport(str);
                //(new DatabaseBackgroundHelper()).execute(1, str);
                GenericAsyncTask.RunDBTask(new CallBack() {
                    @Override
                    public void call() { databaseHelper.DBExport(str); }
                });
            }
        }
    }
    public void DBImport(Context ac, File file) {
        if (isStoragePermissionGranted(ac)) {
            DBImport(ac, file, true);
        }
    }
    public void DBImport(Context ac, final File file, final boolean backup){
        if (isStoragePermissionGranted(ac) && databaseHelper != null) {
            //Clear all old data
            ClearAllObjects();

            //Import new database
            //databaseHelper.importDatabase(file, backup);
            //(new DatabaseBackgroundHelper()).execute(0, file, backup);
            GenericAsyncTask.RunDBTask(new CallBack() {
                @Override public void call() { databaseHelper.importDatabase(file, backup); }
            });
        }
    }
    public boolean DoesBackupExist(Context ac) {
        if (isStoragePermissionGranted(ac)) {
            if (databaseHelper != null && databaseHelper.EXPORT_BACKUP != null){
                return databaseHelper.EXPORT_BACKUP.exists();
            }
            return false;
        }
        return false;
    }
    public void DBImportBackup(Context ac) { DBImport(ac, databaseHelper.EXPORT_BACKUP, false);  }
    public File GetDatabaseByPath(Context ac, String path) {
        if (isStoragePermissionGranted(ac) && databaseHelper != null) {
            return databaseHelper.getDatabaseByPath(path);
        }
        return null;
    }
    public void DBDeleteByPath(Context ac, String path) {
        if (isStoragePermissionGranted(ac)) {
            GetDatabaseByPath(ac, path).delete();
        }
    }
    public ArrayList<File> GetImportDatabaseFiles(Context ac) {
        if (isStoragePermissionGranted(ac) && databaseHelper != null) {
            return databaseHelper.getImportableDatabases();
        }
        return null;
    }
    public ArrayList<String> GetImportDatabaseFilesString(Context ac) {
        if (isStoragePermissionGranted(ac) && databaseHelper != null) {
            return databaseHelper.getImportableDatabasesString();
        }
        return null;
    }

    public String GetExportDirectory(Context ac) {
        if (isStoragePermissionGranted(ac) && databaseHelper != null) {
            return databaseHelper.GetExportDirectory();
        }
        return null;
    }
    public int GetNewestDatabaseVersion(Context ac) {
        if (isStoragePermissionGranted(ac) && databaseHelper != null) {
            return databaseHelper.GetNewestVersion();
        }
        return -1;
    }



    //Notifications
    /*
    public void ShowNotification(){
        //build your notification here.
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(MainActivityInstance)
                        .setSmallIcon(R.drawable.ic_account_multiple_white_24dp)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!"); //notification will be removed when once you enter application.

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) MainActivityInstance.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }


    public void scheduleNotification(Notification notification, int delay) {
        Print("Notification Scheduled");

        Intent notificationIntent = new Intent(MainActivityInstance, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivityContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) MainActivityContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    public Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(MainActivityContext);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_account_multiple_white_24dp);
        return builder.build();
    }
    */

    //Define parent callback interface
    public interface ParentCallback {
        void call(String data, DialogFragmentManagePPC dialogFragment);
    }
    public interface CallBack {
        void call();
    }


    /*
    public static void Sort(SORT_METHODS method)
    {
        switch (method) {
            case DATE_UP:
                Collections.sort(_tabs, new Comparator<Profile>() {
                    @Override
                    public int compare(Profile  tab1, Profile  tab2)
                    {
                        return  (int)Math.signum( (tab2.GetDateStart().getTimeInMillis()) - (tab1.GetDateStart().getTimeInMillis()) );
                    }
                });
                break;
            case DATE_DOWN:
                Collections.sort(_tabs, new Comparator<Profile>() {
                    @Override
                    public int compare(Profile  tab1, Profile  tab2)
                    {
                        return  (int)Math.signum( (tab1.GetDateStart().getTimeInMillis()) - (tab2.GetDateStart().getTimeInMillis()) );
                    }
                });
                break;
            case COST_UP:
                Collections.sort(_tabs, new Comparator<Profile>() {
                    @Override
                    public int compare(Profile  tab1, Profile  tab2)
                    {
                        return  (int)Math.signum( (tab2.GetTransactionsTotalCost()) - (tab1.GetTransactionsTotalCost()) );
                    }
                });
                break;
            case COST_DOWN:
                Collections.sort(_tabs, new Comparator<Profile>() {
                    @Override
                    public int compare(Profile  tab1, Profile  tab2)
                    {
                        return  (int)Math.signum( (tab1.GetTransactionsTotalCost()) - (tab2.GetTransactionsTotalCost()) );
                    }
                });
                break;
        }
    }
    */
}
