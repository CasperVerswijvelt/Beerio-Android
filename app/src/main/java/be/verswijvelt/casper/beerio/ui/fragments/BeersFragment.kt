package be.verswijvelt.casper.beerio.ui.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.LinearLayout
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.ui.adapters.BeerAdapter
import be.verswijvelt.casper.beerio.ui.viewModels.BeersViewModel
import be.verswijvelt.casper.beerio.ui.viewModels.BeersViewModelFactory
import be.verswijvelt.casper.beerio.ui.viewModels.ReloadableViewModel
import kotlinx.android.synthetic.main.empty_dataset_placeholder.*
import kotlinx.android.synthetic.main.fragment_beers.*

class BeersFragment : ReloadableFragment() {

    private var styleId = -1
    private var styleName = ""
    private var styleDescription: String? = null

    private val viewModel by lazy {
        val model = ViewModelProviders.of(this, BeersViewModelFactory(styleId, styleName, styleDescription))
            .get(BeersViewModel::class.java)
        super.reloadableViewModel = model as? ReloadableViewModel<List<*>>
        model
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

        //Hook our swipe refresh layoutinto our superclass: ReloadableFragment
        super.swipeRefreshLayout = swipeRefresh

        //Set adapter and layoutmanager for recyclerview
        recyclerView.adapter = BeerAdapter(navigationController)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)

        //Set customizable text of our no-data-placeholder
        noDataText.text = getString(R.string.no_beers_found_for_style)

        //Observe our beers , if we get a list as answer give it to the adapter, if our answer is null give empty list to adapter
        viewModel.getObservableData().observe(this, Observer {
            (recyclerView.adapter as BeerAdapter).setBeers(it ?: listOf())
        })

        //Observe savedbeers and if we get a new value we inform our recyclerview adapter
        viewModel.savedBeers.observe(this, Observer {
            if (it != null) {
                (recyclerView.adapter as BeerAdapter).setSavedBeers(it)
            }
        })


        reloadDataConditionally()
    }

    override fun onResume() {
        super.onResume()
        //Set swipe to refresh listener
        swipeRefresh.setOnRefreshListener {
            loadData()
        }
    }

    override fun onPause() {
        super.onPause()
        //remove swipe to refresh listener
        swipeRefresh.setOnRefreshListener(null)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        //Inflate the right menu (info button)
        inflater?.inflate(be.verswijvelt.casper.beerio.R.menu.options_info, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.infoButton -> {
                navigationController.showDialog(viewModel.styleName, viewModel.styleDescription)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    companion object {
        const val ARG_STYLEID = "categoryId"
        const val ARG_STYLENAME = "styleName"
        const val ARG_STYLEDESCRIPTION = "styleDescription"

        fun newInstance(styleId: Int, styleName: String, styleDescription: String): BeersFragment {
            val args = Bundle()
            args.putInt(ARG_STYLEID, styleId)
            args.putString(ARG_STYLENAME, styleName)
            args.putString(ARG_STYLEDESCRIPTION, styleDescription)
            val fragment = BeersFragment()
            fragment.arguments = args

            fragment.fragmentTitle = styleName

            return fragment
        }
    }
}
