package be.verswijvelt.casper.beerio.viewModels

import android.arch.lifecycle.ViewModel
import android.support.v4.app.Fragment
import android.util.Log
import be.verswijvelt.casper.beerio.AppConstants
import be.verswijvelt.casper.beerio.fragments.BaseFragment
import be.verswijvelt.casper.beerio.fragments.CategoriesFragment
import be.verswijvelt.casper.beerio.fragments.MyBeersFragment
import java.util.*

class MainActivityViewModel : ViewModel() {
    private var backStacks: HashMap<String, Stack<BaseFragment>> = HashMap()
    var currentTab: String = AppConstants.TAB_MY_BEERS


    init {
        backStacks[AppConstants.TAB_MY_BEERS] = Stack()
        backStacks[AppConstants.TAB_BROWSE_ONLINE] = Stack()

        backStacks[AppConstants.TAB_MY_BEERS]!!.push(MyBeersFragment())
        backStacks[AppConstants.TAB_BROWSE_ONLINE]!!.push(CategoriesFragment())
    }


    fun currentBackStack(): Stack<BaseFragment> {
        return backStacks[currentTab]!!
    }

    fun backStackForTag(tag: String): Stack<BaseFragment>? {
        return backStacks[tag]
    }
}