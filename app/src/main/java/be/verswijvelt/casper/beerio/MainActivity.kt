package be.verswijvelt.casper.beerio

import android.os.Bundle
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import be.verswijvelt.casper.beerio.Fragments.BaseFragment
import be.verswijvelt.casper.beerio.Fragments.CategoriesFragment
import be.verswijvelt.casper.beerio.Fragments.MyBeersFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*



class MainActivity : AppCompatActivity() {


    private lateinit var backStacks: HashMap<String, Stack<Fragment>>

    //In here we save the identifier for the current tab
    private var currentTab: String = AppConstants.TAB_MY_BEERS


    private val mOnNavigationItemSelectedListener = OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.tab_my_beers -> {
                currentTab = AppConstants.TAB_MY_BEERS
            }
            R.id.tab_browse_online -> {
                currentTab = AppConstants.TAB_BROWSE_ONLINE
            }

        }
        Log.d("INFO","---------- Current tab: $currentTab")
        showFragmentForCurrentTab()
        return@OnNavigationItemSelectedListener true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        backStacks = HashMap()
        backStacks[AppConstants.TAB_MY_BEERS] = Stack()
        backStacks[AppConstants.TAB_BROWSE_ONLINE] = Stack()

        //Putting initial fragments in the tabs
        backStacks[AppConstants.TAB_MY_BEERS]!!.push(MyBeersFragment())
        backStacks[AppConstants.TAB_BROWSE_ONLINE]!!.push(CategoriesFragment())


        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        showFragmentForCurrentTab()

    }


    private fun showFragmentForCurrentTab() {
        val manager = supportFragmentManager
        val ft = manager.beginTransaction()
        ft.replace(R.id.fragment_container, backStacks[currentTab]!!.peek())
        ft.commit()
    }



    fun pushFragments(tag: String, fragment: Fragment, shouldAnimate: Boolean) {
        backStacks[tag]!!.push(fragment)
        val manager = supportFragmentManager
        val ft = manager.beginTransaction()
        if (shouldAnimate)
            ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        ft.replace(R.id.fragment_container, fragment)
        ft.commit()
    }


    private fun popFragment() {
        val fragment = backStacks[currentTab]!!.elementAt(backStacks[currentTab]!!.size - 2)

        backStacks[currentTab]?.pop()

        val manager = supportFragmentManager
        val ft = manager.beginTransaction()
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        ft.replace(R.id.fragment_container, fragment)
        ft.commit()
    }

    override fun onBackPressed() {
        if (backStacks[currentTab]!!.size === 1) {
            finish()
            return
        }

        (backStacks[currentTab]!!.lastElement() as BaseFragment).onBackPressed()

        popFragment()
    }



}
