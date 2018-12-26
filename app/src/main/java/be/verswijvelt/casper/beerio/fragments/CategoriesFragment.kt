package be.verswijvelt.casper.beerio.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import be.verswijvelt.casper.beerio.viewModels.CategoriesViewModel
import be.verswijvelt.casper.beerio.adapters.CategoryAdapter
import kotlinx.android.synthetic.main.fragment_categories.*
import android.view.*
import kotlinx.android.synthetic.main.error_placeholder.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CategoriesFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CategoriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CategoriesFragment : BaseFragment() {

    init {
        super.fragmentTitle = "Beer categories"
    }

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
        recyclerView.adapter = CategoryAdapter(navigationController)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)
        showLoader(true)
        viewModel.getCategories().observe(this, Observer {
            if(it != null) {
                (recyclerView.adapter as CategoryAdapter).setCategories(it)
            } else {
                (recyclerView.adapter as CategoryAdapter).setCategories(listOf())
            }
            error_placeholder.visibility = if(it == null) View.VISIBLE else View.GONE
            showLoader(false)
            swipeRefresh.isRefreshing = false
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


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(be.verswijvelt.casper.beerio.R.menu.options_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
