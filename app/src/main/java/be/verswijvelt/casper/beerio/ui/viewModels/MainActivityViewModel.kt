package be.verswijvelt.casper.beerio.ui.viewModels

import android.arch.lifecycle.ViewModel
import be.verswijvelt.casper.beerio.ui.AppConstants
import be.verswijvelt.casper.beerio.ui.fragments.BaseFragment
import be.verswijvelt.casper.beerio.ui.fragments.CategoriesFragment
import be.verswijvelt.casper.beerio.ui.fragments.MyBeersFragment
import java.util.*

class MainActivityViewModel : ViewModel() {
    private var backStacks: HashMap<String, Stack<BaseFragment>> = HashMap()
    var currentTab: String = AppConstants.TAB_MY_BEERS


    init {
        backStacks[AppConstants.TAB_MY_BEERS] = Stack()
        backStacks[AppConstants.TAB_BROWSE_ONLINE] = Stack()
    }


    fun currentBackStack(): Stack<BaseFragment> {
        return backStacks[currentTab]!!
    }

    fun currentFragment() : BaseFragment {
        return backStacks[currentTab]!!.peek()
    }

    fun backStackForTag(tag: String): Stack<BaseFragment>? {
        return backStacks[tag]
    }
}