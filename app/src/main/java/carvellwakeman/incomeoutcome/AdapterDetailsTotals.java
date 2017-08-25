package carvellwakeman.incomeoutcome;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class AdapterDetailsTotals
{
    //Calling activity context
    ActivityDetailsTransaction _activity;

    // View Container for totals
    ViewGroup _container;

    //Budget
    Budget _budget;

    //Transactions
    //SparseArray<SparseArray<Double>> _totals; // Outer array holds people that owe, inner array (columns) hold people that are owed
    SparseArray<Double> _totals; // Money owed to you, negative means you owe them

    //Activity type (Expense or income) (0 or 1)
    Transaction.TRANSACTION_TYPE activityType = Transaction.TRANSACTION_TYPE.Expense;

    //Constructor
    public AdapterDetailsTotals(ActivityDetailsTransaction parent, ViewGroup container, int budgetID, Transaction.TRANSACTION_TYPE activityType)
    {
        _budget = BudgetManager.getInstance().GetBudget(budgetID);

        this._activity = parent;
        this._container = container;

        this.activityType = activityType;

        this._totals = new SparseArray<>();


        //Load up transactions from budget
        GetTotals(_activity.sortMethod, _activity.filterMethods);
    }


    //Custom transaction getters
    public void GetTotals(Helper.SORT_METHODS sort, HashMap<Helper.FILTER_METHODS, String>  filters){
        _totals.clear();

        if (activityType == Transaction.TRANSACTION_TYPE.Expense) { //Expense
            Double currVal = 0.0d;
            for (Transaction t : _budget.GetTransactionsInTimeframe(_activity, Transaction.TRANSACTION_TYPE.Expense, sort, filters)){
                for (HashMap.Entry<Integer, Double> entry : t.GetSplitArray().entrySet()){

                    // You paid or you were involved in the payment
                    if ( t.GetPaidBy() == Person.Me.GetID()       && entry.getKey() != Person.Me.GetID())  {
                        currVal = (_totals.get(entry.getKey()) != null ? _totals.get(entry.getKey()) : 0.0d);

                        _totals.put(entry.getKey(), currVal + t.GetDebt(entry.getKey(), Person.Me.GetID()));
                    } else if (t.GetPaidBy() != Person.Me.GetID() && entry.getKey() == Person.Me.GetID()){
                        currVal = (_totals.get(t.GetPaidBy()) != null ? _totals.get(t.GetPaidBy()) : 0.0d);

                        _totals.put(t.GetPaidBy(),  currVal - t.GetDebt(Person.Me.GetID(), t.GetPaidBy()));
                    }

                }
            }
        }
        else if (activityType == Transaction.TRANSACTION_TYPE.Income) { //Income (Should anything be done here?)
            //for (Transaction t : _budget.GetTransactionsInTimeframe(Transaction.TRANSACTION_TYPE.Income, sort, filters)){
            //}
        }
    }


    // Populate container
    public void PopulateContainer(){
        // Clear existing children
        _container.removeAllViews();

        // Inflate new children
        LayoutInflater inflater = _activity.getLayoutInflater();
        PersonManager pm = PersonManager.getInstance();

        // Bind totals to view holder
        int size = _totals.size();
        Person op;
        String a = _activity.getString(R.string.placeholder_Unknown);
        String b = Person.Me.GetName();
        for (int i = 0; i < size; i++){
            op = pm.GetPerson(_totals.keyAt(i));
            if (op != null){
                a = op.GetName();
            }

            View v = inflater.inflate(R.layout.row_layout_transaction_total, null);
            TextView tvA = (TextView) v.findViewById(R.id.total_personA);
            tvA.setText(a);
            TransactionTotalsViewHolder vh = new TransactionTotalsViewHolder(v);
            vh.bind(a, b, _totals.valueAt(i));

            _container.addView(vh.itemView, i);
        }

    }




    //View Holder class
    public class TransactionTotalsViewHolder extends RecyclerView.ViewHolder
    {
        TextView personA;
        TextView personB;
        TextView whoOwesWho;
        TextView value;


        public TransactionTotalsViewHolder(View itemView)
        {
            super(itemView);

            personA = (TextView) itemView.findViewById(R.id.total_personA);
            personB = (TextView) itemView.findViewById(R.id.total_personB);
            whoOwesWho = (TextView) itemView.findViewById(R.id.total_who_owes_who);
            value = (TextView) itemView.findViewById(R.id.total_value);
        }

        public void bind(String a, String b, Double v){
            if (activityType == Transaction.TRANSACTION_TYPE.Expense) { //Expenses
                if (v == 0.0d){
                    personA.setVisibility(View.GONE);
                    personB.setText(a);
                    whoOwesWho.setVisibility(View.VISIBLE);
                    value.setVisibility(View.GONE);
                    whoOwesWho.setText(R.string.balance_even);
                } else {
                    // They owe you
                    if (v > 0) {
                        personA.setText(a);
                        personB.setText(b);
                        //Plurality
                        if (b.equals(Person.Me.GetName())) { whoOwesWho.setText(R.string.balance_uneven_plural); }
                        else { whoOwesWho.setText(R.string.balance_uneven); }
                        value.setText(Helper.currencyFormat.format(v));
                    } else { // You owe them
                        personA.setText(b);
                        personB.setText(a);
                        //Plurality
                        if (a.equals(Person.Me.GetName())) { whoOwesWho.setText(R.string.balance_uneven_plural); }
                        else { whoOwesWho.setText(R.string.balance_uneven); }
                        value.setText(Helper.currencyFormat.format(Math.abs(v)));
                    }
                }
            }
            else if (activityType == Transaction.TRANSACTION_TYPE.Income) { //Income
                personB.setVisibility(View.GONE);
                whoOwesWho.setVisibility(View.GONE);
                value.setVisibility(View.VISIBLE);

                personA.setText(a);
                value.setText(Helper.currencyFormat.format(Math.abs(v)));
            }

        }

    }
}
