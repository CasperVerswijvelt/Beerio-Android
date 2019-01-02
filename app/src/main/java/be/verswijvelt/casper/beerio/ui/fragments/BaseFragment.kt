package be.verswijvelt.casper.beerio.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import be.verswijvelt.casper.beerio.ui.NavigationController

//BaseFragment class, is super class of all my fragments (except settings fragment) to minimize duplicate code in these fragments
abstract class BaseFragment : Fragment() {

    var fragmentTitle: String = ""
    protected lateinit var navigationController: NavigationController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationController = this.activity as NavigationController
        setHasOptionsMenu(true)
    }

    fun onBackPressed() {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {}

    open fun showLoader(show: Boolean) {
        navigationController.showLoader(show)
    }
}