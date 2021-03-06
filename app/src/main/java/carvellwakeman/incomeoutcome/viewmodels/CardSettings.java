package carvellwakeman.incomeoutcome.viewmodels;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import carvellwakeman.incomeoutcome.R;

public class CardSettings extends Card
{
    LinearLayout body;

    int settingsCount;

    public CardSettings(Context context, LayoutInflater inflater, ViewGroup insertPoint, int index, int layout, String title){
        super(context, inflater, layout, insertPoint, index);

        //Settings count
        settingsCount = 0;

        //Set Title
        TextView tv = (TextView) getBase().findViewById(R.id.row_layout_settingscard_title);
        tv.setText(title);

        //Set Body
        body = (LinearLayout) getBase().findViewById(R.id.row_layout_settingscard_layout);
    }

    public void AddSetting(Setting setting) {
        body.addView(setting.getView(), settingsCount, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        settingsCount++;
    }

    //public void insert(ViewGroup insertPoint, int index){
        //insertPoint.addView(v, index, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    //}

}
