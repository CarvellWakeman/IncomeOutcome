package carvellwakeman.incomeoutcome;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.zip.Inflater;

public abstract class Card {

    LayoutInflater _inflater;
    int _layout;
    View.OnClickListener _clickListener;

    View v;

    public Card(LayoutInflater inflater, int layout, View.OnClickListener clickListener){
        _inflater = inflater;
        _layout = layout;
        _clickListener = clickListener;

        //Populate layout
        v = _inflater.inflate(_layout, null);

        //Set listener
        v.setOnClickListener(_clickListener);
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

}
