package carvellwakeman.incomeoutcome;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Setting
{
    View v;

    TextView tv;
    TextView stv;
    ImageView iv;

    public Setting(LayoutInflater inflater, int icon, String title, String subtitle, View.OnClickListener listener)
    {
        //Populate settings
        v = inflater.inflate(R.layout.row_layout_setting, null);

        //Icon
        iv = (ImageView) v.findViewById(R.id.row_layout_setting_icon);

        //Title
        tv = (TextView) v.findViewById(R.id.row_layout_setting_title);

        //Suttitle
        stv = (TextView) v.findViewById(R.id.row_layout_setting_subtitle);

        //Set data
        SetIcon(icon);
        SetTitle(title);
        SetSubTitle(subtitle);

        //Set listener
        v.setOnClickListener(listener);
    }


    public View getView()
    {
        return v;
    }

    public void SetIcon(int icon){ iv.setImageResource(icon); }

    public void SetTitle(String title){ tv.setText(title); }
    public void SetSubTitle(String subtitle){
        if (subtitle != null && !subtitle.equals("")) { stv.setVisibility(View.VISIBLE); } else { stv.setVisibility(View.GONE); }
        stv.setText(subtitle);
    }


}
