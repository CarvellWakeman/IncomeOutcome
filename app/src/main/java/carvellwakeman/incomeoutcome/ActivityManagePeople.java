package carvellwakeman.incomeoutcome;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;

public class ActivityManagePeople extends ActivityManageEntity<Person> {

    public ActivityManagePeople() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_managepeople);
        super.onCreate(savedInstanceState);


        toolbar.setTitle(R.string.title_managepeople);


        //Set adapter
        adapter = new AdapterManagePeople(this);
        recyclerView.setAdapter(adapter);

    }


    //Check if the user is allowed to save
    @Override
    public void CheckCanSave() {
        String name = editText_name.getText().toString();

        if (name.equals("")) {
            SetSaveButtonEnabled(false);
        } else {
            if (PersonManager.getInstance().GetPerson(name) != null) {
                SetSaveButtonEnabled(false);
            } else {
                SetSaveButtonEnabled(true);
            }
        }
    }


    // Get person
    @Override
    public Person GetEntity(){
        return PersonManager.getInstance().GetPerson(editText_name.getText().toString());
    }

    // Edit person
    @Override
    public void EditEntity(Integer id, final DialogFragmentManagePPC dialogFragment){

        editingEntity = PersonManager.getInstance().GetPerson(id);
        if (editingEntity != null){
            menuState = MENU_STATE.EDIT;

            //Open edit layout
            OpenEditMenu();

            //Load information
            editText_name.setText(editingEntity.GetName());

            //Dismiss fragment
            dialogFragment.dismiss();
        }
    }

    // Delete person
    @Override
    public void DeleteEntity(final Integer id, final DialogFragmentManagePPC dialogFragment){
        if (id != 0) {
            new AlertDialog.Builder(this).setTitle(R.string.confirm_areyousure_deletesingle)
                    .setPositiveButton(R.string.action_deleteitem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Person person = PersonManager.getInstance().GetPerson(id);
                            PersonManager.getInstance().RemovePerson(person);
                            DatabaseManager.getInstance().removePersonSetting(person);

                            adapter.notifyDataSetChanged();
                            dialogFragment.dismiss();
                            dialog.dismiss();
                        }})
                    .setNegativeButton(R.string.action_cancel, null).create().show();

        }
    }

    // Select category
    @Override
    public void SelectEntity(Person person){
        Intent intent = new Intent();
        intent.putExtra("entity", person.GetID());
        setResult(1, intent);
        finish();
    }

    // Save category
    @Override
    public void SaveAction(){
        String newPerson = editText_name.getText().toString();

        if (!newPerson.equals("")) {
            //New
            if (editingEntity == null){
                editingEntity = new Person(newPerson);
            } else { //Update
                editingEntity.SetName(newPerson);
            }

            //Add or update old person
            PersonManager.getInstance().AddPerson(editingEntity);
            DatabaseManager.getInstance().insertSetting(editingEntity, true);
        }
    }


    //Expand and retract sub menus
    public void OpenEditMenu(){
        super.OpenEditMenu();

        //Set title
        toolbar.setTitle( R.string.title_editperson );
    }

    public void OpenAddMenu(){
        super.OpenAddMenu();

        //Set title
        toolbar.setTitle( R.string.title_addnewperson );
    }

    public void OpenSelectMode(){
        super.OpenSelectMode();

        //Set title
        toolbar.setTitle( R.string.title_selectperson );
    }

    public void CloseSubMenus(){
        super.CloseSubMenus();

        //Set title
        toolbar.setTitle( R.string.title_managepeople );
    }

}