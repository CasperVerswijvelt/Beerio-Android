package be.verswijvelt.casper.beerio.ui.utils

import android.support.test.espresso.matcher.BoundedMatcher
import android.support.v7.widget.RecyclerView
import be.verswijvelt.casper.beerio.ui.adapters.BeerDetailsViewHolder
import be.verswijvelt.casper.beerio.ui.adapters.MyBeerHolder
import org.hamcrest.Description
import org.hamcrest.Matcher

class Utils{
    companion object {
        fun withBeerDetailHolderValue(text: String): Matcher<RecyclerView.ViewHolder> {
            return object :
                BoundedMatcher<RecyclerView.ViewHolder, BeerDetailsViewHolder>(BeerDetailsViewHolder::class.java) {

                override fun describeTo(description: Description) {
                    description.appendText("No ViewHolder found with text: $text")
                }

                override fun matchesSafely(item: BeerDetailsViewHolder): Boolean {
                    return item.value?.text.toString().contains(text)
                }
            }
        }
        fun withBeerDetailHolderKey(text: String): Matcher<RecyclerView.ViewHolder> {
            return object :
                BoundedMatcher<RecyclerView.ViewHolder, BeerDetailsViewHolder>(BeerDetailsViewHolder::class.java) {

                override fun describeTo(description: Description) {
                    description.appendText("No ViewHolder found with text: $text")
                }

                override fun matchesSafely(item: BeerDetailsViewHolder): Boolean {
                    return item.key.text.toString().contains(text)
                }
            }
        }

        fun withBeerHolderBeerName(text: String): Matcher<RecyclerView.ViewHolder> {
            return object :
                BoundedMatcher<RecyclerView.ViewHolder, MyBeerHolder>(MyBeerHolder::class.java) {

                override fun describeTo(description: Description) {
                    description.appendText("No ViewHolder found with text: $text")
                }

                override fun matchesSafely(item: MyBeerHolder): Boolean {
                    return item.name.text.toString() == text
                }
            }
        }
    }
}