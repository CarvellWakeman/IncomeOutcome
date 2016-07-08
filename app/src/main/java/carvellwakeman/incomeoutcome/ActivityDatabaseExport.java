package carvellwakeman.incomeoutcome;


import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import org.joda.time.LocalDate;

import java.util.ArrayList;

public class ActivityDatabaseExport extends AppCompatActivity {

    boolean SaveButtonState;
    boolean OverrideState;

    TextView textView_directory;

    Toolbar toolbar;
    MenuItem button_export;

    TextInputLayout TIL;
    EditText editText_filename;

    SwitchCompat switch_override;

    ArrayList<String> existingDatabases;

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_export);
        //public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.dialog_exportdatabase, container, false);
        //view.setBackgroundColor(Color.WHITE);

        SaveButtonState = false;
        OverrideState = false;

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        existingDatabases = ProfileManager.GetImportDatabaseFilesString();

        textView_directory = (TextView) findViewById(R.id.textView_dialogex_directory);

        switch_override = (SwitchCompat) findViewById(R.id.switch_override_export);


        TIL = (TextInputLayout) findViewById(R.id.TIL_dialog_filename);

        TIL.setErrorEnabled(true);
        editText_filename = TIL.getEditText();

        //Configure toolbar
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.inflateMenu(R.menu.toolbar_menu_export);
        toolbar.setTitle(R.string.title_exportdatabase);
        setSupportActionBar(toolbar);
        //button_export = toolbar.getMenu().findItem(R.id.toolbar_save);
        //SetSaveButtonEnabled(true);


        textView_directory.setText("*Export to " + ProfileManager.GetExportDirectory());

        editText_filename.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
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
        });

        editText_filename.setText("data_export_" + (new LocalDate()).toString(ProfileManager.simpleDateFormatSaving));


        switch_override.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SetSaveButtonEnabled(isChecked && OverrideState);
            }
        });

        // Inflate the layout to use as dialog or embedded fragment
        //return view;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_export, menu);
        button_export = menu.findItem(R.id.toolbar_export);
        editText_filename.setText(editText_filename.getText()); //Refresh export button
        return true;
    }


    //Toolbar button handling
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            case R.id.toolbar_export: //EXPORT button

                String str = editText_filename.getText().toString();
                if (!str.equals("")) {
                    //if (!existingDatabases.contains(str)) {
                        ProfileManager.ExportDatabase(str);
                        finish();
                    //}
                }
                return true;
            default:
                return false;
        }
    }

    //Update positive button text
    public void SetSaveButtonEnabled(Boolean enabled){
        SaveButtonState = !SaveButtonState;

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