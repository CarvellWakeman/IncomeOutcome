package carvellwakeman.incomeoutcome.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import carvellwakeman.incomeoutcome.*;
import carvellwakeman.incomeoutcome.data.BudgetManager;
import carvellwakeman.incomeoutcome.dialogs.DialogFragmentTransferTransaction;
import carvellwakeman.incomeoutcome.models.Budget;
import carvellwakeman.incomeoutcome.viewholders.ViewHolderTextUnderline;

import java.util.ArrayList;

public class AdapterTransferTransactions extends RecyclerView.Adapter<AdapterTransferTransactions.TransferViewHolder>
{
    private DialogFragmentTransferTransaction _parent;
    private ArrayList<Budget> _budgets;

    //Constructor
    public AdapterTransferTransactions(DialogFragmentTransferTransaction parent, Budget current)
    {
        _parent = parent;

        _budgets = BudgetManager.getInstance().GetBudgets();
        _budgets.remove(current);
    }

    @Override
    public TransferViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_text_underline, parent, false);

        return new TransferViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TransferViewHolder holder, int position)
    {
        //Budget
        Budget br = BudgetManager.getInstance().GetBudgets().get(position);
        if (br != null) {
            //Textview
            holder.title.setText(br.GetName());
            holder.subTitle.setText(br.GetDateFormatted(_parent.getActivity()));

            if (holder.subTitle.getText() != null) { holder.subTitle.setVisibility(View.VISIBLE); }

            holder.secondaryIcon.setVisibility(View.INVISIBLE);
            holder.row_divider.setVisibility(View.INVISIBLE);
        }
    }

    @Override public int getItemCount() { return _budgets.size(); }




    public class TransferViewHolder extends ViewHolderTextUnderline implements View.OnClickListener, View.OnLongClickListener
    {
        TransferViewHolder(View itemView) { super(itemView); }

        @Override
        public void onClick(View v) {
            Budget br = BudgetManager.getInstance().GetBudgets().get(getAdapterPosition());
            if (br != null) {
                _parent.TransferTransactions(br);
            }
        }

        @Override
        public boolean onLongClick(View v){
            return true;
        }
    }

}

