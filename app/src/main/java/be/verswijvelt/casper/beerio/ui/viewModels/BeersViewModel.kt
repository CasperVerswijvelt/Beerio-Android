package be.verswijvelt.casper.beerio.ui.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.data.services.BeerRepository

class BeersViewModel(private val styleId: Int, val styleName: String, val styleDescription: String?) : ViewModel() {

    init {
        loadData()
    }

    //We hold in this viewmodel both our beers and savedBeers, so we can compare them and show which beers are already saved
    private val beers: MutableLiveData<List<Beer>> by lazy {
        MutableLiveData<List<Beer>>()
    }
    val savedBeers: LiveData<List<Beer>> = BeerRepository.getInstance().allSavedBeers

    //Loaddata function so we can later reload the data aswell (by swipe down to refresh)
    fun loadData() {
        BeerRepository.getInstance().fetchBeers(styleId) {
            beers.postValue(it)
        }
    }

    fun getBeers(): LiveData<List<Beer>> {
        return beers
    }
}


class BeersViewModelFactory(
    private val styleId: Int,
    private val styleName: String,
    private val styleDescription: String?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BeersViewModel(styleId, styleName, styleDescription) as T
    }
}