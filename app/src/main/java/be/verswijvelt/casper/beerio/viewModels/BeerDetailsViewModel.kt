package be.verswijvelt.casper.beerio.viewModels

import android.app.Application
import android.arch.lifecycle.*
import android.util.Log
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.data.room.BeerRoomDatabase
import be.verswijvelt.casper.beerio.data.services.BeerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class BeerDetailsViewModel(inBeer : Beer, application: Application) : AndroidViewModel(application) {

    private val repository : BeerRepository
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)


    private val beer: MutableLiveData<Beer> by lazy {
        MutableLiveData<Beer>()
    }
    val savedBeer: LiveData<Beer?>

    init {
        val beerDao = BeerRoomDatabase.getDatabase(application).beerDao()
        repository = BeerRepository(beerDao)

        this.beer.postValue(inBeer)
        this.savedBeer = repository.findById(inBeer.id)
    }


    fun saveBeer() = scope.launch(Dispatchers.IO) {
        beer.value.let {
            repository.insert(beer.value!!)
        }
    }

    fun deleteBeer() = scope.launch(Dispatchers.IO) {
        beer.value.let {
            repository.delete(beer.value!!.id)
        }
    }


    fun getBeer() : LiveData<Beer> {
        return beer
    }

}


class BeerDetailsViewModelFactory(private val beer: Beer, private val application: Application) :  ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BeerDetailsViewModel(beer,application) as T
    }
}