package be.verswijvelt.casper.beerio.ui.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.ui.adapters.BeerAdapter
import be.verswijvelt.casper.beerio.ui.viewModels.BeersViewModel
import be.verswijvelt.casper.beerio.ui.viewModels.BeersViewModelFactory
import kotlinx.android.synthetic.main.empty_dataset_placeholder.*
import kotlinx.android.synthetic.main.error_placeholder.*
import kotlinx.android.synthetic.main.fragment_beers.*

class BeersFragment : BaseFragment() {

    private var styleId = -1
    private var styleName = ""
    private var styleDescription :String?= null

    private val viewModel by lazy {
        ViewModelProviders.of(this, BeersViewModelFactory(styleId,styleName,styleDescription)).get(BeersViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments!!.let { bundle ->
            if (bundle.containsKey(ARG_STYLEID)) styleId = bundle.getInt(ARG_STYLEID)
            if (bundle.containsKey(ARG_STYLENAME)) styleName = bundle.getString(ARG_STYLENAME)!!
            if (bundle.containsKey(ARG_STYLEDESCRIPTION)) styleDescription = bundle.getString(ARG_STYLEDESCRIPTION)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set adapter and layoutmanager for recyclerview
        recyclerView.adapter = BeerAdapter(navigationController)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)
        //Set customizable text of our no-data-placeholder
        noDataText.text = getString(R.string.no_beers_found_for_style)
        showLoader(true)
        //Observe our beers , if we get a list as answer give it to the adapter, if our answer is null give empty list to adapter
        viewModel.getBeers().observe(this, Observer {
            if(it != null) {
                (recyclerView.adapter as BeerAdapter).setBeers(it)
            } else {
                (recyclerView.adapter as BeerAdapter).setBeers(listOf())
            }

            //Show empty data set placeholder if received value is not null but is empty
            emptyDataSetPlaceHolder.visibility = if(it != null && it.isEmpty()) View.VISIBLE else View.GONE
            //show error placeholder if value is null
            error_placeholder.visibility = if(it == null) View.VISIBLE else View.GONE
            showLoader(false)
            swipeRefresh.isRefreshing = false
        })
        //Observe savedbeers and if we get a new value we inform our recyclerview adapter
        viewModel.savedBeers.observe(this, Observer {
            if(it != null) {
                (recyclerView.adapter as BeerAdapter).setSavedBeers(it)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        //Set swipe to refresh listener
        swipeRefresh.setOnRefreshListener {
            viewModel.loadData()
        }
    }

    override fun onPause() {
        super.onPause()
        //remove swipe to refresh listener
        swipeRefresh.setOnRefreshListener(null)
    }


    companion object {
        const val ARG_STYLEID = "categoryId"
        const val ARG_STYLENAME = "styleName"
        const val ARG_STYLEDESCRIPTION = "styleDescription"

        fun newInstance(styleId : Int, styleName:String,styleDescription:String): BeersFragment {
            val args = Bundle()
            args.putInt(ARG_STYLEID,styleId)
            args.putString(ARG_STYLENAME,styleName)
            args.putString(ARG_STYLEDESCRIPTION,styleDescription)
            val fragment = BeersFragment()
            fragment.arguments = args

            fragment.fragmentTitle = styleName

            return fragment
        }
    }

    //override what should happen when title in toolbar is clicked when this fragment is shown
    override fun getTitleClickedHandler(): () -> Unit {
        return {
            navigationController.showDialog(viewModel.styleName, viewModel.styleDescription)
        }
    }

}
