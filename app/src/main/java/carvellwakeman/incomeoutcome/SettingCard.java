package carvellwakeman.incomeoutcome;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingCard
{
    LayoutInflater i;

    View v;
    LinearLayout body;

    int settingsCount;

    public SettingCard(LayoutInflater inflater, String title)
    {
        //Settings count
        settingsCount = 0;

        //Set inflater
        i = inflater;

        //Profiles, people & categories
        v = i.inflate(R.layout.row_layout_setting_card, null);

        //Set Title
        TextView tv = (TextView) v.findViewById(R.id.row_layout_settingscard_title);
        tv.setText(title);

        //Set Body
        body = (LinearLayout) v.findViewById(R.id.row_layout_settingscard_layout);

    }

    public void AddSetting(Setting setting)
    {
        ((ViewGroup)body).addView(setting.getView(), settingsCount, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        settingsCount++;
    }

    public void insert(ViewGroup insertPoint, int index){
        insertPoint.addView(v, index, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

}
