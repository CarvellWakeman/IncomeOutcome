package carvellwakeman.incomeoutcome;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

public class AdapterBasic extends RecyclerView.Adapter<AdapterBasic.TextLayoutViewHolder>
{
    //Calling activity context
    Context context;

    //ID strings
    int _profileID;
    Profile _profile;

    // Data
    HashMap<String, Transaction> _data;

    //Constructor
    public AdapterBasic(Context context, int profileID, HashMap<String, Transaction> data)
    {
        _profileID = profileID;
        _profile = ProfileManager.GetProfileByID(profileID);

        _data = data;

        if ( _profile != null ) {
            //_profile.CalculateTimeFrame(activityType);
            //data = _profile.CalculateTotalsInTimeFrame(activityType);
            //data = _profile.TransactionTotals;
        }

        this.context = context;
    }


    //When creating a view holder
    @Override
    public TextLayoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_text, parent, false);

        return new TextLayoutViewHolder(itemView);
    }

    //Bind view holder to the recyclerView
    @Override
    public void onBindViewHolder(final TextLayoutViewHolder holder, int position)
    {
        if (_profile != null) {

            String[] op = _data.keySet().toArray(new String[_data.keySet().size()]);
            if (position < op.length) {

                if (op[position] != null) {
                    double total = _data.get(op[position]).GetValue() - _data.get(op[position]).GetSplitValue();
                    holder.title.setText(context.getString(R.string.info_title_value, op[position], String.valueOf(ProfileManager.currencyFormat.format(total))));
                }
            }
        }

    }


    //How many items are there
    @Override
    public int getItemCount()
    {
        if (_profile != null) {
            return _data.size();
        }
        return -1;
    }



    //View Holder class
    public class TextLayoutViewHolder extends RecyclerView.ViewHolder //implements View.OnClickListener
    {
        TextView title;

        public TextLayoutViewHolder(View itemView)
        {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.textView_dialog);
        }

        //@Override
        //public void onClick(View v) {}
    }
}
