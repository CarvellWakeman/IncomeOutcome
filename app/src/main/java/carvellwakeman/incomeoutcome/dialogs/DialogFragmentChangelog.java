package carvellwakeman.incomeoutcome.dialogs;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import carvellwakeman.incomeoutcome.App;
import carvellwakeman.incomeoutcome.models.ChangelogChange;
import carvellwakeman.incomeoutcome.viewmodels.NpaLinearLayoutManager;
import carvellwakeman.incomeoutcome.R;
import carvellwakeman.incomeoutcome.adapters.AdapterChangelog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class DialogFragmentChangelog extends DialogFragment
{
    TextView textView_title;
    TextView textView_nodata;

    AdapterChangelog adapter;
    NpaLinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView_changes;

    Button button_positive;

    List<ChangelogChange> changeList = new ArrayList<>();

    public static DialogFragmentChangelog newInstance() {
        DialogFragmentChangelog fg = new DialogFragmentChangelog();

        Bundle args = new Bundle();
        //args.putString("title", title);
        fg.setArguments(args);

        return fg;
    }

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_changelog, container, false);
        view.setBackgroundColor(Color.WHITE);

        textView_title = (TextView) view.findViewById(R.id.textView_dialogtt_title);

        textView_nodata = (TextView) view.findViewById(R.id.textView_changelog_nodata);

        recyclerView_changes = (RecyclerView) view.findViewById(R.id.recyclerView_changelog);

        button_positive = (Button) view.findViewById(R.id.button_dialogtt_positive);

        //Set title
        textView_title.setText(String.format(getString(R.string.subtitle_settings_changelog), App.GetVersion(getActivity())));

        //Read changelog file
        try {
            AssetManager assMan = getActivity().getAssets();
            InputStream inputStream = assMan.open("CHANGELOG.txt");

            //Read changelog
            readChangelog(inputStream);
        } catch (IOException ex){ ex.printStackTrace(); }

        //Visibilitiy
        if (changeList.size() > 0){
            //Set adapter
            adapter = new AdapterChangelog(getActivity(), changeList);
            recyclerView_changes.setAdapter(adapter);

            //LinearLayoutManager for RecyclerView
            linearLayoutManager = new NpaLinearLayoutManager(getActivity());
            recyclerView_changes.setLayoutManager(linearLayoutManager);
        }
        else {
            recyclerView_changes.setVisibility(View.GONE);
            textView_nodata.setVisibility(View.VISIBLE);
        }



        //Positive
        button_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        // Inflate the layout to use as dialog or embedded fragment
        return view;
    }

    public void readChangelog(InputStream is){
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        ChangelogChange change = null;
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                //Check if line is a title line
                if (line.toLowerCase().contains(ChangelogChange.VERSION_INDICATOR)) {
                    //Make a new change
                    change = new ChangelogChange();
                    changeList.add(change);

                    change.ParseTitleString(line);
                } else {
                    if (change != null){ change.ParseEntry(line); }
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        finally {
            try { is.close(); }
            catch (IOException e) { e.printStackTrace(); }
        }
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