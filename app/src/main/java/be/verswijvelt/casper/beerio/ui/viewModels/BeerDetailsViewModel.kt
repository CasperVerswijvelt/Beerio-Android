package be.verswijvelt.casper.beerio.ui.viewModels

import android.app.Application
import android.arch.lifecycle.*
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.data.services.BeerRepository

class BeerDetailsViewModel(inBeer : Beer, application: Application) : AndroidViewModel(application) {

    private val beer: MutableLiveData<Beer> by lazy {
        MutableLiveData<Beer>()
    }
    val savedBeer: LiveData<Beer?>

    init {
        this.beer.postValue(inBeer)
        this.savedBeer = BeerRepository.getInstance().findById(inBeer.id)
    }


    fun saveBeer()  {
        beer.value.let {
            BeerRepository.getInstance().insert(beer.value!!)
        }
    }

    fun deleteBeer() {
        beer.value.let {
            BeerRepository.getInstance().delete(beer.value!!.id)
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