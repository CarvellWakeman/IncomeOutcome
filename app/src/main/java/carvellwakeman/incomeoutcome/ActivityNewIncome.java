package carvellwakeman.incomeoutcome;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.joda.time.LocalDate;
//import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;


public class ActivityNewIncome extends AppCompatActivity
{
    //Data structure IDs
    int _profileID;
    int _incomeID;
    int _editCopyOrClone;

    LocalDate _cloneDate;

    //Helper variables
    Double tCost;

    //Fragments
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    FragmentTimePeriod fragment_timePeriod;


    //Views
    Toolbar toolbar;

    //Spinner spinner_categories;

    EditText editText_sourcename;
    EditText editText_description;
    EditText editText_payment;

    FrameLayout frameLayout_timePeriod;


    boolean noInfiniteLoopPlease = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_income);

        //Variable defaults
        noInfiniteLoopPlease = true;
        tCost = 0d;
        _profileID = -1;
        _incomeID = 0;
        _editCopyOrClone = 0;
        _cloneDate = null;


        //Time Period fragment
        fragment_timePeriod = new FragmentTimePeriod();


        //Find Views
        toolbar = (Toolbar) findViewById(R.id.toolbar);


        frameLayout_timePeriod = (FrameLayout) findViewById(R.id.frameLayout_timePeriod);

        //spinner_categories = (Spinner) findViewById(R.id.spinner_newIncome_categories);


        editText_sourcename = ((TextInputLayout)findViewById(R.id.TIL_newIncome_sourcename)).getEditText();

        editText_description = ((TextInputLayout)findViewById(R.id.TIL_newIncome_description)).getEditText();


        editText_payment = ((TextInputLayout)findViewById(R.id.TIL_newIncome_amount)).getEditText();


        //Categories spinner
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_title,  ProfileManager.categories){
        //    @Override
        //    public boolean isEnabled(int position) {
        //        return (position !=0);
        //    }
        //};
        //adapter.setDropDownViewResource(R.layout.spinner_dropdown_list_primary);
        //spinner_categories.setAdapter(adapter);


        //Configure toolbar
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.inflateMenu(R.menu.toolbar_menu_save);
        setSupportActionBar(toolbar);


        //Set TimePeriod fragment
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout_timePeriod, fragment_timePeriod);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


        //Set Title
        toolbar.setTitle("New Income");
        
        
        //Cost formatting
        editText_payment.setKeyListener(DigitsKeyListener.getInstance(false, true));
        editText_payment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                UpdatePayment();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        //Get intent from launching activity
        Intent intent = getIntent();
        _profileID = intent.getIntExtra("profile", -1);
        _incomeID = intent.getIntExtra("income", 0);
        _editCopyOrClone = intent.getIntExtra("edit", 0);
        _cloneDate = (LocalDate) intent.getSerializableExtra("cloneDate");

        //Copy expense details if expense was provided in the intent
        if (_incomeID != 0){
            EditIncome();
        }

        //Clear timeperiod blacklistdates queue
        Profile pr = ProfileManager.GetProfileByID(_profileID);
        if (pr != null){
            Income in = pr.GetIncome(_incomeID);
            if (in != null) {
                if (in.GetTimePeriod() != null) {
                    in.GetTimePeriod().ClearBlacklistQueue();
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_save, menu);
        return true;
    }

    //Toolbar button handling
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home: //X close button
                this.finish();
                return true;
            case R.id.toolbar_save: //SAVE button
                FinishNewIncome();
                return true;
            default:
                return false;
        }
    }

    //Get return results from activities
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if(resultCode == RESULT_OK){}
    }


    //Edit Income
    public void EditIncome()
    {
        if (_profileID != -1) {
            if (_incomeID != 0) {
                Profile pr = ProfileManager.GetProfileByID(_profileID);
                Income in = (pr != null ? pr.GetIncome(_incomeID) : null);

                //If we were sent an expense instead of creating a new one
                if (in != null) {

                    //Set activity title appropriately
                    if (_editCopyOrClone == 2){ toolbar.setTitle("Copy Income"); } else { toolbar.setTitle("Edit Income"); }

                    //Copy category
                    //if (ProfileManager.categories.contains(in.GetCategory())) {
                    //    spinner_categories.setSelection(ProfileManager.categories.indexOf(in.GetCategory()), true);
                    //}

                    //Copy source name
                    editText_sourcename.setText(in.GetSourceName());

                    //Copy editText_description
                    editText_description.setText(in.GetDescription());

                    //Copy time period (If no clone date was provided)
                    if (_cloneDate == null) {
                        fragment_timePeriod.SetTimePeriod(in.GetTimePeriod());
                    }
                    else { fragment_timePeriod.SetTimePeriod(new TimePeriod(_cloneDate)); }


                    //Copy editText_cost
                    editText_payment.setText(String.valueOf(in.GetValue()));
                }
            }
        }
    }


    //Payment amount updates
    public void UpdatePayment()
    {
        //Find total amount
        tCost = getDouble(editText_payment.getText().toString());
    }


    //Helpers
    public Double getDouble(String str)
    {
        try {
            return Double.parseDouble(str);
        }
        catch (NumberFormatException ex) {
            return 0.0d;
        }
    }


    //Buttons
    public void FinishNewIncome()
    {
        //Update payment
        UpdatePayment();

        //Find profile
        Profile pr = null;
        if (_profileID != -1) {
            pr = ProfileManager.GetProfileByID(_profileID);
        }

        //If profile exists
        if (pr != null) {

            //Find original transaction
            Income originalInc = pr.GetIncome(_incomeID);

            //Create intent to send back
            Intent intent = new Intent();

            //Create a new transaction or locate the one we're editing
            Income newInc;

            if (_incomeID != 0) {
                if (_editCopyOrClone == 1) { //edit
                    newInc = originalInc;
                }
                else { newInc = new Income(); }
            }
            else {
                newInc = new Income();
            }


            //If the expense now exists
            if (newInc != null) {

                //Set Cost
                newInc.SetValue(tCost);

                //Set Name
                newInc.SetSourceName(editText_sourcename.getText().toString());

                //Set Category
                //newInc.SetCategory((String) spinner_categories.getSelectedItem());

                //Set Description
                newInc.SetDescription(editText_description.getText().toString());

                //Set Time Period
                TimePeriod tp = fragment_timePeriod.GetTimePeriod();

                //Set time period
                newInc.SetTimePeriod(tp);


                //Return new (or edited) transaction and profile in intent
                intent.putExtra("profile", _profileID);
                intent.putExtra("newIncome", newInc);

                //Return original transaction if cloned, and clone date
                if (_editCopyOrClone == 3) {
                    intent.putExtra("originalIncome", _incomeID);
                    intent.putExtra("cloneDate", _cloneDate);
                }

                //Clear timeperiod blacklistdates queue
                if (originalInc != null) {
                    if (originalInc.GetTimePeriod() != null) {
                        originalInc.GetTimePeriod().RemoveBlacklistDateQueue();
                    }
                }

                //Set result and finish activity
                setResult(RESULT_OK, intent);
                finish();
            }
            else {
                Toast.makeText(this, "Missing Income Data", Toast.LENGTH_LONG).show();
            }
        }
    }

}



