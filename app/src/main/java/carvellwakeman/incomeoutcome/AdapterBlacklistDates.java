package carvellwakeman.incomeoutcome;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdapterBlacklistDates extends RecyclerView.Adapter<AdapterBlacklistDates.BlacklistDateViewHolder>
{
    Activity context;
    TimePeriod timePeriod;

    //Constructor
    public AdapterBlacklistDates(Activity context, TimePeriod timePeriod)
    {
        this.context = context;
        this.timePeriod = timePeriod;
    }


    //When creating a view holder
    @Override
    public BlacklistDateViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_blacklistdate, parent, false);

        return new BlacklistDateViewHolder(itemView);
    }

    //Bind view holder to the recyclerView
    @Override
    public void onBindViewHolder(final BlacklistDateViewHolder holder, int position)
    {
        holder.textView.setText(timePeriod.GetBlacklistDateString(position));
    }


    //How many items are there
    @Override
    public int getItemCount()
    {
        if (timePeriod != null) {
            return timePeriod.GetBlacklistDatesCountWithoutQueue();
        }
        return -1;
    }



    //View Holder class
    public class BlacklistDateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        LinearLayout ll;
        TextView textView;

        public BlacklistDateViewHolder(View itemView)
        {
            super(itemView);

            ll = (LinearLayout)itemView.findViewById(R.id.linearlayout_row_layout_blacklistdate);
            textView = (TextView)itemView.findViewById(R.id.textview_row_layout_blacklistdate);

            ll.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            String items[] = {"Remove"};
            final int pos = getAdapterPosition();
            new android.support.v7.app.AlertDialog.Builder(context).setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch (item) {
                        case 0: //Remove
                            timePeriod.RemoveBlacklistDate(timePeriod.GetBlacklistDate(pos).date);
                            notifyItemRemoved(pos);
                            break;
                        default:
                            dialog.cancel();
                            break;
                    }
                }
            }).create().show();
        }
    }
}
