package carvellwakeman.incomeoutcome;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class NumberDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Calling activity
        final NewExpenseActivity activity = (NewExpenseActivity) getActivity();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Inflater
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.number_picker, null);
        builder.setView(view);

        //Edit Text
        final TextInputLayout TIL = (TextInputLayout)view.findViewById(R.id.TIL_numberPicker);
        final EditText numberInput = (TIL != null ? TIL.getEditText() : null);

        //Positive and negative buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (numberInput != null) {
                    activity.numberPickerDialogGetValue(Integer.valueOf(numberInput.getText().toString()));
                }
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                activity.numberPickerDialogGetValue(-1);
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
