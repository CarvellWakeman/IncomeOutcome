package carvellwakeman.incomeoutcome;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class AdapterManageBudgets extends RecyclerView.Adapter<AdapterManageBudgets.ProfileViewHolder>
{
    ActivityManageBudgets parent;

    public AdapterManageBudgets(ActivityManageBudgets _parent)
    {
        parent = _parent;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_text_underline, parent, false);
        return new ProfileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProfileViewHolder holder, int position)
    {
        //Budget
        Budget br = BudgetManager.getInstance().GetBudgets().get(position);
        if (br != null) {
            //Textview
            holder.title.setText(br.GetName());
            holder.subTitle.setText(br.GetDateFormatted());

            if (holder.subTitle.getText() != null) { holder.subTitle.setVisibility(View.VISIBLE); }

            if (br.GetSelected()){
                holder.secondaryIcon.setVisibility(View.VISIBLE);
            } else {
                holder.secondaryIcon.setVisibility(View.GONE);
            }
        }
    }

    @Override public int getItemCount()
    {
        return BudgetManager.getInstance().GetBudgetCount();
    }


    public class ProfileViewHolder extends ViewHolderTextUnderline implements View.OnClickListener, View.OnLongClickListener
    {
        public ProfileViewHolder(View itemView) { super(itemView); }

        @Override
        public void onClick(View v) {
            Budget br = BudgetManager.getInstance().GetBudgets().get(getAdapterPosition());
            if (br != null) {
                String descriptionString =
                        br.GetPeriodFormatted() + "\n" +
                        br.GetDateFormatted() + "\n" +
                                br.GetTransactionCount() + " " + Helper.getString(R.string.misc_transactoins);

                Helper.OpenDialogFragment(parent, DialogFragmentManagePPC.newInstance(parent, br.GetName(), descriptionString, String.valueOf(br.GetID()),
                        new ParentCallBack() { @Override public void call(String data, DialogFragmentManagePPC dialogFragment) { parent.EditBudget(Integer.valueOf(data), dialogFragment); } },
                        new ParentCallBack() { @Override public void call(String data, DialogFragmentManagePPC dialogFragment) { parent.SelectBudget(Integer.valueOf(data), dialogFragment); } },
                        new ParentCallBack() { @Override public void call(String data, DialogFragmentManagePPC dialogFragment) { parent.RemoveBudget(Integer.valueOf(data), dialogFragment); } }),
                        true); //TODO: Handle mIsLargeDisplay
                //parent.EditProfile(ProfileManager.getInstance().GetProfileByIndex(getAdapterPosition()));
            }
        }

        @Override
        public boolean onLongClick(View v){
            parent.SelectBudget(BudgetManager.getInstance().GetBudgets().get(getAdapterPosition()).GetID(), null);
            notifyDataSetChanged();
            return true;
        }
    }
}
