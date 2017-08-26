package carvellwakeman.incomeoutcome;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.zip.Inflater;

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

        // Deleted person addition
        Intent intent = getIntent();
        if (intent.getBooleanExtra("includeDeletedPerson", false)){
            View itemView = getLayoutInflater().inflate(R.layout.row_layout_text_underline, null);
            ViewHolderPerson vhe = new ViewHolderPerson(this, itemView);
            vhe.base.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("entity", Person.Deleted.GetID());
                    setResult(1, intent);
                    finish();
                }
            });
            vhe.title.setText(Person.Deleted.GetName());
            vhe.icon.setImageDrawable(Helper.getDrawable(R.drawable.ic_face_white_24dp));

            //Inflate parent
            edit_layout.addView(itemView, 0);
        }
    }


    // Get person
    @Override
    public Person GetEntity(){
        return PersonManager.getInstance().GetPerson(editText_name.getText().toString());
    }

    // Edit person
    @Override
    public void EditEntity(Integer id, final DialogFragmentManageBPC dialogFragment){

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
    public void DeleteEntity(final Integer id, final DialogFragmentManageBPC dialogFragment){
        if (id != 0) {
            new AlertDialog.Builder(this).setTitle(R.string.confirm_areyousure_deletesingle)
                .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    // Update transactions using this category
                    BudgetManager bm = BudgetManager.getInstance();
                    DatabaseManager dm = DatabaseManager.getInstance(ActivityManagePeople.this);

                    for (Budget b : bm.GetBudgets()){
                        for (Transaction t : b.GetAllTransactions()){
                            if (t.GetPaidBy() == id){
                                t.SetPaidBy(Person.Deleted.GetID());
                            }
                            Double splitVal = t.GetSplitArray().get(id);
                            if (splitVal != null){
                                t.SetSplit(Person.Deleted.GetID(), splitVal);
                                t.RemoveSplit(id);
                            }
                            dm.insert(t, true);
                        }
                    }

                    Person person = PersonManager.getInstance().GetPerson(id);
                    PersonManager.getInstance().RemovePerson(person);
                    DatabaseManager.getInstance(ActivityManagePeople.this).removePersonSetting(person);

                    adapter.notifyDataSetChanged();
                    dialogFragment.dismiss();
                    dialog.dismiss();
                }})
                .setNegativeButton(R.string.action_cancel, null)
                .create().show();

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
            DatabaseManager.getInstance(ActivityManagePeople.this).insertSetting(editingEntity, true);
        }
    }


    //Expand and retract sub menus
    @Override
    public void OpenEditMenu(){
        super.OpenEditMenu();

        //Set title
        toolbar.setTitle( R.string.title_editperson );
    }

    @Override
    public void OpenAddMenu(){
        super.OpenAddMenu();

        //Set title
        toolbar.setTitle( R.string.title_addnewperson );
    }

    @Override
    public void OpenSelectMode(){
        super.OpenSelectMode();

        //Set title
        toolbar.setTitle( R.string.title_selectperson );
    }

    @Override
    public void CloseSubMenus(){
        super.CloseSubMenus();

        //Set title
        toolbar.setTitle( R.string.title_managepeople );
    }

}