package be.verswijvelt.casper.beerio.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONCategory
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONStyle
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.data.room.BeerRoomDatabase
import be.verswijvelt.casper.beerio.data.services.BeerRepository
import be.verswijvelt.casper.beerio.data.services.OnlineDataService
import be.verswijvelt.casper.beerio.ui.fragments.*
import be.verswijvelt.casper.beerio.ui.viewModels.MainActivityViewModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_main.*
import net.danlew.android.joda.JodaTimeAndroid
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


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

        //Dependency injection
        BeerRepository.getInstance().setBeerDao(BeerRoomDatabase.getDatabase(application).beerDao())
        BeerRepository.getInstance().setOnlineDataService(OnlineDataService(PreferenceManager.getDefaultSharedPreferences(this)))

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
                ft.setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out
                )
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
        switchFragment(viewModel.currentBackStack().peek(),
            R.anim.fade_in,
            R.anim.fade_out
        )
    }


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

        updateToolbarTitle()
        showLoader(false)

        showBackButton(viewModel.currentBackStack().size > 1)


        invalidateOptionsMenu()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data == null) return
        super.onActivityResult(requestCode, resultCode, data)
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
        builder.setPositiveButton(
            "Ty"
        ) { dialog, id ->
            // User clicked OK button
        }
        builder.setTitle(title)
        builder.setMessage(text)

        // Create the AlertDialog
        val alertDialog: AlertDialog? = builder.create()

        alertDialog?.show()

    }

    override fun updateToolbarTitle() {
        toolbar.title = viewModel.currentBackStack().peek().fragmentTitle
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
        pushFragments(viewModel.currentTab, AddBeerFragment.newInstance(), true)
    }

    override fun showEditBeerScreen(beer: Beer) {
        pushFragments(viewModel.currentTab, AddBeerFragment.newInstance(beer),true)
    }

    override fun exitCurrentFragment() {
        popFragment()
    }



    override fun saveBeerImageLocally(url: String, id: String) {
        Log.d("BEERIODEBUG","saveBeerImageLocally $url $id")
        val fileName = filesDir.path + "/" + id + ".png"
        Picasso.get()
            .load(url)
            .resize(1000,1000)
            .onlyScaleDown()
            .centerCrop()
            .into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                    Thread(Runnable {
                        val file = File(fileName)
                        try {
                            file.createNewFile()
                            val ostream = FileOutputStream(file,false)
                            bitmap.compress(Bitmap.CompressFormat.PNG, 80, ostream)
                            ostream.flush()
                            ostream.close()
                            Picasso.get().invalidate("file://$fileName")
                            Log.d("BEERIODEBUG","Wrote image to $fileName")
                            runOnUiThread {
                                (viewModel.currentBackStack().peek() as? BeerDetailsFragment)?.updateData()
                            }
                        } catch (e: IOException) {
                            Log.e("IOException", e.localizedMessage)
                        }
                    }).start()
                }
                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    Log.d("BEERIODEBUG",e?.localizedMessage)
                    e?.printStackTrace()
                }
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            })
    }

    override fun deleteImage(id: String) {
        val fileName = filesDir.path + "/" + id + ".png"
        File(fileName).delete()
        Picasso.get().invalidate("file://$fileName")
    }


    override fun getFilesDirectory(): File {
        return filesDir
    }


}


interface NavigationController {
    fun showLoader(show: Boolean)
    fun showToast(text: String)
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
    fun deleteImage(url:String)

    fun getFilesDirectory(): File

}
