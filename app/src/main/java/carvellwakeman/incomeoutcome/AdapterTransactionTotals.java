package carvellwakeman.incomeoutcome;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

public class AdapterTransactionTotals extends RecyclerView.Adapter<AdapterTransactionTotals.TransactionTotalsViewHolder>
{
    //Activity type
    int activityType;

    //Calling activity context
    Context context;

    //ID strings
    int _profileID;
    Profile _profile;

    // Data
    HashMap<String, Transaction> data;

    //Constructor
    public AdapterTransactionTotals(Context context, int profileID, int activityType, int keyType)
    {
        _profileID = profileID;
        _profile = ProfileManager.GetProfileByID(profileID);

        if ( _profile != null ) {
            data = _profile.CalculateTotalsInTimeFrame(activityType, keyType);
        }

        this.context = context;

        this.activityType = activityType;
    }


    //When creating a view holder
    @Override
    public TransactionTotalsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_transaction_total, parent, false);

        return new TransactionTotalsViewHolder(itemView);
    }

    //Bind view holder to the recyclerView
    @Override
    public void onBindViewHolder(final TransactionTotalsViewHolder holder, int position)
    {
        if (_profile != null) {
            //data = _profile.GetTotalCostPerPersonInTimeFrame();
            //_profile.GetTotalCostPerPersonInTimeFrame();

            String[] op = data.keySet().toArray(new String[data.keySet().size()]);
            if (position < op.length) {

                if (op[position] != null) {
                    Double total = data.get(op[position]).GetValue() - data.get(op[position]).GetSplitValue();

                    if (activityType == 0) { //Expenses
                        if (total < 0) {
                            holder.objectA.setText(op[position]);
                            holder.objectB.setText(R.string.format_me);
                            holder.whoOwesWho.setText(R.string.balance_uneven_plural);
                            holder.total.setText(ProfileManager.currencyFormat.format(Math.abs(total)));
                            holder.total.setVisibility(View.VISIBLE);
                        }
                        else if (total > 0) {
                            holder.objectA.setText(R.string.format_me);
                            holder.objectB.setText(op[position]);
                            holder.whoOwesWho.setText(R.string.balance_uneven);
                            holder.total.setText(ProfileManager.currencyFormat.format(Math.abs(total)));
                            holder.total.setVisibility(View.VISIBLE);
                        }
                        else {
                            holder.objectA.setText("");
                            holder.whoOwesWho.setText(R.string.balance_even);
                            holder.objectB.setText(op[position]);
                            holder.total.setVisibility(View.GONE);
                        }
                    }
                    else if (activityType == 1) { //Income
                        holder.objectB.setVisibility(View.GONE);
                        holder.whoOwesWho.setVisibility(View.GONE);

                        holder.objectA.setText(op[position]);
                        holder.total.setText(ProfileManager.currencyFormat.format(Math.abs(total)));
                        holder.total.setVisibility(View.VISIBLE);
                    }
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
    public class TransactionTotalsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        CardView cv;

        TextView objectA;
        TextView objectB;

        TextView total;


        //Expensen only
        TextView objectASpent;
        TextView objectBSpent;

        TextView objectADebt;
        TextView objectBDebt;

        TextView objectATotal;
        TextView objectBTotal;

        TextView whoOwesWho;


        Boolean moreInfo;


        public TransactionTotalsViewHolder(View itemView)
        {
            super(itemView);

            moreInfo = false;

            cv = (CardView) itemView.findViewById(R.id.card_transaction_title);

            objectA = (TextView) itemView.findViewById(R.id.transaction_objectA);
            objectB = (TextView) itemView.findViewById(R.id.transaction_objectB);

            objectASpent = (TextView) itemView.findViewById(R.id.expense_personA_spent);
            objectBSpent = (TextView) itemView.findViewById(R.id.expense_personB_spent);

            objectADebt = (TextView) itemView.findViewById(R.id.expense_personA_debt);
            objectBDebt = (TextView) itemView.findViewById(R.id.expense_personB_debt);

            objectATotal = (TextView) itemView.findViewById(R.id.expense_personA_total);
            objectBTotal = (TextView) itemView.findViewById(R.id.expense_personB_total);

            whoOwesWho = (TextView) itemView.findViewById(R.id.transaction_who_owes_who);
            total = (TextView) itemView.findViewById(R.id.transaction_total_cost);

            //Expenses only
            if (activityType == 0){
                objectB.setVisibility(View.VISIBLE);
                whoOwesWho.setVisibility(View.VISIBLE);
            }

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
