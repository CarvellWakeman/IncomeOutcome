package carvellwakeman.incomeoutcome;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class Card {

    Context _context;

    private LayoutInflater _inflater;
    private int _layout;

    View _base;
    private LinearLayout _linearLayoutBase;
    TextView _title;

    private ArrayList<View> _children;

    Card(Context context, LayoutInflater inflater, ViewGroup insertPoint, int index) {
        this(context, inflater, R.layout.card, insertPoint, index);
    }
    Card(Context context, LayoutInflater inflater, int layout, ViewGroup insertPoint, int index){
        _context = context;
        _layout = layout;
        _inflater = inflater;

        _children = new ArrayList<>();

        //Populate layout
        _base = _inflater.inflate(_layout, null);

        //Get card views
        _title = (TextView) _base.findViewById(R.id.card_title);
        _linearLayoutBase = (LinearLayout) _base.findViewById(R.id.card_relativelayout);

        //Inflate parent
        insertPoint.addView(_base, index, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    //Accessors
    public View getBase() { return _base; }

    //Title
    public void setTitle(String title){
        _title.setVisibility(View.VISIBLE);
        _title.setText(title);
    }
    public void setTitle(int resource){
        _title.setVisibility(View.VISIBLE);
        _title.setText(resource);
    }

    //Children
    public void AddView(View child) {
        _linearLayoutBase.addView(child, _children.size(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        _children.add(child);
    }
    public void RemoveView(View child) {
        _linearLayoutBase.removeView(child);
        _children.remove(child);
    }
    public void ClearViews(){
        _linearLayoutBase.removeAllViews();
        _children.clear();
    }

    public int ChildCount(){ return _children.size(); }

}
