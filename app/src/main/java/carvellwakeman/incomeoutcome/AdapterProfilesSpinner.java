package carvellwakeman.incomeoutcome;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterProfilesSpinner extends ArrayAdapter<String> {

    Context _context;
    int _layoutResourceId;

    public AdapterProfilesSpinner(Context context, int layoutResourceId, ArrayList<String> data) {
        super(context, layoutResourceId);
        _layoutResourceId = layoutResourceId;
        _context = context;
    }


    @Override
    public String getItem(int position) {
        Profile pr = ProfileManager.GetProfileByIndex(position);
        return pr != null ? pr.GetName() : "";
    }

    @Override
    public int getPosition(String item) {
        return ProfileManager.GetProfileIndex(ProfileManager.GetProfileByName(item));
    }

    @Override
    public int getCount() {
        return ProfileManager.GetProfileCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inf = LayoutInflater.from(_context); //((Activity)_context).getLayoutInflater();
            v = inf.inflate(_layoutResourceId, null);
        }

        Profile pr = ProfileManager.GetProfileByIndex(position);
        if (pr != null){
            TextView title = (TextView) v;

            if (title != null){
                title.setText(pr.GetName());
            }
        }

        return v;
    }
}