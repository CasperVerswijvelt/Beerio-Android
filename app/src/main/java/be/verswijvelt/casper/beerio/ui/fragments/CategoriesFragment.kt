package be.verswijvelt.casper.beerio.ui.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.LinearLayout
import be.verswijvelt.casper.beerio.ui.adapters.CategoryAdapter
import be.verswijvelt.casper.beerio.ui.viewModels.CategoriesViewModel
import be.verswijvelt.casper.beerio.ui.viewModels.ReloadableViewModel
import kotlinx.android.synthetic.main.fragment_categories.*


class CategoriesFragment : ReloadableFragment() {
    private val viewModel by lazy {
        val model = ViewModelProviders.of(this).get(CategoriesViewModel::class.java)
        super.reloadableViewModel = model as? ReloadableViewModel<List<*>>
        model
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(be.verswijvelt.casper.beerio.R.layout.fragment_categories, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Hook our swipe refresh layoutinto our superclass: ReloadableFragment
        super.swipeRefreshLayout = swipeRefresh

        //Set adapter and layoutmanager for our recyclerview
        recyclerView.adapter = CategoryAdapter(navigationController)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)

        //Observe fetched categories and if value changes and it is null, give empty list to adapter, else, give the given list to the adapter
        viewModel.getObservableData().observe(this, Observer {
            (recyclerView.adapter as CategoryAdapter).setCategories(it ?: listOf())
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
        //remove swipe to refresh listener
        swipeRefresh.setOnRefreshListener(null)
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        //Inflate the right menu (settings button)
        inflater?.inflate(be.verswijvelt.casper.beerio.R.menu.options_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
