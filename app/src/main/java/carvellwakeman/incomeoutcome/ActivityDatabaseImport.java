package carvellwakeman.incomeoutcome;


import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import org.joda.time.LocalDate;

import java.io.File;
import java.util.ArrayList;

import static carvellwakeman.incomeoutcome.ProfileManager.Print;

public class ActivityDatabaseImport extends AppCompatActivity {

    boolean menustate = false;
    boolean SaveButtonState = false;
    boolean OverrideState = false;

    AdapterDatabaseImports adapter;

    AppBarLayout appBarLayout;
    android.support.v7.widget.Toolbar toolbar;

    Button button_restorebackup;
    Button button_exportOpen;
    MenuItem button_export;

    TextInputLayout TIL;
    EditText editText_filename;

    SwitchCompat switch_override;

    NpaLinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView_files;

    ArrayList<String> existingDatabases;

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_import);
        //public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.dialog_importdatabase, container, false);
        //view.setBackgroundColor(Color.WHITE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);
        AppBarLayoutExpanded(false);

        button_restorebackup = (Button) findViewById(R.id.button_dialogin_backup);
        button_exportOpen = (Button) findViewById(R.id.button_dialogex_export);

        existingDatabases = ProfileManager.GetImportDatabaseFilesString();

        switch_override = (SwitchCompat) findViewById(R.id.switch_override_export);

        recyclerView_files = (RecyclerView) findViewById(R.id.dialog_recyclerView_files);


        TIL = (TextInputLayout) findViewById(R.id.TIL_dialog_filename);
        TIL.setErrorEnabled(true);
        editText_filename = TIL.getEditText();


        //Set recyclerview adapter
        adapter = new AdapterDatabaseImports(this);
        recyclerView_files.setAdapter(adapter);

        //LinearLayoutManager for RecyclerView
        linearLayoutManager = new NpaLinearLayoutManager(this);
        linearLayoutManager.setOrientation(NpaLinearLayoutManager.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        recyclerView_files.setLayoutManager(linearLayoutManager);


        //Configure toolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.inflateMenu(R.menu.toolbar_menu_export);
        toolbar.setTitle(R.string.title_importdatabase);
        setSupportActionBar(toolbar);



        //Setup backup restore button if there is a backup
        if (ProfileManager.DoesBackupExist()){
            button_restorebackup.setEnabled(true);
            button_restorebackup.setText(R.string.info_backupnotice);
        }

        //Button listeners
        button_restorebackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ActivityDatabaseImport.this).setTitle(R.string.confirm_areyousure_deleteall)
                        .setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ProfileManager.ImportDatabaseBackup();
                                finish();
                            }})
                        .setNegativeButton(R.string.confirm_no, null)
                        .create().show();
            }
        });

        button_exportOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckCanExport();
                ToggleMenu(true);
            }
        });


        editText_filename.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { CheckCanExport(); }
        });

        //editText_filename.setText(ProfileManager.getString(R.string.placeholder_exportfilename) + (new LocalDate()).toString(ProfileManager.simpleDateFormatSaving));
        editText_filename.setText(getString(R.string.format_exportfilename, (new LocalDate()).toString(ProfileManager.simpleDateFormatSaving)));

        switch_override.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SetSaveButtonEnabled(isChecked && OverrideState);
            }
        });

    }


    @Override
    public void onBackPressed() {
        if (menustate){ ToggleMenu(false); }
        else { super.onBackPressed(); }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_export, menu);
        button_export = menu.findItem(R.id.toolbar_export);
        button_export.setVisible(false);
        editText_filename.setText(editText_filename.getText()); //Refresh export button
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (menustate) { ToggleMenu(false); }
                else { finish(); }
                return true;
            case R.id.toolbar_export: //EXPORT button
                String str = editText_filename.getText().toString();
                if (!str.equals("")) {
                    ProfileManager.ExportDatabase(str);
                    ToggleMenu(false);
                    ProfileManager.hideSoftKeyboard(this, editText_filename);
                    existingDatabases = ProfileManager.GetImportDatabaseFilesString();
                    UpdateAdapter();
                }
                return true;
        }
        return false;
    }


    //Import Database
    public void ImportDatabase(final String path, final DialogFragmentManagePPC dialogFragment){

        final File file = ProfileManager.GetDatabaseByPath(path);
        final int version = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE).getVersion();
        final int currentVersion = ProfileManager.GetNewestDatabaseVersion();

        if (version <= currentVersion) { //Version check
            new AlertDialog.Builder(this).setTitle(R.string.confirm_areyousure_deleteall)
                    .setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProfileManager.ImportDatabase(file);
                            dialogFragment.dismiss();
                            finish();
                            dialog.dismiss();
                        }})
                    .setNegativeButton(R.string.confirm_no, null)
                    .create().show();
        }
        else {
            Print("ERROR: Selected database is a newer version than this app supports.");
        }
    }
    public void DeleteDatabase(final String path, final DialogFragmentManagePPC dialogFragment){
        new AlertDialog.Builder(this).setTitle(R.string.confirm_areyousure_deletesingle)
                .setPositiveButton(R.string.action_deleteitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (path != null) {
                            ProfileManager.DeleteDatabaseByPath(path);
                            UpdateAdapter();

                            Print("File Deleted");

                            CheckCanExport();

                            dialogFragment.dismiss();
                            dialog.dismiss();
                        }
                    }})
                .setNegativeButton(R.string.action_cancel, null)
                .create().show();
    }


    //Update positive button text
    public void SetSaveButtonEnabled(Boolean enabled){
        SaveButtonState = enabled;

        if (button_export != null) {
            button_export.setEnabled(enabled);
            if (button_export.getIcon() != null) button_export.getIcon().setAlpha((enabled ? 255 : 130));
        }
    }

    public void SetOverrideState(boolean enabled){
        OverrideState = enabled;

        switch_override.setEnabled(enabled);
        switch_override.setClickable(enabled);
        switch_override.setChecked(false);
    }


    public void CheckCanExport(){
        existingDatabases = ProfileManager.GetImportDatabaseFilesString();

        String str = editText_filename.getText().toString();

        if (!str.equals("")) {
            if (existingDatabases != null && !existingDatabases.contains(str) || existingDatabases == null) {
                SetSaveButtonEnabled(true);
                SetOverrideState(false);
                TIL.setError("");
            } else {
                SetSaveButtonEnabled(false);
                SetOverrideState(true);
                TIL.setError("File name already exists");
            }
        }
        else{
            SetSaveButtonEnabled(false);
            SetOverrideState(false);
            TIL.setError("You need to enter a file name");
        }
    }

    public void ToggleMenu(boolean exportState){
        menustate = exportState;

        //Expand export menu
        AppBarLayoutExpanded(exportState);

        //Export button visibility
        button_export.setVisible(exportState);

        //Toolbar subtitle
        toolbar.setSubtitle( (exportState ? ProfileManager.GetExportDirectory() : "") );

        //Set title
        toolbar.setTitle( (exportState ? R.string.title_exportdatabase : R.string.title_importdatabase) );
    }

    public void AppBarLayoutExpanded(boolean expanded){
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
        lp.height = (expanded ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) getResources().getDimension(R.dimen.toolbar_size));
    }

    public void UpdateAdapter(){ //WHY do I have to keep doing this? Recyclerview giving vague stack trace error when just using adapter.notifyDataSetChanged(). Involves index out of bounds exception, but no clear indication as to why.
        adapter = new AdapterDatabaseImports(this);
        recyclerView_files.setAdapter(adapter);
    }
}