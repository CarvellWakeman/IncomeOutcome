package carvellwakeman.incomeoutcome;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

public class AdapterExpenseTotals extends RecyclerView.Adapter<AdapterExpenseTotals.ExpenseTotalsViewHolder>
{
    //Calling activity context
    ActivityDetailsExpense activity;

    //ID strings
    int _profileID;
    Profile _profile;

    // Data
    HashMap<String, Expense> data;

    //Constructor
    public AdapterExpenseTotals(ActivityDetailsExpense activity, int profileID)
    {
        _profileID = profileID;
        _profile = ProfileManager.GetProfileByID(profileID);

        if ( _profile != null ) {
            _profile.GetTotalCostPerPersonInTimeFrame();
            data = _profile._ExpenseTotals;
        }

        this.activity = activity;
    }


    //When creating a view holder
    @Override
    public ExpenseTotalsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_person_total, parent, false);

        return new ExpenseTotalsViewHolder(itemView);
    }

    //Bind view holder to the recyclerView
    @Override
    public void onBindViewHolder(final ExpenseTotalsViewHolder holder, int position)
    {
        if (_profile != null) {
            //data = _profile.GetTotalCostPerPersonInTimeFrame();
            //_profile.GetTotalCostPerPersonInTimeFrame();

            String[] op = data.keySet().toArray(new String[data.keySet().size()]);
            if (position < op.length) {

                if (op[position] != null) {
                    Double total = data.get(op[position]).GetValue() - data.get(op[position]).GetSplitValue();
                    if (total < 0) {
                        holder.personA.setText(op[position]);
                        holder.personB.setText(R.string.format_me);
                        holder.whoOwesWho.setText(R.string.balance_uneven_plural);
                        holder.total.setText(ProfileManager.currencyFormat.format(Math.abs(total)));
                        holder.total.setVisibility(View.VISIBLE);
                    }
                    else if (total > 0) {
                        holder.personA.setText(R.string.format_me);
                        holder.personB.setText(op[position]);
                        holder.whoOwesWho.setText(R.string.balance_uneven);
                        holder.total.setText(ProfileManager.currencyFormat.format(Math.abs(total)));
                        holder.total.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.personA.setText("");
                        holder.whoOwesWho.setText(R.string.balance_even);
                        holder.personB.setText(op[position]);
                        holder.total.setVisibility(View.GONE);
                    }


                }
            }





            //holder.personATotal.setText(data.get(op).GetValueFormatted());
            //holder.personBTotal.setText(data.get(op).GetSplitValueFormatted());

            //holder.personATotal.setText( String.valueOf(_profile.GetExpenseIOweTo(ProfileManager.GetOtherPersonByIndex(position))) );
            //holder.personBTotal.setText( String.valueOf(ProfileManager.GetOtherPersonByIndex(position).GetMoneyOwed(_profile)) );

            //ProfileManager.GetOtherPersonByIndex(position).GetMoneyOwed(_profile);
            //_profile.GetExpenseIOweTo(ProfileManager.GetOtherPersonByIndex(position));
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
    public class ExpenseTotalsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        CardView cv;

        TextView personA;
        TextView personB;

        TextView personASpent;
        TextView personBSpent;

        TextView personADebt;
        TextView personBDebt;

        TextView personATotal;
        TextView personBTotal;


        TextView whoOwesWho;
        TextView total;

        Boolean moreInfo;


        public ExpenseTotalsViewHolder(View itemView)
        {
            super(itemView);

            moreInfo = false;

            cv = (CardView) itemView.findViewById(R.id.card_expense_title);

            personA = (TextView) itemView.findViewById(R.id.income_total_source);
            personB = (TextView) itemView.findViewById(R.id.expense_personB);

            personASpent = (TextView) itemView.findViewById(R.id.expense_personA_spent);
            personBSpent = (TextView) itemView.findViewById(R.id.expense_personB_spent);

            personADebt = (TextView) itemView.findViewById(R.id.expense_personA_debt);
            personBDebt = (TextView) itemView.findViewById(R.id.expense_personB_debt);

            personATotal = (TextView) itemView.findViewById(R.id.expense_personA_total);
            personBTotal = (TextView) itemView.findViewById(R.id.expense_personB_total);

            whoOwesWho = (TextView) itemView.findViewById(R.id.expense_who_owes_who);
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
