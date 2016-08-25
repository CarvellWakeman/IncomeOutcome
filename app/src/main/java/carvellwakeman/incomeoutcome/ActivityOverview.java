package carvellwakeman.incomeoutcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.Toast;


public class ActivityOverview extends AppCompatActivity
{
    //ArrayList<Integer> toolbar_menus;

    Toolbar toolbar;


    int _profileID;
    Profile _profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);


        //Get the intent that opened this activity
        Intent intent = getIntent();

        //Set our activity's data
        _profileID = intent.getIntExtra("profile", -1);
        _profile = ProfileManager.GetProfileByID(_profileID);
        if (_profile == null)
        {
            Toast.makeText(this, "Invalid Profile Data, Cannot Open details activity.", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            //Find Views
            toolbar = (Toolbar) findViewById(R.id.toolbar);


            //Configure toolbar
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            //toolbar.inflateMenu(R.menu.toolbar);
            setSupportActionBar(toolbar);

            //Title
            toolbar.setTitle(R.string.title_overview);

            //Subtitle
            toolbar.setSubtitle(_profile.GetDateFormatted());


            //Populate cards
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup insertPoint = (ViewGroup) findViewById(R.id.overview_layout);

            //Cards
            CardIncome incomeCard = new CardIncome( _profileID, this, inflater, R.layout.card_income,
                new View.OnClickListener() { @Override public void onClick(View v) {
                    ProfileManager.Print("Income Card Click");
                }});

            CardExpenses expensesCard = new CardExpenses( _profileID, this, inflater, R.layout.card_expenses,
                new View.OnClickListener() { @Override public void onClick(View v) {
                    ProfileManager.Print("Expenses Card Click");
                }});

            CardSplit splitCard = new CardSplit( _profileID, this, inflater, R.layout.card_split,
                new View.OnClickListener() { @Override public void onClick(View v) {
                    ProfileManager.Print("Split Card Click");
                }});


            incomeCard.insert(insertPoint, 0);
            expensesCard.insert(insertPoint, 1);
            splitCard.insert(insertPoint, 2);

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //getMenuInflater().inflate(m, menu);

        return true;
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


    //Send back a RESULT_OK to MainActivity when back is pressed
    @Override
    public void onBackPressed()
    {
        //Send back a RESULT_OK to MainActivity
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);

        super.onBackPressed();
    }


    //Get return results from activities
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
        }
    }


}
