package carvellwakeman.incomeoutcome;


import android.content.Context;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ActivityMainTest {

    Context mContext;


    @Before
    public void Setup(){
        mContext = App.GetContext();
    }


    @Rule
    public ActivityTestRule<ActivityMain> mActivityRule =
            new ActivityTestRule<>(ActivityMain.class);

    @Test
    @UiThreadTest
    public void selectedBudget(){
        Budget bud = new Budget("touch");
        // BROKEN

        // Initial toolbar title is not budget name

        // Set selected budget
        bud.SetSelected(true);
        mActivityRule.getActivity()._selectedBudget = bud;

        // Refresh activity, toolbar title is budget name
        mActivityRule.getActivity().RefreshActivity();
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText(bud.GetName() + " " + mContext.getString(R.string.title_overview))));
    }

    @Test
    public void nextPeriod() {
        // Click the next period button
        onView(withId(R.id.button_nextPeriod))
                .perform(click());

        // Check that the text was changed.
        //onView(withId(R.id.button_nextPeriod)).check(matches(withText("Lalala")));
    }

    /*
    @Test
    public void changeText_newActivity() {
        // Type text and then press the button.
        onView(withId(R.id.inputField)).perform(typeText("NewText"),
                closeSoftKeyboard());
        onView(withId(R.id.switchActivity)).perform(click());

        // This view is in a different Activity, no need to tell Espresso.
        onView(withId(R.id.resultView)).check(matches(withText("NewText")));
    }
    */
}
