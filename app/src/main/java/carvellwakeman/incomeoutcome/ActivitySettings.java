package carvellwakeman.incomeoutcome;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

    //Settings population
    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Boolean mIsLargeLayout = false; //TODO Handle this in values


        //Find Views
        toolbar = (Toolbar) findViewById(R.id.toolbar_settings);

        //Populate settings
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.settings_layout);


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
                        if (ProfileManager.isStoragePermissionGranted(ActivitySettings.this)) {
                            //ProfileManager.OpenDialogFragment(ActivitySettings.this, new DialogFragmentDatabaseImport(), mIsLargeLayout);
                            startActivity(new Intent(ActivitySettings.this, ActivityDatabaseImport.class));
                        }
                        else {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(ActivitySettings.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                                ProfileManager.OpenDialogFragment(ActivitySettings.this, DialogFragmentPermissionReasoning.newInstance(ActivitySettings.this, new int[]{ R.string.tt_permission_writestorage1 }, new int[]{ R.string.tt_permission_writestorage2 }, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }), true);
                            } else {
                                //Request permission
                                ActivityCompat.requestPermissions(ActivitySettings.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                        }
                    }}
            ));
        //Delete all data
            database.AddSetting(new Setting(inflater, R.drawable.ic_delete_white_24dp, getString(R.string.title_settings_deletealldata), getString(R.string.subtitle_settings_deletealldata),
                    new View.OnClickListener() { @Override public void onClick(View v) {
                        new AlertDialog.Builder(ActivitySettings.this).setTitle(R.string.confirm_areyousure_deleteall)
                                .setPositiveButton(R.string.action_deleteitem, new DialogInterface.OnClickListener() {
                                    @Override public void onClick(DialogInterface dialog, int which) {
                                        ProfileManager.getInstance().DeleteDatabase();
                                        ProfileManager.getInstance().ClearAllObjects();
                                        ProfileManager.getInstance().GetDatabaseHelper().TryCreateDatabase();
                                    }})
                                .setNegativeButton(R.string.action_cancel, null)
                                .create().show();
                    }}
            ));
        //Load default categories
            database.AddSetting(new Setting(inflater, R.drawable.ic_database_plus_white_24dp, getString(R.string.title_settings_defaultcategories), getString(R.string.subtitle_settings_defaultcategories), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileManager.getInstance().RemoveAllCategories();
                    ProfileManager.getInstance().LoadDefaultCategories(ActivitySettings.this);
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
            CardView c = (CardView) debug.getView().findViewById(R.id.row_layout_settingscard);
            if (c != null) { c.setBackgroundColor(Color.YELLOW); }
        }




        //Insert categories
        //profilesPeopleCategories.insert(insertPoint, 0);
        //database.insert(insertPoint, 1);
         //debug.insert(insertPoint, 2);
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(ActivitySettings.this, ActivityDatabaseImport.class));
                }
                return;
            }
        }
    }


    //Return results from child activities
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case 1:
                    break;
            }
        }
    }



}
