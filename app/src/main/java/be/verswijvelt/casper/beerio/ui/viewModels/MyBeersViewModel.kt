package be.verswijvelt.casper.beerio.ui.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.data.services.BeerRepository

//Very simple viewmodel
class MyBeersViewModel : ViewModel() {
    val beers: LiveData<List<Beer>> = BeerRepository.getInstance().allSavedBeers
}
