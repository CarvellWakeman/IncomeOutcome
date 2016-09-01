package carvellwakeman.incomeoutcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.Toast;

import java.util.ArrayList;

public class ActivityMain extends AppCompatActivity implements GestureDetector.OnGestureListener
{
    Toolbar toolbar;

    int _profileID;
    Profile _profile;

    CardVersus versusCard;
    CardTransaction expensesCard;
    CardTransaction incomeCard;

    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the profile manager
        ProfileManager.initialize(this);

        //Set our activity's data
        _profile = ProfileManager.GetCurrentProfile();
        if (_profile != null) {
            _profileID = _profile.GetID();


            //Find Views
            toolbar = (Toolbar) findViewById(R.id.toolbar);

            //Swiping gesture setup
            gestureDetector = new GestureDetector(this, this);
            gestureListener = new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            };

            toolbar.setOnTouchListener(gestureListener);


            //Configure toolbar
            //toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            //toolbar.setNavigationOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { onBackPressed(); } });
            toolbar.inflateMenu(R.menu.toolbar_menu_main);
            setSupportActionBar(toolbar);
            ToolbarTitleUpdate();


            //Card inflater
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup insertPoint = (ViewGroup) findViewById(R.id.overview_layout);


            //Cards
            versusCard = new CardVersus(_profileID, this, inflater, R.layout.card_versus);

            expensesCard = new CardTransaction(_profileID, 0, 1, ProfileManager.getString(R.string.header_expenses_summary), this, inflater, R.layout.card_transaction);
            incomeCard = new CardTransaction(_profileID, 1, 1, ProfileManager.getString(R.string.header_income_summary), this, inflater, R.layout.card_transaction);


            versusCard.insert(insertPoint, 0);
            expensesCard.insert(insertPoint, 1);
            incomeCard.insert(insertPoint, 2);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu_main, menu);
        return true;
    }


    //Toolbar button handling
    Intent intent;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {

            case android.R.id.home:
                intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                return true;
            case R.id.toolbar_action_settings: //Start settings activity
                intent = new Intent(ActivityMain.this, ActivitySettings.class);
                startActivityForResult(intent, 0);
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


    //Gestures
    @Override
    public boolean onTouchEvent(MotionEvent me) { return gestureDetector.onTouchEvent(me); }
    @Override
    public boolean onDown(MotionEvent e) {return true;}
    @Override
    public void onLongPress(MotionEvent e) {}
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {return true;}
    @Override
    public void onShowPress(MotionEvent e) {}
    @Override
    public boolean onSingleTapUp(MotionEvent e) { return true; }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        final int SWIPE_MIN_DISTANCE = 120;
        final int SWIPE_MAX_OFF_PATH = 250;
        final int SWIPE_THRESHOLD_VELOCITY = 200;

        if (e1 != null && e2 != null) {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) { return false; }

            if (_profile != null) {
                //Right to Left
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    _profile.TimePeriodPlus(1);
                }
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    _profile.TimePeriodMinus(1);
                }

                this.recreate();
                //_profile.CalculateTimeFrame(activityType);
                //_profile.CalculateTotalsInTimeFrame(activityType, keyType);
            }
        }
        return true;
    }


    //Get return results from activities
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {}

        //Refresh graphs if we return to this page. Not the most efficient, but simple
        RefreshOverview();
    }


    //Refresh overview
    public void RefreshOverview(){
        _profile = ProfileManager.GetCurrentProfile();
        if (_profile != null) {
            _profileID = _profile.GetID();

            ToolbarTitleUpdate();

            versusCard.SetProfileID(_profileID);
            expensesCard.SetProfileID(_profileID);
            incomeCard.SetProfileID(_profileID);

            versusCard.SetData();
            expensesCard.SetData();
            incomeCard.SetData();
        }
    }

    //Toolbar title update
    public void ToolbarTitleUpdate(){
        //Title
        toolbar.setTitle(_profile.GetName() + " " + ProfileManager.getString(R.string.title_overview));

        //Subtitle
        toolbar.setSubtitle(_profile.GetDateFormatted());
    }

}
