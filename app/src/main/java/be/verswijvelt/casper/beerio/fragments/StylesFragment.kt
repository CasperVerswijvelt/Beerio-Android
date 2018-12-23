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
import be.verswijvelt.casper.beerio.adapters.StyleAdapter
import be.verswijvelt.casper.beerio.viewModels.StylesViewModel
import be.verswijvelt.casper.beerio.viewModels.StylesViewModelFactory
import kotlinx.android.synthetic.main.fragment_styles.*
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.HORIZONTAL
import kotlinx.android.synthetic.main.empty_dataset.*


class StylesFragment : BaseFragment() {


    private val viewModel by lazy {
        ViewModelProviders.of(this,StylesViewModelFactory(categoryId)).get(StylesViewModel::class.java)
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
        recyclerView.adapter = StyleAdapter(navigationController)
        noDataText.text = "No styles could be found for this category"
        showLoader(true)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)
        viewModel.getStyles().observe(this, Observer {
            if(it != null) {
                showLoader(false)
                (recyclerView.adapter as StyleAdapter).setStyles(it)
                recyclerView.scheduleLayoutAnimation()
                emptyDataSetPlaceHolder.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
            }
        })
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
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

}
