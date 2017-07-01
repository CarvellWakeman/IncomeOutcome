package carvellwakeman.incomeoutcome;

import android.view.View;

public class ViewHolderBudget extends ViewHolderEntity<Budget> {

    public ViewHolderBudget(ActivityManageEntity parent, View itemView) { super(parent, itemView); }

    @Override
    public void itemClick(final ActivityManageEntity parent, Budget budget) {
        if (budget != null) {
            String descriptionString =
                    budget.GetPeriodFormatted() + "\n" +
                            budget.GetDateFormatted() + "\n" +
                            budget.GetTransactionCount() + " " + Helper.getString(R.string.misc_transactoins);

            Helper.OpenDialogFragment(parent, DialogFragmentManagePPC.newInstance(parent, budget.GetName(), descriptionString, String.valueOf(budget.GetID()),
                    new ParentCallBack() { @Override public void call(String data, DialogFragmentManagePPC dialogFragment) { parent.EditEntity(Integer.valueOf(data), dialogFragment); } },
                    new ParentCallBack() { @Override public void call(String data, DialogFragmentManagePPC dialogFragment) { ((ActivityManageBudgets)parent).SelectBudget(Integer.valueOf(data), dialogFragment); } },
                    new ParentCallBack() { @Override public void call(String data, DialogFragmentManagePPC dialogFragment) { parent.DeleteEntity(Integer.valueOf(data), dialogFragment); } }),
                    true); //TODO: Handle mIsLargeDisplay
        }
    }

    @Override
    public void itemLongClick(ActivityManageEntity parent, Budget entity){
        ((ActivityManageBudgets)mParent).SelectBudget(BudgetManager.getInstance().GetBudgets().get(getAdapterPosition()).GetID(), null);
    }
}
