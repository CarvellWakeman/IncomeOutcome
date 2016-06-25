package carvellwakeman.incomeoutcome;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


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
        //toolbar.inflateMenu(R.menu.toolbar_menu_sortfilter);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




        //Setting Categories
        SettingCard profilesPeopleCategories = new SettingCard( inflater, getString(R.string.title_settings_profilespeoplecategories));
        //Manage profiles
            profilesPeopleCategories.AddSetting(new Setting(inflater, R.drawable.ic_account_white_24dp, getString(R.string.title_manageprofiles), null,
                    new View.OnClickListener() { @Override public void onClick(View v) {
                        //ProfileManager.OpenDialogFragment(ActivitySettings.this, new DialogFragmentManageProfiles(), mIsLargeLayout);
                        startActivity(new Intent(ActivitySettings.this, ActivityManageProfiles.class));
                    }}
            ));
        //Manage people
            profilesPeopleCategories.AddSetting(new Setting(inflater, R.drawable.ic_face_white_24dp, getString(R.string.title_manageotherpeople), null,
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

        SettingCard database = new SettingCard(inflater, getString(R.string.title_settings_database));
        //Import
            database.AddSetting(new Setting(inflater, R.drawable.ic_input_white_24dp, getString(R.string.title_settings_import), getString(R.string.subtitle_settings_import),
                    new View.OnClickListener() { @Override public void onClick(View v) {
                        if (ProfileManager.isStoragePermissionGranted())
                            //ProfileManager.OpenDialogFragment(ActivitySettings.this, new DialogFragmentDatabaseImport(), mIsLargeLayout);
                            startActivity(new Intent(ActivitySettings.this, ActivityDatabaseImport.class));
                    }}
            ));
        //Export
            database.AddSetting(new Setting(inflater, R.drawable.ic_export_white_24dp, getString(R.string.title_settings_export), null,
                    new View.OnClickListener() { @Override public void onClick(View v) {
                        if (ProfileManager.isStoragePermissionGranted())
                            //ProfileManager.OpenDialogFragment(ActivitySettings.this, new DialogFragmentDatabaseExport(), mIsLargeLayout);
                            startActivity(new Intent(ActivitySettings.this, ActivityDatabaseExport.class));
                    }}
            ));
        //Delete all data
            database.AddSetting(new Setting(inflater, R.drawable.ic_delete_white_24dp, getString(R.string.title_settings_deletealldata), getString(R.string.subtitle_settings_deletealldata),
                    new View.OnClickListener() { @Override public void onClick(View v) {
                        new AlertDialog.Builder(ActivitySettings.this).setTitle(R.string.confirm_areyousure_deleteall)
                                .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                                    @Override public void onClick(DialogInterface dialog, int which) {
                                        ProfileManager.DeleteDatabase();
                                        ProfileManager.ClearAllObjects();
                                        ProfileManager.LoadDefaultSettings();
                                    }})
                                .setNegativeButton(R.string.action_cancel, null)
                                .create().show();
                    }}
            ));




        //Insert categories
        profilesPeopleCategories.insert(insertPoint, 0);
        database.insert(insertPoint, 1);
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
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
