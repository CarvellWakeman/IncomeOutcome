package carvellwakeman.incomeoutcome.viewmodels;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import carvellwakeman.incomeoutcome.R;

public class Setting
{
    View v;

    LinearLayout base;
    TextView _title;
    TextView _subtitle;
    ImageView _icon;

    public Setting(LayoutInflater inflater, Integer icon, String title, String subtitle, View.OnClickListener listener)
    {
        //Populate settings
        v = inflater.inflate(R.layout.row_layout_setting, null);

        //Icon
        _icon = (ImageView) v.findViewById(R.id.row_layout_setting_icon);

        //Title
        _title = (TextView) v.findViewById(R.id.row_layout_setting_title);

        //Suttitle
        _subtitle = (TextView) v.findViewById(R.id.row_layout_setting_subtitle);

        //Set data
        if (icon!=null) { SetIcon(icon); } else { _icon.setVisibility(View.GONE); }
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

    public void SetLongClickListener(View.OnLongClickListener listener){
        v.setOnLongClickListener(listener);
    }

    public View getView() { return v; }

    public void SetIcon(int icon){ _icon.setImageResource(icon); }
    public void SetIconColor(int color) {
        //_icon.setColorFilter(R.color.white);
        _icon.setColorFilter(color);
    }

    public void SetTitle(String title){ _title.setText(title); }
    public void SetSubTitle(String subtitle){
        if (subtitle != null && !subtitle.equals("")) { _subtitle.setVisibility(View.VISIBLE); } else { _subtitle.setVisibility(View.GONE); }
        _subtitle.setText(subtitle);
    }

    public void SetPadding(int T, int B, int S, int E){
        v.setPadding(S,T,E,B);
    }

}
