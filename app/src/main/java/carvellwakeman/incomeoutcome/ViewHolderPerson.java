package carvellwakeman.incomeoutcome;

import android.view.View;

public class ViewHolderPerson extends ViewHolderEntity<Person> {

    public ViewHolderPerson(ActivityManageEntity parent, View itemView) { super(parent, itemView); }

    @Override
    public void itemClick(final ActivityManageEntity parent, Person person) {
        if (person != null) {
            Helper.OpenDialogFragment(parent, DialogFragmentManageBPC.newInstance(parent, person.GetName(), parent.getString(R.string.info_delete_person_inuse_warning), String.valueOf(person.GetID()),
                    new ParentCallBack() { @Override public void call(String data, DialogFragmentManageBPC dialogFragment) { parent.EditEntity(Integer.valueOf(data), dialogFragment); } },
                    null,
                    new ParentCallBack() { @Override public void call(String data, DialogFragmentManageBPC dialogFragment) { parent.DeleteEntity(Integer.valueOf(data), dialogFragment); } }
            ), true); //TODO: Handle mIsLargeDisplay
        }
    }

}
