package carvellwakeman.incomeoutcome;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;

import java.io.File;

public class ActivityDatabaseImport extends AppCompatActivity {

    TextView textView_backupnotice;

    AdapterDatabaseImports adapter;

    android.support.v7.widget.Toolbar toolbar;
    //MenuItem button_save;

    FloatingActionButton button_new;
    Button button_restorebackup;

    NpaLinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView_files;

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importdatabase);
        //public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.dialog_importdatabase, container, false);
        //view.setBackgroundColor(Color.WHITE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        textView_backupnotice = (TextView) findViewById(R.id.textView_dialogin_backupnotice);

        button_restorebackup = (Button) findViewById(R.id.button_dialogin_backup);

        recyclerView_files = (RecyclerView) findViewById(R.id.dialog_recyclerView_files);


        //Set recyclerview adapter
        adapter = new AdapterDatabaseImports(this);
        recyclerView_files.setAdapter(adapter);

        //LinearLayoutManager for RecyclerView
        linearLayoutManager = new NpaLinearLayoutManager(this);
        linearLayoutManager.setOrientation(NpaLinearLayoutManager.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        recyclerView_files.setLayoutManager(linearLayoutManager);


        //Configure toolbar
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //toolbar.inflateMenu(R.menu.toolbar_menu_save);
        toolbar.setTitle(R.string.title_importdatabase);
        setSupportActionBar(toolbar);

        //button_save = toolbar.getMenu().findItem(R.id.toolbar_save);
        //button_save.setVisible(false);


        //Setup backup restore button if there is a backup
        if (ProfileManager.DoesBackupExist()){
            textView_backupnotice.setVisibility(View.VISIBLE);
            button_restorebackup.setEnabled(true);
            button_restorebackup.setText(R.string.info_restorebackup);
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


        // Inflate the layout to use as dialog or embedded fragment
        //return view;
    }


    //Toolbar button handling
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    //Import Database
    public void ImportDatabase(File file){
        ProfileManager.ImportDatabase(file);
    }


    /* The system calls this only when creating the layout in a dialog.
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.setCanceledOnTouchOutside(false); //Disable closing dialog by clicking outside of it
        return dialog;
    }


    @Override
    public void onResume() {
        super.onResume();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //dialog.getWindow().setBackgroundDrawable(null);
        }
    }
    */
}