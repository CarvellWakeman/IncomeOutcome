package carvellwakeman.incomeoutcome;


import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.joda.time.LocalDate;

import java.io.File;
import java.util.ArrayList;

public class DialogFragmentDatabaseExport extends DialogFragment {

    TextView textView_title;
    TextView textView_directory;

    Button button_positive;
    Button button_negative;

    TextInputLayout TIL;
    EditText editText_filename;

    ArrayList<String> existingDatabases;

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_exportdatabase, container, false);
        view.setBackgroundColor(Color.WHITE);

        existingDatabases = ProfileManager.GetImportDatabaseFilesString();

        textView_title = (TextView) view.findViewById(R.id.textView_dialogin_title);
        textView_directory = (TextView) view.findViewById(R.id.textView_dialogex_directory);

        button_positive = (Button) view.findViewById(R.id.button_dialogex_positive);
        button_negative = (Button) view.findViewById(R.id.button_dialogex_negative);

        TIL = (TextInputLayout)view.findViewById(R.id.TIL_dialog_filename);


        TIL.setErrorEnabled(true);
        editText_filename = TIL.getEditText();


        textView_directory.setText("*Export to " + ProfileManager.GetExportDirectory());

        editText_filename.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = editText_filename.getText().toString();

                if (!str.equals("")) {
                    if (existingDatabases != null) {
                        if (!existingDatabases.contains(str)) {
                            SetPositiveButtonEnabled(true);
                            TIL.setError("");
                        }
                        else {
                            SetPositiveButtonEnabled(false);
                            TIL.setError("File name already exists");
                        }
                    }
                }
                else{ SetPositiveButtonEnabled(false); TIL.setError("You need to enter a file name"); }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        editText_filename.setText("data_export_" + (new LocalDate()).toString(ProfileManager.simpleDateFormatSaving));

        //Button listeners
        button_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = editText_filename.getText().toString();
                if (!str.equals("")) {
                    if (!existingDatabases.contains(str)) {
                        ProfileManager.ExportDatabase(str);
                        dismiss();
                    }
                }
            }
        });


        //Close fragment/dialog
        button_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { dismiss(); }
        });

        // Inflate the layout to use as dialog or embedded fragment
        return view;
    }

    //Update positive button text
    public void SetPositiveButtonEnabled(Boolean enabled){
        button_positive.setEnabled(enabled);
    }


    /** The system calls this only when creating the layout in a dialog. */
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
}