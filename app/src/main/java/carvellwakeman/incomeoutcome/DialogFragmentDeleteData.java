package carvellwakeman.incomeoutcome;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioButton;
import org.joda.time.LocalDate;

public class DialogFragmentDeleteData extends DialogFragment {

    Activity _parent;
    ProfileManager.CallBack _callBack;

    RadioButton radioButton_deleteall;
    RadioButton radioButton_deletetransactions;
    RadioButton radioButton_deletepeople;
    RadioButton radioButton_deletecategories;

    Button button_positive;
    Button button_negative;


    static DialogFragmentDeleteData newInstance(Activity caller, ProfileManager.CallBack callBack) {
        DialogFragmentDeleteData fg = new DialogFragmentDeleteData();
        fg._callBack = callBack;
        fg._parent = caller;
        return fg;
    }

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_deletedata, container, false);
        view.setBackgroundColor(Color.WHITE);

        radioButton_deleteall = (RadioButton) view.findViewById(R.id.radioButton_deleteall);
        radioButton_deletetransactions = (RadioButton) view.findViewById(R.id.radioButton_deletetransactions);
        radioButton_deletepeople = (RadioButton) view.findViewById(R.id.radiobutton_deletepeople);
        radioButton_deletecategories = (RadioButton) view.findViewById(R.id.radiobutton_deletecategories);

        button_positive = (Button) view.findViewById(R.id.button_dialogpb_positive);
        button_negative = (Button) view.findViewById(R.id.button_dialogpb_negative);

        //Button listener
        button_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioButton_deleteall.isChecked()){ //Delete ALL data
                    new AlertDialog.Builder(_parent).setTitle(R.string.confirm_areyousure_deleteall)
                            .setPositiveButton(R.string.action_deleteall, new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {
                                    ProfileManager.getInstance().DBDelete(_parent);
                                    ProfileManager.getInstance().ClearAllObjects();
                                    ProfileManager.getInstance().GetDatabaseHelper().TryCreateDatabase();
                                    if (_callBack != null) { _callBack.call(); }
                                    DialogFragmentDeleteData.this.dismiss();
                                    dismiss();
                                }})
                            .setNegativeButton(R.string.action_cancel, null)
                            .create().show();
                }
                else if (radioButton_deletetransactions.isChecked()){ //Delete profiles and transactions
                    new AlertDialog.Builder(_parent).setTitle(R.string.confirm_areyousure_deletesingle)
                            .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {
                                    ProfileManager.getInstance().RemoveAllProfilesAndTransactions(_parent);
                                    ProfileManager.getInstance().DBDeleteTransactionsAndProfiles(_parent);
                                    if (_callBack != null) { _callBack.call(); }
                                    DialogFragmentDeleteData.this.dismiss();
                                    dismiss();
                                }})
                            .setNegativeButton(R.string.action_cancel, null)
                            .create().show();
                }
                else if (radioButton_deletepeople.isChecked()){ //Delete people
                    new AlertDialog.Builder(_parent).setTitle(R.string.confirm_areyousure_deletesingle)
                            .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {
                                    ProfileManager.getInstance().RemoveAllPeople(_parent);
                                    if (_callBack != null) { _callBack.call(); }
                                    DialogFragmentDeleteData.this.dismiss();
                                    dismiss();
                                }})
                            .setNegativeButton(R.string.action_cancel, null)
                            .create().show();
                }
                else if (radioButton_deletecategories.isChecked()){ //Delete categories
                    new AlertDialog.Builder(_parent).setTitle(R.string.confirm_areyousure_deletesingle)
                            .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {
                                    ProfileManager.getInstance().RemoveAllCategories(_parent);
                                    if (_callBack != null) { _callBack.call(); }
                                    DialogFragmentDeleteData.this.dismiss();
                                    dismiss();
                                }})
                            .setNegativeButton(R.string.action_cancel, null)
                            .create().show();
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