package carvellwakeman.incomeoutcome;


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
    ActivityDetailsTransaction _parent;
    Profile _profile;
    ProfileManager.FILTER_METHODS filterMethod;

    ArrayAdapter adapter;

    TextView textView_title;

    Spinner spinner_filter;

    Button button_positive;
    Button button_negative;


    NpaLinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;

    static DialogFragmentFilter newInstance(ActivityDetailsTransaction parent, Profile profile, ProfileManager.FILTER_METHODS method) {
        DialogFragmentFilter fg = new DialogFragmentFilter();
        fg._parent = parent;
        Bundle args = new Bundle();
        args.putSerializable("profile", profile);
        args.putSerializable("method", method);
        fg.setArguments(args);

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

        textView_title = (TextView) view.findViewById(R.id.textView_dialogtt_title);

        spinner_filter = (Spinner) view.findViewById(R.id.spinner_filter);

        button_positive = (Button) view.findViewById(R.id.button_dialogtt_positive);
        button_negative = (Button) view.findViewById(R.id.button_dialogtt_negative);


        //Populate spinner
        ArrayAdapter<String> adapter = null;
        switch(filterMethod){
            case CATEGORY:
                adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_dropdown_title, ProfileManager.GetCategoriesString());
                break;
            case SOURCE:
                ArrayList<String> sources = new ArrayList<>();
                for (int i = 0; i < _profile.GetTransactionsSize(); i++){
                    if (!sources.contains(_profile.GetTransactionAtIndex(i).GetSourceName())){
                        sources.add(_profile.GetTransactionAtIndex(i).GetSourceName());
                    }
                }
                adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_dropdown_title, sources);
                break;
            case PAIDBY:
                adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_dropdown_title, ProfileManager.GetOtherPeople());
                break;
        }
        if (adapter != null){
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_list_primary);
            spinner_filter.setAdapter(adapter);
        }

        //Positive
        button_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _profile.Filter(filterMethod, spinner_filter.getSelectedItem(), _parent.activityType);
                _parent.elementsAdapter.notifyDataSetChanged();
                _parent.totalsAdapter.notifyDataSetChanged();
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
            //dialog.getWindow().setBackgroundDrawable(null);
        }
    }
}