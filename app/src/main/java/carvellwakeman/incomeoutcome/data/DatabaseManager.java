package carvellwakeman.incomeoutcome.data;


import android.content.Context;
import android.os.AsyncTask;
import android.content.ContentValues;
import android.database.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Environment;
import carvellwakeman.incomeoutcome.*;
import carvellwakeman.incomeoutcome.helpers.Helper;
import carvellwakeman.incomeoutcome.models.*;
import org.apache.commons.io.FileUtils;
import org.joda.time.Period;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;


public class DatabaseManager extends SQLiteOpenHelper
{
    private static DatabaseManager instance;

    private Context mContext;

    SQLiteDatabase database;

    private ContentValues contentValues_tr;
    private ContentValues contentValues_tp;

    //DATABASE_VERSION
    private static final int DATABASE_VERSION = 8;
    //File information
    private static final String FILE_NAME = "data.db";

    public static File EXPORT_DIRECTORY;
    private static File EXPORT_DIRECTORY_BACKUP;
    private static String BACKUP_FILENAME;
    public static File EXPORT_BACKUP;

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
            COLUMN_uniqueID + INT_TYPE + "," +
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
            COLUMN_category + INT_TYPE + "," +
            COLUMN_source + TEXT_TYPE + "," +
            COLUMN_description + TEXT_TYPE  + "," +
            COLUMN_value + DOUBLE_TYPE  + "," +
            COLUMN_paidBy + INT_TYPE + "," +
            COLUMN_split + TEXT_TYPE + "," +
            COLUMN_paidBack + TEXT_TYPE + "," +
            COLUMN_children + TEXT_TYPE + "," +
            COLUMN_when + INT_TYPE
            + ");";

    private static final String CREATE_TABLE_TIMEPERIOD = "CREATE TABLE IF NOT EXISTS " + TABLE_TIMEPERIODS + "(" +
            COLUMN_uniqueID + INT_TYPE + "," +
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


                //Reformat otherPerson table
                "ALTER TABLE " + TABLE_SETTINGS_OTHERPEOPLE + " RENAME TO " + "temp_otherpeople" + STATEMENT_DELIMITER +

                //Create correctly formatted table
                CREATE_TABLE_SETTINGS_OTHERPEOPLE + STATEMENT_DELIMITER +

                //Copy data from temp to TABLE_SETTINGS_OTHERPEOPLE
                "INSERT INTO " + TABLE_SETTINGS_OTHERPEOPLE + "(" +
                COLUMN_uniqueID + "," +
                COLUMN_splitWith + "," +
                COLUMN_splitWith2 +
                ") SELECT " +
                "NULL," +
                COLUMN_splitWith + "," +
                COLUMN_splitWith2 +
                " FROM temp_otherpeople" + STATEMENT_DELIMITER +

                //Drop the temp table
                "DROP TABLE temp_otherpeople" + STATEMENT_DELIMITER +

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
                    "(SELECT "  + COLUMN_uniqueID + " FROM " + TABLE_SETTINGS_CATEGORIES + " WHERE " + "temp_transactions." + COLUMN_category + " = " + TABLE_SETTINGS_CATEGORIES + "." + COLUMN_category + ")," +
                    COLUMN_source + "," +
                    COLUMN_description  + "," +
                    COLUMN_value  + "," +
                    //COLUMN_IPaid becomes COLUMN_paidBy
                        "CASE WHEN " + COLUMN_IPaid + " = 0 THEN " + COLUMN_splitWith + " ELSE (SELECT 'You') END" + "," +
                    //COLUMN_split is made from COLUMN_value, COLUMN_splitValue and COLUMN_splitWith
                        "(SELECT 'You') || ':' || (" + COLUMN_value + "-" + COLUMN_splitValue + ") || (CASE WHEN " + COLUMN_splitWith + " = '' THEN '' ELSE '|' || " + COLUMN_splitWith + " || ':' || " + COLUMN_splitValue + " END)" + "," +
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

    private static final String UPGRADE_7_8 =
                //Reformat timeperiods table to replace the ID column with a UID column
                //"ALTER TABLE " + TABLE_TIMEPERIODS + " CHANGE " + COLUMN_ID + " " + COLUMN_uniqueID + INT_TYPE;

                //Rename timeperiods table
                "ALTER TABLE " + TABLE_TIMEPERIODS + " RENAME TO " + "temp_tp" + STATEMENT_DELIMITER +

                //Create correctly formatted table
                CREATE_TABLE_TIMEPERIOD + STATEMENT_DELIMITER +

                //Copy data from temp to TABLE_TRANSACTIONS
                "INSERT INTO " + TABLE_TIMEPERIODS + "(" +
                        COLUMN_uniqueID + "," +
                        COLUMN_tp_parent + "," +
                        COLUMN_tp_date + "," +
                        COLUMN_tp_repeatFreq + "," +
                        COLUMN_tp_repeatUntil + "," +
                        COLUMN_tp_repeatNTimes + "," +
                        COLUMN_tp_repeatUntilDate + "," +
                        COLUMN_tp_repeatEveryN + "," +
                        COLUMN_tp_repeatDayOfWeek + ",  " +
                        COLUMN_tp_repeatDayOfMonth + "," +
                        COLUMN_tp_dateOfYear + "," +
                        COLUMN_tp_blacklistDates +
                ") SELECT " +
                        COLUMN_ID + "," +
                        COLUMN_tp_parent + "," +
                        COLUMN_tp_date + "," +
                        COLUMN_tp_repeatFreq + "," +
                        COLUMN_tp_repeatUntil + "," +
                        COLUMN_tp_repeatNTimes + "," +
                        COLUMN_tp_repeatUntilDate + "," +
                        COLUMN_tp_repeatEveryN + "," +
                        COLUMN_tp_repeatDayOfWeek + "," +
                        COLUMN_tp_repeatDayOfMonth + "," +
                        COLUMN_tp_dateOfYear + "," +
                        COLUMN_tp_blacklistDates +
                " FROM temp_tp" + STATEMENT_DELIMITER +

                //Drop the temp table
                "DROP TABLE temp_tp";

    private DatabaseManager(Context context){
        super(context, FILE_NAME, null, DATABASE_VERSION);

        this.mContext = context;
        
        EXPORT_DIRECTORY = new File(Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name_nospace) + "/");
        EXPORT_DIRECTORY_BACKUP = new File(EXPORT_DIRECTORY.getAbsolutePath() + "/backup/");
        BACKUP_FILENAME = "data_backup.db"; //TODO Read from strings file
        EXPORT_BACKUP = new File(EXPORT_DIRECTORY_BACKUP, BACKUP_FILENAME);
    }
    public static DatabaseManager getInstance(Context context){
        if (instance == null){ instance = new DatabaseManager(context); }
        return instance;
    }
    //public void initialize() {
        //Helper.Log(mContext, "DBM", "initialize called");

        //Triggers onUpgrade (if necessary)
        //database = getWritableDatabase();
        //Helper.Log(mContext, "DBM", "initialize called ver." + String.valueOf(database.getVersion()));

        //tryCreateDatabase(database);
    //}

    //Database constructor and updating
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Helper.Log(mContext, "DBM", "OnCreate called ver." + String.valueOf(db.getVersion()));
        tryCreateDatabase(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Helper.Log(mContext, "DBM", "OnUpgrade called (" + oldVersion + "->" + newVersion + ")");
        //Helper.Log(mContext, "DBM", "onUpgrade() ver." + String.valueOf(db.getVersion()));
        //Helper.Log(mContext, "DBM", "onUpgrade() oldVersion " + String.valueOf(oldVersion));
        //Helper.Log(mContext, "DBM", "onUpgrade() newVersion " + String.valueOf(newVersion));

        Upgrade(db, oldVersion, newVersion);
    }
    public void Upgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //elper.Log(mContext, "DBM", "Upgrading from " + oldVersion + " to " + newVersion);
        //Helper.Log(mContext, "DBM", "Upgrade() ver." + String.valueOf(db.getVersion()));

        //Helper.PrintLong(mContext, UPGRADE_6_7);
        //Helper.PrintLong(mContext, "(SELECT "  + COLUMN_uniqueID + " FROM " + TABLE_SETTINGS_CATEGORIES + " WHERE " + "temp_transactions." + COLUMN_category + " = " + TABLE_SETTINGS_CATEGORIES + "." + COLUMN_category + "),");

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
                    Helper.Print(mContext, "Upgrade from Ver.3 to Ver.4");
                case 4: //To version 5
                    SQLExecuteMultiple(db, UPGRADE_4_5);
                    Helper.Print(mContext, "Upgrade from Ver.4 to Ver.5");
                case 5: //To version 6
                    SQLExecuteMultiple(db, UPGRADE_5_6);
                    Helper.Print(mContext, "Upgrade from Ver.5 to Ver.6");
                case 6: //To version 7
                    SQLExecuteMultiple(db, UPGRADE_6_7);

                    //Fill out UID column in TABLE_OTHERPEOPLE
                    Cursor c = db.query(TABLE_SETTINGS_OTHERPEOPLE, null, null, null, null, null, null);
                    while (c.moveToNext()) {
                        //Find other person name
                        String name = c.getString(c.getColumnIndex(COLUMN_splitWith));
                        Person person = new Person(name);
                        Integer UID = person.GetID();

                        contentValues_tr = new ContentValues();
                        contentValues_tr.put(COLUMN_uniqueID, UID );

                        //Insert/update row and return result
                        long result = db.update(TABLE_SETTINGS_OTHERPEOPLE, contentValues_tr, COLUMN_splitWith + "=?", new String[] { name });


                        //Replace names in split array with UID
                        Cursor c1 = db.query(TABLE_TRANSACTIONS, null, null, null, null, null, null);
                        while (c1.moveToNext()) {
                            //Find other person name
                            Integer Transaction_ID = c1.getInt(c1.getColumnIndex(COLUMN_uniqueID));
                            String paidBy = c1.getString(c1.getColumnIndex(COLUMN_paidBy));
                            String split = c1.getString(c1.getColumnIndex(COLUMN_split));

                            contentValues_tr = new ContentValues();

                            //Replace PaidBy with UID
                            if (paidBy.equals(name)) {
                                contentValues_tr.put(COLUMN_paidBy, String.valueOf(UID));
                            }
                            else {
                                contentValues_tr.put(COLUMN_paidBy, -1);
                            }
                            //Helper.Print(mContext, "paidBy:" + paidBy + "\nSplit:" + split + "\nPerson:" + name + "(" + String.valueOf(UID) + ")");

                            //Update split with UID
                            contentValues_tr.put(COLUMN_split, split.replaceAll(name, String.valueOf(UID)).replaceAll(mContext.getString(R.string.format_me), String.valueOf(-1)));

                            //Insert/update row and return result
                            //long result2 = 0;
                            long result2 = db.update(TABLE_TRANSACTIONS, contentValues_tr, COLUMN_uniqueID + "=?", new String[]{String.valueOf(Transaction_ID)});
                            //Helper.Print(mContext, "Result2:" + String.valueOf(result2));
                        }
                        c1.close();


                    }
                    c.close();

                    Helper.Print(mContext, "Upgrade from Ver.6 to Ver.7");
                case 7: //To version 8
                    SQLExecuteMultiple(db, UPGRADE_7_8);
                    Helper.Print(mContext, "Upgrade from Ver.7 to Ver.8");
                case 8: //To version 9
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

        // If all went correctly, set database version
        db.setVersion(newVersion);

    }

    //SQL
    public void SQLExecuteMultiple(SQLiteDatabase db, String inputStatement){
        String[] statements = inputStatement.split(STATEMENT_DELIMITER);

        for(String statement : statements){
            try { db.execSQL(statement); }
            catch (Exception ex) { Helper.PrintLong(mContext, ex.getMessage()); break; }
        }
    }

    //Task Execution
    public void runDBTask(final Runnable BackgroundAction) { runDBTask(BackgroundAction, null, null); }
    public void runDBTask(final Runnable BackgroundAction, final Runnable PreAction, final Runnable PostAction){
        AsyncTask<Object, String, String> task = new AsyncTask<Object, String, String>() {
            @Override protected void onPreExecute() {
                try {
                    if (PreAction != null) { PreAction.run(); }
                } catch (Exception ex){}//Not much we can do at this point
            }

            @Override protected String doInBackground(Object... params) {
                try {
                    BackgroundAction.run();
                    return null;
                } catch (Exception ex){ return ex.toString(); }

            }

            @Override protected void onProgressUpdate(String... text) {}

            @Override protected void onPostExecute(String result) {
                //Failure on doInBackground
                if (result != null) { Helper.PrintUserLong(mContext, "Database Operation Failure: " + result); }

                try {
                    if (PostAction != null) { PostAction.run(); }
                } catch (Exception ex){}//Not much we can do at this point
            }
        };

        //Start process
        task.execute();
    }

    //Database directories & Versioning
    public String getExportDirectory(){ return EXPORT_DIRECTORY.getAbsolutePath(); }
    public ArrayList<File> getImportableDatabases(){
        try {
            ArrayList<File> DatabaseFiles = new ArrayList<>();

            if (Helper.isStoragePermissionGranted(mContext)){
                File data = new File(Environment.getExternalStorageDirectory() + "/" + mContext.getString(R.string.app_name_nospace) + "/");

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
            File data = new File(Environment.getExternalStorageDirectory() + "/" + mContext.getString(R.string.app_name_nospace) + "/");
            ArrayList<String> DatabaseFiles = new ArrayList<>();

            if (Helper.isStoragePermissionGranted(mContext)){
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
        if (Helper.isStoragePermissionGranted(mContext)){
            File data = new File(Environment.getExternalStorageDirectory() + "/" + mContext.getString(R.string.app_name_nospace) + "/");

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
                //Helper.Print(mContext, "DBM", "Error creating Settings tables");
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

    private void dropAndRecreateAllTables(final SQLiteDatabase database) { runDBTask(new Runnable() { @Override public void run() { _dropAndRecreateAllTables(database); } } ); }
    private void _dropAndRecreateAllTables(SQLiteDatabase database){
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
        //Helper.Log(mContext, "DBM", "Try create database");
        tryCreateDatabase(database);
    }

    private void dropTable(final String tableName) { runDBTask( new Runnable() { @Override public void run() { _dropTable(tableName); } } ); }
    private void _dropTable(String tableName){
        database.execSQL("DROP TABLE IF EXISTS " + tableName);
    }


    public void deleteAllTableContent() { runDBTask( new Runnable() { @Override public void run() { _deleteAllTableContent(); } } ); }
    private void _deleteAllTableContent(){
        _deleteTableContent(TABLE_SETTINGS_CATEGORIES);
        _deleteTableContent(TABLE_SETTINGS_OTHERPEOPLE);
        _deleteTableContent(TABLE_SETTINGS_PROFILES);
        _deleteTableContent(TABLE_SETTINGS_BUDGETS);
        _deleteTableContent(TABLE_TRANSACTIONS);
        _deleteTableContent(TABLE_TIMEPERIODS);

        //Legacy
        //SQLExecuteMultiple(database, "DELETE FROM sqlite_sequence WHERE name='"+TABLE_TIMEPERIODS+"';"); //Reset autoincrement id counter for TABLE_TIMEPERIODS
        _deleteTableContent(TABLE_EXPENSES);
        _deleteTableContent(TABLE_INCOME);
    }

    public void deleteTableContent(final String tableName) { runDBTask( new Runnable() { @Override public void run() { _deleteTableContent(tableName); } } ); }
    private void _deleteTableContent(String tableName){
        if (isTableExists(tableName, true)) {
            database.delete(tableName, null, null);
        }
    }


    //Import and Export
    public void exportDatabase(String name, File destination) {

        try {
            File datadir = Environment.getDataDirectory();

            //Manage database name if *.db
            String[] sp1 = FILE_NAME.split("\\.");
            String filename = sp1[0];

            if (Helper.isStoragePermissionGranted(mContext)){
                //Create backup directory if it does not exist
                destination.mkdirs();

                if (destination.canWrite()) {

                    //Database paths
                    String currentDBPath = "/data/" + mContext.getPackageName() + "/databases/" + filename + ".db";
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


    //public void importDatabase(final File importFile, final boolean backup) { runDBTask( new Runnable() { @Override public void run() { _importDatabase(importFile, backup); } }); }
    //private void _importDatabase(File importFile){ _importDatabase(importFile, false); }
    public void importDatabase(File importFile, boolean backup){

        try {
            //Database paths
            String currentDBPath = "/data/" + mContext.getPackageName() + "/databases/";
            File currentDB = new File(Environment.getDataDirectory(), currentDBPath + FILE_NAME);

            if (Helper.isStoragePermissionGranted(mContext)){

                if (currentDB.canWrite() || importFile.canRead()) {

                    if (currentDB.exists()) {

                        //Backup currentDB (Secret)
                        if (backup) {
                            exportDatabase(BACKUP_FILENAME, EXPORT_DIRECTORY_BACKUP);
                        }

                        //Delete current database
                        //Helper.Log(mContext, "DBM", "Delete and recreate all tables");
                        //_dropAndRecreateAllTables(getWritableDatabase());
                        _deleteAllTableContent();
                        //Create new empty database
                        //Helper.Log(mContext, "DBM", "Try create database");
                        tryCreateDatabase(getWritableDatabase());

                        //Transfer database from import to local directory (ASyncTask?)
                        FileUtils.copyFile(importFile, currentDB);

                        //Force upgrade even though DATABASE_VERSION has not changed because the database we are importing may be older than DATABASE_VERSION (WHY DOES THIS WORK?)
                        //Call getWritableDatabase() to trigger onUpgrade if it is necessary
                        //getWritableDatabase(); // Do it forcefully now.
                        //Force call onUpgrade
                        //Helper.Log(mContext, "DBM", "Force upgrade");
                        final int oldVersion = SQLiteDatabase.openDatabase(importFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE).getVersion();
                        Upgrade(getWritableDatabase(), oldVersion, getVersion());
                        //getWritableDatabase().close();


                        //Load new database (Do not include)
                        //(new DatabaseBackgroundHelper()).execute(8); //loadSettings();
                        //(new DatabaseBackgroundHelper()).execute(9); //loadTransactions();
                        //_loadSettings();
                        //_loadTransactions();

                        //ProfileManager.PrintUser(activityContext, importFile.getName() + " imported");
                    }
                }
            }
            else { //No UI thread work in a background thread!
                //Helper.PrintUser(mContext, "Storage permission not granted, cannot import database");
            }

        } catch (IOException e) {

            //ProfileManager.Print(activityContext, "Error importing database");
            //Helper.PrintLong(mContext, e.getMessage());
        }

    }


    //Object Specific Operations
    //Insertion
    public void insertSetting(final Category category, final boolean tryUpdate) { runDBTask(new Runnable() { @Override public void run() { _insertSetting(category, tryUpdate); } } ); }
    public void insertSetting(final Category category, final Runnable postCallback, final boolean tryUpdate) {
        runDBTask( new Runnable() { @Override public void run() { _insertSetting(category, tryUpdate); } }, null, postCallback );
    }
    public long _insertSetting(Category category, boolean tryUpdate) {
        database = getWritableDatabase();

        if (database != null && isTableExists(TABLE_SETTINGS_CATEGORIES, false) && category != null) {
            contentValues_tr = new ContentValues();

            //Fill out row
            contentValues_tr.put(COLUMN_uniqueID, category.GetID());
            contentValues_tr.put(COLUMN_category, category.GetTitle());
            contentValues_tr.put(COLUMN_categorycolor, String.format("#%06X", 0xFFFFFF & category.GetColor()));

            //Insert/update row and return result
            long result = 0;
            if (tryUpdate){  result = database.update(TABLE_SETTINGS_CATEGORIES, contentValues_tr, COLUMN_uniqueID + "=?", new String[] { String.valueOf(category.GetID()) }); }
            if (result == 0) { result = database.insert(TABLE_SETTINGS_CATEGORIES, null, contentValues_tr); }

            return result;
        } else{ return -1; }
    }

    public void insertSetting(final Person person, final boolean tryUpdate) { runDBTask(new Runnable() { @Override public void run() { _insertSetting(person, tryUpdate); } } ); }
    public void insertSetting(final Person person, final Runnable postCallback, final boolean tryUpdate) {
        runDBTask( new Runnable() { @Override public void run() { _insertSetting(person, tryUpdate); } }, null, postCallback );
    }
    public long _insertSetting(Person person, boolean tryUpdate) {
        database = getWritableDatabase();

        if (database != null && isTableExists(TABLE_SETTINGS_OTHERPEOPLE, false) && person != null) {
            contentValues_tr = new ContentValues();

            //Fill out row
            contentValues_tr.put(COLUMN_uniqueID, person.GetID());
            contentValues_tr.put(COLUMN_splitWith, person.GetName());

            //Insert/update row and return result
            long result = 0;
            if (tryUpdate){ result = database.update(TABLE_SETTINGS_OTHERPEOPLE, contentValues_tr, COLUMN_uniqueID + "=?", new String[] { String.valueOf(person.GetID()) }); }
            if (result == 0) { result = database.insert(TABLE_SETTINGS_OTHERPEOPLE, null, contentValues_tr); }

            return result;
        } else{ return -1; }
    }

    public void insertSetting(final Budget budget, final boolean tryUpdate) { runDBTask(new Runnable() { @Override public void run() { _insertSetting(budget, tryUpdate); } } ); }
    public void insertSetting(final Budget budget, final Runnable postCallback, final boolean tryUpdate) {
        runDBTask( new Runnable() { @Override public void run() { _insertSetting(budget, tryUpdate); } }, null, postCallback );
    }
    public long _insertSetting(Budget budget, boolean tryUpdate) {
        database = getWritableDatabase();

        if (budget != null) {
            if (database != null) {
                if (isTableExists(TABLE_SETTINGS_BUDGETS, false)) {
                    contentValues_tr = new ContentValues();

                    //Fill out object variables
                    contentValues_tr.put(COLUMN_uniqueID, budget.GetID());
                    contentValues_tr.put(COLUMN_budget, budget.GetName());

                    //Selection
                    contentValues_tr.put(COLUMN_selected, (budget.GetSelected() ? 1 : 0));

                    if (budget.GetStartDate() != null) {
                        contentValues_tr.put(COLUMN_startdate, budget.GetStartDate().toString(mContext.getString(R.string.date_format_saving)));
                    }
                    if (budget.GetEndDate() != null) {
                        contentValues_tr.put(COLUMN_enddate, budget.GetEndDate().toString(mContext.getString(R.string.date_format_saving)));
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

    public void insert(final Transaction transaction, final boolean tryupdate) { runDBTask(new Runnable() { @Override public void run() { _insert(transaction, tryupdate); } } ); }
    public void insert(final Transaction transaction, Runnable postCallback, final boolean tryUpdate) {
        runDBTask( new Runnable() { @Override public void run() { _insert(transaction, tryUpdate); } }, null, postCallback );
    }
    public long _insert(Transaction transaction, boolean tryupdate) {
        database = getWritableDatabase();

        if (database != null && isTableExists(TABLE_TRANSACTIONS, false)) {
            contentValues_tr = new ContentValues();

            //Timeperiod
            //long tp_id = _insert(transaction.GetID(), transaction.GetTimePeriod(), tryupdate);
            _insert(transaction.GetID(), transaction.GetTimePeriod(), tryupdate);

            //Fill out row
            contentValues_tr.put(COLUMN_type, transaction.GetType().ordinal());
            contentValues_tr.put(COLUMN_budget, transaction.GetBudgetID());
            contentValues_tr.put(COLUMN_uniqueID, transaction.GetID());
            contentValues_tr.put(COLUMN_parentID, transaction.GetParentID());
            contentValues_tr.put(COLUMN_category, transaction.GetCategory());
            contentValues_tr.put(COLUMN_source, transaction.GetSource());
            contentValues_tr.put(COLUMN_description, transaction.GetDescription());
            contentValues_tr.put(COLUMN_value, transaction.GetValue());
            //contentValues_tr.put(COLUMN_staticValue, transaction.GetStatic()); //New transactions no longer have the static field (It was never used)
            contentValues_tr.put(COLUMN_when, transaction.GetTimePeriod().GetID()); //if (!tryupdate) { }

            contentValues_tr.put(COLUMN_paidBy, transaction.GetPaidBy());
            //Helper.Log(mContext, "DB", "SplitStringSave:'" + transaction.GetSplitArrayString() + "'");

            contentValues_tr.put(COLUMN_split, transaction.GetSplitArrayString());
            contentValues_tr.put(COLUMN_paidBack, (transaction.GetPaidBack() != null ? transaction.GetPaidBack().toString(mContext.getString(R.string.date_format_saving)) : "") );
            //contentValues_tr.put(COLUMN_children, transaction.GetChildrenFormatted());

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

    // Shouldn't be used, this is done by _insert(Transaction)
    //public void insert(final int transactionUID, final TimePeriod tp, final boolean tryupdate) { runDBTask( new Runnable() { @Override public void run() { _insert(transactionUID, tp, tryupdate); } } ); }
    //public void insert(final int transactionUID, final TimePeriod tp, final Runnable postCallback, final boolean tryUpdate) {
    //    runDBTask( new Runnable() { @Override public void run() { _insert(transactionUID, tp, tryUpdate); } }, null, postCallback );
    //}
    private void _insert(int transactionUID, TimePeriod tp, Boolean tryupdate) {
        database = getWritableDatabase();

        if (database != null && isTableExists(TABLE_TIMEPERIODS, false)) {
            contentValues_tp = new ContentValues();

            if (tp != null) {
                contentValues_tp.put(COLUMN_uniqueID, tp.GetID());
                contentValues_tp.put(COLUMN_tp_parent, transactionUID);
                contentValues_tp.put(COLUMN_tp_date, (tp.GetDate() != null ? tp.GetDate().toString(mContext.getString(R.string.date_format_saving)) : ""));
                //contentValues_tp.put(COLUMN_tp_firstOcc, (tp.GetFirstOccurrence() != null ? tp.GetFirstOccurrence().toString(ProfileManager.simpleDateFormatSaving) : ""));
                contentValues_tp.put(COLUMN_tp_repeatFreq, (tp.GetRepeatFrequency() != null ? tp.GetRepeatFrequency().ordinal() : 0));
                contentValues_tp.put(COLUMN_tp_repeatUntil, (tp.GetRepeatUntil() != null ? tp.GetRepeatUntil().ordinal() : 0));
                contentValues_tp.put(COLUMN_tp_repeatNTimes, tp.GetRepeatANumberOfTimes());
                contentValues_tp.put(COLUMN_tp_repeatUntilDate, (tp.GetRepeatUntilDate() != null ? tp.GetRepeatUntilDate().toString(mContext.getString(R.string.date_format_saving)) : ""));
                contentValues_tp.put(COLUMN_tp_repeatEveryN, tp.GetRepeatEveryN());
                contentValues_tp.put(COLUMN_tp_repeatDayOfWeek, tp.GetRepeatDayOfWeekBinary());
                contentValues_tp.put(COLUMN_tp_repeatDayOfMonth, tp.GetRepeatDayOfMonth());
                contentValues_tp.put(COLUMN_tp_dateOfYear, (tp.GetDateOfYear() != null ? tp.GetDateOfYear().toString(mContext.getString(R.string.date_format_saving)) : ""));
                contentValues_tp.put(COLUMN_tp_blacklistDates, tp.GetBlacklistDatesString(mContext));
            }

            //Insert/update row and return result
            long result = 0;
            if (contentValues_tp != null && contentValues_tp.size() > 0) {
                if (tryupdate) { result = database.update(TABLE_TIMEPERIODS, contentValues_tp, COLUMN_tp_parent + "=" + transactionUID, null); }
                if (result == 0) { result = database.insert(TABLE_TIMEPERIODS, null, contentValues_tp); }
            }
        }
    }

    private TimePeriod _queryTimeperiod(SQLiteDatabase database, int id) {

        Cursor c = database.query(TABLE_TIMEPERIODS, null, COLUMN_uniqueID + "=" + id, null, null, null, null);

        //Loop through data
        while (c.moveToNext()) {
            //Create timeperiod object
            TimePeriod tp = new TimePeriod();

            //COLUMN_uniqueID INT_TYPE
            tp.SetID(c.getInt(c.getColumnIndex(COLUMN_uniqueID)));
            //COLUMN_tp_date DATE_TYPE
            tp.SetDate(Helper.ConvertDateFromString(mContext, c.getString(c.getColumnIndex(COLUMN_tp_date))));
            //COLUMN_tp_repeatFreq INT_TYPE
            tp.SetRepeatFrequency(tp.GetRepeatFrequencyFromIndex(c.getInt(c.getColumnIndex(COLUMN_tp_repeatFreq))));
            //COLUMN_tp_repeatUntil INT_TYPE
            tp.SetRepeatUntil(tp.GetRepeatUntilFromIndex(c.getInt(c.getColumnIndex(COLUMN_tp_repeatUntil))));
            //COLUMN_tp_repeatNTimes INT_TYPE
            tp.SetRepeatANumberOfTimes(c.getInt(c.getColumnIndex(COLUMN_tp_repeatNTimes)));
            //COLUMN_tp_repeatUntilDate DATE_TYPE
            tp.SetRepeatUntilDate(Helper.ConvertDateFromString(mContext, c.getString(c.getColumnIndex(COLUMN_tp_repeatUntilDate))));
            //COLUMN_tp_repeatEveryN INT_TYPE
            tp.SetRepeatEveryN(c.getInt(c.getColumnIndex(COLUMN_tp_repeatEveryN)));
            //COLUMN_tp_repeatDayOfWeek TEXT_TYPE
            tp.SetRepeatDayOfWeekFromBinary(c.getString(c.getColumnIndex(COLUMN_tp_repeatDayOfWeek)));
            //COLUMN_tp_repeatDayOfMonth INT_TYPE
            tp.SetRepeatDayOfMonth(c.getInt(c.getColumnIndex(COLUMN_tp_repeatDayOfMonth)));
            //COLUMN_tp_dateOfYear DATE_TYPE
            tp.SetDateOfYear(Helper.ConvertDateFromString(mContext, c.getString(c.getColumnIndex(COLUMN_tp_dateOfYear))));
            //COLUMN_tp_blacklistDates TEXT_TYPE
            tp.SetBlacklistDatesFromString(mContext, c.getString(c.getColumnIndex(COLUMN_tp_blacklistDates)));

            return tp;
        }


        c.close();

        return null;
    }


    //Loading
    public void loadSettings(Runnable callback) { runDBTask( new Runnable() { @Override public void run() { _loadSettings(); } }, null, callback ); }
    public void _loadSettings() {
        //Get managers
        CategoryManager cm = CategoryManager.getInstance();
        PersonManager pm = PersonManager.getInstance();
        BudgetManager bm = BudgetManager.getInstance();

        database = getReadableDatabase();

        Cursor c = null;
        try {
            //Categories
            c = database.query(TABLE_SETTINGS_CATEGORIES, null, null, null, null, null, null);
            while (c.moveToNext()) {
                //Find categories
                String title = c.getString(c.getColumnIndex(COLUMN_category));
                int catColor = Color.parseColor(c.getString(c.getColumnIndex(COLUMN_categorycolor)));
                Integer ID = c.getInt(c.getColumnIndex(COLUMN_uniqueID));

                //Fill out category
                if (title != null){
                    Category cat = cm.AddCategory(title, catColor);
                    cat.SetID(ID);
                }
            }

            //People
            c = database.query(TABLE_SETTINGS_OTHERPEOPLE, null, null, null, null, null, null);
            while (c.moveToNext()) {
                //Find other people
                String person = c.getString(c.getColumnIndex(COLUMN_splitWith));
                Integer ID = c.getInt(c.getColumnIndex(COLUMN_uniqueID));

                //Fill out other people
                if (!person.equals("")){
                    Person p = pm.AddPerson(person);
                    p.SetID(ID);
                }
            }

            //Budgets
            c = database.query(TABLE_SETTINGS_BUDGETS, null, null, null, null, null, null);
            while (c.moveToNext()) {
                //Find budgets
                int uniqueID = c.getInt(c.getColumnIndex(COLUMN_uniqueID));
                String title = c.getString(c.getColumnIndex(COLUMN_budget));
                int selected = c.getInt(c.getColumnIndex(COLUMN_selected));
                String start_date = c.getString(c.getColumnIndex(COLUMN_startdate));
                String end_date = c.getString(c.getColumnIndex(COLUMN_enddate));
                String period = c.getString(c.getColumnIndex(COLUMN_period));

                //Fill out budgets
                if (title != null && !title.equals("")){
                    Budget br = new Budget(title);
                    br.SetID(uniqueID);
                    if (start_date != null) { br.SetStartDate(Helper.ConvertDateFromString(mContext, start_date)); }
                    if (end_date != null) { br.SetEndDate(Helper.ConvertDateFromString(mContext, end_date)); }

                    if (period != null) { br.SetPeriod(Period.parse(period)); }
                    bm.AddBudget(br);

                    //TODO: Find a way to set the selected budget somehow
                    if (selected == 1) { bm.SetSelectedBudget(br); }
                }
            }

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

    public void loadTransactions(Runnable callback) { runDBTask( new Runnable() { @Override public void run() { _loadTransactions(); } }, null, callback); }
    public void _loadTransactions() {
        database = getReadableDatabase();

        try {
            Cursor c = database.query(TABLE_TRANSACTIONS, null, null, null, null, null, null);
            //Helper.Log(mContext, "DB", "TotalCount:" + String.valueOf(c.getCount()));

            while (c.moveToNext()) {
                //Find budget
                int BudgetID = c.getInt(c.getColumnIndex(COLUMN_budget));
                Budget br = BudgetManager.getInstance().GetBudget(BudgetID);


                if (br != null) {
                    //Create new transaction
                    Transaction tr = new Transaction();

                    //Load and apply transaction properties

                    //COLUMN_type + TEXT_TYPE
                    tr.SetType(Transaction.TRANSACTION_TYPE.values()[c.getInt(c.getColumnIndex(COLUMN_type))]);
                    //COLUMN_uniqueID + TEXT_TYPE
                    tr.SetID(c.getInt(c.getColumnIndex(COLUMN_uniqueID)));
                    //COLUMN_parentID + TEXT_TYPE
                    tr.SetParentID(c.getInt(c.getColumnIndex(COLUMN_parentID)));
                    //COLUMN_budget + INT_TYPE
                    tr.SetBudgetID(BudgetID);
                    //COLUMN_category + TEXT_TYPE
                    tr.SetCategory(c.getInt(c.getColumnIndex(COLUMN_category)));
                    //COLUMN_source + TEXT_TYPE
                    tr.SetSource(c.getString(c.getColumnIndex(COLUMN_source)));
                    //COLUMN_description + TEXT_TYPE
                    tr.SetDescription(c.getString(c.getColumnIndex(COLUMN_description)));
                    //COLUMN_value + DOUBLE_TYPE
                    tr.SetValue(c.getDouble(c.getColumnIndex(COLUMN_value)));
                    //COLUMN_paidBack + DATE_TYPE
                    tr.SetPaidBack(Helper.ConvertDateFromString(mContext, c.getString(c.getColumnIndex(COLUMN_paidBack))));

                    //COLUMN_IPaid + BOOLEAN_TYPE
                    Integer paidBy = c.getInt(c.getColumnIndex(COLUMN_paidBy));
                    //Helper.Print(mContext, "PaidBy:" + String.valueOf(paidBy));
                    tr.SetPaidBy(paidBy);
                    //COLUMN_splitWith + TEXT_TYPE + "," + //COLUMN_splitValue + DOUBLE_TYPE
                    String splitString = c.getString(c.getColumnIndex(COLUMN_split));
                    //Helper.Log(mContext, "DB", "Transaction: " + tr.GetSource() + " SplitString:'" + splitString + "'");
                    if (!splitString.equals("")) { tr.SetSplitFromArrayString(splitString); }
                    //COLUMN_paidBack + TEXT_TYPE + "," +
                    tr.SetPaidBack(Helper.ConvertDateFromString(mContext, c.getString(c.getColumnIndex(COLUMN_paidBack))));
                    //COLUMN_when + INT_TYPE //+ "," +
                    TimePeriod tp = _queryTimeperiod(database, c.getInt(c.getColumnIndex(COLUMN_when)));
                    //if (tp == null) { Helper.Log(mContext, "DB", "NULL TP for tran " + tr.GetID()); } else { Helper.Log(mContext, "DB", tp.GetDateFormatted()); }
                    tr.SetTimePeriod(tp);
                    //COLUMN_children
                    //tr.AddChildrenFromFormattedString(c.getString(c.getColumnIndex(COLUMN_children)));


                    //Helper.Print(mContext, "Transaction: " + tr.GetSource());
                    //Add loaded transaction to profile
                    br.AddTransaction(tr);

                    //Helper.Print(mContext, "Count2:" + String.valueOf(br.GetTransactionCount()));
                }
                else { //NO UI WORK on worker thread
                    //Helper.Log(mContext, "DB",  "Transaction could not be loaded, budget not found.");
                }

            }
            //Helper.Print(mContext, "Count:" + String.valueOf(BudgetManager.getInstance().GetSelectedBudget().GetTransactionCount()));

            c.close();
        } catch (Exception ex){
            //Helper.Print(mContext, "Error:" + ex.getMessage());
        }

    }

    //Removal
    public void removeCategorySetting(final Category category) { runDBTask( new Runnable() { @Override public void run() { _removeCategorySetting(category); } } ); }
    public void removeCategorySetting(final Category category, final Runnable postCallback) {
        runDBTask( new Runnable() { @Override public void run() { _removeCategorySetting(category); } }, null, postCallback );
    }
    public boolean _removeCategorySetting(Category category){
        database = getWritableDatabase();
        return category != null && database.delete(TABLE_SETTINGS_CATEGORIES, COLUMN_uniqueID + "=?", new String[]{ String.valueOf(category.GetID()) }) > 0;
    }

    public void removePersonSetting(final Person person) { runDBTask(new Runnable() { @Override public void run() { _removePersonSetting(person); } } ); }
    public void removePersonSetting(final Person person, final Runnable preCallback, final Runnable postCallback) {
        runDBTask( new Runnable() { @Override public void run() { _removePersonSetting(person); } }, preCallback, postCallback );
    }
    public boolean _removePersonSetting(Person person){
        database = getWritableDatabase();
        return person != null && database.delete(TABLE_SETTINGS_OTHERPEOPLE, COLUMN_uniqueID + "=?", new String[]{ String.valueOf(person.GetID()) }) > 0;
    }

    public void removeBudgetSetting(final Budget budget) { runDBTask( new Runnable() { @Override public void run() { _removeBudgetSetting(budget); } } ); }
    public void removeBudgetSetting(final Budget budget, final Runnable postCallback) {
        runDBTask( new Runnable() { @Override public void run() { _removeBudgetSetting(budget); } }, null, postCallback );
    }
    public boolean _removeBudgetSetting(Budget budget){
        database = getWritableDatabase();
        return budget != null && database.delete(TABLE_SETTINGS_BUDGETS, COLUMN_uniqueID + "=?", new String[]{String.valueOf(budget.GetID())}) > 0;
    }

    public void remove(final Transaction transaction) { runDBTask(new Runnable() { @Override public void run() { _remove(transaction); } } ); }
    public void remove(final Transaction transaction, final Runnable postCallback) {
        runDBTask( new Runnable() { @Override public void run() { _remove(transaction); } }, null, postCallback );
    }
    public boolean _remove(Transaction transaction){
        database = getWritableDatabase();

        if (transaction != null) {
            //Find transaction
            Cursor c = database.query(TABLE_TRANSACTIONS, new String[]{COLUMN_when}, COLUMN_uniqueID + "=?", new String[]{String.valueOf(transaction.GetID())}, null, null, null);

            //Delete timeperiod
            database.delete(TABLE_TIMEPERIODS, COLUMN_tp_parent + "=?", new String[] { String.valueOf(transaction.GetID()) });
            //_removeTimePeriod(c);

            c.close();

            return database.delete(TABLE_TRANSACTIONS, COLUMN_uniqueID + "=?", new String[]{String.valueOf(transaction.GetID())}) > 0;
        }

        return false;
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
