package carvellwakeman.incomeoutcome;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

public class AdapterIncomeTotals extends RecyclerView.Adapter<AdapterIncomeTotals.IncomeTotalsViewHolder>
{
    //Calling activity context
    ActivityDetailsIncome activity;

    //ID strings
    int _profileID;
    Profile _profile;

    // Data
    HashMap<String, Income> data;

    //Constructor
    public AdapterIncomeTotals(ActivityDetailsIncome activity, int profileID)
    {
        _profileID = profileID;
        _profile = ProfileManager.GetProfileByID(profileID);

        if ( _profile != null ) {
            _profile.GetTotalIncomePerSourceInTimeFrame();
            data = _profile._IncomeTotals;
        }

        this.activity = activity;
    }


    //When creating a view holder
    @Override
    public IncomeTotalsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_source_total, parent, false);

        return new IncomeTotalsViewHolder(itemView);
    }

    //Bind view holder to the recyclerView
    @Override
    public void onBindViewHolder(final IncomeTotalsViewHolder holder, int position)
    {
        if (_profile != null) {

            String[] op = data.keySet().toArray(new String[data.keySet().size()]);
            if (position < op.length) {
                if (op[position] != null) {

                    Double total = data.get(op[position]).GetValue();

                    holder.category.setText(op[position]);
                    holder.total.setText(ProfileManager.currencyFormat.format(Math.abs(total)));
                }
            }
        }
    }


    //How many items are there
    @Override
    public int getItemCount()
    {
        if (_profile != null) {
            return data.size();
        }
        return -1;
    }



    //View Holder class
    public class IncomeTotalsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        CardView cv;

        TextView category;
        TextView total;

        Boolean moreInfo;


        public IncomeTotalsViewHolder(View itemView)
        {
            super(itemView);

            moreInfo = false;

            cv = (CardView) itemView.findViewById(R.id.card_income_title);

            category = (TextView) itemView.findViewById(R.id.income_total_source);
            total = (TextView) itemView.findViewById(R.id.income_total_cost);

            //Short and long click listeners for the expenses context menu
            cv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            toggleMoreInfo();
        }


        //More Info
        public void toggleMoreInfo(){
            moreInfo = !moreInfo;
            if (moreInfo) { moreInfoOn(); } else { moreInfoOff(); }
        }
        public void moreInfoOn() { moreInfo = true; }
        public void moreInfoOff() { moreInfo = false; }
    }
}
