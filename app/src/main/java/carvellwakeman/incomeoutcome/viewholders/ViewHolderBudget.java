package carvellwakeman.incomeoutcome.viewholders;

import android.view.View;
import carvellwakeman.incomeoutcome.*;
import carvellwakeman.incomeoutcome.dialogs.DialogFragmentManageBPC;
import carvellwakeman.incomeoutcome.helpers.Helper;
import carvellwakeman.incomeoutcome.interfaces.ParentCallBack;
import carvellwakeman.incomeoutcome.models.Budget;
import carvellwakeman.incomeoutcome.activities.ActivityManageBudgets;
import carvellwakeman.incomeoutcome.activities.ActivityManageEntity;

public class ViewHolderBudget extends ViewHolderEntity<Budget> {

    public ViewHolderBudget(ActivityManageEntity parent, View itemView) { super(parent, itemView); }

    @Override
    public void itemClick(final ActivityManageEntity parent, Budget budget) {
        if (budget != null) {
            String descriptionString =
                    budget.GetPeriodFormatted(parent) + "\n" +
                            budget.GetDateFormatted(parent) + "\n" +
                            budget.GetTransactionCount() + " " + parent.getString(R.string.misc_transactoins);

            Helper.OpenDialogFragment(parent, DialogFragmentManageBPC.newInstance(parent, budget.GetName(), descriptionString, String.valueOf(budget.GetID()),
                    new ParentCallBack() { @Override public void call(String data, DialogFragmentManageBPC dialogFragment) { parent.EditEntity(Integer.valueOf(data), dialogFragment); } },
                    new ParentCallBack() { @Override public void call(String data, DialogFragmentManageBPC dialogFragment) { ((ActivityManageBudgets)parent).SelectBudget(Integer.valueOf(data), dialogFragment); } },
                    new ParentCallBack() { @Override public void call(String data, DialogFragmentManageBPC dialogFragment) { parent.DeleteEntity(Integer.valueOf(data), dialogFragment); } }),
                    true); //TODO: Handle mIsLargeDisplay
        }
    }

    @Override
    public void itemLongClick(ActivityManageEntity parent, Budget entity){
        ((ActivityManageBudgets)mParent).SelectBudget(entity.GetID(), null);
    }
}
