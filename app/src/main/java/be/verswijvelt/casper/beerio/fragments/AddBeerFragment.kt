package be.verswijvelt.casper.beerio.fragments

import android.app.Activity
import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.ImageButton
import android.widget.NumberPicker
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.databinding.FragmentAddBeerBinding
import be.verswijvelt.casper.beerio.viewModels.AddBeerViewModel
import be.verswijvelt.casper.beerio.viewModels.AddBeerViewModelFactory
import com.fxn.pix.Pix
import kotlinx.android.synthetic.main.fragment_add_beer.*
import org.joda.time.DateTime
import java.io.File
import java.util.*


class AddBeerFragment : BaseFragment() {

    private var savedBeer : Beer? = null

    private val viewModel by lazy {
        val factory = if(savedBeer == null) AddBeerViewModelFactory() else AddBeerViewModelFactory(savedBeer)
        val model = ViewModelProviders.of(this,factory).get(AddBeerViewModel::class.java)

        if(savedBeer != null ) {
            val file = File(navigationController.getFilesDirectory().path + "/" + savedBeer!!.id + ".png")
            if(file.exists()) {
                model.selectedImage.set("file://"+file.absolutePath)
            }
        }

        model
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments!!.let { bundle ->
            if (bundle.containsKey(ARG_BEER)) this.savedBeer = bundle.getSerializable(ARG_BEER) as Beer
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val binding: FragmentAddBeerBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_beer, container, false)
        binding.viewModel = viewModel
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        yearButton.setOnClickListener {
            showYearDialog()
        }
        selectImageButton.setOnClickListener {
            showImagePicker()
        }
        removeSelectedImageButton.setOnClickListener {
            viewModel.selectedImage.set("")
        }
        showSelectedImageButton.setOnClickListener {
            navigationController.showImage("Selected image", viewModel.selectedImage.get()!!)
        }
    }


    override fun onPause() {
        super.onPause()
        yearButton.setOnClickListener(null)
        selectImageButton.setOnClickListener(null)
        removeSelectedImageButton.setOnClickListener(null)
        showSelectedImageButton.setOnClickListener(null)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(be.verswijvelt.casper.beerio.R.menu.options_savebutton, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return when(item!!.itemId) {
            R.id.saveedit_beer -> {
                saveBeer()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }

        }
    }

    private fun showYearDialog() {
        val d = Dialog(context!!)
        d.setTitle("Year picker")
        d.setContentView(R.layout.yearpicker)
        d.show()

        val yearpickerNumberPicker: NumberPicker = d.findViewById(R.id.yearpickerNumberPicker)
        val yearpickerSetButton: ImageButton = d.findViewById(R.id.yearPickerSetButton)
        val yearpickerCancelButton: ImageButton = d.findViewById(R.id.yearpickerCancelButton)


        yearpickerNumberPicker.maxValue = DateTime.now().year
        yearpickerNumberPicker.minValue = 1500
        yearpickerNumberPicker.wrapSelectorWheel = false
        yearpickerNumberPicker.value = viewModel.year.get()
        yearpickerNumberPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS


        yearpickerSetButton.setOnClickListener {
            viewModel.year.set(yearpickerNumberPicker.value)
            d.dismiss()
        }

        yearpickerCancelButton.setOnClickListener {
            d.dismiss()
        }

    }

    private fun saveBeer() {
        if (viewModel.name.get()?.actualValue() == null) {
            val builder = AlertDialog.Builder(context!!)
            builder.setPositiveButton(android.R.string.ok, null)
            builder.setTitle("Missing information")
            builder.setMessage("Fill in at least a name for your beer")

            val alertDialog: AlertDialog? = builder.create()

            alertDialog?.show()
            return
        }


        val beer = Beer()

        beer.id = savedBeer?.id?:System.currentTimeMillis().toString(16) + "." + UUID.randomUUID().toString()
        beer.name = viewModel.name.get()?.actualValue() ?: "You had to enter a name ..."
        beer.description = viewModel.description.get()?.actualValue()
        beer.foodPairings = viewModel.foodPairings.get()?.actualValue()
        beer.originalGravity = viewModel.originalGravity.get()?.actualValue()
        beer.alcoholByVolume = viewModel.alcoholByVolume.get()?.actualValue()
        beer.internationalBitteringUnit = viewModel.internationalBitteringUnit.get()?.actualValue()
        beer.servingTemperature = viewModel.servingTemperature.get()?.actualValue()
        beer.year = viewModel.year.get()
        beer.isRetired = viewModel.isRetired.get()
        beer.isOrganic = viewModel.isOrganic.get()
        beer.iselfMade = true
        beer.dateSaved = savedBeer?.dateSaved?:DateTime.now()
        beer.dateAdded = savedBeer?.dateAdded?:DateTime.now()
        if(savedBeer != null) beer.notes = savedBeer!!.notes
        if (viewModel.selectedImage.get()?.isEmpty() != false) {
            navigationController.deleteImage(beer.id)
        } else {
            navigationController.saveBeerImageLocally(viewModel.selectedImage.get()!!, beer.id)
        }
        if(savedBeer == null) {
            navigationController.saveBeer(beer)
        } else {
            navigationController.updateBeer(beer)
        }
        navigationController.exitCurrentFragment()
    }

    private fun showImagePicker() {
        Pix.start(this,                    //Activity or Fragment Instance
            6969,                //Request code for activity results
            1)   //Number of images to restict selection count
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === Activity.RESULT_OK && requestCode === 6969) {
            val returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS).firstOrNull()
            if(returnValue != null)
                viewModel.selectedImage.set("file://$returnValue")
        }

    }


    companion object {
        const val ARG_BEER = "beer"

        fun newInstance(savedBeer : Beer? = null): AddBeerFragment {
            val fragment = AddBeerFragment()
            val args = Bundle()
            if(savedBeer!= null) args.putSerializable(ARG_BEER,savedBeer)
            fragment.arguments = args
            fragment.fragmentTitle = if (savedBeer==null) "Add new beer" else "Edit beer"
            return fragment
        }
    }

}

fun String.actualValue(): String? {
    return if (trim().isEmpty())
        null
    else
        this
}
