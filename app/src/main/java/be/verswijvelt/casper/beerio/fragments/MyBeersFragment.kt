package be.verswijvelt.casper.beerio.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.adapters.MyBeerAdapter
import be.verswijvelt.casper.beerio.viewModels.MyBeersViewModel
import kotlinx.android.synthetic.main.fragment_my_beers.*


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

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MyBeersViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_beers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = MyBeerAdapter(navigationController)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)
        showLoader(true)
        viewModel.beers.observe(this, Observer {

            if(it != null) {
                (recyclerView.adapter as MyBeerAdapter).setMyBeers(it)
            } else {
                (recyclerView.adapter as MyBeerAdapter).setMyBeers(listOf())
            }
            showLoader(false)
        })


        val swipeHandler = object : SwipeToDeleteCallback(context!!) {

            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                (recyclerView.adapter as MyBeerAdapter).removeAt(p0.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onResume() {
        super.onResume()
        fab.setOnClickListener {
            navigationController.showAddBeerScreen()
        }
    }

    override fun onPause() {
        super.onPause()
        fab.setOnClickListener(null)
    }


}
