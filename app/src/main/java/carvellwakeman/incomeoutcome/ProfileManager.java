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

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import org.joda.time.LocalDate;
import org.joda.time.Period;
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
        CATEGORY,
        SOURCE
    }

    //Constructor and Init
    private ProfileManager(){}
    static ProfileManager getInstance(){ return instance; }

    public void initialize(ActivityMain ac)
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
        (new DatabaseBackgroundHelper()).execute(8); //databaseHelper.loadSettings();
        (new DatabaseBackgroundHelper()).execute(9); //databaseHelper.loadTransactions();
        //databaseHelper.loadExpenses();
        //databaseHelper.loadIncome();

        //Load default categories if database is empty
        if (databaseHelper.isTableEmpty(DatabaseHelper.TABLE_SETTINGS_CATEGORIES) && databaseHelper.isTableEmpty(DatabaseHelper.TABLE_TRANSACTIONS)){
            LoadDefaultCategories(ac);
        }

        //Try to create the database
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
        AddCategory("Groceries", Color.argb(255, 0, 0, 255));
        AddCategory("Fast Food", Color.argb(255, 0, 20, 200));
        AddCategory("Restaurant", Color.argb(255, 50, 50, 150));
        AddCategory("Snacks", Color.argb(255, 50, 80, 150));

        AddCategory("Rent", Color.argb(255, 200, 0, 0));
        AddCategory("Mortgage", Color.argb(255, 180, 30, 0));
        AddCategory("ATM Withdrawal", Color.argb(255, 150, 50, 0));

        AddCategory("Electricity", Color.argb(255, 250, 255, 30));
        AddCategory("Sewer", Color.argb(255, 165, 165, 60));
        AddCategory("Water", Color.argb(255, 36, 174, 212));
        AddCategory("Garbage", Color.argb(255, 62, 105, 54));
        AddCategory("Internet", Color.argb(255, 50, 50, 150));
        AddCategory("Entertainment", Color.argb(255, 180, 255, 120));

        AddCategory("Gasoline", Color.argb(255, 150, 0, 150));
        AddCategory("Travel", Color.argb(255, 230, 50, 255));
        AddCategory("Vehicle", Color.argb(255, 85, 0, 80));

        AddCategory("Office Supplies", Color.argb(255, 255, 180, 80));
        AddCategory("Home Supplies", Color.argb(255, 255, 100, 40));
        AddCategory("Kitchen Supplies", Color.argb(255, 255, 50, 20));
        AddCategory("Home Improvement", Color.argb(255, 50, 0, 255));
        AddCategory("Home Repair", Color.argb(255, 115, 80, 255));
        AddCategory("Pet", Color.argb(255, 200, 255, 200));

        AddCategory("Hobbies", Color.argb(255, 30, 200, 80));
        AddCategory("Second-Hand", Color.argb(255, 15, 140, 50));
        AddCategory("Clothing/Jewelry", Color.argb(255, 20, 200, 5));

        AddCategory("Gifts", Color.argb(255, 0, 255, 255));

        AddCategory("Medical", Color.argb(255, 130, 0, 40));
        AddCategory("Prescription", Color.argb(255, 180, 45, 50));
        AddCategory("Health & Beauty", Color.argb(255, 100, 0, 200));
        AddCategory("Personal", Color.argb(255, 216, 66, 216));

        AddCategory("Other", Color.argb(255, 140, 140, 140));

        //MainActivityInstance.UpdateProfileList(false);
        //MainActivityInstance.SetSelection(GetProfileIndex(GetCurrentProfile()));

        //Success
        Print(ac, "Default categories loaded");
    }


    //Universal print
    public static void Print(Context c, String msg){ if (isDebugMode(c)) { Toast.makeText(c, msg, Toast.LENGTH_SHORT).show(); } }
    public static void PrintLong(Context c, String msg){ if (isDebugMode(c)) { Toast.makeText(c, msg, Toast.LENGTH_LONG).show(); } }
    public static void PrintUser(Context c, String msg){ Toast.makeText(c, msg, Toast.LENGTH_SHORT).show(); }
    public static void Log(Context c, String cat, String msg){ if (isDebugMode(c)) { Log.e(cat, msg); } }
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

    //ActivityMain profile list update
    public static void UpdateProfileList(Boolean select){
        //MainActivityInstance.UpdateProfileList(select);
    }


    //Profile Management

    //Add profile
    public void AddProfile(Profile profile, Boolean dontsave) {
        if (profile != null) {
            _profiles.add(profile);
            //MainActivityInstance.UpdateProfileList(false);
        }
    }
    public void AddProfile(Profile profile) { if (profile != null) { AddProfile(profile, true); InsertSettingDatabase(profile, true); } }

    public void UpdateProfile(Profile profile) {
        InsertSettingDatabase(profile, true);
        //MainActivityInstance.UpdateProfileList(false); //INFINITE LOOP
    }

    //Delete profile
    public void DeleteProfile(Profile profile)
    {
        boolean reselect = false;
        reselect = (profile == GetCurrentProfile());

        if (profile != null) {
            profile.RemoveAll();
            _profiles.remove(profile);
            RemoveProfileSettingDatabase(profile);
            //Print("DeleteProfile");
        }
        if (reselect) { SelectProfile(GetProfileByIndex(0)); }

        //MainActivityInstance.UpdateProfileList(false);
        //MainActivityInstance.SetSelection(GetProfileIndex(GetCurrentProfile()));
    }
    public void DeleteProfileByID(String id)
    {
        if (id != null && !id.equals("")) {
            for (int i = 0; i < _profiles.size(); i++) {
                if (_profiles.get(i).toString().equals(id)) {
                    DeleteProfile(_profiles.get(i));
                    break;
                }
            }
        }
    }


    //Get Profile at index
    public Profile GetProfileByIndex(int index)
    {
        if (GetProfileCount() >= index+1 && index >= 0) {
            return _profiles.get(index);
        }
        return null;
    }

    //Get Profile with ID
    public Profile GetProfileByID(int id)
    {
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
        if (profile != null) { return _profiles.indexOf(profile); }
        else { return 0; }
    }

    //Select Profile
    public boolean SelectProfile(Profile profile){
        if (profile != null) {
            Profile old = GetCurrentProfile();
            //Update new profile to be active
            _currentProfileID = profile.GetID();
            InsertSettingDatabase(profile, true);
            //MainActivityInstance.SetSelection(GetProfileIndex(profile));
            //MainActivityInstance.UpdateStartEndDate();

            //Update old profile to be unselected
            if (_profiles.size() > 0 && old != null) {
                InsertSettingDatabase(old, true);
            }
            return _currentProfileID >= 0;
        }
        return false;
    }
    public boolean SelectProfile(String name){
        for (int i = 0; i < _profiles.size(); i++) {
            if (_profiles.get(i).GetName().equals(name)) {
                SelectProfile(_profiles.get(i));
                return true;
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
    public int GetProfileCount()
    {
        return _profiles.size();
    }

    //Get profiles array
    public ArrayList<Profile> GetProfiles(){
        return _profiles;
    }

    //Get String array of profile names
    public ArrayList<String> GetProfileNames(){
        ArrayList<String> ar = new ArrayList<>();

        for (int i = 0; i < _profiles.size(); i++){
            ar.add(_profiles.get(i).GetName());
        }

        return ar;
    }

    public Boolean HasProfile(String profile){
        for (int i = 0; i < _profiles.size(); i++){
            if (_profiles.get(i).GetName().equals(profile)){
                return true;
            }
        }
        return false;
    }

    //Clear profiles
    public void ClearProfiles(){
        for (Profile pr : _profiles){
            pr.ClearAllObjects();
        }
        _profiles.clear();
        _currentProfileID = -1;
    }




    //Other People
    //Add other person
    public void AddOtherPerson(String name, Boolean dontsave) {
        if (!name.equals("")) {
            _otherPeople.add(name);
        }
    }
    public void AddOtherPerson(String name){
        AddOtherPerson(name, true);
        InsertSettingDatabase(name, true);
    }

    //Remove other person
    public void RemoveOtherPerson(String name){
        for (int i = 0; i < _otherPeople.size(); i++){
            if (_otherPeople.get(i).equals(name)){
                _otherPeople.remove(i);
                RemovePersonSettingDatabase(name);
            }
        }
    }

    //Get other person by index
    public String GetOtherPersonByIndex(int idx){
        return _otherPeople.get(idx);
    }

    //Get other person by name
    public boolean HasOtherPerson(String name){
        for (int i = 0; i < _otherPeople.size(); i++){
            if (_otherPeople.get(i).equals(name)){
                return true;
            }
        }
        return false;
    }

    //Get array of OtherPeople objects
    public ArrayList<String> GetOtherPeople(){
        return _otherPeople;
    }

    //Get count of other people
    public int GetOtherPeopleCount(){
        return _otherPeople.size();
    }

    //Clear other people
    public void ClearOtherPeople(){
        _otherPeople.clear();
    }

    //Update other person
    public void UpdateOtherPerson(String old, String name){
        for (Profile pr : _profiles){
            pr.UpdateOtherPerson(old, name);
        }
    }



    //Categories
    //Add category
    public void AddCategory(Category category, Boolean dontsave) {
        if (category != null) {
            _categories.add(category);
        }
    }
    public void AddCategory(Category category){
        AddCategory(category, true);
        InsertSettingDatabase(category, true);
    }
    public void AddCategory(String name, int color){
        AddCategory(new Category(name, color));
    }

    //Remove category
    public void RemoveCategory(Category category) {
        _categories.remove(category);
        RemoveCategorySettingDatabase(category.GetTitle());
    }
    public void RemoveCategory(String title){
        for (int i = 0; i < _categories.size(); i++){
            if (_categories.get(i).GetTitle().equals(title)){
                RemoveCategory(_categories.get(i));
            }
        }
    }
    public void RemoveAllCategories(){
        ArrayList<Category> temp = new ArrayList<>();
        temp.addAll(_categories);

        for (int i = 0; i < temp.size(); i++){
            RemoveCategory(temp.get(i));
        }
    }

    //Get category by index
    public Boolean HasCategory(String category){
        for (int i = 0; i < _categories.size(); i++){
            if (_categories.get(i).GetTitle().equals(category)){
                return true;
            }
        }
        return false;
    }
    public Category GetCategory(String category)
    {
        for (int i = 0; i < _categories.size(); i++){
            if (_categories.get(i).GetTitle().equals(category)){
                return _categories.get(i);
            }
        }
        return null;
    }
    public Category GetCategoryByIndex(int idx){
        return _categories.get(idx);
    }

    public int GetCategoryIndex(String title) { return _categories.indexOf(GetCategory(title)); }

    //Get array of OtherPeople objects
    public ArrayList<Category> GetCategories(){
        return _categories;
    }
    public ArrayList<String> GetCategoriesString(){
        ArrayList<String> arr = new ArrayList<>();
        for(Category c : _categories){
            arr.add(c.GetTitle());
        }
        return arr;
    }

    public int GetCategoriesCount() { return _categories.size(); }


    //Get ArrayList<String> of _categories
    //public static ArrayList<Category> GetCategories(){
    //    return _categories;
    //}
    public ArrayList<String> GetCategoryTitles(){
        ArrayList<String> arr = new ArrayList<>();
        arr.add(0,getString(R.string.select_category));

        for (Category cat : _categories){
            arr.add(cat.GetTitle());
        }


        return arr;
    }

    //Clear _categories
    public void ClearCategories(){
        _categories.clear();
    }

    //Update category
    public void UpdateCategory(String old, Category category){
        for (Profile pr : _profiles){
            pr.UpdateCategory(old, category.GetTitle());
        }
        InsertSettingDatabase(category, true);
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

    public static void setRefreshToolbarEnable(CollapsingToolbarLayout collapsingToolbarLayout,
                                               boolean refreshToolbarEnable) {
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
        InputMethodManager imm = (InputMethodManager)act.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
            if (ac.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                ////Log.v("PERMISSIONS","External Write Storage permission is granted");
                return true;
            } else {
                ////Log.v("PERMISSIONS", "External Write Storage permission is not granted");
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            ////Log.v("PERMISSIONS","Permission is granted");
            return true;
        }
    }

    //DEBUG
    public static boolean isDebugMode(Context ac){
        return (0 != (ac.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }


    //External Database Management
    public DatabaseHelper GetDatabaseHelper() { return databaseHelper; }
    public void InsertSettingDatabase(Category category, boolean tryupdate){
        if ( category != null ){ //|| databaseHelper.insertSetting(category, tryupdate) == -1
            //Print("Error inserting category into database");
            (new DatabaseBackgroundHelper()).execute(2, category, tryupdate);
        }
    }
    public void InsertSettingDatabase(String name, boolean tryupdate){
        if (!name.equals("") ) { //|| databaseHelper.insertSetting(name, tryupdate) == -1
            //Print("Error inserting person into database");
            (new DatabaseBackgroundHelper()).execute(3, name, tryupdate);
        }
    }
    public void InsertSettingDatabase(Profile profile, boolean tryupdate){
        if (profile != null ) { //|| databaseHelper.insertSetting(profile, tryupdate) == -1
            //Print("Error inserting profile into database");
            (new DatabaseBackgroundHelper()).execute(4, profile, tryupdate);
        }

    }

    public void InsertTransactionDatabase(Profile pr, Transaction tr, boolean tryupdate){
        if ( pr != null && tr != null){
            //Print("Transaction " + (tryupdate ? "updated" : "inserted into database") );
            (new DatabaseBackgroundHelper()).execute(5, pr, tr, tryupdate);
        } else { }//Print("Error inserting transaction into database"); }
    }



    public void RemoveCategorySettingDatabase(String category){
        if ( !category.equals("") ){ //databaseHelper.removeCategorySetting(category)
            //Print("Category removed from database");
            (new DatabaseBackgroundHelper()).execute(10, category);
        } else { }//Print("Error removing category from database"); }
    }
    public void RemovePersonSettingDatabase(String person){
        if ( !person.equals("") ){ //databaseHelper.removeSettingPerson(person)
            //Print("Person removed from database");
            (new DatabaseBackgroundHelper()).execute(11, person);
        } else { }//Print("Error removing person from database"); }
    }
    public void RemoveProfileSettingDatabase(Profile pr){
        if ( pr != null ){ //databaseHelper.removeProfileSetting(pr)
            //Print("Profile removed from database");
            (new DatabaseBackgroundHelper()).execute(12, pr);
        } else { }//Print("Error removing profile from database"); }
    }

    public void RemoveTransactionDatabase(Transaction tr){
        if ( tr != null ){ //databaseHelper.remove(tr)
            //Print("Transaction removed from database");
            (new DatabaseBackgroundHelper()).execute(13, tr);
        } else { }//Print("Error removing transaction from database"); }
    }
    public void DeleteDatabase(){
        //databaseHelper.DeleteDB();
        (new DatabaseBackgroundHelper()).execute(15);
    }

    public void ExportDatabase(String str){
        if (!str.equals("")) {
            //databaseHelper.exportDatabase(str);
            (new DatabaseBackgroundHelper()).execute(1, str);
        }
    }
    public void ImportDatabase(Context c, File file) { ImportDatabase(c, file, true); }
    public void ImportDatabase(Context c, File file, boolean backup){
        if (isStoragePermissionGranted(c)) {
            //Clear all old data
            ClearAllObjects();

            //Import new database
            //databaseHelper.importDatabase(file, backup);
            (new DatabaseBackgroundHelper()).execute(0, file, backup);
        }
    }
    public Boolean DoesBackupExist() { return databaseHelper.EXPORT_BACKUP.exists(); }
    public void ImportDatabaseBackup(Context c) { ImportDatabase(c, databaseHelper.EXPORT_BACKUP, false);  }
    public File GetDatabaseByPath(String path) { return databaseHelper.getDatabaseByPath(path); }
    public void DeleteDatabaseByPath(String path) { GetDatabaseByPath(path).delete(); }
    public ArrayList<File> GetImportDatabaseFiles() { return databaseHelper.getImportableDatabases(); }
    public ArrayList<String> GetImportDatabaseFilesString() { return databaseHelper.getImportableDatabasesString();  }

    public String GetExportDirectory() { return databaseHelper.GetExportDirectory(); }
    public int GetNewestDatabaseVersion() { return databaseHelper.GetNewestVersion(); }



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
