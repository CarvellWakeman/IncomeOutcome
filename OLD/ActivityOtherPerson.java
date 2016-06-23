package carvellwakeman.incomeoutcome;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

public class ActivityOtherPerson extends AppCompatActivity
{
    //ListView Adapter
    ArrayAdapter<String> adapter;

    //Views
    Toolbar toolbar;

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otherperson);

        //Find Views
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        list = (ListView) findViewById(R.id.listView_people);

        //Configure toolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(toolbar);

        //Set activity title
        toolbar.setTitle("Split Expenses with");


        //Set listview adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ProfileManager.GetOtherPeople());
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String items[] = {"Delete"};

                new AlertDialog.Builder(ActivityOtherPerson.this).setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0: //Delete
                                ProfileManager.RemoveOtherPerson(ProfileManager.GetOtherPersonByIndex(position));

                                adapter.clear();
                                adapter.addAll(ProfileManager.GetOtherPeople());
                                break;
                            default:
                                dialog.cancel();
                                break;
                        }
                    }
                }).create().show();
            }
        });
    }

    //Toolbar button handling
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    //Buttons
    public void OtherPersonNewPerson(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(ActivityOtherPerson.this);
        alert.setMessage("Allow Splitting of Expenses with");
        final EditText name = new EditText(ActivityOtherPerson.this);
        name.setHint("Enter Name...");
        alert.setView(name);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!name.getText().toString().equals("")) {
                    if (ProfileManager.HasOtherPerson(name.getText().toString())) {
                        ProfileManager.AddOtherPerson(name.getText().toString());

                        adapter.clear();
                        adapter.addAll(ProfileManager.GetOtherPeople());
                    }
                    else {
                        Toast.makeText(ActivityOtherPerson.this, "Person already exists", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(ActivityOtherPerson.this, "Name field empty, try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alert.setNegativeButton("Cancel", null);
        alert.show();
    }
}
