package be.verswijvelt.casper.beerio.fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.viewModels.CategoriesViewModel
import be.verswijvelt.casper.beerio.viewModels.ImageViewModel
import be.verswijvelt.casper.beerio.viewModels.ImageViewModelFactory
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

        Picasso.get()
            .load(url)
            .error(R.drawable.beer)
            .into(photo_view)
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
