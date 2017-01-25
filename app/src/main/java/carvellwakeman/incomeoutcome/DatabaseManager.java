package carvellwakeman.incomeoutcome;


import android.os.AsyncTask;
import android.content.ContentValues;
import android.database.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Environment;
import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class DatabaseManager extends SQLiteOpenHelper
{
    static DatabaseManager instance = new DatabaseManager();

    SQLiteDatabase database;

    private ContentValues contentValues_tr;
    private ContentValues contentValues_tp;

    //DATABASE_VERSION
    private static final int DATABASE_VERSION = 7;
    //File information
    private static final String FILE_NAME = "data.db";
    private File EXPORT_DIRECTORY;
    private File EXPORT_DIRECTORY_BACKUP;
    private String BACKUP_FILENAME;
    public File EXPORT_BACKUP;
    //Other
    //private static final String ITEM_DELIMITER = "\\|";
    private static final String STATEMENT_DELIMITER = "\n";
    //Tables
    public static final String TABLE_SETTINGS_CATEGORIES = "SETTINGS_CATEGORIES_DATA";
    public static final String TABLE_SETTINGS_OTHERPEOPLE = "SETTINGS_OTHERPEOPLE_DATA";
    public static final String TABLE_SETTINGS_PROFILES = "SETTINGS_PROFILES_DATA";
    public static final String TABLE_SETTINGS_BUDGETS = "SETTINGS_BUDGETS_DATA";
        public static final String TABLE_EXPENSES = "EXPENSE_DATA";
        public static final String TABLE_INCOME = "INCOME_DATA";
    public static final String TABLE_TRANSACTIONS = "TRANSACTION_DATA";
    public static final String TABLE_TIMEPERIODS = "TIMEPERIOD_DATA";
    //Data types
    private static final String PRIMARYKEY = " PRIMARY KEY AUTOINCREMENT";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String BOOLEAN_TYPE = " BOOLEAN";
    private static final String DATE_TYPE = " DATE";
    //Transactions
    private static final String COLUMN_type = "TYPE";
    private static final String COLUMN_category = "CATEGORY";
    private static final String COLUMN_profile = "PROFILE";
    private static final String COLUMN_budget = "BUDGET";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_uniqueID = "UID";
    private static final String COLUMN_parentID = "PARENTID";
    private static final String COLUMN_source = "SOURCE";
    private static final String COLUMN_description = "DESCRIPTION";
    private static final String COLUMN_value = "VALUE";
    private static final String COLUMN_staticValue = "STATICVALUE";
    private static final String COLUMN_children = "CHILDREN";
    private static final String COLUMN_when = "TIMEPERIOD";
    private static final String COLUMN_IPaid = "IPAID";
    private static final String COLUMN_paidBy = "PAIDBY";
    private static final String COLUMN_splitWith = "SPLITWITH";
    private static final String COLUMN_splitValue = "SPLITVALUE";
    private static final String COLUMN_split = "SPLIT";
    private static final String COLUMN_paidBack = "PAIDBACK";
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
    //Settings only
    private static final String COLUMN_period = "PERIOD";
    private static final String COLUMN_categorycolor = "CATEGORY_COLOR";
    private static final String COLUMN_profileSelected = "PROFILE_SEL";
    private static final String COLUMN_selected = "SELECTED";
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
    //private static final String CREATE_TABLE_SETTINGS_PROFILES = "CREATE TABLE IF NOT EXISTS " + TABLE_SETTINGS_PROFILES + "(" +
    //        COLUMN_uniqueID + INT_TYPE + "," +
    //        COLUMN_profile + TEXT_TYPE + "," +
    //        COLUMN_profileSelected + INT_TYPE + "," +
    //        COLUMN_startdate + DATE_TYPE + "," +
    //        COLUMN_enddate + DATE_TYPE + "," +
    //        COLUMN_period + TEXT_TYPE
    //        + ");";
    private static final String CREATE_TABLE_SETTINGS_BUDGETS = "CREATE TABLE IF NOT EXISTS " + TABLE_SETTINGS_BUDGETS + "(" +
            COLUMN_uniqueID + INT_TYPE + "," +
            COLUMN_budget + TEXT_TYPE + "," +
            COLUMN_selected + INT_TYPE + "," +
            COLUMN_startdate + DATE_TYPE + "," +
            COLUMN_enddate + DATE_TYPE + "," +
            COLUMN_period + TEXT_TYPE
            + ");";

    /*
    private static final String CREATE_TABLE_EXPENSES = "CREATE TABLE IF NOT EXISTS " + TABLE_EXPENSES + "(" + COLUMN_ID + INT_TYPE + PRIMARYKEY + "," +
            COLUMN_profile + INT_TYPE + "," +
            COLUMN_uniqueID + INT_TYPE + "," +
            COLUMN_parentID + INT_TYPE + "," +
            COLUMN_category + TEXT_TYPE + "," +
            COLUMN_source + TEXT_TYPE + "," +
            COLUMN_description + TEXT_TYPE  + "," +
            COLUMN_value + DOUBLE_TYPE  + "," +
            COLUMN_staticValue + BOOLEAN_TYPE  + "," +
            COLUMN_IPaid + BOOLEAN_TYPE + "," +
            COLUMN_splitWith + TEXT_TYPE + "," +
            COLUMN_splitValue + DOUBLE_TYPE  + "," +
            COLUMN_paidBack + TEXT_TYPE + "," +
            COLUMN_children + TEXT_TYPE + "," +
            COLUMN_when + INT_TYPE
            + ");";

    private static final String CREATE_TABLE_INCOME = "CREATE TABLE IF NOT EXISTS " + TABLE_INCOME + "(" + COLUMN_ID + INT_TYPE + PRIMARYKEY + "," +
            COLUMN_profile + INT_TYPE + "," +
            COLUMN_uniqueID + INT_TYPE + "," +
            COLUMN_parentID + INT_TYPE + "," +
            COLUMN_category + TEXT_TYPE + "," +
            COLUMN_source + TEXT_TYPE + "," +
            COLUMN_description + TEXT_TYPE  + "," +
            COLUMN_value + DOUBLE_TYPE  + "," +
            COLUMN_staticValue + BOOLEAN_TYPE  + "," +
            COLUMN_children + TEXT_TYPE + "," +
            COLUMN_when + INT_TYPE
            + ");";
            */
    private static final String CREATE_TABLE_TRANSACTIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSACTIONS + "(" +
            COLUMN_type + INT_TYPE + "," +
            COLUMN_budget + INT_TYPE + "," +
            COLUMN_uniqueID + INT_TYPE + "," +
            COLUMN_parentID + INT_TYPE + "," +
            COLUMN_category + TEXT_TYPE + "," +
            COLUMN_source + TEXT_TYPE + "," +
            COLUMN_description + TEXT_TYPE  + "," +
            COLUMN_value + DOUBLE_TYPE  + "," +
            COLUMN_paidBy + TEXT_TYPE + "," +
            COLUMN_split + TEXT_TYPE + "," +
            COLUMN_paidBack + TEXT_TYPE + "," +
            COLUMN_children + TEXT_TYPE + "," +
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
    private static final String DROP_TABLE_SETTINGS_BUDGETS = "DROP TABLE IF EXISTS " + TABLE_SETTINGS_BUDGETS;
        private static final String DROP_TABLE_EXPENSES = "DROP TABLE IF EXISTS " + TABLE_EXPENSES;
        private static final String DROP_TABLE_INCOME = "DROP TABLE IF EXISTS " + TABLE_INCOME;
    private static final String DROP_TABLE_TRANSACTIONS = "DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS;
    private static final String DROP_TABLE_TIMEPERIODS = "DROP TABLE IF EXISTS " + TABLE_TIMEPERIODS;

    //Upgrade statements
    //private static final String UPGRADE_TABLE_SETTINGS_CATEGORIES_1_2 = "ALTER TABLE " + TABLE_SETTINGS_CATEGORIES + " ADD COLUMN " + COLUMN_splitWith2 + TEXT_TYPE;
    //Add column COLUMN_splitWith2, copy value from COLUMN_splitWith into COLUMN_splitWith2
    private static final String UPGRADE_2_3 = "ALTER TABLE " + TABLE_SETTINGS_OTHERPEOPLE +
            " ADD COLUMN " + COLUMN_splitWith2 + TEXT_TYPE +
            STATEMENT_DELIMITER +
            "UPDATE " + TABLE_SETTINGS_OTHERPEOPLE + " SET " + COLUMN_splitWith2 + " = " + COLUMN_splitWith;

    private static final String UPGRADE_3_4 = "ALTER TABLE " + TABLE_EXPENSES +
            " ADD COLUMN " + COLUMN_children + TEXT_TYPE +
            STATEMENT_DELIMITER +
            "ALTER TABLE " + TABLE_INCOME +
            " ADD COLUMN " + COLUMN_children + TEXT_TYPE;

    private static final String UPGRADE_4_5 = "ALTER TABLE " + TABLE_SETTINGS_PROFILES +
            " ADD COLUMN " + COLUMN_period + TEXT_TYPE +
            STATEMENT_DELIMITER +
            "UPDATE " + TABLE_SETTINGS_PROFILES + " SET " + COLUMN_period + " = '" + (new Period(0,1,0,0,0,0,0,0)).toString() + "'" ; //Default value monthly period

    private static final String UPGRADE_5_6 =
            //Create transactions table
            CREATE_TABLE_TRANSACTIONS +
            STATEMENT_DELIMITER +
                    //Copy data from table_expenses into transactions table
            "INSERT INTO " + TABLE_TRANSACTIONS + "(" +
                COLUMN_type + "," +
                COLUMN_profile + "," +
                COLUMN_uniqueID + "," +
                COLUMN_parentID + "," +
                COLUMN_category + "," +
                COLUMN_source + "," +
                COLUMN_description + "," +
                COLUMN_value + "," +
                COLUMN_staticValue + "," +
                COLUMN_IPaid + "," +
                COLUMN_splitWith + "," +
                COLUMN_splitValue + "," +
                COLUMN_paidBack + "," +
                COLUMN_children + "," +
                COLUMN_when +
                ") SELECT NULL," +
                COLUMN_profile + "," +
                COLUMN_uniqueID + "," +
                COLUMN_parentID + "," +
                COLUMN_category + "," +
                COLUMN_source + "," +
                COLUMN_description + "," +
                COLUMN_value + "," +
                COLUMN_staticValue + "," +
                COLUMN_IPaid + "," +
                COLUMN_splitWith + "," +
                COLUMN_splitValue + "," +
                COLUMN_paidBack + "," +
                COLUMN_children + "," +
                COLUMN_when +
                " FROM "  + TABLE_EXPENSES + STATEMENT_DELIMITER +
            "UPDATE " + TABLE_TRANSACTIONS + " SET " + COLUMN_type + "='0' WHERE " + COLUMN_type + " IS NULL" + STATEMENT_DELIMITER +
                    //Copy data from table_income into transactions table
            "INSERT INTO " + TABLE_TRANSACTIONS + "(" +
                COLUMN_type + "," +
                COLUMN_profile + "," +
                COLUMN_uniqueID + "," +
                COLUMN_parentID + "," +
                COLUMN_category + "," +
                COLUMN_source + "," +
                COLUMN_description + "," +
                COLUMN_value + "," +
                COLUMN_staticValue + "," +
                COLUMN_IPaid + "," +
                COLUMN_splitWith + "," +
                COLUMN_splitValue + "," +
                COLUMN_paidBack + "," +
                COLUMN_children + "," +
                COLUMN_when +
                ") SELECT NULL," +
                COLUMN_profile + "," +
                COLUMN_uniqueID + "," +
                COLUMN_parentID + "," +
                COLUMN_category + "," +
                COLUMN_source + "," +
                COLUMN_description + "," +
                COLUMN_value + "," +
                COLUMN_staticValue +
                ",NULL,NULL,NULL,NULL," +
                COLUMN_children + "," +
                COLUMN_when +
                " FROM "  + TABLE_INCOME + STATEMENT_DELIMITER +
                    //Transactions table must have a type column
            "UPDATE " + TABLE_TRANSACTIONS + " SET " + COLUMN_type + "='1' WHERE " + COLUMN_type + " IS NULL" + STATEMENT_DELIMITER +
                    //Drop expenses and income tables
            DROP_TABLE_EXPENSES + STATEMENT_DELIMITER + DROP_TABLE_INCOME;

    private static final String UPGRADE_6_7 =
            CREATE_TABLE_SETTINGS_BUDGETS +
                    STATEMENT_DELIMITER +
                    //Create budget table - very similar to profile table
                    "INSERT INTO " + TABLE_SETTINGS_BUDGETS +
                    " SELECT " +
                        COLUMN_uniqueID + "," +
                        COLUMN_profile + "," +
                        COLUMN_profileSelected + "," +
                        COLUMN_startdate + "," +
                        COLUMN_enddate + "," +
                        COLUMN_period +
                    " FROM "  + TABLE_SETTINGS_PROFILES +
                    STATEMENT_DELIMITER +

                    //Drop old profiles table
                    DROP_TABLE_SETTINGS_PROFILES + STATEMENT_DELIMITER +


                    //Reformat data from TABLE_TRANSACTIONS by rebuilding it

                    //Rename loaded table
                    "ALTER TABLE " + TABLE_TRANSACTIONS + " RENAME TO " + "temp_transactions" + STATEMENT_DELIMITER +

                    //Create correctly formatted table
                    CREATE_TABLE_TRANSACTIONS + STATEMENT_DELIMITER +

                    //Copy data from temp to TABLE_TRANSACTIONS
                    "INSERT INTO " + TABLE_TRANSACTIONS + "(" +
                        COLUMN_type + "," +
                        COLUMN_budget + "," +
                        COLUMN_uniqueID + "," +
                        COLUMN_parentID + "," +
                        COLUMN_category + "," +
                        COLUMN_source + "," +
                        COLUMN_description  + "," +
                        COLUMN_value  + "," +
                        COLUMN_paidBy + "," +
                        COLUMN_split + "," +
                        COLUMN_paidBack + "," +
                        COLUMN_children + "," +
                        COLUMN_when +
                    ") SELECT " +
                        COLUMN_type + "," +
                        COLUMN_profile + "," +
                        COLUMN_uniqueID + "," +
                        COLUMN_parentID + "," +
                        COLUMN_category + "," +
                        COLUMN_source + "," +
                        COLUMN_description  + "," +
                        COLUMN_value  + "," +
                        //COLUMN_IPaid becomes COLUMN_paidBy
                            "CASE WHEN " + COLUMN_IPaid + " = 0 THEN " + COLUMN_splitWith + " ELSE (SELECT '"+Helper.getString(R.string.format_me)+"') END" + "," +
                        //COLUMN_split is made from COLUMN_value, COLUMN_splitValue and COLUMN_splitWith
                            "(SELECT '"+Helper.getString(R.string.format_me)+"') || ':' || (" + COLUMN_value + "-" + COLUMN_splitValue + ") || '|' || " + COLUMN_splitWith + " || ':' || " + COLUMN_splitValue + "," +
                        COLUMN_paidBack + "," +
                        COLUMN_children + "," +
                        COLUMN_when +
                    " FROM temp_transactions" + STATEMENT_DELIMITER +

                    //Drop the temp table
                    "DROP TABLE temp_transactions";



                    /*
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_profile     + ") SELECT " + COLUMN_profile     + " FROM " + TABLE_EXPENSES + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_uniqueID    + ") SELECT " + COLUMN_uniqueID    + " FROM " + TABLE_EXPENSES + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_parentID    + ") SELECT " + COLUMN_parentID    + " FROM " + TABLE_EXPENSES + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_category    + ") SELECT " + COLUMN_category    + " FROM " + TABLE_EXPENSES + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_source  + ") SELECT " + COLUMN_source  + " FROM " + TABLE_EXPENSES + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_description + ") SELECT " + COLUMN_description + " FROM " + TABLE_EXPENSES + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_value       + ") SELECT " + COLUMN_value       + " FROM " + TABLE_EXPENSES + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_staticValue + ") SELECT " + COLUMN_staticValue + " FROM " + TABLE_EXPENSES + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_IPaid       + ") SELECT " + COLUMN_IPaid       + " FROM " + TABLE_EXPENSES + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_splitWith   + ") SELECT " + COLUMN_splitWith   + " FROM " + TABLE_EXPENSES + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_splitValue  + ") SELECT " + COLUMN_splitValue  + " FROM " + TABLE_EXPENSES + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_paidBack    + ") SELECT " + COLUMN_paidBack    + " FROM " + TABLE_EXPENSES + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_children    + ") SELECT " + COLUMN_children    + " FROM " + TABLE_EXPENSES + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_when        + ") SELECT " + COLUMN_when        + " FROM " + TABLE_EXPENSES + STATEMENT_DELIMITER +
                    "UPDATE "      + TABLE_TRANSACTIONS + " SET " + COLUMN_type     + "='0' WHERE " + COLUMN_type        + " IS NULL"                + STATEMENT_DELIMITER +

                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_profile     + ") SELECT " + COLUMN_profile     + " FROM " + TABLE_INCOME + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_uniqueID    + ") SELECT " + COLUMN_uniqueID    + " FROM " + TABLE_INCOME + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_parentID    + ") SELECT " + COLUMN_parentID    + " FROM " + TABLE_INCOME + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_category    + ") SELECT " + COLUMN_category    + " FROM " + TABLE_INCOME + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_source  + ") SELECT " + COLUMN_source  + " FROM " + TABLE_INCOME + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_description + ") SELECT " + COLUMN_description + " FROM " + TABLE_INCOME + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_value       + ") SELECT " + COLUMN_value       + " FROM " + TABLE_INCOME + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_staticValue + ") SELECT " + COLUMN_staticValue + " FROM " + TABLE_INCOME + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_children    + ") SELECT " + COLUMN_children    + " FROM " + TABLE_INCOME + STATEMENT_DELIMITER +
                    "INSERT INTO " + TABLE_TRANSACTIONS + " (" + COLUMN_when        + ") SELECT " + COLUMN_when        + " FROM " + TABLE_INCOME + STATEMENT_DELIMITER +
                    "UPDATE "      + TABLE_TRANSACTIONS + " SET " + COLUMN_type     + "='1' WHERE " + COLUMN_type      + " IS NULL";//             + STATEMENT_DELIMITER +
                    //DROP_TABLE_EXPENSES                                                                                                        + STATEMENT_DELIMITER +
                    //DROP_TABLE_INCOME;
                    */

    private DatabaseManager(){ super(App.GetContext(), FILE_NAME, null, DATABASE_VERSION); }
    static DatabaseManager getInstance(){ return instance; }
    public void initialize() {
        EXPORT_DIRECTORY = new File(Environment.getExternalStorageDirectory() + "/" + Helper.getString(R.string.app_name_nospace) + "/");

        EXPORT_DIRECTORY_BACKUP = new File(EXPORT_DIRECTORY.getAbsolutePath() + "/backup/");
        BACKUP_FILENAME = "data_backup.db"; //TODO Read from strings file
        EXPORT_BACKUP = new File(EXPORT_DIRECTORY_BACKUP, BACKUP_FILENAME);

        //Triggers onUpgrade (if necessary)
        database = getReadableDatabase();

        tryCreateDatabase();
    }

    //Database constructor and updating
    @Override
    public void onCreate(SQLiteDatabase db) {
        tryCreateDatabase(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Helper.Print(App.GetContext(), "OnUpgrade (" + oldVersion + "->" + newVersion + ")");
        //Helper.PrintLong(App.GetContext(), UPGRADE_6_7);

        try {
            ContentValues cv = new ContentValues();

            switch(oldVersion){
                case 1: //To version 2
                    cv.clear();
                    //ProfileManager.Print(activityContext, "Version 1 Upgrade not supported");
                case 2: //To version 3 (Not neccessary to upgrade)
                    //SQLExecuteMultiple(db, UPGRADE_2_3);
                    //ProfileManager.Print("Upgrade from Ver.2 to Ver.3");
                case 3: //To version 4
                    SQLExecuteMultiple(db, UPGRADE_3_4);
                    Helper.Print(App.GetContext(), "Upgrade from Ver.3 to Ver.4");
                case 4: //To version 5
                    SQLExecuteMultiple(db, UPGRADE_4_5);
                    Helper.Print(App.GetContext(), "Upgrade from Ver.4 to Ver.5");
                case 5: //To version 6
                    SQLExecuteMultiple(db, UPGRADE_5_6);
                    Helper.Print(App.GetContext(), "Upgrade from Ver.5 to Ver.6");
                case 6: //To version 7
                    SQLExecuteMultiple(db, UPGRADE_6_7);
                    Helper.Print(App.GetContext(), "Upgrade from Ver.6 to Ver.7");
                case 7: //To version 8
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
            //ProfileManager.PrintLong(activityContext, ex.getMessage());
            //ProfileManager.Print(activityContext, "Error upgrading database");
        }
    }

    //SQL
    public void SQLExecuteMultiple(SQLiteDatabase db, String inputStatement){
        String[] statements = inputStatement.split(STATEMENT_DELIMITER);

        for(String statement : statements){
            try { db.execSQL(statement); }
            catch (Exception ex) { Helper.PrintLong(App.GetContext(), ex.getMessage()); break; }
        }
    }

    //Task Execution
    public void runDBTask(final CallBack BackgroundAction) { runDBTask(BackgroundAction, null, null); }
    public void runDBTask(CallBack BackgroundAction, final CallBack[] PreActions, final CallBack[] PostActions){
        AsyncTask<Object, String, String> task = new AsyncTask<Object, String, String>() {
            @Override protected void onPreExecute() {
                try {
                    for (CallBack action : PreActions) {
                        action.call();
                    }
                } catch (Exception ex){}//Not much we can do at this point
            }

            @Override protected String doInBackground(Object... params) {
                try {
                    CallBack action = (CallBack)params[0];
                    action.call();
                    return null;
                } catch (Exception ex){ return ex.toString(); }

            }

            @Override protected void onProgressUpdate(String... text) {}

            @Override protected void onPostExecute(String result) {
                //Failure on doInBackground
                if (result != null) { Helper.PrintUserLong(App.GetContext(), "Database Operation Failure: " + result); }

                try {
                    for (CallBack action : PostActions) {
                        action.call();
                    }
                } catch (Exception ex){}//Not much we can do at this point
            }
        };

        //Start process
        task.execute(BackgroundAction);
    }

    //Database directories & Versioning
    public String getExportDirectory(){ return EXPORT_DIRECTORY.getAbsolutePath(); }
    public ArrayList<File> getImportableDatabases(){
        try {
            ArrayList<File> DatabaseFiles = new ArrayList<>();

            if (Helper.isStoragePermissionGranted()){
                File data = new File(Environment.getExternalStorageDirectory() + "/" + Helper.getString(R.string.app_name_nospace) + "/");

                if (data.canRead()) {
                    for (File file : data.listFiles()) {
                        if(file.getName().endsWith(".db")){
                            DatabaseFiles.add(file);
                        }
                    }

                    return DatabaseFiles;
                }
                else {
                    //ProfileManager.Print(activityContext, "Cannot Read Database Import Directory");
                }
            }
            else {
                //ProfileManager.Print(activityContext, "Storage permission not granted, cannot import databases");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public ArrayList<String> getImportableDatabasesString(){
        try {
            File data = new File(Environment.getExternalStorageDirectory() + "/" + Helper.getString(R.string.app_name_nospace) + "/");
            ArrayList<String> DatabaseFiles = new ArrayList<>();

            if (Helper.isStoragePermissionGranted()){
                if (data.canRead()) {
                    for (File file : data.listFiles()) {
                        if(file.getName().endsWith(".db")){
                            DatabaseFiles.add(file.getName().replace(".db", ""));
                        }
                    }

                    return DatabaseFiles;
                }
                else {
                    //ProfileManager.Print(activityContext, "Cannot Read Database Import Directory");
                }
            }
            else {
                //ProfileManager.Print(activityContext, "Storage permission not granted, cannot import databases");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public File getDatabaseByPath(String path){
        if (Helper.isStoragePermissionGranted()){
            File data = new File(Environment.getExternalStorageDirectory() + "/" + Helper.getString(R.string.app_name_nospace) + "/");

            if (data.canRead()) {
                for (File file : data.listFiles()) {
                    if(file.getName().endsWith(".db")){
                        if (file.getAbsolutePath().equals(path)) { return file; }
                    }
                }
            }
            else {
                //ProfileManager.Print(activityContext, "Cannot Read Database Import Directory");
            }
        }
        else {
            //ProfileManager.Print(activityContext, "Storage permission not granted, cannot import databases");
        }

        return null;
    }
    public boolean doesBackupExist(){ return EXPORT_BACKUP != null && EXPORT_BACKUP.exists(); }

    public int getVersion() { return DATABASE_VERSION; }


    //Database properties
    public boolean isDatabaseEmpty(){

        return (!isTableExists(TABLE_SETTINGS_PROFILES, false) ||
                !isTableExists(TABLE_SETTINGS_OTHERPEOPLE, false) ||
                !isTableExists(TABLE_SETTINGS_CATEGORIES, false) ||
                !isTableExists(TABLE_TRANSACTIONS, false) ||
                !isTableExists(TABLE_TIMEPERIODS, false) ||

                !isTableEmpty(TABLE_SETTINGS_PROFILES) ||
                !isTableEmpty(TABLE_SETTINGS_OTHERPEOPLE) ||
                !isTableEmpty(TABLE_SETTINGS_CATEGORIES) ||
                !isTableEmpty(TABLE_TRANSACTIONS) ||
                !isTableEmpty(TABLE_TIMEPERIODS));
    }
    public boolean isTableExists(String tableName, boolean openDb) {
        /*
        if(openDb) {
            if(database == null || !database.isOpen()) {
                database = getReadableDatabase();
            }

            if(!database.isReadOnly()) {
                //database.close();
                database = getReadableDatabase();
            }
        }

        database = getReadableDatabase();

        if (database != null) {
            Cursor cursor = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.close();
                    return true;
                }
                else { cursor.close(); }
            }
        }
        return false;
        */
        if (database != null){
            Cursor c = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'", null);
            if (c != null){
                if (c.getCount() > 0){
                    c.close();
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    public boolean isTableEmpty(String tableName){
        if (isTableExists(tableName, false)) {
            database = getReadableDatabase();
            long cnt = DatabaseUtils.queryNumEntries(database, tableName);

            return cnt == 0;
        }
        return true;
    }


    //Creating and deleting database & tables
    public void tryCreateDatabase(){ tryCreateDatabase(getWritableDatabase()); }
    public void tryCreateDatabase(SQLiteDatabase db){
        //Try to create database
        try {
            //Try create settings tables
            try {
                //if (!isTableExists(TABLE_SETTINGS_CATEGORIES, false)) {
                    db.execSQL(CREATE_TABLE_SETTINGS_CATEGORIES);
                    //Log.e("DATABASE", "TryCreateDatabase:Categories Table");
                //}
                //else { Log.e("DATABASE", "Categories Table Exists"); }
                //if (!isTableExists(TABLE_SETTINGS_OTHERPEOPLE, false)) {
                    db.execSQL(CREATE_TABLE_SETTINGS_OTHERPEOPLE);
                //    Log.e("DATABASE", "TryCreateDatabase:people Table");
                //}
                //else { Log.e("DATABASE", "People Table Exists"); }
                //if (!isTableExists(TABLE_SETTINGS_PROFILES, false)) {
                    db.execSQL(CREATE_TABLE_SETTINGS_BUDGETS);
                //    Log.e("DATABASE", "TryCreateDatabase:profiles Table");
                //}
                //else { Log.e("DATABASE", "Profiles Table Exists"); }
                //ProfileManager.Print("Settings tables created");
            } catch (SQLException ex){
                //ProfileManager.Print(activityContext, "Error creating Settings table");
                ex.printStackTrace();
            }
            //Try create transactions table
            try {
                //if (!isTableExists(TABLE_TRANSACTIONS, false)) {
                    db.execSQL(CREATE_TABLE_TRANSACTIONS);
                    //ProfileManager.Print("Transactions table created");
                //}
            } catch (SQLException ex){
                //ProfileManager.Print(activityContext, "Error creating transactions table");
                ex.printStackTrace();
            }
            //Try create income table
            //try {
            //    if (!isTableExists(TABLE_INCOME, false)) {
            //        db.execSQL(CREATE_TABLE_INCOME);
            //        //ProfileManager.Print("Income table created");
            //    }
            //} catch (SQLException ex){
            //    ProfileManager.Print("Error creating Income table");
            //    ex.printStackTrace();
            //}
            //Try create timeperiod table
            try {
                //if (!isTableExists(TABLE_TIMEPERIODS, false)) {
                    db.execSQL(CREATE_TABLE_TIMEPERIOD);
                    //ProfileManager.Print("TimePeriod table created");
                //}
            } catch (SQLException ex){
                //ProfileManager.Print(activityContext, "Error creating TimePeriod table");
                ex.printStackTrace();
            }

            //database = getWritableDatabase();
            //ProfileManager.Print("Database created");
        }
        catch(SQLException ex){
            //ProfileManager.Print(activityContext, "Error creating database");
            ex.printStackTrace();
        }
    }

    private void dropAndRecreateAllTables() { runDBTask( new CallBack() { @Override public void call() { _dropAndRecreateAllTables(); } } ); }
    private void _dropAndRecreateAllTables(){
        _dropTable(TABLE_SETTINGS_CATEGORIES);
        _dropTable(TABLE_SETTINGS_OTHERPEOPLE);
        _dropTable(TABLE_SETTINGS_PROFILES);
        _dropTable(TABLE_SETTINGS_BUDGETS);
        _dropTable(TABLE_TRANSACTIONS);
        _dropTable(TABLE_TIMEPERIODS);
        //Legacy
        _dropTable(TABLE_EXPENSES);
        _dropTable(TABLE_INCOME);


        //Recreate
        tryCreateDatabase(database);
    }

    private void dropTable(final String tableName) { runDBTask( new CallBack() { @Override public void call() { _dropTable(tableName); } } ); }
    private void _dropTable(String tableName){
        SQLExecuteMultiple(database, "DROP TABLE IF EXISTS " + tableName);
    }


    public void deleteAllTableContent() { runDBTask( new CallBack() { @Override public void call() { _deleteAllTableContent(); } } ); }
    private void _deleteAllTableContent(){
        _deleteTableContent(TABLE_SETTINGS_CATEGORIES);
        _deleteTableContent(TABLE_SETTINGS_OTHERPEOPLE);
        _deleteTableContent(TABLE_SETTINGS_PROFILES);
        _deleteTableContent(TABLE_SETTINGS_BUDGETS);
        _deleteTableContent(TABLE_TRANSACTIONS);
        _deleteTableContent(TABLE_TIMEPERIODS);
        //Legacy
        _deleteTableContent(TABLE_EXPENSES);
        _deleteTableContent(TABLE_INCOME);
    }

    public void deleteTableContent(final String tableName) { runDBTask( new CallBack() { @Override public void call() { _deleteTableContent(tableName); } } ); }
    private void _deleteTableContent(String tableName){
        if (isTableExists(tableName, true)) {
            database.delete(tableName, null, null);
        }
    }




    //Import and Export
    public void exportDatabase(final String name) { runDBTask( new CallBack() { @Override public void call() { _exportDatabase(name); } } ); }
    private void _exportDatabase(String name) { _exportDatabase(name, EXPORT_DIRECTORY); }
    private void _exportDatabase(String name, File destination) {

        try {
            File datadir = Environment.getDataDirectory();

            //Manage database name if *.db
            String[] sp1 = FILE_NAME.split("\\.");
            String filename = sp1[0];

            if (Helper.isStoragePermissionGranted()){
                //Create backup directory if it does not exist
                destination.mkdirs();

                if (destination.canWrite()) {

                    //Database paths
                    String currentDBPath = "/data/" + App.GetPackageName() + "/databases/" + filename + ".db";
                    //String backupFilename = filename + "_export_" + (new LocalDate()).toString(ProfileManager.simpleDateFormatSaving) + ".db";
                    String backupFilename = name.replace(".db", "").concat(".db");
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
                //ProfileManager.Print(activityContext, "Storage permission not granted, cannot export database");
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void importDatabase(final File importFile, final boolean backup) { runDBTask( new CallBack() { @Override public void call() { _importDatabase(importFile, backup); } }); }
    //private void _importDatabase(File importFile){ _importDatabase(importFile, false); }
    private void _importDatabase(File importFile, boolean backup){

        try {
            //Database paths
            String currentDBPath = "/data/" + App.GetPackageName() + "/databases/";
            File currentDB = new File(Environment.getDataDirectory(), currentDBPath + FILE_NAME);

            if (Helper.isStoragePermissionGranted()){
                if (currentDB.canWrite() || importFile.canRead()) {
                    if (currentDB.exists()) {

                        //Backup currentDB (Secret)
                        if (backup) {
                            _exportDatabase(BACKUP_FILENAME, EXPORT_DIRECTORY_BACKUP);
                        }

                        //Delete current database
                        _dropAndRecreateAllTables();
                        //Create new empty database
                        tryCreateDatabase();

                        //Transfer database from import to local directory (ASyncTask)
                        FileUtils.copyFile(importFile, currentDB);


                        //Force upgrade even though DATABASE_VERSION has not changed because the database we are importing may be older than DATABASE_VERSION (WHY DOES THIS WORK?)
                        //Call getWritableDatabase() to trigger onUpgrade if it is necessary
                        //getWritableDatabase();
                        //Force call onUpgrade
                        final int oldVersion = SQLiteDatabase.openDatabase(importFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE).getVersion();
                        onUpgrade(getWritableDatabase(), oldVersion, DATABASE_VERSION);
                        //getWritableDatabase().close();


                        //Load new database
                        //(new DatabaseBackgroundHelper()).execute(8); //loadSettings();
                        //(new DatabaseBackgroundHelper()).execute(9); //loadTransactions();
                        //_loadSettings();
                        //_loadTransactions();

                        //ProfileManager.PrintUser(activityContext, importFile.getName() + " imported");
                    }
                }
            }
            else { //No UI thread work in a background thread!
                //Helper.PrintUser(App.GetContext(), "Storage permission not granted, cannot import database");
            }

        } catch (Exception e) {
            //ProfileManager.Print(activityContext, "Error importing database");
            //ProfileManager.PrintLong(activityContext, e.getMessage());
        }

    }




    //Helpers
    //Date conversion for loading
    public static LocalDate ConvertDateFromString(String str){
        if (str != null && str.length() != 0) {
            try {
                DateTimeFormatter dtf = DateTimeFormat.forPattern(Helper.getString(R.string.date_format_saving));
                return dtf.parseLocalDate(str);
            }
            catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }




    //Object Specific Operations
    //Insertion
    public void insertSetting(final Category category, final boolean tryUpdate) { runDBTask( new CallBack() { @Override public void call() { _insertSetting(category, tryUpdate); } } ); }
    private long _insertSetting(Category category, boolean tryUpdate)
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

    public void insertSetting(final String person, final boolean tryUpdate) { runDBTask( new CallBack() { @Override public void call() { _insertSetting(person, tryUpdate); } } ); }
    private long _insertSetting(String person, boolean tryUpdate)
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

    public void insertSetting(final Budget budget, final boolean tryUpdate) { runDBTask( new CallBack() { @Override public void call() { _insertSetting(budget, tryUpdate); } } ); }
    private long _insertSetting(Budget budget, boolean tryUpdate)
    {
        database = getWritableDatabase();

        if (budget != null) {
            if (database != null) {
                if (isTableExists(TABLE_SETTINGS_BUDGETS, false)) {
                    contentValues_tr = new ContentValues();

                    //Fill out object variables
                    contentValues_tr.put(COLUMN_uniqueID, budget.GetID());
                    contentValues_tr.put(COLUMN_budget, budget.GetName());

                    //Selection
                    if (budget.GetSelected()) {
                        contentValues_tr.put(COLUMN_selected, 1);
                    } else {
                        contentValues_tr.put(COLUMN_selected, 0);
                    }

                    if (budget.GetStartDate() != null) {
                        contentValues_tr.put(COLUMN_startdate, budget.GetStartDate().toString(Helper.getString(R.string.date_format_saving)));
                    }
                    if (budget.GetEndDate() != null) {
                        contentValues_tr.put(COLUMN_enddate, budget.GetEndDate().toString(Helper.getString(R.string.date_format_saving)));
                    }
                    if (budget.GetPeriod() != null) {
                        contentValues_tr.put(COLUMN_period, budget.GetPeriod().toString());
                    }

                    //Insert/update row and return result
                    long result = 0;
                    if (tryUpdate) {
                        result = database.update(TABLE_SETTINGS_BUDGETS, contentValues_tr, COLUMN_uniqueID + "=" + budget.GetID(), null);
                    }
                    if (result == 0) { result = database.insert(TABLE_SETTINGS_BUDGETS, null, contentValues_tr); }

                    return result;
                }
                //ProfileManager.Print(activityContext, "Table Does Not Exist");
                return -1;
            }
            //ProfileManager.Print(activityContext, "Database is null");
            return -1;
        } else { return -1; }//ProfileManager.Print(activityContext, "Budget is null"); return -1; }
    }

    public void insert(final Budget budget, final new_Transaction transaction, final boolean tryupdate) { runDBTask(new CallBack() { @Override public void call() { _insert(budget, transaction, tryupdate); } } ); }
    private long _insert(Budget budget, new_Transaction transaction, boolean tryupdate)
    {
        database = getWritableDatabase();

        if (database != null && isTableExists(TABLE_TRANSACTIONS, false)) {
            contentValues_tr = new ContentValues();

            //Timeperiod
            long tp_id = _insert(transaction.GetID(), transaction.GetTimePeriod(), tryupdate);

            //Fill out row
            contentValues_tr.put(COLUMN_type, transaction.GetType().ordinal());
            contentValues_tr.put(COLUMN_budget, budget.GetID());
            contentValues_tr.put(COLUMN_uniqueID, transaction.GetID());
            contentValues_tr.put(COLUMN_parentID, transaction.GetParentID());
            contentValues_tr.put(COLUMN_category, transaction.GetCategory());
            contentValues_tr.put(COLUMN_source, transaction.GetSource());
            contentValues_tr.put(COLUMN_description, transaction.GetDescription());
            contentValues_tr.put(COLUMN_value, transaction.GetValue());
            //contentValues_tr.put(COLUMN_staticValue, transaction.GetStatic()); //New transactions no longer use the static field
            if (!tryupdate) { contentValues_tr.put(COLUMN_when, tp_id); }

            contentValues_tr.put(COLUMN_paidBy, transaction.GetPaidBy());
            contentValues_tr.put(COLUMN_split, transaction.GetSplitArrayString());
            contentValues_tr.put(COLUMN_paidBack, (transaction.GetPaidBack() != null ? transaction.GetPaidBack().toString(Helper.getString(R.string.date_format)) : "") );
            contentValues_tr.put(COLUMN_children, transaction.GetChildrenFormatted());

            //Insert/update row and return result
            long result = 0;
            if (tryupdate){ result = database.update(TABLE_TRANSACTIONS, contentValues_tr, COLUMN_uniqueID + "=" + transaction.GetID(), null); }
            if (result == 0) { result = database.insert(TABLE_TRANSACTIONS, null, contentValues_tr); }

            return result;
        }
        else{
            return -1;
        }
    }

    public void insert(final int UID, final TimePeriod tp, final boolean tryupdate) { runDBTask( new CallBack() { @Override public void call() { _insert(UID, tp, tryupdate); } } ); }
    private long _insert(int UID, TimePeriod tp, Boolean tryupdate)
    {
        database = getWritableDatabase();

        if (database != null&& isTableExists(TABLE_TIMEPERIODS, false)) {
            contentValues_tp = new ContentValues();

            if (tp != null) {
                contentValues_tp.put(COLUMN_tp_parent, UID);
                contentValues_tp.put(COLUMN_tp_date, (tp.GetDate() != null ? tp.GetDate().toString(Helper.getString(R.string.date_format_saving)) : ""));
                //contentValues_tp.put(COLUMN_tp_firstOcc, (tp.GetFirstOccurrence() != null ? tp.GetFirstOccurrence().toString(ProfileManager.simpleDateFormatSaving) : ""));
                contentValues_tp.put(COLUMN_tp_repeatFreq, (tp.GetRepeatFrequency() != null ? tp.GetRepeatFrequency().ordinal() : 0));
                contentValues_tp.put(COLUMN_tp_repeatUntil, (tp.GetRepeatUntil() != null ? tp.GetRepeatUntil().ordinal() : 0));
                contentValues_tp.put(COLUMN_tp_repeatNTimes, tp.GetRepeatANumberOfTimes());
                contentValues_tp.put(COLUMN_tp_repeatUntilDate, (tp.GetRepeatUntilDate() != null ? tp.GetRepeatUntilDate().toString(Helper.getString(R.string.date_format_saving)) : ""));
                contentValues_tp.put(COLUMN_tp_repeatEveryN, tp.GetRepeatEveryN());
                contentValues_tp.put(COLUMN_tp_repeatDayOfWeek, tp.GetRepeatDayOfWeekBinary());
                contentValues_tp.put(COLUMN_tp_repeatDayOfMonth, tp.GetRepeatDayOfMonth());
                contentValues_tp.put(COLUMN_tp_dateOfYear, (tp.GetDateOfYear() != null ? tp.GetDateOfYear().toString(Helper.getString(R.string.date_format_saving)) : ""));
                contentValues_tp.put(COLUMN_tp_blacklistDates, tp.GetBlacklistDatesSaving());
            }

            //Insert/update row and return result
            long result = 0;
            if (contentValues_tp != null && contentValues_tp.size() > 0) {
                if (tryupdate) { result = database.update(TABLE_TIMEPERIODS, contentValues_tp, COLUMN_tp_parent + "=" + UID, null); }
                if (result == 0) { result = database.insert(TABLE_TIMEPERIODS, null, contentValues_tp); }
            }

            return result;
        }
        return -1;
    }

    private TimePeriod _queryTimeperiod(int id)
    {
        database = getWritableDatabase();

        Cursor c = database.query(TABLE_TIMEPERIODS, null, COLUMN_ID + "=" + id, null, null, null, null);

        //Loop through data
        while (c.moveToNext()) {
            //Create timeperiod object
            TimePeriod tp = new TimePeriod();

            //COLUMN_tp_date + DATE_TYPE + "," +
            tp.SetDate(ConvertDateFromString(c.getString(c.getColumnIndex(COLUMN_tp_date))));
            //COLUMN_tp_repeatFreq + INT_TYPE + "," +
            tp.SetRepeatFrequency(tp.GetRepeatFrequencyFromIndex(c.getInt(c.getColumnIndex(COLUMN_tp_repeatFreq))));
            //COLUMN_tp_repeatUntil + INT_TYPE + "," +
            tp.SetRepeatUntil(tp.GetRepeatUntilFromIndex(c.getInt(c.getColumnIndex(COLUMN_tp_repeatUntil))));
            //COLUMN_tp_repeatNTimes + INT_TYPE + "," +
            tp.SetRepeatANumberOfTimes(c.getInt(c.getColumnIndex(COLUMN_tp_repeatNTimes)));
            //COLUMN_tp_repeatUntilDate + DATE_TYPE + "," +
            tp.SetRepeatUntilDate(ConvertDateFromString(c.getString(c.getColumnIndex(COLUMN_tp_repeatUntilDate))));
            //COLUMN_tp_repeatEveryN + INT_TYPE + "," +
            tp.SetRepeatEveryN(c.getInt(c.getColumnIndex(COLUMN_tp_repeatEveryN)));
            //COLUMN_tp_repeatDayOfWeek + TEXT_TYPE + "," +
            tp.SetRepeatDayOfWeekFromBinary(c.getString(c.getColumnIndex(COLUMN_tp_repeatDayOfWeek)));
            //COLUMN_tp_repeatDayOfMonth + INT_TYPE + "," +
            tp.SetRepeatDayOfMonth(c.getInt(c.getColumnIndex(COLUMN_tp_repeatDayOfMonth)));
            //COLUMN_tp_dateOfYear + DATE_TYPE + "," +
            tp.SetDateOfYear(ConvertDateFromString(c.getString(c.getColumnIndex(COLUMN_tp_dateOfYear))));
            //COLUMN_tp_blacklistDates + TEXT_TYPE
            String[] s1 = c.getString(c.getColumnIndex(COLUMN_tp_blacklistDates)).split(Pattern.quote(","));
            if (s1.length > 0) {
                for (int i = 0; i < s1.length; i++) {
                    String[] s2 = s1[i].split(Pattern.quote("|"));
                    if (s2.length > 1) {
                        tp.AddBlacklistDate(ConvertDateFromString(s2[0]), Integer.valueOf(s2[1]) == 1);
                    }
                }
            }

            return tp;
        }


        c.close();

        return null;
    }

    //Loading
    public void loadSettings() { runDBTask( new CallBack() { @Override public void call() { _loadSettings(); } } ); }
    private void _loadSettings()
    {
        database = getWritableDatabase();

        Cursor c = null;
        try {
            //Categories
            c = database.query(TABLE_SETTINGS_CATEGORIES, null, null, null, null, null, null);
            while (c.moveToNext()) {
                //Find categories
                String category = c.getString(c.getColumnIndex(COLUMN_category));
                int catColor = Color.parseColor(c.getString(c.getColumnIndex(COLUMN_categorycolor)));

                //Fill out category
                if (category != null){
                    CategoryManager.getInstance().AddCategory(category, catColor);
                }
            }

            //People
            c = database.query(TABLE_SETTINGS_OTHERPEOPLE, null, null, null, null, null, null);
            while (c.moveToNext()) {
                //Find other people
                String person = c.getString(c.getColumnIndex(COLUMN_splitWith));

                //Fill out other people
                if (person != null){
                    OtherPersonManager.getInstance().AddOtherPerson(person);
                }
            }
            /*
            //Budgets
            c = database.query(TABLE_SETTINGS_BUDGETS, null, null, null, null, null, null);
            while (c.moveToNext()) {
                //Find budgets
                int uniqueID = c.getInt(c.getColumnIndex(COLUMN_uniqueID));
                String title = c.getString(c.getColumnIndex(COLUMN_budget));
                int selected = c.getInt(c.getColumnIndex(COLUMN_profileSelected));
                String start_date = c.getString(c.getColumnIndex(COLUMN_startdate));
                String end_date = c.getString(c.getColumnIndex(COLUMN_enddate));
                String period = c.getString(c.getColumnIndex(COLUMN_period));

                //Fill out budgets
                if (title != null && !title.equals("")){
                    Budget br = new Budget(title);
                    br.SetSelected(selected==1);
                    br.SetID(uniqueID);
                    if (start_date != null){ br.SetStartDate(ConvertDateFromString(start_date)); }
                    if (end_date != null){ br.SetEndDate(ConvertDateFromString(end_date)); }
                    if (period != null){ try { br.SetPeriod(Period.parse(period)); } catch (Exception e) { e.printStackTrace(); } }
                    BudgetManager.getInstance().AddBudget(br);

                    //TODO: Find a way to set the selected budget somehow
                    //if (selected == 1){ ProfileManager.getInstance().SelectProfileDontSave(p); }
                }
            }
            */
        }
        catch (Exception ex){
            //ProfileManager.Print(activityContext, "ERROR: No settings found");
            //ex.printStackTrace();
        } finally {
            if (c != null) { c.close(); }
        }

        //Transfer database from import to local directory (ASyncTask)
        //DatabaseBackgroundHelper dbh = new DatabaseBackgroundHelper();
        //dbh.execute(1, database);


    }

    public void loadTransactions() { runDBTask( new CallBack() { @Override public void call() { _loadTransactions(); } } ); }
    private void _loadTransactions()
    {
        database = getWritableDatabase();

        try {
            Cursor c = database.query(TABLE_TRANSACTIONS, null, null, null, null, null, null);

            while (c.moveToNext()) {
                //Find profile
                int ID = c.getInt(c.getColumnIndex(COLUMN_budget));
                Budget br = BudgetManager.getInstance().GetBudget(ID);

                if (br != null) {
                    //Create new transaction
                    new_Transaction tr = new new_Transaction();

                    //Load and apply transaction properties

                    //COLUMN_type + TEXT_TYPE + "," +
                    tr.SetType(new_Transaction.TRANSACTION_TYPE.values()[c.getInt(c.getColumnIndex(COLUMN_type))]);
                    //COLUMN_uniqueID + TEXT_TYPE + "," +
                    tr.SetID(c.getInt(c.getColumnIndex(COLUMN_uniqueID)));
                    //COLUMN_parentID + TEXT_TYPE + "," +
                    tr.SetParentID(c.getInt(c.getColumnIndex(COLUMN_parentID)));
                    //COLUMN_category + TEXT_TYPE + "," +
                    tr.SetCategory(c.getString(c.getColumnIndex(COLUMN_category)));
                    //COLUMN_source + TEXT_TYPE + "," +
                    tr.SetSource(c.getString(c.getColumnIndex(COLUMN_source)));
                    //COLUMN_description + TEXT_TYPE  + "," +
                    tr.SetDescription(c.getString(c.getColumnIndex(COLUMN_description)));
                    //COLUMN_value + DOUBLE_TYPE  + "," +
                    tr.SetValue(c.getDouble(c.getColumnIndex(COLUMN_value)));
                    //COLUMN_staticValue + BOOLEAN_TYPE  + "," +
                    //tr.SetStatic(c.getInt(c.getColumnIndex(COLUMN_staticValue)) == 1);
                    //COLUMN_IPaid + BOOLEAN_TYPE + "," +
                    tr.SetPaidBy(c.getString(c.getColumnIndex(COLUMN_paidBy)));
                    //COLUMN_splitWith + TEXT_TYPE + "," + //COLUMN_splitValue + DOUBLE_TYPE  + "," +
                    tr.SetSplitFromArrayString(c.getString(c.getColumnIndex(COLUMN_split)));
                    //COLUMN_paidBack + TEXT_TYPE + "," +
                    tr.SetPaidBack(ConvertDateFromString(c.getString(c.getColumnIndex(COLUMN_paidBack))));
                    //COLUMN_when + INT_TYPE //+ "," +
                    tr.SetTimePeriod(_queryTimeperiod(c.getInt(c.getColumnIndex(COLUMN_when))));
                    //COLUMN_children
                    tr.AddChildrenFromFormattedString(c.getString(c.getColumnIndex(COLUMN_children)));


                    //ProfileManager.Print("Transaction Loaded");
                    //Add loaded transaction to profile
                    br.AddTransaction(tr);
                }
                else {
                    //ProfileManager.Print("Transaction could not be loaded, profile -" + _profileID + "- not found.");
                }
            }


            c.close();
        } catch (Exception ex){

        }

    }

    //Removal
    public void removeCategorySetting(final String category) { runDBTask( new CallBack() { @Override public void call() { _removeCategorySetting(category); } } ); }
    private boolean _removeCategorySetting(String category){
        database = getWritableDatabase();
        return !category.equals("") && database.delete(TABLE_SETTINGS_CATEGORIES, COLUMN_category + "=?", new String[]{ category }) > 0;
    }

    public void removePersonSetting(final String name) { runDBTask( new CallBack() { @Override public void call() { _removePersonSetting(name); } } ); }
    private boolean _removePersonSetting(String name){
        database = getWritableDatabase();
        return !name.equals("") && database.delete(TABLE_SETTINGS_OTHERPEOPLE, COLUMN_splitWith + "=?", new String[]{ name }) > 0;
    }

    public void removeProfileSetting(final Budget budget) { runDBTask( new CallBack() { @Override public void call() { _removeProfileSetting(budget); } } ); }
    private boolean _removeProfileSetting(Budget budget){
        database = getWritableDatabase();
        return budget != null && database.delete(TABLE_SETTINGS_BUDGETS, COLUMN_budget + "=?", new String[]{String.valueOf(budget.GetName())}) > 0;
    }

    public void remove(final new_Transaction transaction) { runDBTask( new CallBack() { @Override public void call() { _remove(transaction); } } ); }
    public boolean _remove(new_Transaction transaction){
        database = getWritableDatabase();

        if (transaction != null) {
            Cursor c = database.query(TABLE_TRANSACTIONS, new String[]{COLUMN_when}, COLUMN_uniqueID + "=?", new String[]{String.valueOf(transaction.GetID())}, null, null, null);
            _removeTimePeriod(c);
            c.close();

            return database.delete(TABLE_TRANSACTIONS, COLUMN_uniqueID + "=?", new String[]{String.valueOf(transaction.GetID())}) > 0;
        }

        return false;
    }

    private void _removeTimePeriod(Cursor c){
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
            //ProfileManager.Print(activityContext, "TimePeriod not found");
        }
    }



    //Debug
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
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(Query, null);


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
            //Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            //Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }

    }
}
