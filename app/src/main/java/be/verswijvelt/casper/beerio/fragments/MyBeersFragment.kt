package be.verswijvelt.casper.beerio.fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.viewModels.StylesViewModel
import be.verswijvelt.casper.beerio.viewModels.StylesViewModelFactory


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MyBeersFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MyBeersFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MyBeersFragment : BaseFragment() {

    init {
        super.fragmentTitle = "My Beers"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_beers, container, false)
    }


}
