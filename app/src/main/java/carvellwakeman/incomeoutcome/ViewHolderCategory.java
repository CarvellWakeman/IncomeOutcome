package carvellwakeman.incomeoutcome;

import android.view.View;

public class ViewHolderCategory extends ViewHolderEntity<Category> {

    public ViewHolderCategory(ActivityManageEntity parent, View itemView) { super(parent, itemView); }

    @Override
    public void itemClick(final ActivityManageEntity parent, Category cat) {
        if (cat != null) {
            Helper.OpenDialogFragment(parent, DialogFragmentManageBPC.newInstance(parent, cat.GetTitle(), "", String.valueOf(cat.GetID()),
                    new ParentCallBack() { @Override public void call(String data, DialogFragmentManageBPC dialogFragment) { parent.EditEntity(Integer.valueOf(data), dialogFragment); } },
                    null,
                    new ParentCallBack() { @Override public void call(String data, DialogFragmentManageBPC dialogFragment) { parent.DeleteEntity(Integer.valueOf(data), dialogFragment); } }),
                    true);
        }
    }
}
