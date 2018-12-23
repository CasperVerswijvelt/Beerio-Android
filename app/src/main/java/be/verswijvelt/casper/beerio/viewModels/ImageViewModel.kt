package be.verswijvelt.casper.beerio.viewModels

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider

class ImageViewModel(private val url : String) : ViewModel() {
    // TODO: Implement the ViewModel
}


class ImageViewModelFactory(private val url : String) :  ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ImageViewModel(url) as T
    }
}