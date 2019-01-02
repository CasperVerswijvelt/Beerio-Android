package be.verswijvelt.casper.beerio.ui.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.LinearLayout
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.ui.adapters.StyleAdapter
import be.verswijvelt.casper.beerio.ui.viewModels.ReloadableViewModel
import be.verswijvelt.casper.beerio.ui.viewModels.StylesViewModel
import be.verswijvelt.casper.beerio.ui.viewModels.StylesViewModelFactory
import kotlinx.android.synthetic.main.empty_dataset_placeholder.*
import kotlinx.android.synthetic.main.fragment_styles.*


class StylesFragment : ReloadableFragment() {


    private val viewModel by lazy {
        val model = ViewModelProviders.of(this, StylesViewModelFactory(categoryId, categoryName, categoryDescription))
            .get(StylesViewModel::class.java)
        super.reloadableViewModel = model as? ReloadableViewModel<List<*>>
        model
    }

    private var categoryId = -1
    private var categoryName = ""
    private var categoryDescription: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments!!.let { bundle ->
            if (bundle.containsKey(ARG_CATEGORYID)) categoryId = bundle.getInt(ARG_CATEGORYID)
            if (bundle.containsKey(ARG_CATEGORYNAME)) categoryName = bundle.getString(ARG_CATEGORYNAME)!!
            if (bundle.containsKey(ARG_CATEGORYDESCRIPTION)) categoryDescription =
                    bundle.getString(ARG_CATEGORYDESCRIPTION)

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_styles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Hook our swipe refresh layoutinto our superclass: ReloadableFragment
        super.swipeRefreshLayout = swipeRefresh

        //Set adapter and layoutmanager of our recyclerview
        recyclerView.adapter = StyleAdapter(navigationController)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)

        //Set customizable text of or no-data-placeholder
        noDataText.text = getString(R.string.no_styles_found_for_category)

        //Observe our fetched styles, if given value is null, give empty list to adapter, else, give give list to adapter
        viewModel.getObservableData().observe(this, Observer {
            (recyclerView.adapter as StyleAdapter).setStyles(it ?: listOf())
        })

        //Ask base fragment to load data using the reloadableViewModel we set
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
        //Remove swipe to refresh listener
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
                navigationController.showDialog(viewModel.categoryName, viewModel.cateogryDescription)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    companion object {
        const val ARG_CATEGORYID = "categoryId"
        const val ARG_CATEGORYNAME = "categoryName"
        const val ARG_CATEGORYDESCRIPTION = "categoryDescription"

        fun newInstance(categoryId: Int, categoryName: String, categoryDescription: String?): StylesFragment {
            val args = Bundle()
            args.putInt(ARG_CATEGORYID, categoryId)
            args.putString(ARG_CATEGORYNAME, categoryName)
            args.putString(ARG_CATEGORYDESCRIPTION, categoryDescription)
            val fragment = StylesFragment()
            fragment.arguments = args
            fragment.fragmentTitle = categoryName
            return fragment
        }
    }
}
