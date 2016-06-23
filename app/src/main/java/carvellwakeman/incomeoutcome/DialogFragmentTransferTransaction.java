package carvellwakeman.incomeoutcome;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;

public class DialogFragmentTransferTransaction extends DialogFragment
{
    Profile current;

    AdapterTransferTransaction adapter;

    TextView textView_title;

    Button button_positive;
    Button button_negative;


    NpaLinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;

    static DialogFragmentTransferTransaction newInstance(Profile current) {
        DialogFragmentTransferTransaction fg = new DialogFragmentTransferTransaction();
        Bundle args = new Bundle();
        args.putSerializable("profile", current);
        fg.setArguments(args);

        return fg;
    }

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_transfertransaction, container, false);
        view.setBackgroundColor(Color.WHITE);

        current = (Profile)getArguments().getSerializable("profile");

        textView_title = (TextView) view.findViewById(R.id.textView_dialogtt_title);

        button_positive = (Button) view.findViewById(R.id.button_dialogtt_positive);
        button_negative = (Button) view.findViewById(R.id.button_dialogtt_negative);


        recyclerView = (RecyclerView) view.findViewById(R.id.dialog_recyclerView_transfertransaction);


        //Set profiles adapter
        adapter = new AdapterTransferTransaction(this);
        recyclerView.setAdapter(adapter);

        //LinearLayoutManager for RecyclerView
        linearLayoutManager = new NpaLinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(NpaLinearLayoutManager.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(linearLayoutManager);

        //Delete all
        button_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setTitle(R.string.confirm_areyousure_deletetransactions)
                        .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ProfileManager.DeleteProfile(current);
                                dismiss();
                            }})
                        .setNegativeButton(R.string.action_cancel, null)
                        .create().show();

            }
        });

        //Close fragment/dialog
        button_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });



        // Inflate the layout to use as dialog or embedded fragment
        return view;
    }


    //Transfer Transaction
    public void TransferTransaction(Profile pr){
        current.TransferAllTransactions(pr);
        ProfileManager.DeleteProfile(current);
        dismiss();
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