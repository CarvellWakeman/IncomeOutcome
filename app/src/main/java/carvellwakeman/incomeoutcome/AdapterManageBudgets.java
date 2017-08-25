package carvellwakeman.incomeoutcome;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class AdapterManageBudgets extends AdapterManageEntity
{
    public AdapterManageBudgets(ActivityManageBudgets _parent) { super(_parent); }

    @Override
    public ViewHolderBudget onCreateViewHolder(ViewGroup vg, int viewType)
    {
        View itemView = LayoutInflater.from(vg.getContext()).inflate(R.layout.row_layout_text_underline, vg, false);
        return new ViewHolderBudget(parent, itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolderEntity holder, int position)
    {
        //Budget
        Budget br = BudgetManager.getInstance().GetBudgets().get(position);
        if (br != null) {
            holder.mEntity = br;

            // Fields
            holder.title.setText(br.GetName());
            holder.subTitle.setText(br.GetDateFormatted(parent) + "\n" + br.GetPeriodFormatted(parent));

            if (holder.subTitle.getText() != null) { holder.subTitle.setVisibility(View.VISIBLE); }

            if (br.GetSelected()){
                holder.secondaryIcon.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override public int getItemCount()
    {
        return BudgetManager.getInstance().GetBudgetCount();
    }

}
