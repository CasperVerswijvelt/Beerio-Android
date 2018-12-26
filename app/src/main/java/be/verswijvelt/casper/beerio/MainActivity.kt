package be.verswijvelt.casper.beerio

import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONCategory
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONStyle
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.data.services.IDataService
import be.verswijvelt.casper.beerio.fragments.*
import be.verswijvelt.casper.beerio.viewModels.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import net.danlew.android.joda.JodaTimeAndroid
import org.w3c.dom.Text


class MainActivity : AppCompatActivity(), NavigationController {
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
    }

    private val mOnNavigationItemSelectedListener = OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.tab_my_beers -> {
                viewModel.currentTab = AppConstants.TAB_MY_BEERS
            }
            R.id.tab_browse_online -> {
                viewModel.currentTab = AppConstants.TAB_BROWSE_ONLINE
            }

        }
        Log.d("INFO", "---------- Current tab: $viewModel.currentTab")
        showFragmentForCurrentTab()
        return@OnNavigationItemSelectedListener true
    }
    private val mOnNavigationItemReselectedListener = BottomNavigationView.OnNavigationItemReselectedListener { _ ->
        if (viewModel.currentBackStack().size != 1) {
            while (viewModel.currentBackStack().size != 1) {
                viewModel.currentBackStack().pop()
            }
            showFragmentForCurrentTab()
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {

        JodaTimeAndroid.init(this)

        IDataService.getInstance().setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        showLoader(false)

    }

    override fun onResume() {
        super.onResume()
        viewModel.currentTab =
                if (navigation.selectedItemId == R.id.tab_my_beers) AppConstants.TAB_MY_BEERS else AppConstants.TAB_BROWSE_ONLINE
        showFragmentForCurrentTab()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.setOnNavigationItemReselectedListener(mOnNavigationItemReselectedListener)

        setToolbarTitleClickListener {
            viewModel.currentBackStack().peek().getTitleClickedHandler().invoke()
        }

    }


    override fun onPause() {
        super.onPause()

        navigation.setOnNavigationItemSelectedListener(null)
        navigation.setOnNavigationItemReselectedListener(null)
        removeToolbarTitleClickListener()

    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.settingsButton -> {
                val manager = supportFragmentManager
                val ft = manager.beginTransaction()
                ft.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
                ft.replace(R.id.fragment_container, SettingsFragment())
                ft.commit()

                toolbar.title = "Settings"

                showBackButton(true)

                showLoader(false)

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showFragmentForCurrentTab() {
        switchFragment(viewModel.currentBackStack().peek(), R.anim.abc_fade_in, R.anim.abc_fade_out)
    }


    private fun pushFragments(tag: String, fragment: BaseFragment, shouldAnimate: Boolean) {
        viewModel.backStackForTag(tag)?.push(fragment)
        if (shouldAnimate) {
            switchFragment(fragment, R.anim.slide_in_right, R.anim.slide_out_left)
        } else {
            switchFragment(fragment)
        }
    }


    private fun popFragment() {
        val fragment = viewModel.currentBackStack().elementAt(viewModel.currentBackStack().size - 2)
        viewModel.currentBackStack().pop()
        switchFragment(fragment, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }


    private fun switchFragment(fragment: BaseFragment, enterAnimation: Int? = null, exitAnimation: Int? = null) {
        val manager = supportFragmentManager
        val ft = manager.beginTransaction()
        if (enterAnimation != null && exitAnimation != null) ft.setCustomAnimations(enterAnimation, exitAnimation)
        ft.replace(R.id.fragment_container, fragment)
        ft.commit()

        toolbar.title = fragment.fragmentTitle
        showLoader(false)

        showBackButton(viewModel.currentBackStack().size > 1)


        invalidateOptionsMenu()
    }


    override fun onBackPressed() {
        if (fragment_container.getChildAt(0) is LinearLayout) {
            showFragmentForCurrentTab()
            return
        }

        if (viewModel.currentBackStack().size == 1 && viewModel.currentTab != AppConstants.TAB_MY_BEERS) {
            navigation.selectedItemId = R.id.tab_my_beers
            viewModel.currentTab = AppConstants.TAB_MY_BEERS
            showFragmentForCurrentTab()
            return
        } else if (viewModel.currentBackStack().size == 1) {
            finish()
            return
        }

        (viewModel.currentBackStack().lastElement() as BaseFragment).onBackPressed()

        popFragment()
    }

    private fun showBackButton(bool: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(bool)
        supportActionBar?.setDisplayShowHomeEnabled(bool)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setToolbarTitleClickListener(handler: () -> Unit) {
        if (toolbar == null)
            return

        for (i in 0..toolbar.childCount) {
            if (toolbar.getChildAt(i) is TextView) {
                (toolbar.getChildAt(i) as TextView).setOnClickListener {
                    handler()
                }
            }
        }
    }

    private fun removeToolbarTitleClickListener() {
        for (i in 0..toolbar.childCount) {
            if (toolbar.getChildAt(i) is TextView) {
                (toolbar.getChildAt(i) as TextView).setOnClickListener(null)
            }
        }
    }


    //NavigationController interface implementation

    override fun showLoader(show: Boolean) {
        progressBar?.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun showDialog(title: String, text: String?) {

        val builder = AlertDialog.Builder(this)
        builder.setPositiveButton("Ty"
        ) { dialog, id ->
            // User clicked OK button
        }
        builder.setTitle(title)
        builder.setMessage(text)

        // Create the AlertDialog
        val alertDialog: AlertDialog? = builder.create()

        alertDialog?.show()

    }



    override fun showCategory(category: JSONCategory) {
        pushFragments(
            AppConstants.TAB_BROWSE_ONLINE,
            StylesFragment.newInstance(category.id, category.name, category.description),
            true
        )

    }

    override fun showBeers(style: JSONStyle) {
        pushFragments(
            AppConstants.TAB_BROWSE_ONLINE,
            BeersFragment.newInstance(style.id, style.name, style.description),
            true
        )
    }

    override fun showBeer(beer: Beer) {
        pushFragments(AppConstants.TAB_BROWSE_ONLINE, BeerDetailsFragment.newInstance(beer), true)
    }

    override fun showImage(toolbarTitle: String, url: String) {
        pushFragments(AppConstants.TAB_BROWSE_ONLINE, ImageFragment.newInstance(url, toolbarTitle), true)
    }

}


interface NavigationController {
    fun showLoader(show: Boolean)
    fun showToast(text: String)
    fun showDialog(title: String, text: String?)

    fun showCategory(category: JSONCategory)
    fun showBeers(style: JSONStyle)
    fun showBeer(beer: Beer)
    fun showImage(toolbarTitle: String, url: String)


}
