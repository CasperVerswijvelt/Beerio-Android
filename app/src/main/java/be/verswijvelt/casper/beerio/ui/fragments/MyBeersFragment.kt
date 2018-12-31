package be.verswijvelt.casper.beerio.ui.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.ui.adapters.MyBeerAdapter
import be.verswijvelt.casper.beerio.ui.other.SwipeToDeleteCallback
import be.verswijvelt.casper.beerio.ui.viewModels.MyBeersViewModel
import kotlinx.android.synthetic.main.empty_dataset_placeholder.*
import kotlinx.android.synthetic.main.fragment_my_beers.*



class MyBeersFragment : BaseFragment() {

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
        //Set adapter and layoutmanager of our recyclerview
        recyclerView.adapter = MyBeerAdapter(navigationController)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)
        //Set customizable text of our no-data-placeholder
        noDataText.text = getString(R.string.no_beers_saved)

        //Observe the beers on our viewModel, if new value is null, give empty list to our adapter, else, give the given list to our adapter
        viewModel.beers.observe(this, Observer {
            if(it != null) {
                (recyclerView.adapter as MyBeerAdapter).setMyBeers(it)
            } else {
                (recyclerView.adapter as MyBeerAdapter).setMyBeers(listOf())
            }
            showLoader(false)

            //Show empty data set placeholder if received value is null or empty
            emptyDataSetPlaceHolder.visibility = if(it == null || it.isEmpty()) View.VISIBLE else View.GONE
        })

        showLoader(true)
        //Setup swipe to delete handler
        val swipeHandler = object : SwipeToDeleteCallback(context!!) {
            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                AlertDialog.Builder(context!!)
                    .setTitle(getString(R.string.delete_beer_dialogtitle))
                    .setMessage(getString(R.string.delete_beer_confirmation))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes)
                            { _, _ ->  (recyclerView.adapter as MyBeerAdapter).removeAt(p0.adapterPosition) }
                    .setNegativeButton(android.R.string.no)
                            { _, _ ->  (recyclerView.adapter as MyBeerAdapter).notifyItemChanged(p0.adapterPosition) }
                    .show()


            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onResume() {
        super.onResume()

        //Set fab clicked listener
        fab.setOnClickListener {
            navigationController.showAddBeerScreen()
        }
    }

    override fun onPause() {
        super.onPause()

        //remove fab clicked listener
        fab.setOnClickListener(null)
    }


}
