package com.wright.paul.allergytravelcardapp;

import android.content.pm.ActivityInfo;
import android.os.SystemClock;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import com.wright.paul.allergytravelcardapp.userInterface.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void testMoveFromMainActivityLandscapeToCardActivity() {
        // Click the create card button on the main activity
        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        SystemClock.sleep(100);
        onView(withId(R.id.addCardButton)).perform(click());

        // Check that the card activity is displayed.
        // Check that the view elements of the card activity are displayed.
        onView(withId(R.id.iconImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.cardTitleTextView)).check(matches(isDisplayed()));
        onView(withId(R.id.flagImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.allergyImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.cardDividerImageView1)).check(matches(isDisplayed()));
    }

    @Test
    public void testMainActivityPortrait() {
        addCard();

        //set device orientation to portrait
        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SystemClock.sleep(100);

        //check create card button and listview is visible
        onView(withId(R.id.fragment_card_list)).check(matches(isDisplayed()));
    }

    @Test
    public void testMainActivityLandscape() {
        //set device orientation to landscape
        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        SystemClock.sleep(100);

        //check listview is visible
        onView(withId(R.id.fragment_card_list)).check(matches(isDisplayed()));

        //check create card button and spinners are visible
        //onView(withId(R.id.fragment_create_card)).check(matches(isDisplayed()));
    }

    @Test
    public void testMoveFromMainActivityPortraitToCreateCardActivity() {
        //set device orientation to portrait
        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SystemClock.sleep(100);

        //check create card button and listview is visible, click the create card button
        onView(withId(R.id.fragment_card_list)).check(matches(isDisplayed()));
        onView(withId(R.id.createNewCardButton)).check(matches(isDisplayed())).perform(click());

        // Click the create new card button on the main activity
        // onView(withId(R.id.createNewCardButton)).perform(click());

        // Check that the two spinners and create card button are displayed.
        onView(withId(R.id.fragment_create_card)).check(matches(isDisplayed()));
        // onView(withId(R.id.allergySpinner)).check(matches(isDisplayed()));
        //onView(withId(R.id.createCardButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testMainActivitySelectFirstItem() {

        //addCard();

        // Select the first item in listview
        onData(anything()).inAdapterView(withId(R.id.listView)).atPosition(0).perform(click());

        // Check that the view elements of the card activity are displayed.
        onView(withId(R.id.cardBodyTextView)).check(matches(isDisplayed()));
    }

    @Test
    public void testMainActivityContexMenuView() {

       addCard();

        // Select the first item in listview
        onData(anything()).inAdapterView(withId(R.id.listView)).atPosition(0).perform(longClick());

        //check 'view card' context menu exists and click it
        onView(withText(R.string.view_card)).check(matches(isDisplayed())).perform(click());

        // Check that the view elements of the card activity are displayed.
        onView(withId(R.id.iconImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.cardTitleTextView)).check(matches(isDisplayed()));
        onView(withId(R.id.flagImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.allergyImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.cardDividerImageView1)).check(matches(isDisplayed()));
    }

    @Test
    public void testMainActivityContextMenuCheck() {

       // addCard();
        // Select the first item in listview
        onData(anything()).inAdapterView(withId(R.id.listView)).atPosition(0).perform(longClick());

        //check all context menu items exist
        onView(withText(R.string.view_card)).check(matches(isDisplayed()));
        onView(withText(R.string.delete_card)).check(matches(isDisplayed()));
        onView(withText(R.string.move_card_bottom)).check(matches(isDisplayed()));
        onView(withText(R.string.move_card_top)).check(matches(isDisplayed()));
        onView(withText(R.string.create_notification)).check(matches(isDisplayed()));
        onView(withText(R.string.show_countries)).check(matches(isDisplayed()));
    }

    private void addCard() {
        //create a card
        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        SystemClock.sleep(100);
        onView(withId(R.id.addCardButton)).perform(click());
        Espresso.pressBack();
    }
}