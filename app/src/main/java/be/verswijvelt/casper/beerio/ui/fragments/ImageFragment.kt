package be.verswijvelt.casper.beerio.ui.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.*

import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.ui.viewModels.ImageViewModel
import be.verswijvelt.casper.beerio.ui.viewModels.ImageViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_image.*



class ImageFragment : BaseFragment() {
    private var url = ""

    private val viewModel by lazy {
        ViewModelProviders.of(this, ImageViewModelFactory(url)).get(ImageViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments!!.let { bundle ->
            if (bundle.containsKey(ARG_IMAGEURL)) url = bundle.getString(ARG_IMAGEURL)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Load the image with url in our viewmodel, and when loaded place it into our zoomable image view
        Picasso.get()
            .load(viewModel.url)
            .error(R.drawable.beer)
            .into(photo_view)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        //Inflate the right menu (info button)
        inflater?.inflate(be.verswijvelt.casper.beerio.R.menu.options_imageoptions, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.copy_imageurl -> {
                val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                val clip = ClipData.newPlainText("Image url", viewModel.url)
                clipboard!!.primaryClip = clip

                navigationController.notify(getString(R.string.image_url_copied))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    companion object {

        const val ARG_IMAGEURL = "imageUrl"
        fun newInstance(url: String, toolbarTitle:String): ImageFragment {
            val args = Bundle()
            args.putString(ARG_IMAGEURL, url)
            val fragment = ImageFragment()
            fragment.arguments = args

            fragment.fragmentTitle = toolbarTitle

            return fragment
        }
    }

}
