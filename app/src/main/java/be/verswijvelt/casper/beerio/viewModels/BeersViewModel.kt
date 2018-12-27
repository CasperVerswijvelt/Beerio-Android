package be.verswijvelt.casper.beerio.viewModels

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.data.room.BeerRoomDatabase
import be.verswijvelt.casper.beerio.data.services.BeerRepository
import be.verswijvelt.casper.beerio.data.services.IOnlineDataService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class BeersViewModel(private val styleId: Int, val styleName: String, val styleDescription: String?, private val application: Application) : ViewModel() {


    private val repository : BeerRepository
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)


    init {
        val beerDao = BeerRoomDatabase.getDatabase(application).beerDao()
        repository = BeerRepository(beerDao)


        loadData()
    }

    private val beers: MutableLiveData<List<Beer>> by lazy {
        MutableLiveData<List<Beer>>()
    }

    val savedBeers: LiveData<List<Beer>> = repository.allBeers


    fun loadData() {
        IOnlineDataService.getInstance().fetchBeers(styleId) {
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
    private val styleDescription: String?,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BeersViewModel(styleId, styleName, styleDescription, application) as T
    }
}