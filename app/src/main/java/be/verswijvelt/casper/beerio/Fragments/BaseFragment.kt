package be.verswijvelt.casper.beerio.Fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import be.verswijvelt.casper.beerio.MainActivity


open class BaseFragment : Fragment() {
    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = this.activity as MainActivity
    }

    fun onBackPressed() {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {}
}