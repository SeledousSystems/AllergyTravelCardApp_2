package com.wright.paul.allergytravelcardapp;

import android.content.pm.ActivityInfo;
import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import com.wright.paul.allergytravelcardapp.userInterface.CreateCardActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class CreateCardActivityTest {

    @Rule
    public ActivityTestRule<CreateCardActivity> mActivityRule = new ActivityTestRule<>(
            CreateCardActivity.class);

    @Test
    public void testMoveFromCreateCardActivityPortraitToCardActivity() {
        // Click the create card button on the main activity
        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SystemClock.sleep(100);
        onView(withId(R.id.addCardButton)).perform(click());

        // Check that the view elements of the card activity are displayed.
        onView(withId(R.id.iconImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.cardTitleTextView)).check(matches(isDisplayed()));
        onView(withId(R.id.flagImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.allergyImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.cardDividerImageView1)).check(matches(isDisplayed()));
    }

    @Test
    public void testChangeOrientationPortraitToLandscape() {
        //set device orientation to portrait
        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SystemClock.sleep(100);

        //switch orientation to landscape
        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        SystemClock.sleep(100);

        //check view switches to main activity
        //check card list view fragment is visible
        onView(withId(R.id.fragment_card_list)).check(matches(isDisplayed()));

    }

    @Test
    public void testChangeOrientationLandscapeToPortrait() {
        //set device orientation to portrait
        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        SystemClock.sleep(100);

        //switch orientation to landscape
        mActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SystemClock.sleep(100);

        //check view switches to main activity
        onView(withId(R.id.fragment_create_card)).check(matches(isDisplayed()));
    }

    @Test
    public void testLanguageSpinner() {

        onView(withId(R.id.languageSpinner)).perform(click());
        onView(withText("Chinese")).check(matches(isDisplayed()));
    }

    @Test
    public void testAllergySpinner() {

        onView(withId(R.id.allergySpinner)).perform(click());
        onView(withText("Celery")).check(matches(isDisplayed()));
    }



}