package be.verswijvelt.casper.beerio.viewModels

import android.app.Application
import android.arch.lifecycle.*
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.data.room.BeerRoomDatabase
import be.verswijvelt.casper.beerio.data.services.BeerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MyBeersViewModel(application: Application) : AndroidViewModel(application) {

    private val repository : BeerRepository

    val beers: LiveData<List<Beer>>

    private var parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    private val scope = CoroutineScope(coroutineContext)


    init {
        val beerDao = BeerRoomDatabase.getDatabase(application).beerDao()
        repository = BeerRepository(beerDao)

        beers = repository.allBeers
    }



    fun insert(beer: Beer) = scope.launch(Dispatchers.IO) {
        repository.insert(beer)
    }



}
