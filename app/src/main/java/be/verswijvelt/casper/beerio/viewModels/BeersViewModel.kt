package be.verswijvelt.casper.beerio.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider
import android.util.Log
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.data.services.IDataService

class BeersViewModel(private val styleId: Int, val styleName: String, val styleDescription: String?) : ViewModel() {
    private val beers: MutableLiveData<List<Beer>> by lazy {
        MutableLiveData<List<Beer>>()
    }

    init {
        loadData()
    }

    fun loadData() {
        IDataService.getInstance().fetchBeers(styleId) {
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