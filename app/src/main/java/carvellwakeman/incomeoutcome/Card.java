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

    public Card(Context context, LayoutInflater inflater, int layout){
        this.context = context;
        _inflater = inflater;
        _layout = layout;

        //Populate layout
        v = _inflater.inflate(_layout, null);

        //Set listener
        v.setOnClickListener(this);
    }

    //Accessors
    public View getView()
    {
        return v;
    }

    //Mutators
    public void insert(ViewGroup insertPoint, int index){
        insertPoint.addView(v, index, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    //Click listener
    public void onClick(View view){
        //ProfileManager.Print("Card Click");
    }

}
