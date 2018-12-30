package be.verswijvelt.casper.beerio.ui.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import be.verswijvelt.casper.beerio.ui.viewModels.CategoriesViewModel
import be.verswijvelt.casper.beerio.ui.adapters.CategoryAdapter
import kotlinx.android.synthetic.main.fragment_categories.*
import android.view.*
import kotlinx.android.synthetic.main.error_placeholder.*



class CategoriesFragment : BaseFragment() {
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(CategoriesViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(be.verswijvelt.casper.beerio.R.layout.fragment_categories, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Set adapter and layoutmanager for our recyclerview
        recyclerView.adapter = CategoryAdapter(navigationController)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)
        showLoader(true)

        //Observe fetched categories and if value changes and it is null, give empty list to adapter, else, give the given list to the adapter
        viewModel.getCategories().observe(this, Observer {
            if(it != null) {
                (recyclerView.adapter as CategoryAdapter).setCategories(it)
            } else {
                (recyclerView.adapter as CategoryAdapter).setCategories(listOf())
            }
            //Show error placeholder if updated value is null
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
        //remove swipe to refresh listener
        swipeRefresh.setOnRefreshListener(null)
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        //Inflate the right menu (settings button)
        inflater?.inflate(be.verswijvelt.casper.beerio.R.menu.options_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
