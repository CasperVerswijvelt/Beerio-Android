package be.verswijvelt.casper.beerio.ui.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.InputType
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.ui.adapters.BeerDetailsAdapter
import be.verswijvelt.casper.beerio.ui.other.SwipeToDeleteCallback
import be.verswijvelt.casper.beerio.ui.viewModels.BeerDetailsViewModel
import be.verswijvelt.casper.beerio.ui.viewModels.BeerDetailsViewModelFactory
import kotlinx.android.synthetic.main.fragment_beer_details.*


class BeerDetailsFragment : BaseFragment() {

    private lateinit var beer : Beer

    private val viewModel by lazy {
        ViewModelProviders.of(this,BeerDetailsViewModelFactory(beer)).get(BeerDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments!!.let { bundle ->
            if (bundle.containsKey(ARG_BEER)) {
                this.beer = bundle.getSerializable(ARG_BEER) as Beer
            }
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
        //Setting layout manager and adapter for our recyclerview
        recyclerView.adapter = BeerDetailsAdapter(navigationController)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)
        //Setting onscrolllitener for recyclerview, if we scroll down, hide the fab, else show it (if beer is saved)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if ( dy < 0 && !fab.isShown) {
                    if(viewModel.savedBeer.value != null) fab.show()
                } else if (dy> 0 && fab.isShown)
                    fab.hide()
            }
        })
        //Initially show loader
        showLoader(true)

        //Observe beer value and if value changes, hide loader and set beer in adapter
        viewModel.getBeer().observe(this, Observer {
            if(it != null) {
                showLoader(false)
                (recyclerView.adapter as BeerDetailsAdapter).setBeer(it)
            }
        })
        //Observe savedBeer value and if value changes, notify adapter of the savedBeer changed, change fragmenttitle to name of the new savedBeer,
        // ask the navigationController to update it's toolbartitle according to our fragmenttitle, and show/hide the fab
        // according to the savedBeer being null or not
        viewModel.savedBeer.observe(this, Observer {
            (recyclerView.adapter as BeerDetailsAdapter).setSavedBeer(it)
            if(it!=null)this.fragmentTitle = it.name
            navigationController.updateToolbarTitle()
            if(it==null) fab.hide() else fab.show()
            activity?.invalidateOptionsMenu()
        })



        //Setting up swipe handler for swipe to delete
        val swipeHandler = object : SwipeToDeleteCallback(context!!) {
            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                (recyclerView.adapter as BeerDetailsAdapter).removeAt(p0.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onResume() {
        super.onResume()

        //Setting fab click listener
        fab.setOnClickListener {

            //if the beer is saved, show a text input dialog and use the result to call our viewmodel to add note to beer
            if(viewModel.savedBeer.value != null) {
                val input = EditText(context!!)
                input.inputType = InputType.TYPE_CLASS_TEXT
                val builder = AlertDialog.Builder(context!!)
                    .setTitle(getString(R.string.add_note))
                    .setView(input)
                    .setPositiveButton(getString(R.string.add)) { _, _ ->
                        if(viewModel.addNoteToBeer(input.text.toString()))
                            navigationController.notify(getString(R.string.note_added))
                    }
                    .setNegativeButton(android.R.string.cancel
                    ) { dialog, _ -> dialog.cancel() }
                builder.show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        //Remove fab listener
        fab.setOnClickListener(null)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        //Inflate the right menu (with save and edit item) and show the edit item only if the beer is self made
        inflater?.inflate(if(viewModel.savedBeer.value != null) be.verswijvelt.casper.beerio.R.menu.options_editandremovebutton else be.verswijvelt.casper.beerio.R.menu.options_addbutton, menu)
        menu?.findItem(R.id.edit_beer)?.isVisible = viewModel.savedBeer.value?.iselfMade == true
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item!!.itemId) {
            be.verswijvelt.casper.beerio.R.id.save_beer -> {
                val img = viewModel.getBeer().value?.labels?.large
                if(img != null) {
                    navigationController.saveBeerImageLocally(img,viewModel.getBeer().value!!.id)
                }
                if(viewModel.saveBeer())
                    navigationController.notify(getString(R.string.beer_Saved))
                true
            }
            be.verswijvelt.casper.beerio.R.id.delete_beer -> {
                //If delete beer was pressed, show confirmation dialog and if yes is clicked ask viewmodel to delete the beer
                AlertDialog.Builder(context!!)
                    .setTitle(getString(R.string.delete_beer_dialogtitle))
                    .setMessage(getString(R.string.delete_beer_confirmation))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(
                        android.R.string.yes
                    ) { _, _ ->
                        if(viewModel.deleteBeer())
                            navigationController.notify(getString(R.string.beer_deleted))

                    }
                    .setNegativeButton(android.R.string.no, null).show()
                true
            }
            R.id.edit_beer -> {
                val beer = viewModel.getBeer().value
                if(beer!=null) navigationController.showEditBeerScreen(beer)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }

        }
    }

    //Force update the beer adapater from outside of this class
    fun updateData() {
        (recyclerView.adapter as BeerDetailsAdapter).updateBeer()
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
}
