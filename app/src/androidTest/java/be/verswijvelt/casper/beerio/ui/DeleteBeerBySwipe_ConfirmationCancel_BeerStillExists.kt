package be.verswijvelt.casper.beerio.ui


import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.ui.utils.Utils
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class DeleteBeerBySwipe_ConfirmationCancel_BeerStillExists {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun deleteBeerBySwipe_ConfirmationCancel_BeerStillExists() {
        val testName = "DeleteBeerBySwipeCancelTestName"
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
        appCompatEditText.perform(scrollTo(), replaceText(testName), closeSoftKeyboard())

        val actionMenuItemView = onView(
            allOf(
                withId(R.id.saveedit_beer),
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

        onView(ViewMatchers.withId(R.id.recyclerView)).perform(RecyclerViewActions.scrollToHolder(Utils.withBeerHolderBeerName(testName))).perform(
            click())


        val textView = onView(withText(testName))
        textView.check(matches(withText(testName)))

        textView.perform(swipeLeft())

        val appCompatButton = onView(
            allOf(
                withId(android.R.id.button2), withText(android.R.string.cancel),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.buttonPanel),
                        0
                    ),
                    2
                )
            )
        )
        appCompatButton.perform(scrollTo(), click())

        val textView3 = onView(
            allOf(
                withId(R.id.beerName), withText(testName),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textView3.check(matches(withText(testName)))
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
