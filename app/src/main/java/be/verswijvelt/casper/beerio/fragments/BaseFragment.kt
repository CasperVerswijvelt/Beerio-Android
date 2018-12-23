package be.verswijvelt.casper.beerio.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import be.verswijvelt.casper.beerio.NavigationController


open class BaseFragment : Fragment() {
    var fragmentTitle : String =""
    protected lateinit var navigationController: NavigationController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationController = this.activity as NavigationController
    }

    fun onBackPressed() {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {}

    fun showLoader(show:Boolean) {
        navigationController.showLoader(show)
    }
}