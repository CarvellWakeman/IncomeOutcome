package carvellwakeman.incomeoutcome;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Setting
{
    View v;

    LinearLayout base;
    TextView tv;
    TextView stv;
    ImageView iv;

    public Setting(LayoutInflater inflater, Integer icon, String title, String subtitle, View.OnClickListener listener)
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
        if (icon!=null) { SetIcon(icon); } else { iv.setVisibility(View.GONE); }
        SetTitle(title);
        SetSubTitle(subtitle);

        //Set listener
        if (listener != null) {
            v.setOnClickListener(listener);
        } else {
            v.setClickable(false);
            v.setFocusable(false);
        }
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

    public void SetPadding(int T, int B, int S, int E){
        v.setPadding(S,T,E,B);
    }


}
