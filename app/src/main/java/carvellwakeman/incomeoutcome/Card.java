package carvellwakeman.incomeoutcome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.zip.Inflater;

public abstract class Card implements View.OnClickListener {

    Context context;
    LayoutInflater _inflater;
    int _layout;

    View v;

    public Card(Context context, LayoutInflater inflater, int layout, ViewGroup insertPoint, int index){
        this.context = context;
        _inflater = inflater;
        _layout = layout;

        //Populate layout
        v = _inflater.inflate(_layout, null);

        //Set listener
        v.setOnClickListener(this);

        //Inflate parent
        insertPoint.addView(v, index, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    //Accessors
    public View getView()
    {
        return v;
    }

    //Click listener
    public void onClick(View view){
        //ProfileManager.Print("Card Click");
    }

}
