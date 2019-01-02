package be.verswijvelt.casper.beerio.ui.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.data.services.BeerRepository

class BeersViewModel(private val styleId: Int, val styleName: String, val styleDescription: String?) : ReloadableViewModel<List<Beer>>() {

    //We hold in this viewmodel both our beers and savedBeers, so we can compare them and show which beers are already saved
    private val beers: MutableLiveData<List<Beer>> by lazy {
        MutableLiveData<List<Beer>>()
    }
    val savedBeers: LiveData<List<Beer>> = BeerRepository.getInstance().allSavedBeers

    //Loaddata function so we can later reload the data aswell (by swipe down to refresh)
    override fun loadData() {
        isLoadingData.postValue(true)
        BeerRepository.getInstance().fetchBeers(styleId) {
            beers.postValue(it)
            isLoadingData.postValue(false)
        }
    }

    override fun hasData(): Boolean {
        return beers.value != null
    }

    override fun getObservableData(): LiveData<List<Beer>> {
        return beers
    }
}


class BeersViewModelFactory(
    private val styleId: Int,
    private val styleName: String,
    private val styleDescription: String?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BeersViewModel(styleId, styleName, styleDescription) as T
    }
}