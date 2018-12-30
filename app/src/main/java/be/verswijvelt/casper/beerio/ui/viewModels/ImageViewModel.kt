package be.verswijvelt.casper.beerio.ui.viewModels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class ImageViewModel(val url : String) : ViewModel()


class ImageViewModelFactory(private val url : String) :  ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ImageViewModel(url) as T
    }
}