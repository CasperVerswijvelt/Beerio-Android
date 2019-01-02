package be.verswijvelt.casper.beerio.ui.viewModels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

//Viewmodel that just holds an url: the url or filepath to an image
class ImageViewModel(val url : String) : ViewModel()


class ImageViewModelFactory(private val url : String) :  ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ImageViewModel(url) as T
    }
}