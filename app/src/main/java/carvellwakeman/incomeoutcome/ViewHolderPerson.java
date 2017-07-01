package carvellwakeman.incomeoutcome;

import android.view.View;

public class ViewHolderPerson extends ViewHolderEntity<Person> {

    public ViewHolderPerson(ActivityManageEntity parent, View itemView) { super(parent, itemView); }

    @Override
    public void itemClick(final ActivityManageEntity parent, Person person) {
        if (person != null) {
            Helper.OpenDialogFragment(parent, DialogFragmentManagePPC.newInstance(parent, person.GetName(), "", String.valueOf(person.GetID()),
                    new ParentCallBack() { @Override public void call(String data, DialogFragmentManagePPC dialogFragment) { parent.EditEntity(Integer.valueOf(data), dialogFragment); } },
                    null,
                    new ParentCallBack() { @Override public void call(String data, DialogFragmentManagePPC dialogFragment) { parent.DeleteEntity(Integer.valueOf(data), dialogFragment); } }
            ), true); //TODO: Handle mIsLargeDisplay
        }
    }

}
