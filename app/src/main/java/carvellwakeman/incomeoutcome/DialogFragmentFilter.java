package carvellwakeman.incomeoutcome;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class DialogFragmentFilter extends DialogFragment
{
    Transaction.TRANSACTION_TYPE _activityType = Transaction.TRANSACTION_TYPE.Expense;

    ActivityDetailsTransaction _parent;
    Budget _budget;
    Helper.FILTER_METHODS _method;

    ArrayAdapter adapter;

    String _title;


    TextView textView_title;
    Spinner spinner_filter;
    Button button_positive;
    Button button_negative;


    static DialogFragmentFilter newInstance(ActivityDetailsTransaction parent, Budget budget, Helper.FILTER_METHODS method, String title, Transaction.TRANSACTION_TYPE activityType) {
        DialogFragmentFilter fg = new DialogFragmentFilter();
        fg._parent = parent;
        fg._budget = budget;
        fg._method = method;
        fg._title = title;
        fg._title = title;
        fg._activityType = activityType;

        switch (method) {
            case CATEGORY:
                if (CategoryManager.getInstance().GetCategoriesCount() == 0) { Helper.PrintUser(parent, parent.getString(R.string.tt_nocategories) ); return null; }
                fg.adapter = new ArrayAdapter<>(parent, R.layout.spinner_dropdown_title, CategoryManager.getInstance().GetCategoriesTitles());
                break;
            case SOURCE:
                if (budget != null) {
                    ArrayList<String> sources = new ArrayList<>();
                    for (Transaction tran : budget.GetTransactions(activityType)){
                        if (!sources.contains(tran.GetSource())) {
                            if (tran.GetSource().equals("")){
                                sources.add(parent.getString(R.string.info_nosource));
                            } else {
                                sources.add(tran.GetSource());
                            }
                        }
                    }

                    fg.adapter = new ArrayAdapter<>(parent, R.layout.spinner_dropdown_title, sources);
                }
                break;
            case PAIDBY:
                ArrayList<String> peopleIncludingMe = PersonManager.getInstance().GetPeopleNames();
                peopleIncludingMe.add(parent.getString(R.string.format_me));
                fg.adapter = new ArrayAdapter<>(parent, R.layout.spinner_dropdown_title, peopleIncludingMe);
                break;
            case SPLITWITH:
                if (PersonManager.getInstance().GetPeopleCount() == 0) { Helper.PrintUser(parent, parent.getString(R.string.tt_nopeople) ); return null; }
                ArrayList<String> peopleIncludingNotSplit = PersonManager.getInstance().GetPeopleNames();
                peopleIncludingNotSplit.add(parent.getString(R.string.tt_not_split));
                fg.adapter = new ArrayAdapter<>(parent, R.layout.spinner_dropdown_title, peopleIncludingNotSplit);
                break;
            case PAIDBACK:
                String[] yesno = {parent.getString(R.string.confirm_no), parent.getString(R.string.confirm_yes)};
                fg.adapter = new ArrayAdapter<>(parent, R.layout.spinner_dropdown_title, yesno);
                break;
        }

        return fg;
    }

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_filter, container, false);
        view.setBackgroundColor(Color.WHITE);

        textView_title = (TextView) view.findViewById(R.id.textView_dialogtt_title);

        spinner_filter = (Spinner) view.findViewById(R.id.spinner_filter);

        button_positive = (Button) view.findViewById(R.id.button_dialogtt_positive);
        button_negative = (Button) view.findViewById(R.id.button_dialogtt_negative);

        //Set title
        textView_title.setText(_title);

        //Populate spinner
        if (adapter != null) {
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_list_primary);
            spinner_filter.setAdapter(adapter);
        }


        //Positive
        button_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            _parent.filterMethods.put(_method, spinner_filter.getSelectedItem().toString());
            _parent.RefreshActivity();
            dismiss();
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


    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.setCanceledOnTouchOutside(false); //Disable closing dialog by clicking outside of it
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}