package com.example.yvonnewu.frontend;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Yvonne Wu on 11/1/2017.
 */
public class ProfilePageActivityTest
{
    @Rule
    public ActivityTestRule<ProfilePageActivity> mActivityRule = new ActivityTestRule<>(ProfilePageActivity.class);
    private String name = "TestUser";
    private String phone = "1234";
    private String email = "TestEmail";
    private String location = "TestLocation";
    private String userId = "DummyID";
    private String imageURL = null;
    private User ownerUser;

    @Before
    public void setUp() throws Exception
    {
        ownerUser = User.getInstance();
        ownerUser.setID(userId);
        ownerUser.setUserPic(imageURL);
    }

    @Test
    // test if fields are empty, user cannot proceed and is alerted
    public void verifyFieldsCheck()
    {
        //click save info button
        Espresso.onView(withId(R.id.saveBtnProfile)).perform(click());

        //check if the alert box is shown
        Espresso.onView(withText(R.string.checkFieldsAlert)).inRoot(isDialog())
                .check(matches(isDisplayed()));
        Espresso.onView(withText(R.string.okBtnAlert)).inRoot(isDialog()).perform(click());

    }

    @After
    public void tearDown() throws Exception
    {
    }

}