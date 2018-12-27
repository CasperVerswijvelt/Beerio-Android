package be.verswijvelt.casper.beerio.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.InputType
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import be.verswijvelt.casper.beerio.adapters.BeerDetailsAdapter
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.data.models.Note
import be.verswijvelt.casper.beerio.viewModels.BeerDetailsViewModel
import be.verswijvelt.casper.beerio.viewModels.BeerDetailsViewModelFactory
import kotlinx.android.synthetic.main.fragment_beer_details.*
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import be.verswijvelt.casper.beerio.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception


class BeerDetailsFragment : BaseFragment() {

    private lateinit var beer : Beer

    private val viewModel by lazy {
        ViewModelProviders.of(this,BeerDetailsViewModelFactory(beer,activity!!.application)).get(BeerDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments!!.let { bundle ->
            if (bundle.containsKey(ARG_BEER)) this.beer = bundle.getSerializable(ARG_BEER) as Beer
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
                if ( dy < 0 && !fab.isShown) {
                    if(viewModel.savedBeer.value != null) fab.show()
                } else if (dy> 0 && fab.isShown)
                    fab.hide()
            }
        })


        showLoader(true)


        viewModel.getBeer().observe(this, Observer {
            if(it != null) {
                showLoader(false)
                (recyclerView.adapter as BeerDetailsAdapter).setBeer(it)
            }
        })
        viewModel.savedBeer.observe(this, Observer {
            (recyclerView.adapter as BeerDetailsAdapter).setSavedBeer(it)
            if(it==null) fab.hide() else fab.show()
            activity?.invalidateOptionsMenu()
        })



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
        fab.setOnClickListener {
            if(viewModel.savedBeer.value != null) {
                // Set up the input
                val input = EditText(context!!)
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.inputType = InputType.TYPE_CLASS_TEXT

                val builder = AlertDialog.Builder(context!!)
                    .setTitle("Add note")
                    .setView(input)
                    .setPositiveButton("Add") { _, _ ->
                        beer.notes.add(Note(input.text.toString()))
                        navigationController.updateBeer(beer)
                    }
                    .setNegativeButton("Cancel"
                    ) { dialog, _ -> dialog.cancel() }

                builder.show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fab.setOnClickListener(null)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(if(viewModel.savedBeer.value != null) be.verswijvelt.casper.beerio.R.menu.options_removebutton else be.verswijvelt.casper.beerio.R.menu.options_addbutton, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return when(item!!.itemId) {
            be.verswijvelt.casper.beerio.R.id.save_beer -> {
                val img = viewModel.getBeer().value?.labels?.large
                if(img != null) {
                    saveImage(img,viewModel.getBeer().value!!.id)
                }
                viewModel.saveBeer()
                true
            }
            be.verswijvelt.casper.beerio.R.id.delete_beer -> {
                AlertDialog.Builder(context!!)
                    .setTitle("Delete beer")
                    .setMessage("Are you sure you want to remove this beer from your local library? Doing this will also remove all your notes for this beer.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(
                        android.R.string.yes
                    ) { _, _ ->  viewModel.deleteBeer() }
                    .setNegativeButton(android.R.string.no, null).show()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }

        }
    }

    //save image
    fun saveImage(url: String, id:String) {
        Picasso.get()
            .load(url)
            .into(getTarget(id))
    }

    //target to save
    private fun getTarget(fileName: String): Target {
        return object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                Thread(Runnable {
                    val file = File(context!!.filesDir.path + "/" + fileName + ".png")
                    try {
                        file.createNewFile()
                        val ostream = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.PNG, 80, ostream)
                        ostream.flush()
                        ostream.close()
                    } catch (e: IOException) {
                        Log.e("IOException", e.getLocalizedMessage())
                    }
                }).start()
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

            }
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
}
