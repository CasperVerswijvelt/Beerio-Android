package be.verswijvelt.casper.beerio.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.adapters.BeerAdapter
import be.verswijvelt.casper.beerio.viewModels.BeersViewModel
import be.verswijvelt.casper.beerio.viewModels.BeersViewModelFactory
import kotlinx.android.synthetic.main.empty_dataset_placeholder.*
import kotlinx.android.synthetic.main.error_placeholder.*
import kotlinx.android.synthetic.main.fragment_beers.*

class BeersFragment : BaseFragment() {

    private var styleId = -1
    private var styleName = ""
    private var styleDescription :String?= null

    private val viewModel by lazy {
        ViewModelProviders.of(this, BeersViewModelFactory(styleId,styleName,styleDescription, activity!!.application)).get(BeersViewModel::class.java)
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
        recyclerView.adapter = BeerAdapter(navigationController)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)
        noDataText.text = "No beers could be found for this style"
        showLoader(true)
        viewModel.getBeers().observe(this, Observer {

            if(it != null) {
                (recyclerView.adapter as BeerAdapter).setBeers(it)
            } else {
                (recyclerView.adapter as BeerAdapter).setBeers(listOf())
            }

            emptyDataSetPlaceHolder.visibility = if(it != null && it.isEmpty()) View.VISIBLE else View.GONE
            error_placeholder.visibility = if(it == null) View.VISIBLE else View.GONE
            showLoader(false)
            swipeRefresh.isRefreshing = false
        })
        viewModel.savedBeers.observe(this, Observer {
            if(it != null) {
                (recyclerView.adapter as BeerAdapter).setSavedBeers(it)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        swipeRefresh.setOnRefreshListener {
            viewModel.loadData()
        }
    }

    override fun onPause() {
        super.onPause()
        swipeRefresh.setOnRefreshListener(null)
    }


    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
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

    override fun getTitleClickedHandler(): () -> Unit {
        return {
            navigationController.showDialog(viewModel.styleName, viewModel.styleDescription)
        }
    }

}
