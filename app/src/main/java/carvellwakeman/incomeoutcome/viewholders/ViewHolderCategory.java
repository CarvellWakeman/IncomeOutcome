package carvellwakeman.incomeoutcome.viewholders;

import android.view.View;
import carvellwakeman.incomeoutcome.*;
import carvellwakeman.incomeoutcome.dialogs.DialogFragmentManageBPC;
import carvellwakeman.incomeoutcome.helpers.Helper;
import carvellwakeman.incomeoutcome.interfaces.ParentCallBack;
import carvellwakeman.incomeoutcome.models.Category;
import carvellwakeman.incomeoutcome.activities.ActivityManageEntity;

public class ViewHolderCategory extends ViewHolderEntity<Category> {

    public ViewHolderCategory(ActivityManageEntity parent, View itemView) { super(parent, itemView); }

    @Override
    public void itemClick(final ActivityManageEntity parent, Category cat) {
        if (cat != null) {
            Helper.OpenDialogFragment(parent, DialogFragmentManageBPC.newInstance(parent, cat.GetTitle(), parent.getString(R.string.info_delete_category_inuse_warning), String.valueOf(cat.GetID()),
                    new ParentCallBack() { @Override public void call(String data, DialogFragmentManageBPC dialogFragment) { parent.EditEntity(Integer.valueOf(data), dialogFragment); } },
                    null,
                    new ParentCallBack() { @Override public void call(String data, DialogFragmentManageBPC dialogFragment) { parent.DeleteEntity(Integer.valueOf(data), dialogFragment); } }),
                    true);
        }
    }
}
