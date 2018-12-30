package be.verswijvelt.casper.beerio.ui.fragments

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.ImageButton
import android.widget.NumberPicker
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.databinding.FragmentAddBeerBinding
import be.verswijvelt.casper.beerio.ui.viewModels.AddBeerViewModel
import be.verswijvelt.casper.beerio.ui.viewModels.AddBeerViewModelFactory
import be.verswijvelt.casper.beerio.utils.actualValue
import com.fxn.pix.Pix
import kotlinx.android.synthetic.main.fragment_add_beer.*
import org.joda.time.DateTime
import java.io.File


class AddBeerFragment : BaseFragment() {

    private var savedBeer : Beer? = null

    private val viewModel by lazy {
        //create viewmodel lazily with a savedBeer if there is one present, else without savedBeer
        val factory = if(savedBeer == null) AddBeerViewModelFactory() else AddBeerViewModelFactory(savedBeer)
        val model = ViewModelProviders.of(this,factory).get(AddBeerViewModel::class.java)

        if(savedBeer != null ) {
            //A savedBeer is present, check if a file exists that represents it's bottle label image, if yes, set selectedImage in viewmodel
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

        //Initialize data binding
        val binding: FragmentAddBeerBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_beer, container, false)
        binding.viewModel = viewModel
        return binding.root
    }


    override fun onResume() {
        super.onResume()

        //Set all listeners
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
            navigationController.showImage(getString(R.string.selected_image_toolbartitle), viewModel.selectedImage.get()!!)
        }
    }


    override fun onPause() {
        super.onPause()

        //Remove all listeners
        yearButton.setOnClickListener(null)
        selectImageButton.setOnClickListener(null)
        removeSelectedImageButton.setOnClickListener(null)
        showSelectedImageButton.setOnClickListener(null)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        //Inflate the right menu (with checkmark to save)
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

        //Create the dialog with custom xml for year picker
        val d = Dialog(context!!)
        d.setTitle(getString(R.string.year_picker_title))
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


        //Set button click listeners
        yearpickerSetButton.setOnClickListener {
            viewModel.year.set(yearpickerNumberPicker.value)
            d.dismiss()
        }
        yearpickerCancelButton.setOnClickListener {
            d.dismiss()
        }

    }

    private fun saveBeer() {
        //If no name is filled in, show dialog asking to give at least a name
        if (viewModel.name.get()?.actualValue() == null) {
            val builder = AlertDialog.Builder(context!!)
            builder.setPositiveButton(android.R.string.ok, null)
            builder.setTitle(getString(R.string.missing_information))
            builder.setMessage(getString(R.string.fill_in_name))
            val alertDialog: AlertDialog? = builder.create()
            alertDialog?.show()
            return
        }

        //Delegate saving of beer to viewmodel
        val beerId = viewModel.saveBeer()

        //If no image is selected, delete the current image if it exists, else, save the selected image
        if (viewModel.selectedImage.get()?.isEmpty() != false) {
            navigationController.deleteImage(beerId)
        } else {
            navigationController.saveBeerImageLocally(viewModel.selectedImage.get()!!, beerId)
        }
        navigationController.exitCurrentFragment()
    }

    private fun showImagePicker() {
        //Delegate image picker to PixImagePicker library
        Pix.start(this,
            6969,
            1)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        //If resultcode is ok and requestcode is 6969, the code which we set when starting our image picker intent,
        // check if we received date, if yes set it as selectedImge in our viewmodel
        if (resultCode == RESULT_OK && requestCode == 6969) {
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
            return fragment
        }
    }

}
