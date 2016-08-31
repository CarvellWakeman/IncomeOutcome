package carvellwakeman.incomeoutcome;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CardSettings extends Card
{
    LinearLayout body;

    int settingsCount;

    public CardSettings(Context context, LayoutInflater inflater, int layout, String title){
        super(context, inflater, layout);

        //Settings count
        settingsCount = 0;

        //Set Title
        TextView tv = (TextView) v.findViewById(R.id.row_layout_settingscard_title);
        tv.setText(title);

        //Set Body
        body = (LinearLayout) v.findViewById(R.id.row_layout_settingscard_layout);
    }

    public void AddSetting(Setting setting) {
        body.addView(setting.getView(), settingsCount, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        settingsCount++;
    }

    public void insert(ViewGroup insertPoint, int index){
        insertPoint.addView(v, index, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

}
