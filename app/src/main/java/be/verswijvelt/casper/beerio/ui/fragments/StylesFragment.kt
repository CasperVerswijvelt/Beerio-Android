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
import be.verswijvelt.casper.beerio.ui.adapters.StyleAdapter
import be.verswijvelt.casper.beerio.ui.viewModels.StylesViewModel
import be.verswijvelt.casper.beerio.ui.viewModels.StylesViewModelFactory
import kotlinx.android.synthetic.main.empty_dataset_placeholder.*
import kotlinx.android.synthetic.main.error_placeholder.*
import kotlinx.android.synthetic.main.fragment_styles.*


class StylesFragment : BaseFragment() {


    private val viewModel by lazy {
        ViewModelProviders.of(this,StylesViewModelFactory(categoryId, categoryName, categoryDescription)).get(StylesViewModel::class.java)
    }

    private var categoryId = -1
    private var categoryName = ""
    private var categoryDescription : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments!!.let { bundle ->
            if (bundle.containsKey(ARG_CATEGORYID)) categoryId = bundle.getInt(ARG_CATEGORYID)
            if (bundle.containsKey(ARG_CATEGORYNAME)) categoryName = bundle.getString(ARG_CATEGORYNAME)!!
            if (bundle.containsKey(ARG_CATEGORYDESCRIPTION)) categoryDescription = bundle.getString(ARG_CATEGORYDESCRIPTION)

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
        //Set adapter and layoutmanager of our recyclerview
        recyclerView.adapter = StyleAdapter(navigationController)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)
        //Set customizable text of or no-data-placeholder
        noDataText.text = getString(R.string.no_styles_found_for_category)
        showLoader(true)

        //Observe our fetched styles, if given value is null, give empty list to adapter, else, give give list to adapter
        viewModel.getStyles().observe(this, Observer {
            if(it != null) {
                (recyclerView.adapter as StyleAdapter).setStyles(it)
            } else {
                (recyclerView.adapter as StyleAdapter).setStyles(listOf())
            }


            //Set visibiltiy of empty dataset placeholder accoriding to given value being null and not empty, or not
            emptyDataSetPlaceHolder.visibility = if(it != null && it.isEmpty()) View.VISIBLE else View.GONE
            //Set visibiltiy of error placeholder according to give value being null or not
            error_placeholder.visibility = if(it == null) View.VISIBLE else View.GONE
            showLoader(false)
            swipeRefresh.isRefreshing = false
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
        //Remove swipe to refresh listener
        swipeRefresh.setOnRefreshListener(null)
    }

    companion object {
        const val ARG_CATEGORYID = "categoryId"
        const val ARG_CATEGORYNAME = "categoryName"
        const val ARG_CATEGORYDESCRIPTION = "categoryDescription"

        fun newInstance(categoryId : Int, categoryName:String, categoryDescription:String?): StylesFragment {
            val args = Bundle()
            args.putInt(ARG_CATEGORYID,categoryId)
            args.putString(ARG_CATEGORYNAME,categoryName)
            args.putString(ARG_CATEGORYDESCRIPTION,categoryDescription)
            val fragment = StylesFragment()
            fragment.arguments = args
            fragment.fragmentTitle = categoryName
            return fragment
        }
    }

    //override what should happen when title in toolbar is clicked when this fragment is shown
    override fun getTitleClickedHandler(): () -> Unit {
        return {
            navigationController.showDialog(viewModel.categoryName, viewModel.cateogryDescription)
        }
    }

}
