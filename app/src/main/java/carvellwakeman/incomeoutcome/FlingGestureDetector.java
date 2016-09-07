package carvellwakeman.incomeoutcome;

import android.view.GestureDetector;
import android.view.MotionEvent;
import org.joda.time.LocalDate;

class FlingGestureDetector extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) { return false; }
            Profile pr = ProfileManager.getInstance().GetCurrentProfile();
            if (pr != null) {
                LocalDate e;
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (pr.GetEndTime() == null) { e = new LocalDate(); }
                    else { e = pr.GetEndTime().plusMonths(1); }
                    e = e.withDayOfMonth(e.dayOfMonth().getMaximumValue());

                    pr.SetStartTime(e.withDayOfMonth(e.dayOfMonth().getMinimumValue()));
                    pr.SetEndTime(e);
                }
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (pr.GetEndTime() == null) { e = new LocalDate(); } else { e = pr.GetEndTime().minusMonths(1); }
                    e = e.withDayOfMonth(e.dayOfMonth().getMaximumValue());

                    pr.SetStartTime(e.withDayOfMonth(e.dayOfMonth().getMinimumValue()));
                    pr.SetEndTime(e);
                }

                //UpdateStartEndDate();

            }
        } catch (Exception e) {
            // nothing
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }
}