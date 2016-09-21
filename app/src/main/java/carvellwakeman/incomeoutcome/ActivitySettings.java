package carvellwakeman.incomeoutcome;


import android.Manifest;
import android.content.Context;
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
import android.widget.LinearLayout;

import java.util.Arrays;


public class ActivitySettings extends AppCompatActivity
{
    Toolbar toolbar;

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
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();


        //Populate settings
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.settings_layout);

        insertPoint.removeAllViews();

        //Setting Categories
        CardSettings profilesPeopleCategories = new CardSettings(this, inflater, insertPoint, 0, R.layout.row_layout_setting_card, getString(R.string.title_settings_profilespeoplecategories));
        //Manage profiles
        profilesPeopleCategories.AddSetting(new Setting(inflater, R.drawable.ic_account_white_24dp, getString(R.string.title_manageprofiles), null,
                new View.OnClickListener() { @Override public void onClick(View v) {
                    //ProfileManager.OpenDialogFragment(ActivitySettings.this, new DialogFragmentManageProfiles(), mIsLargeLayout);
                    startActivity(new Intent(ActivitySettings.this, ActivityManageProfiles.class));
                }}
        ));
        //Manage people
        profilesPeopleCategories.AddSetting(new Setting(inflater, R.drawable.ic_face_white_24dp, getString(R.string.title_managepeople), null,
                new View.OnClickListener() { @Override public void onClick(View v) {
                    //ProfileManager.OpenDialogFragment(ActivitySettings.this, new DialogFragmentManagePeople(), mIsLargeLayout);
                    startActivity(new Intent(ActivitySettings.this, ActivityManagePeople.class));
                }}
        ));
        //Manage categories
        profilesPeopleCategories.AddSetting(new Setting(inflater, R.drawable.ic_view_list_white_24dp, getString(R.string.title_managecategories), null,
                new View.OnClickListener() { @Override public void onClick(View v) {
                    //ProfileManager.OpenDialogFragment(ActivitySettings.this, new DialogFragmentManageCategories(), mIsLargeLayout);
                    startActivity(new Intent(ActivitySettings.this, ActivityManageCategories.class));
                }}
        ));

        CardSettings database = new CardSettings(this, inflater, insertPoint, 1, R.layout.row_layout_setting_card, getString(R.string.title_settings_database));
        //Import and Export
        database.AddSetting(new Setting(inflater, R.drawable.ic_file_white_24dp, getString(R.string.title_settings_importexport), getString(R.string.subtitle_settings_importexport),
                new View.OnClickListener() { @Override public void onClick(View v) {
                    ImportClick();
                }}
        ));
        //Delete all data
        database.AddSetting(new Setting(inflater, R.drawable.ic_delete_white_24dp, getString(R.string.title_settrings_deletedata), getString(R.string.subtitle_settings_deletedata),
                new View.OnClickListener() { @Override public void onClick(View v) {
                    ProfileManager.OpenDialogFragment(ActivitySettings.this, DialogFragmentDeleteData.newInstance(ActivitySettings.this, new ProfileManager.CallBack() { @Override public void call() {

                    }}), true);
                }}));
        //Load default categories
        database.AddSetting(new Setting(inflater, R.drawable.ic_database_plus_white_24dp, getString(R.string.title_settings_defaultcategories), getString(R.string.subtitle_settings_defaultcategories), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ActivitySettings.this).setTitle(R.string.confirm_areyousure_deleteall)
                        .setPositiveButton(R.string.action_continue, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {
                                ProfileManager.getInstance().RemoveAllCategories(ActivitySettings.this);
                                ProfileManager.getInstance().LoadDefaultCategories(ActivitySettings.this);
                            }})
                        .setNegativeButton(R.string.action_cancel, null)
                        .create().show();
            }
        }));
        if (ProfileManager.isDebugMode(this)) {
            CardSettings debug = new CardSettings(this, inflater, insertPoint, 2, R.layout.row_layout_setting_card, "Debug");
            //View Database
            debug.AddSetting(new Setting(inflater, R.drawable.ic_database_white_24dp, "View Database", "View and edit current database details", new View.OnClickListener() {
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
            CardView c = (CardView) debug.getView().findViewById(R.id.row_layout_settingscard);
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
        if (ProfileManager.isStoragePermissionGranted(ActivitySettings.this) || Build.VERSION.SDK_INT < 23) {
            Intent intent = new Intent(ActivitySettings.this, ActivityDatabaseImport.class);
            startActivity(intent);
        }
        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                int titleID = R.string.tt_permission_writestorage1;
                int subTitleID = R.string.tt_permission_writestorage2;
                String[] permissions = new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE };
                ProfileManager.OpenDialogFragment(this, DialogFragmentPermissionReasoning.newInstance(ActivitySettings.this, titleID, subTitleID, permissions, 1), true);
            } else {
                //Request permission
                ActivitySettings.this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    public void MyTabImportClick(){
        if (ProfileManager.isStoragePermissionGranted(ActivitySettings.this) || Build.VERSION.SDK_INT < 23) {
            new AlertDialog.Builder(ActivitySettings.this).setTitle(R.string.confirm_areyousure_deleteall)
                    .setPositiveButton(R.string.action_continue, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            String result = MyTabConversion.load(ActivitySettings.this);
                            if (!result.equals("")){
                                ProfileManager.PrintUser(ActivitySettings.this, "Error:" + result);
                            }
                            else {
                                ProfileManager.PrintUser(ActivitySettings.this, "Tab Data successfully loaded");
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
                ProfileManager.OpenDialogFragment(ActivitySettings.this, DialogFragmentPermissionReasoning.newInstance(ActivitySettings.this, titleID, subTitleID, permissions, 2), true);
            } else {
                //Request permission
                ActivitySettings.this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }
        }
    }



}
