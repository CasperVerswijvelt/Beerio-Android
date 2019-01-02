package be.verswijvelt.casper.beerio.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener
import android.support.design.widget.Snackbar.*
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONCategory
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONStyle
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.data.room.BeerRoomDatabase
import be.verswijvelt.casper.beerio.data.services.BeerRepository
import be.verswijvelt.casper.beerio.data.services.OnlineDataService
import be.verswijvelt.casper.beerio.ui.fragments.*
import be.verswijvelt.casper.beerio.ui.other.AppConstants
import be.verswijvelt.casper.beerio.ui.other.WifiReceiver
import be.verswijvelt.casper.beerio.ui.viewModels.MainActivityViewModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_main.*
import net.danlew.android.joda.JodaTimeAndroid
import org.jetbrains.anko.doAsync
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity(), NavigationController {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
    }

    //We need to keep a strong reference to this target, else it gets garbage collected since picasso only keeps weak reference
    private var imageSaveTarget : Target? = null

    //Listener for options_navigation item selected
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

    //Listener for options_navigation item reselected
    private val mOnNavigationItemReselectedListener = BottomNavigationView.OnNavigationItemReselectedListener {
        if (viewModel.currentBackStack().size != 1) {
            while (viewModel.currentBackStack().size != 1) {
                viewModel.currentBackStack().pop()
            }
            showFragmentForCurrentTab()
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        JodaTimeAndroid.init(this)

        //Setting initial pages in the viewmodel
        val myBeersFragment = MyBeersFragment()
        myBeersFragment.fragmentTitle = getString(R.string.my_beers_toolbartitle)
        val categoriesFragment = CategoriesFragment()
        categoriesFragment.fragmentTitle = getString(R.string.categories_toolbartitle)
        if(viewModel.backStackForTag(AppConstants.TAB_MY_BEERS).isNullOrEmpty())viewModel.backStackForTag(AppConstants.TAB_MY_BEERS)?.push(myBeersFragment)
        if(viewModel.backStackForTag(AppConstants.TAB_BROWSE_ONLINE).isNullOrEmpty())viewModel.backStackForTag(AppConstants.TAB_BROWSE_ONLINE)?.push(categoriesFragment)

        //Dependency injection
        BeerRepository.getInstance().setBeerDao(BeerRoomDatabase.getDatabase(application).beerDao())
        BeerRepository.getInstance().setOnlineDataService(OnlineDataService(PreferenceManager.getDefaultSharedPreferences(this)))

        //Layout
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //Register wifi listener
        registerReceiver(WifiReceiver {
            if (it) (viewModel.currentFragment() as? ReloadableFragment)?.connectedStateChanged(it)
        }, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION).apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        })

        showLoader(false)


    }

    override fun onResume() {
        super.onResume()
        //Set currenttab in viewmodel according to selected item in options_navigation bar
        viewModel.currentTab = if (navigation.selectedItemId == R.id.tab_my_beers) AppConstants.TAB_MY_BEERS else AppConstants.TAB_BROWSE_ONLINE
        showFragmentForCurrentTab()

        //Setting listeners for options_navigation item selected, reselected
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.setOnNavigationItemReselectedListener(mOnNavigationItemReselectedListener)

    }


    override fun onPause() {
        super.onPause()

        //Remove all listeners
        navigation.setOnNavigationItemSelectedListener(null)
        navigation.setOnNavigationItemReselectedListener(null)
    }


    //when options item is selected, open settings fragment if settings was tapped
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.settingsButton -> {
                //Switch to settigns fragment without putting it on our backstack, since it couldn't inherit of BaseFragment
                switchFragment(SettingsFragment(),R.anim.fade_in,R.anim.fade_out)

                toolbar.title = getString(R.string.settings_toolbartitle)
                showBackButton(true)
                showLoader(false)

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    //Checks which fragment should currently be show according to our reloadableViewModel, and show it
    private fun showFragmentForCurrentTab() {
        switchFragment(viewModel.currentFragment(),
            R.anim.fade_in,
            R.anim.fade_out
        )
    }


    //Push a fragment on our custom backstack for a specific tag  (one of either tabs)
    private fun pushFragments(tag: String, fragment: BaseFragment, shouldAnimate: Boolean) {
        viewModel.backStackForTag(tag)?.push(fragment)
        if (shouldAnimate) {
            switchFragment(fragment,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        } else {
            switchFragment(fragment)
        }
    }

    //Pop the currently shown fragment from the backstack for the tab that it is on
    private fun popFragment() {
        viewModel.currentBackStack().pop()
        switchFragment(viewModel.currentFragment(), android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }


    //Handle the switching of displayed fragment, contained in a single method
    private fun switchFragment(fragment: Fragment, enterAnimation: Int? = null, exitAnimation: Int? = null) {
        showLoader(false)
        val manager = supportFragmentManager
        val ft = manager.beginTransaction()
        if (enterAnimation != null && exitAnimation != null) ft.setCustomAnimations(enterAnimation, exitAnimation)
        ft.replace(R.id.fragment_container, fragment)
        ft.commit()

        //Update toolbar title according to current shown fragment
        updateToolbarTitle()

        //Only show back button if there are more than 1 fragment on the current backstack
        showBackButton(viewModel.currentBackStack().size > 1)

        //Invalidate options menu so it shows again
        invalidateOptionsMenu()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Stop this method from running in fragments if the data is null, needed for our image picker that kept crashing because data was null
        // See my reply at https://github.com/esafirm/android-image-picker/issues/186#issuecomment-450508790
        // for more detailed description of the problem
        if(data == null) return
        super.onActivityResult(requestCode, resultCode, data)
    }


    //Override the onbackpressed method and adjust implementation according to our own custom backstack
    override fun onBackPressed() {
        //If the fragment that should be currently visible is not resumed, it means that our settigns fragment is show.
        // Continue by just showing the fragment for current tab without poping anything from the backstack
        if (!viewModel.currentFragment().isResumed) {
            showFragmentForCurrentTab()
            return
        }
        //If the size of the current backstack is 1 and the selected tab is not MY_BEERS, then switch to MY_BEERS tab and show current fragment
        if (viewModel.currentBackStack().size == 1 && viewModel.currentTab != AppConstants.TAB_MY_BEERS) {
            navigation.selectedItemId = R.id.tab_my_beers
            viewModel.currentTab = AppConstants.TAB_MY_BEERS
            showFragmentForCurrentTab()
            return
        } //Else; if the backstack is size 1, still, it mans that the selected tab is MY_BEERS, we close the app at this point
        else if (viewModel.currentBackStack().size == 1) {
            finish()
            return
        }

        //Run onbackpressed in the fragment we are about to pop from the backstack, so they can react to it.
        (viewModel.currentBackStack().lastElement()).onBackPressed()
        popFragment()
    }


    //When arrow back is pressed, simulate back button pressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //Helper method that sets the back button to show or not show
    private fun showBackButton(bool: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(bool)
        supportActionBar?.setDisplayShowHomeEnabled(bool)
    }

    //NavigationController interface implementation
    override fun showLoader(show: Boolean) {
        progressBar?.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun notify(text: String, length: Int) {
        make(findViewById(R.id.viewSnack), text, length).show()
    }

    override fun showDialog(title: String, text: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setPositiveButton(android.R.string.ok,null)
        builder.setTitle(title)
        builder.setMessage(text)
        val alertDialog: AlertDialog? = builder.create()
        alertDialog?.show()
    }

    override fun updateToolbarTitle() {
        toolbar.title = viewModel.currentFragment().fragmentTitle
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
        pushFragments(viewModel.currentTab, BeerDetailsFragment.newInstance(beer), true)
    }

    override fun showImage(toolbarTitle: String, url: String) {
        pushFragments(viewModel.currentTab, ImageFragment.newInstance(url, toolbarTitle), true)
    }

    override fun showAddBeerScreen() {
        val fragment = AddBeerFragment.newInstance()
        fragment.fragmentTitle = getString(R.string.add_new_beer_toolbartitle)
        pushFragments(viewModel.currentTab,fragment , true)
    }

    override fun showEditBeerScreen(beer: Beer) {
        val fragment = AddBeerFragment.newInstance(beer)
        fragment.fragmentTitle = getString(R.string.edit_beer_toolbartitle)
        pushFragments(viewModel.currentTab,fragment ,true)
    }

    override fun exitCurrentFragment() {
        popFragment()
    }



    //Method that saves a beer image locally in the right location in files Directory in this app.
    // Placed this method in the navigationController because otherwise it would be alot of duplicate code to do it in the other places it was needed
    override fun saveBeerImageLocally(url: String, id: String) {
        val fileName = filesDir.path + "/" + id + ".png"
        imageSaveTarget = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                doAsync {
                    val file = File(fileName)
                    try {
                        file.createNewFile()
                        val ostream = FileOutputStream(file,false)
                        bitmap.compress(Bitmap.CompressFormat.PNG, 80, ostream)
                        ostream.flush()
                        ostream.close()
                        Picasso.get().invalidate("file://$fileName")
                        runOnUiThread {
                            //Update the beer details fragment to show that the beer has a bottle label available to show, after the image has been saved
                            (viewModel.currentFragment() as? BeerDetailsFragment)?.updateData()
                            imageSaveTarget = null
                        }

                    } catch (e: IOException) {
                        Log.e("IOException", e.localizedMessage)
                    }
                }
            }
            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                e?.printStackTrace()
            }
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }
        }
        Picasso.get()
            .load(url)
            .resize(1000,1000)
            .onlyScaleDown()
            .centerCrop()
            .into(imageSaveTarget!!)
    }

    //Remove a beer image from the right location according to beer id
    override fun deleteImage(id: String) {
        val fileName = filesDir.path + "/" + id + ".png"
        File(fileName).delete()
        Picasso.get().invalidate("file://$fileName")
    }


    //Method available to return the filesDir of this context,usefull for other fragments that want to know if a specific file exists in this directory
    override fun getFilesDirectory(): File {
        return filesDir
    }


}


interface NavigationController {
    fun showLoader(show: Boolean)
    fun notify(text: String, length: Int = LENGTH_SHORT)
    fun showDialog(title: String, text: String?)
    fun updateToolbarTitle()

    fun showCategory(category: JSONCategory)
    fun showBeers(style: JSONStyle)
    fun showBeer(beer: Beer)
    fun showImage(toolbarTitle: String, url: String)
    fun showAddBeerScreen()
    fun showEditBeerScreen(beer:Beer)
    fun exitCurrentFragment()

    fun saveBeerImageLocally(url: String, id: String)
    fun deleteImage(id:String)

    fun getFilesDirectory(): File

}
