package carvellwakeman.incomeoutcome;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;

public class DialogFragmentPermissionReasoning extends DialogFragment
{
    int requestCode;
    Activity parent;

    int titleResourceID;
    int subtitleResourceID;

    String[] permissions;

    TextView textView_title;
    TextView textView_subtitle;

    Button button_positive;
    Button button_negative;


    static DialogFragmentPermissionReasoning newInstance(Activity parent, int titleResourceID, int subtitleResourceID, String[] permissions, int reqCode) {
        DialogFragmentPermissionReasoning fg = new DialogFragmentPermissionReasoning();
        fg.parent = parent;
        fg.titleResourceID = titleResourceID;
        fg.subtitleResourceID = subtitleResourceID;
        fg.permissions = permissions;
        fg.requestCode = reqCode;

        return fg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_permission_reasoning, container, false);
        view.setBackgroundColor(Color.WHITE);

        textView_title = (TextView) view.findViewById(R.id.textView_dialogdr_title);
        textView_subtitle = (TextView) view.findViewById(R.id.textView_dialogdr_subtitle);

        button_positive = (Button) view.findViewById(R.id.button_dialogdr_positive);
        button_negative = (Button) view.findViewById(R.id.button_dialogdr_negative);

        if (titleResourceID > 0){ textView_title.setText(titleResourceID); }
        if (subtitleResourceID > 0){ textView_subtitle.setText(subtitleResourceID); }

        button_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Request permissions
                for (int i = 0; i < permissions.length; i++){
                    if (Build.VERSION.SDK_INT >= 23) {
                        parent.requestPermissions(new String[]{permissions[i]}, requestCode);
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
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
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