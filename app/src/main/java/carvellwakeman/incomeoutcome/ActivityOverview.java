package carvellwakeman.incomeoutcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.Toast;


public class ActivityOverview extends AppCompatActivity implements GestureDetector.OnGestureListener
{
    //ArrayList<Integer> toolbar_menus;

    Toolbar toolbar;


    int _profileID;
    Profile _profile;

    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

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

            //Swiping gesture setup
            gestureDetector = new GestureDetector(this, this);
            gestureListener = new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            };

            toolbar.setOnTouchListener(gestureListener);


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


            //Card inflater
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup insertPoint = (ViewGroup) findViewById(R.id.overview_layout);

            //Cards
            CardVersus versusCard = new CardVersus( _profileID, this, inflater, R.layout.card_versus);

            CardTransaction expensesCard = new CardTransaction( _profileID, 0, 1, ProfileManager.getString(R.string.header_expenses_summary), this, inflater, R.layout.card_transaction);
            CardTransaction incomeCard = new CardTransaction( _profileID, 1, 1, ProfileManager.getString(R.string.header_income_summary), this, inflater, R.layout.card_transaction);


            versusCard.insert(insertPoint, 0);
            expensesCard.insert(insertPoint, 1);
            incomeCard.insert(insertPoint, 2);

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
        if (data != null) {
        }
    }


}
