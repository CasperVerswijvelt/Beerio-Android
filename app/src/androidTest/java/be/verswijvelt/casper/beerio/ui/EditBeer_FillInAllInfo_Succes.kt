package be.verswijvelt.casper.beerio.ui


import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.ui.adapters.BeerDetailsAdapter
import be.verswijvelt.casper.beerio.ui.utils.Utils
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class EditBeer_FillInAllInfo_Success {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )!!

    @Test
    fun editBeer_FillInAllInfo_Succes() {
        val floatingActionButton = onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.fragment_container),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        floatingActionButton.perform(click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.nameInput),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.support.design.widget.TextInputLayout")),
                        0
                    ),
                    0
                )
            )
        )
        appCompatEditText.perform(scrollTo(), replaceText("EditBeerTestName"), closeSoftKeyboard())

        val actionMenuItemView = onView(
            allOf(
                withId(R.id.saveedit_beer), withContentDescription("Save edited/created beer"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        actionMenuItemView.perform(click())

        onView(withText("EditBeerTestName")).perform(click())

        val actionMenuItemView2 = onView(
            allOf(
                withId(R.id.edit_beer), withContentDescription("Edit this beer"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        actionMenuItemView2.perform(click())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.nameInput),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.support.design.widget.TextInputLayout")),
                        0
                    ),
                    0
                )
            )
        )
        appCompatEditText2.perform(scrollTo(), click())
        appCompatEditText2.perform(scrollTo(), replaceText("Brugse Zot"))
        appCompatEditText2.perform(closeSoftKeyboard())

        val appCompatEditText5 = onView(
            allOf(
                withId(R.id.descriptionInput),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.support.design.widget.TextInputLayout")),
                        0
                    ),
                    0
                )
            )
        )
        appCompatEditText5.perform(
            scrollTo(),
            replaceText("Lekker bier van Brugge uit de Halve Maan"),
            closeSoftKeyboard()
        )

        val appCompatEditText6 = onView(
            allOf(
                withId(R.id.foodPairingsInput),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.support.design.widget.TextInputLayout")),
                        0
                    ),
                    0
                )
            )
        )
        appCompatEditText6.perform(scrollTo(), replaceText("Lekker met al het eten"), closeSoftKeyboard())

        val appCompatEditText7 = onView(
            allOf(
                withId(R.id.originalGravityInput),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.support.design.widget.TextInputLayout")),
                        0
                    ),
                    0
                )
            )
        )
        appCompatEditText7.perform(scrollTo(), replaceText("Zeer graaf"), closeSoftKeyboard())

        val appCompatEditText8 = onView(
            allOf(
                withId(R.id.alcoholByVolumeInput),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.support.design.widget.TextInputLayout")),
                        0
                    ),
                    0
                )
            )
        )
        appCompatEditText8.perform(scrollTo(), replaceText("Zeer zwaar"), closeSoftKeyboard())

        val appCompatEditText9 = onView(
            allOf(
                withId(R.id.internationalBitteringUnitInput),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.support.design.widget.TextInputLayout")),
                        0
                    ),
                    0
                )
            )
        )
        appCompatEditText9.perform(scrollTo(), replaceText("Zeer bitter"), closeSoftKeyboard())

        val appCompatEditText10 = onView(
            allOf(
                withId(R.id.servingTemperatureInput),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.support.design.widget.TextInputLayout")),
                        0
                    ),
                    0
                )
            )
        )
        appCompatEditText10.perform(scrollTo(), replaceText("Zeer koel"), closeSoftKeyboard())

        val appCompatButton = onView(
            allOf(
                withId(R.id.yearButton),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        7
                    ),
                    1
                )
            )
        )
        appCompatButton.perform(scrollTo(), click())


        onView(withId(R.id.yearpickerNumberPicker)).perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isAssignableFrom(NumberPicker::class.java)
            }

            override fun getDescription(): String {
                return "Set the value of a NumberPicker"
            }

            override fun perform(uiController: UiController, view: View) {
                (view as NumberPicker).value = 2006
            }
        })

        val appCompatImageButton = onView(
            withId(R.id.yearPickerSetButton)
        )
        appCompatImageButton.perform(click())

        onView(withText("2006")).check(matches(isDisplayed()))

        val switch = onView(
            allOf(
                withId(R.id.isOrganicSwitch), withText(R.string.is_organic),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    8
                )
            )
        )
        switch.perform(scrollTo(), click())


        val actionMenuItemView3 = onView(
            allOf(
                withId(R.id.saveedit_beer), withContentDescription(R.string.save_edited_created_beer),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        actionMenuItemView3.perform(click())




        val recyclerView = mActivityTestRule.activity.findViewById(R.id.recyclerView) as RecyclerView
        recyclerView.adapter as BeerDetailsAdapter


        onView(ViewMatchers.withId(R.id.recyclerView)).perform(RecyclerViewActions.scrollToHolder(Utils.withBeerDetailHolderValue("Lekker bier van Brugge uit de Halve Maan")))
        onView(ViewMatchers.withId(R.id.recyclerView)).perform(RecyclerViewActions.scrollToHolder(Utils.withBeerDetailHolderValue("Lekker met al het eten")))
        onView(ViewMatchers.withId(R.id.recyclerView)).perform(RecyclerViewActions.scrollToHolder(Utils.withBeerDetailHolderValue("Zeer graaf")))
        onView(ViewMatchers.withId(R.id.recyclerView)).perform(RecyclerViewActions.scrollToHolder(Utils.withBeerDetailHolderValue("Zeer zwaar")))
        onView(ViewMatchers.withId(R.id.recyclerView)).perform(RecyclerViewActions.scrollToHolder(Utils.withBeerDetailHolderValue("Zeer bitter")))
        onView(ViewMatchers.withId(R.id.recyclerView)).perform(RecyclerViewActions.scrollToHolder(Utils.withBeerDetailHolderValue("Zeer koel")))
        onView(ViewMatchers.withId(R.id.recyclerView)).perform(RecyclerViewActions.scrollToHolder(Utils.withBeerDetailHolderValue("2006")))

    }





    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
