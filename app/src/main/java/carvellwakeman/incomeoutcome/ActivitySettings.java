package carvellwakeman.incomeoutcome;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Random;


public class ActivitySettings extends AppCompatActivity
{
    Toolbar toolbar;

    Boolean tempDebugMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Find Views
        toolbar = (Toolbar) findViewById(R.id.toolbar_settings);

        //Toolbar setup
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.title_settings);
        //toolbar.inflateMenu(R.menu.toolbar_menu_sort_filter_paidback);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();

        //Populate settings
        LayoutInflater inflater = getLayoutInflater();//(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.settings_layout);

        insertPoint.removeAllViews();

        //IndexCount
        int indexCount = 0;

        //Setting Categories
        CardSettings budgetsPeopleCategories = new CardSettings(this, inflater, insertPoint, indexCount++, R.layout.row_layout_setting_card, Helper.getString(R.string.title_settings_budgetspeoplecategories));
        //Manage budgets
        budgetsPeopleCategories.AddSetting(new Setting(inflater, R.drawable.ic_account_white_24dp, getString(R.string.title_managebudgets), null,
                new View.OnClickListener() { @Override public void onClick(View v) {
                    //ProfileManager.OpenDialogFragment(ActivitySettings.this, new DialogFragmentManageProfiles(), mIsLargeLayout);
                    startActivity(new Intent(ActivitySettings.this, ActivityManageBudgets.class));
                }}
        ));
        //Manage people
        budgetsPeopleCategories.AddSetting(new Setting(inflater, R.drawable.ic_face_white_24dp, getString(R.string.title_managepeople), null,
                new View.OnClickListener() { @Override public void onClick(View v) {
                    //ProfileManager.OpenDialogFragment(ActivitySettings.this, new DialogFragmentManagePeople(), mIsLargeLayout);
                    startActivity(new Intent(ActivitySettings.this, ActivityManagePeople.class));
                }}
        ));
        //Manage categories
        budgetsPeopleCategories.AddSetting(new Setting(inflater, R.drawable.ic_view_list_white_24dp, getString(R.string.title_managecategories), null,
                new View.OnClickListener() { @Override public void onClick(View v) {
                    //Helper.OpenDialogFragment(ActivitySettings.this, new DialogFragmentManageCategories(), mIsLargeLayout);
                    startActivity(new Intent(ActivitySettings.this, ActivityManageCategories.class));
                }}
        ));

        CardSettings database = new CardSettings(this, inflater, insertPoint, indexCount++, R.layout.row_layout_setting_card, getString(R.string.title_settings_database));
        //Import and Export
        database.AddSetting(new Setting(inflater, R.drawable.ic_file_white_24dp, getString(R.string.title_settings_importexport), null,
                new View.OnClickListener() { @Override public void onClick(View v) {
                    ImportClick();
                }}
        ));
        //Delete data
        database.AddSetting(new Setting(inflater, R.drawable.ic_delete_white_24dp, getString(R.string.title_settrings_deletedata), getString(R.string.subtitle_settings_deletedata),
                new View.OnClickListener() { @Override public void onClick(View v) {
                    Helper.OpenDialogFragment(ActivitySettings.this, DialogFragmentDeleteData.newInstance(ActivitySettings.this, null), true);
                }}));

        //Changelog
        CardSettings about = new CardSettings(this, inflater, insertPoint, indexCount++, R.layout.row_layout_setting_card, getString(R.string.title_settings_about));
        final String whatsNew = String.format(getString(R.string.subtitle_settings_changelog), App.GetVersion(ActivitySettings.this));
        Setting changelog = new Setting(inflater, R.drawable.ic_update_white_24dp, getString(R.string.title_settings_changelog), whatsNew,
                new View.OnClickListener() { @Override public void onClick(View v) {
                    Helper.OpenDialogFragment(ActivitySettings.this, DialogFragmentChangelog.newInstance(), true);
                }}
        );
        changelog.SetLongClickListener(new View.OnLongClickListener(){
            @Override public boolean onLongClick(View v) {
                if (!tempDebugMode && !Helper.isDebugMode(ActivitySettings.this)) {
                    tempDebugMode = true;
                    recreate();
                }
                return true;
            }
        });
        about.AddSetting(changelog);



        //Debug card
        if (Helper.isDebugMode(this) || tempDebugMode) {
            CardSettings debug = new CardSettings(this, inflater, insertPoint, indexCount, R.layout.row_layout_setting_card, "Debug");
            //View Database
            debug.AddSetting(new Setting(inflater, R.drawable.ic_database_white_24dp, getString(R.string.title_settings_viewdatabase), getString(R.string.subtitle_settings_viewdatabase), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent dbmanager = new Intent(ActivitySettings.this, AndroidDatabaseManager.class);
                    startActivity(dbmanager);
                }
            }));
            //MyTab Data Import
            debug.AddSetting(new Setting(inflater, R.drawable.ic_clear_white_24dp, getString(R.string.title_settings_importmytab), getString(R.string.subtitle_settings_importmytab), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyTabImportClick();
                }
            }));
            //Insert 1000 dummy data points
            debug.AddSetting(new Setting(inflater, R.drawable.ic_plus_white_24dp, getString(R.string.title_settings_fakedata), "", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                //Add new budget
                Budget br = BudgetManager.getInstance().GetBudget("DummyData");
                if (br == null) {
                    br = new Budget("DummyData");
                    BudgetManager.getInstance().AddBudget(br);
                    DatabaseManager.getInstance().insertSetting(br, false);
                }

                for (int i = 0; i < 100; i++){
                    Random rand = new Random();

                    new_Transaction.TRANSACTION_TYPE[] types =new_Transaction. TRANSACTION_TYPE.values();
                    new_Transaction.TRANSACTION_TYPE TranType = types[rand.nextInt(2)];
                    ArrayList<Category> categories = CategoryManager.getInstance().GetCategories();
                    LocalDate beginning = new LocalDate(2000,1,1);

                    new_Transaction tr = new new_Transaction(TranType);

                    tr.SetSource("Source" + String.valueOf(Math.abs(rand.nextInt(2000))));
                    if (categories!=null && categories.size()>0) { tr.SetCategory(categories.get(rand.nextInt(categories.size()-1)).GetID()); }
                    tr.SetDescription("Desc" + String.valueOf(Math.abs(rand.nextInt(2000))));
                    tr.SetValue( 1.0d * Math.abs(rand.nextInt(1000)) );
                    tr.SetTimePeriod(new TimePeriod(beginning.plusDays(Math.abs(rand.nextInt(12000)))));

                    br.AddTransaction(tr);
                    DatabaseManager.getInstance().insert(tr, false);
                }

                //ProfileManager.getInstance().SelectProfile(ActivitySettings.this, pr);
                Helper.PrintUser(ActivitySettings.this, "Adding 100 transactions");
                }
            }));

            debug.AddSetting(new Setting(inflater, R.drawable.ic_exclamation_white_24dp, "Structure rewrite testing", "", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Initialize managers
                    BudgetManager bm = BudgetManager.getInstance();
                    DatabaseManager dm = DatabaseManager.getInstance();
                    PersonManager pm = PersonManager.getInstance();
                    CategoryManager cm = CategoryManager.getInstance();

                    //Remove old test budget
                    bm.RemoveBudget(bm.GetBudget("StructureBudget"));

                    //Create new budget
                    Budget b1 = new Budget("StructureBudget");
                    b1.SetStartDate(new LocalDate(2017,1,1));
                    b1.SetEndDate(new LocalDate(2017,1,31));
                    b1.SetSelected(true);

                    bm.AddBudget(b1);


                    //Create people
                    Person pA = pm.AddPerson("A");
                    Person pB = pm.AddPerson("B");


                    //Create category
                    Category c1 = cm.AddCategory("TestCategory", Color.BLUE);
                    Category c2 = cm.AddCategory("OtherCategory", Color.RED);

                    //Create timeperiods
                    TimePeriod tp1 = new TimePeriod();
                    tp1.SetDate(new LocalDate(2017,1,1));
                    tp1.SetRepeatFrequency(Repeat.WEEKLY);
                    tp1.SetRepeatEveryN(1);
                    tp1.SetRepeatDayOfWeekFromBinary("1000000");
                    tp1.SetRepeatUntil(RepeatUntil.TIMES);
                    tp1.SetRepeatANumberOfTimes(4);
                    tp1.SetDate(tp1.GetFirstOccurrence());

                    TimePeriod tp2 = new TimePeriod();
                    tp2.SetDate(new LocalDate(2017,1,3));


                    //Create new transactions
                    new_Transaction t1 = new new_Transaction();
                    t1.SetCategory(c1.GetID());
                    t1.SetSource("The Store");
                    t1.SetDescription("We bought some things");

                    t1.SetValue(10.0d);
                    t1.SetPaidBy(pB.GetID());
                    t1.SetSplit(0, 5.0d);
                    t1.SetSplit(pA.GetID(), 2.5d);
                    t1.SetSplit(pB.GetID(), 2.5d);
                    //t1.SetPaidBack(LocalDate.now());
                    t1.SetTimePeriod(tp1);

                    new_Transaction t2 = new new_Transaction();
                    t2.SetValue(20.0d);
                    t2.SetCategory(c2.GetID());
                    t2.SetSource("The Other Store");
                    t2.SetDescription("Herp Derp");
                    t2.SetTimePeriod(tp2);

                    b1.AddTransaction(t1);
                    b1.AddTransaction(t2);



                    //Database entries
                    dm.insertSetting(pA,true);
                    dm.insertSetting(pB,true);
                    dm.insertSetting(c1,true);
                    dm.insertSetting(c2,true);
                    dm.insertSetting(b1, true);
                    dm.insert(t1,true);
                    dm.insert(t2,true);
                    dm.insert(t1.GetID(), tp1, true);
                    dm.insert(t2.GetID(), tp2, true);



                    Helper.Print(ActivitySettings.this, "Added test data");


                    /*
                    ArrayList<Budget> bs = bm.GetBudgets();
                    for (int i = 0; i < bs.size(); i++){
                        Helper.PrintLong(ActivitySettings.this, "Budget:" +
                                "\nName:" + bs.get(i).GetName() +
                                "\nStartDate:" + bs.get(i).GetStartDate().toString(Helper.getString(R.string.date_format)) +
                                "\nEndDate:" + bs.get(i).GetEndDate().toString(Helper.getString(R.string.date_format)) +
                                "\nPeriod:" + bs.get(i).GetPeriod().toString()
                        );

                        ArrayList<new_Transaction> ts = bs.get(i).GetTransactions(bs.get(i).GetStartDate(), bs.get(i).GetEndDate(), new_Transaction.TRANSACTION_TYPE.Expense);
                        for (int ii = 0; ii < ts.size(); ii++){
                            Helper.PrintLong(ActivitySettings.this, "\nValue:" + String.valueOf(ts.get(ii).GetValue()) +
                                    "\nSource:" + ts.get(ii).GetSource() +
                                    "\nDescription:" + ts.get(ii).GetDescription() +
                                    "\nCategory:" + ts.get(ii).GetCategory() +
                                    "\nOccured:" + ts.get(ii).GetTimePeriod().GetDate().toString(Helper.getString(R.string.date_format))
                            );
                        }
                    }
                    */
                }
            }));

            /*
            //Completely unrelated thing
            debug.AddSetting(new Setting(inflater, R.drawable.ic_calendar_white_24dp, "Replace DateCreated with DateModified", "", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //try {
                        if (ProfileManager.isStoragePermissionGranted(ActivitySettings.this)){
                            File pic = new File(Environment.getExternalStorageDirectory() + "/DCIM/Restored/", "IMG_20161011_125619.jpg");

                            String[] splitTitle = pic.getName().split("_");
                            String IMG = splitTitle[0];
                            String day = splitTitle[1];
                            String time = splitTitle[2].replace(".jpg", "").replace(".png","").replace(".mp4","");

                            DateTimeFormatter dayFormatter = DateTimeFormat.forPattern("yyyyMMdd");
                            DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HHmmss");
                            if (IMG.equals("IMG")){
                                if (!day.equals("")){
                                    DateTime day2 = dayFormatter.parseDateTime(day);
                                    if (!time.equals("")){
                                        DateTime time2 = timeFormatter.parseDateTime(time);

                                        LocalDateTime origTime = new LocalDateTime(day2.getYear(), day2.getMonthOfYear(), day2.getDayOfMonth(), time2.getHourOfDay(), time2.getMinuteOfHour(), time2.getSecondOfMinute());

                                        ProfileManager.Print(ActivitySettings.this, "Setting LastModified to:" + origTime.toString("MMMM dd, yyyy: HH:mm:ss"));


                                        //boolean result = pic.setLastModified(origTime.toDateTime().getMillis());
                                        //ProfileManager.Print(ActivitySettings.this, "Result:" + result);
                                    }
                                }
                            }
                            else {
                                ProfileManager.Print(ActivitySettings.this, "File is not of type IMG_");
                            }
                        }

                    //} catch (Exception e) {
                    //    ProfileManager.Print(ActivitySettings.this, e.getMessage());
                    //}
                }
            }));
            */
            CardView c = (CardView) debug.getBase().findViewById(R.id.row_layout_settingscard);
            if (c != null) { c.setBackgroundColor(Color.YELLOW); }
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case 1: //Import/Export
                    ImportClick();
                    break;
                case 2: //Import MyTab
                    MyTabImportClick();
                    break;
            }
        }
    }

    public void ImportClick(){
        if (Helper.isStoragePermissionGranted() || Build.VERSION.SDK_INT < 23) {
            Intent intent = new Intent(ActivitySettings.this, ActivityDatabaseImport.class);
            startActivity(intent);
        }
        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                int titleID = R.string.tt_permission_writestorage1;
                int subTitleID = R.string.tt_permission_writestorage2;
                String[] permissions = new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE };
                Helper.OpenDialogFragment(this, DialogFragmentPermissionReasoning.newInstance(ActivitySettings.this, titleID, subTitleID, permissions, 1), true);
            } else {
                //Request permission
                if (Build.VERSION.SDK_INT >= 23) { //Unnecessary logic call to make the compiler shut up
                    ActivitySettings.this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        }
    }

    public void MyTabImportClick(){
        if (Helper.isStoragePermissionGranted() || Build.VERSION.SDK_INT < 23) {
            new AlertDialog.Builder(ActivitySettings.this).setTitle(R.string.confirm_areyousure_deleteall)
                    .setPositiveButton(R.string.action_continue, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            String result = MyTabConversion.load(ActivitySettings.this);
                            if (!result.equals("")){
                                Helper.PrintUser(ActivitySettings.this, "Error:" + result);
                            }
                            else {
                                Helper.PrintUser(ActivitySettings.this, "Tab Data successfully loaded");
                            }
                        }})
                    .setNegativeButton(R.string.action_cancel, null)
                    .create().show();
        }
        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ActivitySettings.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                int titleID = R.string.tt_permission_writestorage3;
                int subTitleID = R.string.tt_permission_writestorage4;
                String[] permissions = new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE };
                Helper.OpenDialogFragment(ActivitySettings.this, DialogFragmentPermissionReasoning.newInstance(ActivitySettings.this, titleID, subTitleID, permissions, 2), true);
            } else {
                //Request permission
                if (Build.VERSION.SDK_INT >= 23) { //Unnecessary logic call to make the compiler shut up
                    ActivitySettings.this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                }
            }
        }
    }



}

