package be.verswijvelt.casper.beerio.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.LinearLayout
import be.verswijvelt.casper.beerio.viewModels.CategoriesViewModel
import be.verswijvelt.casper.beerio.adapters.CategoryAdapter
import kotlinx.android.synthetic.main.fragment_categories.*
import android.animation.LayoutTransition
import android.R




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
                showLoader(false)
                (recyclerView.adapter as CategoryAdapter).setCategories(it)
                recyclerView.scheduleLayoutAnimation()
            }
        })
    }


}
