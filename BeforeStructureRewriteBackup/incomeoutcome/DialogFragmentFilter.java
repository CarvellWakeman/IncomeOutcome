package carvellwakeman.incomeoutcome;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class DialogFragmentFilter extends DialogFragment
{
    ProfileManager.CallBack _callBack;
    Profile _profile;
    ProfileManager.FILTER_METHODS filterMethod;

    ArrayAdapter adapter;

    int titleString;

    TextView textView_title;

    Spinner spinner_filter;

    Button button_positive;
    Button button_negative;


    static DialogFragmentFilter newInstance(Activity caller, Profile profile, ProfileManager.FILTER_METHODS method, int title, ProfileManager.CallBack callBack) {
        DialogFragmentFilter fg = new DialogFragmentFilter();
        fg._callBack = callBack;
        Bundle args = new Bundle();
        args.putSerializable("profile", profile);
        args.putSerializable("method", method);
        args.putInt("title", title);
        fg.setArguments(args);

        switch (method) {
            case CATEGORY:
                if (ProfileManager.getInstance().GetCategoriesCount() == 0) { ProfileManager.PrintUser(caller, ProfileManager.getString(R.string.tt_nocategories) ); return null; }
                fg.adapter = new ArrayAdapter<>(caller, R.layout.spinner_dropdown_title, ProfileManager.getInstance().GetCategoriesString());
                break;
            case SOURCE:
                if (profile != null) {
                    ArrayList<String> sources = new ArrayList<>();
                    for (int i = 0; i < profile.GetTransactionsSize(); i++) {
                        if (!sources.contains(profile.GetTransactionAtIndex(i).GetSourceName())) {
                            if (profile.GetTransactionAtIndex(i).GetSourceName() != null) {
                                if (!profile.GetTransactionAtIndex(i).GetSourceName().equals("")) {
                                    sources.add(profile.GetTransactionAtIndex(i).GetSourceName());
                                }
                            }
                        }
                    }
                    if (sources.size() == 0) { ProfileManager.PrintUser(caller, ProfileManager.getString(R.string.tt_nosources) ); return null; }
                    fg.adapter = new ArrayAdapter<>(caller, R.layout.spinner_dropdown_title, sources);
                }
                break;
            case PAIDBY:
                fg.adapter = new ArrayAdapter<>(caller, R.layout.spinner_dropdown_title, ProfileManager.getInstance().GetOtherPeopleIncludingMe());
                break;
            case SPLITWITH:
                if (ProfileManager.getInstance().GetOtherPeopleCount() == 0) { ProfileManager.PrintUser(caller, ProfileManager.getString(R.string.tt_nopeople) ); return null; }
                fg.adapter = new ArrayAdapter<>(caller, R.layout.spinner_dropdown_title, ProfileManager.getInstance().GetOtherPeople());
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

        _profile = (Profile) getArguments().getSerializable("profile");
        filterMethod = (ProfileManager.FILTER_METHODS) getArguments().getSerializable("method");
        titleString = getArguments().getInt("title");

        textView_title = (TextView) view.findViewById(R.id.textView_dialogtt_title);

        spinner_filter = (Spinner) view.findViewById(R.id.spinner_filter);

        button_positive = (Button) view.findViewById(R.id.button_dialogtt_positive);
        button_negative = (Button) view.findViewById(R.id.button_dialogtt_negative);

        //Set title
        textView_title.setText(titleString);

        //Populate spinner
        if (adapter != null) {
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_list_primary);
            spinner_filter.setAdapter(adapter);
        }

        //Positive
        button_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_profile != null) {
                    _profile.SetFilterMethod(filterMethod, spinner_filter.getSelectedItem());
                    SortFilterOptions.DisplayFilter(getActivity(), filterMethod, spinner_filter.getSelectedItem(), _callBack);

                    if (_callBack != null){
                        _callBack.call();
                    }
                }
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
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.

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