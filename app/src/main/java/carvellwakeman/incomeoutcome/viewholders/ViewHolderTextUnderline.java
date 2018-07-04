package carvellwakeman.incomeoutcome.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import carvellwakeman.incomeoutcome.R;


public class ViewHolderTextUnderline extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
{
    public RelativeLayout base;

    public ImageView icon;
    public ImageView secondaryIcon;

    public TextView title;
    public TextView subTitle;
    public TextView subTitle2;

    public View row_divider;

    public ViewHolderTextUnderline(View itemView)
    {
        super(itemView);

        base = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_text_underline);
        icon = (ImageView) itemView.findViewById(R.id.row_layout_text_icon);
        secondaryIcon = (ImageView) itemView.findViewById(R.id.row_layout_text_secondicon);
        title = (TextView) itemView.findViewById(R.id.row_layout_text_title);
        subTitle = (TextView) itemView.findViewById(R.id.row_layout_text_subtitle);
        subTitle2 = (TextView) itemView.findViewById(R.id.row_layout_text_subtitle2);
        row_divider = itemView.findViewById(R.id.transaction_row_divider);

        //Short and long click listeners
        base.setOnClickListener(this);
        base.setOnLongClickListener(this);

    }

    @Override
    public void onClick(View v) { }

    @Override
    public boolean onLongClick(View v){
        return true;
    }
}