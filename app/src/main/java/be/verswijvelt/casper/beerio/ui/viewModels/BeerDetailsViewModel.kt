package be.verswijvelt.casper.beerio.ui.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.data.models.Note
import be.verswijvelt.casper.beerio.data.services.BeerRepository

class BeerDetailsViewModel(inBeer : Beer) : ViewModel() {

    //We hold both a beer object and a savedBeer object, so we can know if the selected beer is saved or not
    private val beer: MutableLiveData<Beer> by lazy {
        MutableLiveData<Beer>()
    }
    val savedBeer: LiveData<Beer?>

    init {
        this.beer.postValue(inBeer)
        this.savedBeer = BeerRepository.getInstance().findById(inBeer.id)
    }


    //Savebeer function that delegates it's work to the beer repository
    fun saveBeer() : Boolean {
        beer.value.let {
            if(it == null)
                return false
            BeerRepository.getInstance().insert(it)
            return true
        }
    }

    //Deletebeer function that delegates it's work to the beer repository
    fun deleteBeer() : Boolean{
        beer.value.let {
            if(it == null)
                return false
            BeerRepository.getInstance().delete(it.id)
            return true
        }
    }

    //AddNoteToBeer function that adds a note to the savedBeer and delegate saving to beer repository
    fun addNoteToBeer(note:String) : Boolean{
        savedBeer.value.let {
            if(it == null)
                return false
            it.notes.add(Note(note.trim()))
            BeerRepository.getInstance().update(it)
            return true
        }
    }


    fun getBeer() : LiveData<Beer> {
        return beer
    }

}


class BeerDetailsViewModelFactory(private val beer: Beer) :  ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BeerDetailsViewModel(beer) as T
    }
}