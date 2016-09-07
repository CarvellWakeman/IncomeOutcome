package carvellwakeman.incomeoutcome;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Setting
{
    View v;

    public Setting(LayoutInflater inflater, int icon, String title, String subtitle, View.OnClickListener listener)
    {
        //Populate settings
        v = inflater.inflate(R.layout.row_layout_setting, null);

        //Set Icon
        ImageView iv = (ImageView) v.findViewById(R.id.row_layout_setting_icon);
        iv.setImageResource(icon);

        //Set Title
        TextView tv = (TextView) v.findViewById(R.id.row_layout_setting_title);
        tv.setText(title);

        //Set Subtitle
        if (subtitle != null && !subtitle.equals("")) {
            TextView stv = (TextView) v.findViewById(R.id.row_layout_setting_subtitle);
            stv.setVisibility(View.VISIBLE);
            stv.setText(subtitle);
        }

        //Set listener
        v.setOnClickListener(listener);
    }


    public View getView()
    {
        return v;
    }


}
