package be.verswijvelt.casper.beerio.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.data.services.IDataService


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CategoriesFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CategoriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CategoriesFragment : BaseFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }


}
