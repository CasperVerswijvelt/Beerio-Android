package be.verswijvelt.casper.beerio.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.adapters.BeerDetailsAdapter
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.viewModels.BeerDetailsViewModel
import be.verswijvelt.casper.beerio.viewModels.BeerDetailsViewModelFactory
import kotlinx.android.synthetic.main.fragment_beer_details.*


class BeerDetailsFragment : BaseFragment() {

    private lateinit var beer : Beer

    private val viewModel by lazy {
        ViewModelProviders.of(this,BeerDetailsViewModelFactory(beer)).get(BeerDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments!!.let { bundle ->
            if (bundle.containsKey(ARG_BEER)) this.beer = bundle.getSerializable(ARG_BEER) as Beer
        }
    }


    companion object {

        const val ARG_BEER = "argBeer"
        fun newInstance(beer:Beer) : BeerDetailsFragment {
            val args = Bundle()
            args.putSerializable(ARG_BEER,beer)
            val fragment = BeerDetailsFragment()
            fragment.arguments = args

            fragment.fragmentTitle = beer.name

            return fragment

        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beer_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = BeerDetailsAdapter(navigationController)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if ( dy < 0 && !fab.isShown)
                    fab.show()
                else if (dy> 0 && fab.isShown)
                    fab.hide()
            }
        })

        fab.setOnClickListener {
            navigationController.showToast("Fab clicked")
        }
        showLoader(true)


        viewModel.getBeer().observe(this, Observer {
            if(it != null) {
                showLoader(false)
                (recyclerView.adapter as BeerDetailsAdapter).setBeer(it)
                recyclerView.scheduleLayoutAnimation()
            }
        })
    }

}
