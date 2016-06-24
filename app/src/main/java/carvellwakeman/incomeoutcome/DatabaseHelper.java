package carvellwakeman.incomeoutcome;


import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class DatabaseHelper extends SQLiteOpenHelper
{

    SQLiteDatabase database;
    private Context activityContext;

    ContentValues contentValues_tr;
    ContentValues contentValues_tp;

    //DATABASE_VERSION
    private static final int DATABASE_VERSION = 3;
    //File information
    private static final String FILE_NAME = "data.db";
    private File EXPORT_DIRECTORY;
    private File EXPORT_DIRECTORY_BACKUP;
    private String BACKUP_FILENAME;
    public File EXPORT_BACKUP;
    //Other
    private static final String STATEMENT_DELIMITER = "\n";
    //Tables
    public static final String TABLE_SETTINGS_CATEGORIES = "SETTINGS_CATEGORIES_DATA";
    public static final String TABLE_SETTINGS_OTHERPEOPLE = "SETTINGS_OTHERPEOPLE_DATA";
    public static final String TABLE_SETTINGS_PROFILES = "SETTINGS_PROFILES_DATA";
    public static final String TABLE_EXPENSES = "EXPENSE_DATA";
    public static final String TABLE_INCOME = "INCOME_DATA";
    public static final String TABLE_TIMEPERIODS = "TIMEPERIOD_DATA";
    //Data types
    private static final String PRIMARYKEY = " PRIMARY KEY AUTOINCREMENT";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String BOOLEAN_TYPE = " BOOLEAN";
    private static final String DATE_TYPE = " DATE";
    //Shared
    private static final String COLUMN_category = "CATEGORY";
    private static final String COLUMN_splitWith = "SPLITWITH";
    private static final String COLUMN_profile = "PROFILE";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_uniqueID = "UID";
    private static final String COLUMN_parentID = "PARENTID";
    private static final String COLUMN_sourcename = "SOURCE";
    private static final String COLUMN_description = "DESCRIPTION";
    private static final String COLUMN_value = "VALUE";
    private static final String COLUMN_staticValue = "STATICVALUE";
    private static final String COLUMN_when = "TIMEPERIOD";
    //private static final String FOREIGNKEY_when = "FOREIGN KEY(" + COLUMN_when + ") REFERENCES " + TABLE_TIMEPERIODS + "(" + COLUMN_ID +") ON DELETE CASCADE";
    //TimePeriods
    private static final String COLUMN_tp_parent = "PARENT_UID";
    private static final String COLUMN_tp_date = "TP_DATE"; //date
    //private static final String COLUMN_tp_firstOcc = "TP_FIRSTOCCUR"; //date
    private static final String COLUMN_tp_repeatFreq = "TP_REPEATFREQ"; //int?
    private static final String COLUMN_tp_repeatUntil = "TP_REPEATUNT"; //int?
    private static final String COLUMN_tp_repeatNTimes = "TP_REPEATNTIMES"; //int
    private static final String COLUMN_tp_repeatUntilDate = "TP_REPEATUNTILDATE"; //date
    private static final String COLUMN_tp_repeatEveryN = "TP_REPEATEVERYN"; //int
    private static final String COLUMN_tp_repeatDayOfWeek = "TP_REPEATDAYOFWEEK"; //string (0101011)
    private static final String COLUMN_tp_repeatDayOfMonth = "TP_REPEATDAYOFMONTH"; //int
    private static final String COLUMN_tp_dateOfYear = "TP_DATEOFYEAR"; //date
    private static final String COLUMN_tp_blacklistDates = "TP_BLACKLISTDATES"; //string (04-16-2016-1,05-28-2016-0) (last number is edited(1), or not(0))
    //Expenses only
    private static final String COLUMN_IPaid = "IPAID";
    private static final String COLUMN_splitValue = "SPLITVALUE";
    private static final String COLUMN_paidBack = "PAIDBACK";
    //Settings only
    private static final String COLUMN_categorycolor = "CATEGORY_COLOR";
    private static final String COLUMN_profileSelected = "PROFILE_SEL";
    private static final String COLUMN_startdate = "START_DATE";
    private static final String COLUMN_enddate = "END_DATE";
    private static final String COLUMN_splitWith2 = "SPLITWITH2";



    //Create statements
    private static final String CREATE_TABLE_SETTINGS_CATEGORIES = "CREATE TABLE IF NOT EXISTS " + TABLE_SETTINGS_CATEGORIES + "(" +
            COLUMN_uniqueID + INT_TYPE + "," +
            COLUMN_category + TEXT_TYPE + "," +
            COLUMN_categorycolor + TEXT_TYPE
            + ");";
    private static final String CREATE_TABLE_SETTINGS_OTHERPEOPLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SETTINGS_OTHERPEOPLE + "(" +
            COLUMN_splitWith + TEXT_TYPE + "," +
            COLUMN_splitWith2 + TEXT_TYPE
            + ");";
    private static final String CREATE_TABLE_SETTINGS_PROFILES = "CREATE TABLE IF NOT EXISTS " + TABLE_SETTINGS_PROFILES + "(" +
            COLUMN_uniqueID + INT_TYPE + "," +
            COLUMN_profile + TEXT_TYPE + "," +
            COLUMN_profileSelected + INT_TYPE + "," +
            COLUMN_startdate + DATE_TYPE + "," +
            COLUMN_enddate + DATE_TYPE
            + ");";

    private static final String CREATE_TABLE_EXPENSES = "CREATE TABLE IF NOT EXISTS " + TABLE_EXPENSES + "(" + COLUMN_ID + INT_TYPE + PRIMARYKEY + "," +
            COLUMN_profile + INT_TYPE + "," +
            COLUMN_uniqueID + INT_TYPE + "," +
            COLUMN_parentID + INT_TYPE + "," +
            COLUMN_category + TEXT_TYPE + "," +
            COLUMN_sourcename + TEXT_TYPE + "," +
            COLUMN_description + TEXT_TYPE  + "," +
            COLUMN_value + DOUBLE_TYPE  + "," +
            COLUMN_staticValue + BOOLEAN_TYPE  + "," +
            COLUMN_IPaid + BOOLEAN_TYPE + "," +
            COLUMN_splitWith + TEXT_TYPE + "," +
            COLUMN_splitValue + DOUBLE_TYPE  + "," +
            COLUMN_paidBack + TEXT_TYPE + "," +
            COLUMN_when + INT_TYPE
            + ");";

    private static final String CREATE_TABLE_INCOME = "CREATE TABLE IF NOT EXISTS " + TABLE_INCOME + "(" + COLUMN_ID + INT_TYPE + PRIMARYKEY + "," +
            COLUMN_profile + INT_TYPE + "," +
            COLUMN_uniqueID + INT_TYPE + "," +
            COLUMN_parentID + INT_TYPE + "," +
            COLUMN_category + TEXT_TYPE + "," +
            COLUMN_sourcename + TEXT_TYPE + "," +
            COLUMN_description + TEXT_TYPE  + "," +
            COLUMN_value + DOUBLE_TYPE  + "," +
            COLUMN_staticValue + BOOLEAN_TYPE  + "," +
            COLUMN_when + INT_TYPE
            + ");";

    private static final String CREATE_TABLE_TIMEPERIOD = "CREATE TABLE IF NOT EXISTS " + TABLE_TIMEPERIODS + "(" + COLUMN_ID + INT_TYPE + PRIMARYKEY + "," +
            COLUMN_tp_parent + TEXT_TYPE + "," +
            COLUMN_tp_date + DATE_TYPE + "," +
            COLUMN_tp_repeatFreq + INT_TYPE + "," +
            COLUMN_tp_repeatUntil + INT_TYPE + "," +
            COLUMN_tp_repeatNTimes + INT_TYPE + "," +
            COLUMN_tp_repeatUntilDate + DATE_TYPE + "," +
            COLUMN_tp_repeatEveryN + INT_TYPE + "," +
            COLUMN_tp_repeatDayOfWeek + TEXT_TYPE + "," +
            COLUMN_tp_repeatDayOfMonth + INT_TYPE + "," +
            COLUMN_tp_dateOfYear + DATE_TYPE + "," +
            COLUMN_tp_blacklistDates + TEXT_TYPE
            + ");";

    //Drop statements
    private static final String DROP_TABLE_SETTINGS_CATEGORIES = "DROP TABLE IF EXISTS " + TABLE_SETTINGS_CATEGORIES;
    private static final String DROP_TABLE_SETTINGS_OTHERPEOPLE = "DROP TABLE IF EXISTS " + TABLE_SETTINGS_OTHERPEOPLE;
    private static final String DROP_TABLE_SETTINGS_PROFILES = "DROP TABLE IF EXISTS " + TABLE_SETTINGS_PROFILES;
    private static final String DROP_TABLE_EXPENSES = "DROP TABLE IF EXISTS " + TABLE_EXPENSES;
    private static final String DROP_TABLE_INCOME = "DROP TABLE IF EXISTS " + TABLE_INCOME;
    private static final String DROP_TABLE_TIMEPERIODS = "DROP TABLE IF EXISTS " + TABLE_TIMEPERIODS;

    //Upgrade statements
    //private static final String UPGRADE_TABLE_SETTINGS_CATEGORIES_1_2 = "ALTER TABLE " + TABLE_SETTINGS_CATEGORIES + " ADD COLUMN " + COLUMN_splitWith2 + TEXT_TYPE;
    //Add column COLUMN_splitWith2, copy value from COLUMN_splitWith into COLUMN_splitWith2
    private static final String UPGRADE_TABLE_SETTINGS_OTHERPEOPLE_2_3 = "ALTER TABLE " + TABLE_SETTINGS_OTHERPEOPLE +
            " ADD COLUMN " + COLUMN_splitWith2 + TEXT_TYPE + STATEMENT_DELIMITER +
            "UPDATE " + TABLE_SETTINGS_OTHERPEOPLE + " SET " + COLUMN_splitWith2 + " = " +COLUMN_splitWith;



    public DatabaseHelper(Context context) {
        super(context, FILE_NAME, null, DATABASE_VERSION);

        activityContext = context;

        EXPORT_DIRECTORY = new File(Environment.getExternalStorageDirectory() + "/" + activityContext.getString(R.string.app_name_nospace) + "/");

        EXPORT_DIRECTORY_BACKUP = new File(EXPORT_DIRECTORY.getAbsolutePath() + "/backup/");
        BACKUP_FILENAME = "data_backup.db";
        EXPORT_BACKUP = new File(EXPORT_DIRECTORY_BACKUP, BACKUP_FILENAME);

        TryCreateDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TryCreateDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ProfileManager.Print("OnUpgrade (" + oldVersion + "->" + newVersion + ")");
        try {
            ContentValues cv = new ContentValues();

            switch(oldVersion){
                case 1: //To version 2
                    cv.clear();
                    ProfileManager.Print("Version 1 Upgrade not supported");
                case 2: //To version 3
                    cv.clear();
                    //Other People table
                    //SQLExecuteMultiple(db, UPGRADE_TABLE_SETTINGS_OTHERPEOPLE_2_3);
                        //Add COLUMN_splitwith2, set default value to DefaultValue
                        //cv.put(COLUMN_splitWith2, "DefaultValue");
                        //db.update(TABLE_SETTINGS_OTHERPEOPLE, cv, null, null);
                    //Convert profile string to ID

                    ProfileManager.Print("Upgrade from Ver.2 to Ver.3");
                case 3: //To version 4
            }
            //OLD
            //if (newVersion > oldVersion){
                //DATABASE_VERSION 1 -> 2
                //if (oldVersion == 1) {
                    //Upgrade from DATABASE_VERSION 1 to 2
                //}
                //DATABASE_VERSION 2 -> 3
                //if (oldVersion == 2){
                //}
                //Recursive upgrade to next version
                //onUpgrade(db, oldVersion+1, newVersion);
            //}

            //db.execSQL(DROP_TABLE_SETTINGS_CATEGORIES);
            //db.execSQL(DROP_TABLE_SETTINGS_OTHERPEOPLE);
            //db.execSQL(DROP_TABLE_SETTINGS_PROFILES);
            //db.execSQL(DROP_TABLE_EXPENSES);
            //db.execSQL(DROP_TABLE_INCOME);
            //db.execSQL(DROP_TABLE_TIMEPERIODS);

            //onCreate(db);

        }
        catch(SQLException ex){
            ProfileManager.PrintLong(ex.getMessage());
            ProfileManager.Print("Error upgrading database");
        }
    }


    public void SQLExecuteMultiple(SQLiteDatabase db, String inputStatement){
        String[] statements = inputStatement.split(STATEMENT_DELIMITER);

        for(String statement : statements){
            db.execSQL(statement);
        }
    }

    public String GetExportDirectory(){ return EXPORT_DIRECTORY.getAbsolutePath(); }
    public int GetNewestVersion() { return DATABASE_VERSION; }

    public boolean isDatabaseEmpty(){

        if (!isTableEmpty(TABLE_SETTINGS_PROFILES) ||
                !isTableEmpty(TABLE_SETTINGS_OTHERPEOPLE) ||
                !isTableEmpty(TABLE_SETTINGS_CATEGORIES) ||
                !isTableEmpty(TABLE_EXPENSES) ||
                !isTableEmpty(TABLE_INCOME) ||
                isTableEmpty(TABLE_TIMEPERIODS))
        {return true; }

        return false;
    }

    public boolean isTableExists(String tableName, boolean openDb) {
        if(openDb) {
            if(database == null || !database.isOpen()) {
                database = getReadableDatabase();
            }

            if(!database.isReadOnly()) {
                database.close();
                database = getReadableDatabase();
            }
        }

        if (database != null) {
            Cursor cursor = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.close();
                    return true;
                }
                cursor.close();
            }
        }
        return false;
    }
    public boolean isTableEmpty(String tableName){
        database = getReadableDatabase();
        long cnt = DatabaseUtils.queryNumEntries(database, tableName);

        return cnt==0;
    }

    public void TryCreateDatabase(){ TryCreateDatabase(getWritableDatabase()); }
    public void TryCreateDatabase(SQLiteDatabase db){
        //Try to create database
        try {
            //Try create settings tables
            try {
                if (!isTableExists(TABLE_SETTINGS_CATEGORIES, false)) {
                    db.execSQL(CREATE_TABLE_SETTINGS_CATEGORIES);
                }
                if (!isTableExists(TABLE_SETTINGS_OTHERPEOPLE, false)) {
                    db.execSQL(CREATE_TABLE_SETTINGS_OTHERPEOPLE);
                }
                if (!isTableExists(TABLE_SETTINGS_PROFILES, false)) {
                    db.execSQL(CREATE_TABLE_SETTINGS_PROFILES);
                }
                //ProfileManager.Print("Settings tables created");
            } catch (SQLException ex){
                ProfileManager.Print("Error creating Settings table");
                ex.printStackTrace();
            }
            //Try create expenses table
            try {
                if (!isTableExists(TABLE_EXPENSES, false)) {
                    db.execSQL(CREATE_TABLE_EXPENSES);
                    //ProfileManager.Print("Expenses table created");
                }
            } catch (SQLException ex){
                ProfileManager.Print("Error creating Expenses table");
                ex.printStackTrace();
            }
            //Try create income table
            try {
                if (!isTableExists(TABLE_INCOME, false)) {
                    db.execSQL(CREATE_TABLE_INCOME);
                    //ProfileManager.Print("Income table created");
                }
            } catch (SQLException ex){
                ProfileManager.Print("Error creating Income table");
                ex.printStackTrace();
            }
            //Try create timeperiod table
            try {
                if (!isTableExists(TABLE_TIMEPERIODS, false)) {
                    db.execSQL(CREATE_TABLE_TIMEPERIOD);
                    //ProfileManager.Print("TimePeriod table created");
                }
            } catch (SQLException ex){
                ProfileManager.Print("Error creating TimePeriod table");
                ex.printStackTrace();
            }

            //database = getWritableDatabase();
            //ProfileManager.Print("Database created");
        }
        catch(SQLException ex){
            ProfileManager.Print("Error creating database");
            ex.printStackTrace();
        }
    }

    public void DeleteDB(){

        if (isTableExists(TABLE_SETTINGS_CATEGORIES, true)){
            database.delete(TABLE_SETTINGS_CATEGORIES, null, null);
            database.execSQL(DROP_TABLE_SETTINGS_CATEGORIES);
        } //else { ProfileManager.Print(TABLE_SETTINGS_CATEGORIES + " not found"); }

        if (isTableExists(TABLE_SETTINGS_OTHERPEOPLE, true)){
            database.delete(TABLE_SETTINGS_OTHERPEOPLE, null, null);
            database.execSQL(DROP_TABLE_SETTINGS_OTHERPEOPLE);
        } //else { ProfileManager.Print(TABLE_SETTINGS_OTHERPEOPLE + " not found"); }

        if (isTableExists(TABLE_SETTINGS_PROFILES, true)){
            database.delete(TABLE_SETTINGS_PROFILES, null, null);
            database.execSQL(DROP_TABLE_SETTINGS_PROFILES);
        } //else { ProfileManager.Print(TABLE_SETTINGS_PROFILES + " not found"); }


        if (isTableExists(TABLE_EXPENSES, true)){
            database.delete(TABLE_EXPENSES, null, null);
            database.execSQL(DROP_TABLE_EXPENSES);
        } //else { ProfileManager.Print(TABLE_EXPENSES + " not found"); }
        if (isTableExists(TABLE_INCOME, true)){
            database.delete(TABLE_INCOME, null, null);
            database.execSQL(DROP_TABLE_INCOME);
        } //else { ProfileManager.Print(TABLE_INCOME + " not found"); }
        if (isTableExists(TABLE_TIMEPERIODS, true)){
            database.delete(TABLE_TIMEPERIODS, null, null);
            database.execSQL(DROP_TABLE_TIMEPERIODS);
        } //else { ProfileManager.Print(TABLE_TIMEPERIODS + " not found"); }

        //onCreate(database);
    }


    public void exportDatabase(String str) { exportDatabase(str, EXPORT_DIRECTORY); }
    public void exportDatabase(String str, File destination) {

        try {
            File datadir = Environment.getDataDirectory();

            //Manage database name if *.db
            String[] sp1 = FILE_NAME.split("\\.");
            String filename = sp1[0];

            if (ProfileManager.isStoragePermissionGranted()){
                //Create backup directory if it does not exist
                destination.mkdirs();

                if (destination.canWrite()) {

                    //Database paths
                    String currentDBPath = "/data/" + activityContext.getPackageName() + "/databases/" + filename + ".db";
                    //String backupFilename = filename + "_export_" + (new LocalDate()).toString(ProfileManager.simpleDateFormatSaving) + ".db";
                    String backupFilename = str.replace(".db", "").concat(".db");
                    File currentDB = new File(datadir, currentDBPath);
                    File backupDB = new File(destination, backupFilename);

                    if (currentDB.exists()) {
                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();

                        //ProfileManager.PrintLong(backupDB.getName() + " exported");
                    }
                }
            }
            else {
                ProfileManager.Print("Storage permission not granted, cannot export database");
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<File> getImportableDatabases(){
        try {
            File data = new File(Environment.getExternalStorageDirectory() + "/" + activityContext.getString(R.string.app_name_nospace) + "/");

            ArrayList<File> DatabaseFiles = new ArrayList<>();

            if (ProfileManager.isStoragePermissionGranted()){
                if (data.canRead()) {
                    for (File file : data.listFiles()) {
                        if(file.getName().endsWith(".db")){
                            DatabaseFiles.add(file);
                        }
                    }

                    return DatabaseFiles;
                }
                else {
                    ProfileManager.Print("Cannot Read Database Import Directory");
                }
            }
            else {
                ProfileManager.Print("Storage permission not granted, cannot import databases");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public ArrayList<String> getImportableDatabasesString(){
        try {
            File data = new File(Environment.getExternalStorageDirectory() + "/" + activityContext.getString(R.string.app_name_nospace) + "/");

            ArrayList<String> DatabaseFiles = new ArrayList<>();

            if (ProfileManager.isStoragePermissionGranted()){
                if (data.canRead()) {
                    for (File file : data.listFiles()) {
                        if(file.getName().endsWith(".db")){
                            DatabaseFiles.add(file.getName().replace(".db", ""));
                        }
                    }

                    return DatabaseFiles;
                }
                else {
                    ProfileManager.Print("Cannot Read Database Import Directory");
                }
            }
            else {
                ProfileManager.Print("Storage permission not granted, cannot import databases");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void importDatabase(File importFile){ importDatabase(importFile, false); }
    public void importDatabase(File importFile, boolean backup){

        try {
            //Database paths
            String currentDBPath = "/data/" + activityContext.getPackageName() + "/databases/";
            File currentDB = new File(Environment.getDataDirectory(), currentDBPath + FILE_NAME);


            if (ProfileManager.isStoragePermissionGranted()){
                if (currentDB.canWrite() || importFile.canRead()) {
                    if (currentDB.exists()) {

                        //Backup currentDB (Secret)
                        if (backup) {
                            exportDatabase(BACKUP_FILENAME, EXPORT_DIRECTORY_BACKUP);
                        }

                        //Delete current database
                        DeleteDB();
                        //Create new empty database
                        TryCreateDatabase();

                        //Transfer database from import to local directory (ASyncTask)
                        //DatabaseBackgroundHelper dbh = new DatabaseBackgroundHelper();
                        //dbh.execute(0, importFile, currentDB);
                        FileUtils.copyFile(importFile, currentDB);


                        //Force upgrade even though DATABASE_VERSION has not changed because the database we are importing may be older than DATABASE_VERSION (WHY DOES THIS WORK?)
                        //Call getWritableDatabase() to trigger onUpgrade if it is necessary
                        //database = getWritableDatabase();
                        //Force call onUpgrade
                        //final int oldVersion = SQLiteDatabase.openDatabase(importFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE).getVersion();
                        //onUpgrade(getWritableDatabase(), oldVersion, DATABASE_VERSION);
                        getWritableDatabase().close();


                        //Load new database
                        loadSettings();
                        loadExpenses();
                        loadIncome();


                        ProfileManager.PrintLong(importFile.getName() + " imported");
                    }
                }
            }
            else {
                ProfileManager.Print("Storage permission not granted, cannot import database");
            }

        } catch (Exception e) {
            ProfileManager.Print("Error importing database");
            ProfileManager.PrintLong(e.getMessage());
        }

    }



    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "message" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }



    public long insertSetting(Category category, Boolean tryUpdate)
    {
        database = getWritableDatabase();

        if (database != null && isTableExists(TABLE_SETTINGS_CATEGORIES, false) && category != null) {
            contentValues_tr = new ContentValues();

            //Fill out row
            contentValues_tr.put(COLUMN_uniqueID, category.GetID());
            contentValues_tr.put(COLUMN_category, category.GetTitle());
            contentValues_tr.put(COLUMN_categorycolor, String.format("#%06X", 0xFFFFFF & category.GetColor()));

            //Insert/update row and return result
            long result = 0;
            if (tryUpdate){  result = database.update(TABLE_SETTINGS_CATEGORIES, contentValues_tr, COLUMN_uniqueID + "=" + category.GetID(), null); }
            if (result == 0) { result = database.insert(TABLE_SETTINGS_CATEGORIES, null, contentValues_tr); }

            return result;
        } else{ return -1; }
    }
    public long insertSetting(String person, Boolean tryUpdate)
    {
        database = getWritableDatabase();

        if (database != null && isTableExists(TABLE_SETTINGS_OTHERPEOPLE, false) && !person.equals("")) {
            contentValues_tr = new ContentValues();

            //Fill out row
            contentValues_tr.put(COLUMN_splitWith, person);

            //Insert/update row and return result
            long result = 0;
            if (tryUpdate){ result = database.update(TABLE_SETTINGS_OTHERPEOPLE, contentValues_tr, COLUMN_splitWith + "=?", new String[] { person }); }
            if (result == 0) { result = database.insert(TABLE_SETTINGS_OTHERPEOPLE, null, contentValues_tr); }

            return result;
        } else{ return -1; }
    }
    public long insertSetting(Profile profile, Boolean tryUpdate)
    {
        database = getWritableDatabase();

        if (profile != null) {
            if (database != null) {
                if (isTableExists(TABLE_SETTINGS_PROFILES, false)) {
                    contentValues_tr = new ContentValues();

                    //Fill out row
                    contentValues_tr.put(COLUMN_uniqueID, profile.GetID());
                    contentValues_tr.put(COLUMN_profile, profile.GetName());

                    Profile pr = ProfileManager.GetCurrentProfile();
                    if (pr != null) {
                        contentValues_tr.put(COLUMN_profileSelected, (pr.GetName().equals(profile.GetName()) ? 1 : 0));
                    }
                    if (profile.GetStartTime() != null) {
                        contentValues_tr.put(COLUMN_startdate, profile.GetStartTime().toString(ProfileManager.simpleDateFormatSaving));
                    }
                    if (profile.GetEndTime() != null) {
                        contentValues_tr.put(COLUMN_enddate, profile.GetEndTime().toString(ProfileManager.simpleDateFormatSaving));
                    }

                    //Insert/update row and return result
                    long result = 0;
                    if (tryUpdate) {
                        result = database.update(TABLE_SETTINGS_PROFILES, contentValues_tr, COLUMN_uniqueID + "=" + profile.GetID(), null);
                    }
                    if (result == 0) { result = database.insert(TABLE_SETTINGS_PROFILES, null, contentValues_tr); }

                    return result;
                }
                ProfileManager.Print("Table Does Not Exist");
                return -1;
            }
            ProfileManager.Print("Database is null");
            return -1;
        } else { ProfileManager.Print("Profile is null"); return -1; }
    }

    public long insert(Profile profile, Expense expense, Boolean tryupdate)
    {
        database = getWritableDatabase();

        if (database != null && isTableExists(TABLE_EXPENSES, false)) {
            contentValues_tr = new ContentValues();

            //Timeperiod
            long tp_id = insert(expense.GetID(), expense.GetTimePeriod(), tryupdate);

            //Fill out row
            contentValues_tr.put(COLUMN_profile, profile.GetID());
            contentValues_tr.put(COLUMN_uniqueID, expense.GetID());
            contentValues_tr.put(COLUMN_parentID, expense.GetParentID());
            contentValues_tr.put(COLUMN_category, expense.GetCategory());
            contentValues_tr.put(COLUMN_sourcename, expense.GetSourceName());
            contentValues_tr.put(COLUMN_description, expense.GetDescription());
            contentValues_tr.put(COLUMN_value, expense.GetValue());
            contentValues_tr.put(COLUMN_staticValue, expense.GetStatic());
            if (!tryupdate) { contentValues_tr.put(COLUMN_when, tp_id); }

            contentValues_tr.put(COLUMN_IPaid, expense.GetIPaid());
            contentValues_tr.put(COLUMN_splitWith, (!expense.GetSplitWith().equals("") ? expense.GetSplitWith() : ""));
            contentValues_tr.put(COLUMN_splitValue, expense.GetSplitValue());
            contentValues_tr.put(COLUMN_paidBack, (expense.GetPaidBack() != null ? expense.GetPaidBack().toString(ProfileManager.simpleDateFormatSaving) : "") );

            //Insert/update row and return result
            long result = 0;
            if (tryupdate){ result = database.update(TABLE_EXPENSES, contentValues_tr, COLUMN_uniqueID + "=" + expense.GetID(), null); }
            if (result == 0) { result = database.insert(TABLE_EXPENSES, null, contentValues_tr); }

            return result;
        }
        else{
            return -1;
        }
    }
    public long insert(Profile profile, Income income, Boolean tryupdate)
    {
        database = getWritableDatabase();

        if (database != null&& isTableExists(TABLE_INCOME, false)) {
            contentValues_tr = new ContentValues();

            //Timeperiod
            long tp_id = insert(income.GetID(), income.GetTimePeriod(), tryupdate);

            //Fill out row
            contentValues_tr.put(COLUMN_profile, profile.GetID());
            contentValues_tr.put(COLUMN_uniqueID, income.GetID());
            contentValues_tr.put(COLUMN_parentID, income.GetParentID());
            contentValues_tr.put(COLUMN_category, income.GetCategory());
            contentValues_tr.put(COLUMN_sourcename, income.GetSourceName());
            contentValues_tr.put(COLUMN_description, income.GetDescription());
            contentValues_tr.put(COLUMN_value, income.GetValue());
            contentValues_tr.put(COLUMN_staticValue, income.GetStatic());
            if (!tryupdate) { contentValues_tr.put(COLUMN_when, tp_id); }

            //Insert/update row and return result
            long result = 0;
            if (tryupdate){ result = database.update(TABLE_INCOME, contentValues_tr, COLUMN_uniqueID + "=" + income.GetID(), null); }
            if (result == 0) { result = database.insert(TABLE_INCOME, null, contentValues_tr); }

            return result;
        }
        else{
            return -1;
        }
    }
    public long insert(int UID, TimePeriod tp, Boolean tryupdate)
    {
        database = getWritableDatabase();

        if (database != null&& isTableExists(TABLE_TIMEPERIODS, false)) {
            contentValues_tp = new ContentValues();

            if (tp != null) {
                contentValues_tp.put(COLUMN_tp_parent, UID);
                contentValues_tp.put(COLUMN_tp_date, (tp.GetDate() != null ? tp.GetDate().toString(ProfileManager.simpleDateFormatSaving) : ""));
                //contentValues_tp.put(COLUMN_tp_firstOcc, (tp.GetFirstOccurrence() != null ? tp.GetFirstOccurrence().toString(ProfileManager.simpleDateFormatSaving) : ""));
                contentValues_tp.put(COLUMN_tp_repeatFreq, (tp.GetRepeatFrequency() != null ? tp.GetRepeatFrequency().ordinal() : 0));
                contentValues_tp.put(COLUMN_tp_repeatUntil, (tp.GetRepeatUntil() != null ? tp.GetRepeatUntil().ordinal() : 0));
                contentValues_tp.put(COLUMN_tp_repeatNTimes, tp.GetRepeatANumberOfTimes());
                contentValues_tp.put(COLUMN_tp_repeatUntilDate, (tp.GetRepeatUntilDate() != null ? tp.GetRepeatUntilDate().toString(ProfileManager.simpleDateFormatSaving) : ""));
                contentValues_tp.put(COLUMN_tp_repeatEveryN, tp.GetRepeatEveryN());
                contentValues_tp.put(COLUMN_tp_repeatDayOfWeek, tp.GetRepeatDayOfWeekBinary());
                contentValues_tp.put(COLUMN_tp_repeatDayOfMonth, tp.GetRepeatDayOfMonth());
                contentValues_tp.put(COLUMN_tp_dateOfYear, (tp.GetDateOfYear() != null ? tp.GetDateOfYear().toString(ProfileManager.simpleDateFormatSaving) : ""));
                contentValues_tp.put(COLUMN_tp_blacklistDates, tp.GetBlacklistDatesSaving());
            }

            //Insert/update row and return result
            long result = 0;
            if (tryupdate) { result = database.update(TABLE_TIMEPERIODS, contentValues_tp, COLUMN_tp_parent + "=" + UID, null); }
            if (result == 0) { result = database.insert(TABLE_TIMEPERIODS, null, contentValues_tp); }

            return result;
        }
        return -1;
    }

    public TimePeriod queryTimeperiod(int id)
    {
        database = getWritableDatabase();

        Cursor c = database.query(TABLE_TIMEPERIODS, null, COLUMN_ID + "=" + id, null, null, null, null);

        //Loop through data
        while (c.moveToNext()) {
            //Create timeperiod object
            TimePeriod tp = new TimePeriod();

            //COLUMN_tp_date + DATE_TYPE + "," +
            tp.SetDate(ProfileManager.ConvertDateFromString(c.getString(c.getColumnIndex(COLUMN_tp_date))));
            //COLUMN_tp_repeatFreq + INT_TYPE + "," +
            tp.SetRepeatFrequency(tp.GetRepeatFrequencyFromIndex(c.getInt(c.getColumnIndex(COLUMN_tp_repeatFreq))));
            //COLUMN_tp_repeatUntil + INT_TYPE + "," +
            tp.SetRepeatUntil(tp.GetRepeatUntilFromIndex(c.getInt(c.getColumnIndex(COLUMN_tp_repeatUntil))));
            //COLUMN_tp_repeatNTimes + INT_TYPE + "," +
            tp.SetRepeatANumberOfTimes(c.getInt(c.getColumnIndex(COLUMN_tp_repeatNTimes)));
            //COLUMN_tp_repeatUntilDate + DATE_TYPE + "," +
            tp.SetRepeatUntilDate(ProfileManager.ConvertDateFromString(c.getString(c.getColumnIndex(COLUMN_tp_repeatUntilDate))));
            //COLUMN_tp_repeatEveryN + INT_TYPE + "," +
            tp.SetRepeatEveryN(c.getInt(c.getColumnIndex(COLUMN_tp_repeatEveryN)));
            //COLUMN_tp_repeatDayOfWeek + TEXT_TYPE + "," +
            tp.SetRepeatDayOfWeekFromBinary(c.getString(c.getColumnIndex(COLUMN_tp_repeatDayOfWeek)));
            //COLUMN_tp_repeatDayOfMonth + INT_TYPE + "," +
            tp.SetRepeatDayOfMonth(c.getInt(c.getColumnIndex(COLUMN_tp_repeatDayOfMonth)));
            //COLUMN_tp_dateOfYear + DATE_TYPE + "," +
            tp.SetDateOfYear(ProfileManager.ConvertDateFromString(c.getString(c.getColumnIndex(COLUMN_tp_dateOfYear))));
            //COLUMN_tp_blacklistDates + TEXT_TYPE
            String[] s1 = c.getString(c.getColumnIndex(COLUMN_tp_blacklistDates)).split(Pattern.quote(","));
            if (s1.length > 0) {
                for (int i = 0; i < s1.length; i++) {
                    String[] s2 = s1[i].split(Pattern.quote("|"));
                    if (s2.length > 1) {
                        tp.AddBlacklistDate(ProfileManager.ConvertDateFromString(s2[0]), Integer.valueOf(s2[1]) == 1);
                    }
                }
            }

            return tp;
        }


        c.close();

        return null;
    }


    public void loadSettings()
    {
        database = getWritableDatabase();

        Cursor c = null;
        try {
            c = database.query(TABLE_SETTINGS_CATEGORIES, null, null, null, null, null, null);
            while (c.moveToNext()) {
                //Find categories
                String category = c.getString(c.getColumnIndex(COLUMN_category));
                int catColor = Color.parseColor(c.getString(c.getColumnIndex(COLUMN_categorycolor)));

                //Fill out category
                if (category != null){
                    ProfileManager.AddCategory(new Category(category, catColor), true);
                }
            }

            c = database.query(TABLE_SETTINGS_OTHERPEOPLE, null, null, null, null, null, null);
            while (c.moveToNext()) {
                //Find other people
                String person = c.getString(c.getColumnIndex(COLUMN_splitWith));

                //Fill out other people
                if (person != null){
                    ProfileManager.AddOtherPerson(person);
                }
            }

            c = database.query(TABLE_SETTINGS_PROFILES, null, null, null, null, null, null);
            while (c.moveToNext()) {
                //Find profiles
                int uniqueID = c.getInt(c.getColumnIndex(COLUMN_uniqueID));
                String profile = c.getString(c.getColumnIndex(COLUMN_profile));
                int profileSel = c.getInt(c.getColumnIndex(COLUMN_profileSelected));
                String start_date = c.getString(c.getColumnIndex(COLUMN_startdate));
                String end_date = c.getString(c.getColumnIndex(COLUMN_enddate));

                //Fill out profiles
                if (profile != null){
                    Profile p = new Profile(profile);
                    p.SetID(uniqueID);
                    if (start_date != null){ p.SetStartTime(ProfileManager.ConvertDateFromString(start_date)); }
                    if (end_date != null){ p.SetEndTime(ProfileManager.ConvertDateFromString(end_date)); }
                    ProfileManager.AddProfile(p, true);

                    if (profileSel == 1){ ProfileManager.SelectProfile(p); }
                }
            }

        }
        catch (CursorIndexOutOfBoundsException ex){
            ProfileManager.Print("ERROR: No settings found");
            ex.printStackTrace();
        } finally {
            if (c != null) { c.close(); }
        }

        //Transfer database from import to local directory (ASyncTask)
        //DatabaseBackgroundHelper dbh = new DatabaseBackgroundHelper();
        //dbh.execute(1, database);


    }
    public void loadExpenses()
    {
        database = getWritableDatabase();

        Cursor c = database.query(TABLE_EXPENSES, null, null, null, null, null, null);

        while (c.moveToNext()) {
            //Find profile
            int _profileID = c.getInt(c.getColumnIndex(COLUMN_profile));
            Profile pr = ProfileManager.GetProfileByID(_profileID);

            if (pr != null) {
                //Create new transaction
                Expense ex = new Expense();

                //Load and apply transaction properties

                //COLUMN_uniqueID + TEXT_TYPE + "," +
                ex.SetID(c.getInt(c.getColumnIndex(COLUMN_uniqueID)));
                //COLUMN_parentID + TEXT_TYPE + "," +
                ex.SetParentID(c.getInt(c.getColumnIndex(COLUMN_parentID)));
                //COLUMN_category + TEXT_TYPE + "," +
                ex.SetCategory(c.getString(c.getColumnIndex(COLUMN_category)));
                //COLUMN_sourcename + TEXT_TYPE + "," +
                ex.SetSourceName(c.getString(c.getColumnIndex(COLUMN_sourcename)));
                //COLUMN_description + TEXT_TYPE  + "," +
                ex.SetDescription(c.getString(c.getColumnIndex(COLUMN_description)));
                //COLUMN_value + DOUBLE_TYPE  + "," +
                ex.SetValue(c.getDouble(c.getColumnIndex(COLUMN_value)));
                //COLUMN_staticValue + BOOLEAN_TYPE  + "," +
                ex.SetStatic(c.getInt(c.getColumnIndex(COLUMN_staticValue)) == 1);
                //COLUMN_IPaid + BOOLEAN_TYPE + "," +
                ex.SetIPaid(c.getInt(c.getColumnIndex(COLUMN_IPaid)) == 1);
                //COLUMN_splitWith + TEXT_TYPE + "," + //COLUMN_splitValue + DOUBLE_TYPE  + "," +
                ex.SetSplitValue(c.getString(c.getColumnIndex(COLUMN_splitWith)), c.getDouble(c.getColumnIndex(COLUMN_splitValue)));
                //COLUMN_paidBack + TEXT_TYPE + "," +
                ex.SetPaidBack(ProfileManager.ConvertDateFromString(c.getString(c.getColumnIndex(COLUMN_paidBack))));
                //COLUMN_when + INT_TYPE //+ "," +
                ex.SetTimePeriod(queryTimeperiod(c.getInt(c.getColumnIndex(COLUMN_when))));

                //ProfileManager.Print("Expense Loaded");
                //Add loaded transaction to profile
                pr.AddExpense(ex, true);
            }
            else {
                ProfileManager.Print("Expense could not be loaded, profile -" + _profileID + "- not found.");
            }
        }


        c.close();
    }
    public void loadIncome()
    {
        database = getWritableDatabase();

        Cursor c = database.query(TABLE_INCOME, null, null, null, null, null, null);

        while (c.moveToNext()) {
            //Find profile
            int _profileID = c.getInt(c.getColumnIndex(COLUMN_profile));
            Profile pr = ProfileManager.GetProfileByID(_profileID);


            if (pr != null) {
                //Create new transaction
                Income in = new Income();

                //Load and apply transaction properties

                //COLUMN_uniqueID + TEXT_TYPE + "," +
                in.SetID(c.getInt(c.getColumnIndex(COLUMN_uniqueID)));
                //COLUMN_parentID + TEXT_TYPE + "," +
                in.SetParentID(c.getInt(c.getColumnIndex(COLUMN_parentID)));
                //COLUMN_category + TEXT_TYPE + "," +
                in.SetCategory(c.getString(c.getColumnIndex(COLUMN_category)));
                //COLUMN_sourcename + TEXT_TYPE + "," +
                in.SetSourceName(c.getString(c.getColumnIndex(COLUMN_sourcename)));
                //COLUMN_description + TEXT_TYPE  + "," +
                in.SetDescription(c.getString(c.getColumnIndex(COLUMN_description)));
                //COLUMN_value + DOUBLE_TYPE  + "," +
                in.SetValue(c.getDouble(c.getColumnIndex(COLUMN_value)));
                //COLUMN_staticValue + BOOLEAN_TYPE  + "," +
                in.SetStatic(c.getInt(c.getColumnIndex(COLUMN_staticValue)) == 1);
                //COLUMN_when + INT_TYPE //+ "," +
                in.SetTimePeriod(queryTimeperiod(c.getInt(c.getColumnIndex(COLUMN_when))));


                //Add loaded transaction to profile
                pr.AddIncome(in, true);
            }
            else {
                ProfileManager.Print("Income could not be loaded, profile -" + _profileID + "- not found.");
            }
        }



        c.close();
    }


    public boolean removeCategorySetting(String category){
        database = getWritableDatabase();
        return !category.equals("") && database.delete(TABLE_SETTINGS_CATEGORIES, COLUMN_category + "=?", new String[]{ category }) > 0;
    }
    public boolean removePersonSetting(String name){
        database = getWritableDatabase();
        return !name.equals("") && database.delete(TABLE_SETTINGS_OTHERPEOPLE, COLUMN_splitWith + "=?", new String[]{ name }) > 0;
    }
    public boolean removeProfileSetting(Profile profile){
        database = getWritableDatabase();
        return profile != null && database.delete(TABLE_SETTINGS_PROFILES, COLUMN_profile + "=?", new String[]{String.valueOf(profile.GetName())}) > 0;
    }

    public boolean remove(Expense expense){
        database = getWritableDatabase();

        if (expense != null) {
            Cursor c = database.query(TABLE_EXPENSES, new String[]{COLUMN_when}, COLUMN_uniqueID + "=?", new String[]{String.valueOf(expense.GetID())}, null, null, null);
            removeTimePeriod(c);
            c.close();

            return database.delete(TABLE_EXPENSES, COLUMN_uniqueID + "=?", new String[]{String.valueOf(expense.GetID())}) > 0;
        }
        return false;
    }
    public boolean remove(Income income){
        database = getWritableDatabase();

        if (income != null) {
            Cursor c = database.query(TABLE_INCOME, new String[]{COLUMN_when}, COLUMN_uniqueID + "=?", new String[]{String.valueOf(income.GetID())}, null, null, null);
            removeTimePeriod(c);
            c.close();

            return database.delete(TABLE_INCOME, COLUMN_uniqueID + "=?", new String[]{String.valueOf(income.GetID())}) > 0;
        }
        return false;
    }
    private void removeTimePeriod(Cursor c){
        database = getWritableDatabase();

        //Loop through data
        if ( c != null ){
            while (c.moveToNext()) {
                //Find timeperiod ID
                int tpID = c.getInt(c.getColumnIndex(COLUMN_when));
                //Remove timeperiod database row
                database.delete(TABLE_TIMEPERIODS, COLUMN_ID + "=?", new String[] { String.valueOf(tpID) });
            }
        }
        else {
            ProfileManager.Print("TimePeriod not found");
        }
    }
}
