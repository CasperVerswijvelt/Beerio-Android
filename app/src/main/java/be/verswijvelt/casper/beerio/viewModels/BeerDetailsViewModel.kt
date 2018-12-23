package be.verswijvelt.casper.beerio.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider
import be.verswijvelt.casper.beerio.data.models.Beer

class BeerDetailsViewModel(inBeer : Beer) : ViewModel() {

    private val beer: MutableLiveData<Beer> by lazy {
        MutableLiveData<Beer>()
    }

    init {
        this.beer.postValue(inBeer)
    }

    fun getBeer() : LiveData<Beer> {
        return beer
    }
}


class BeerDetailsViewModelFactory(private val beer: Beer) :  ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BeerDetailsViewModel(beer) as T
    }
}