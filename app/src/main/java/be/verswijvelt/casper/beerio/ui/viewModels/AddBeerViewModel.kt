package be.verswijvelt.casper.beerio.ui.viewModels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import be.verswijvelt.casper.beerio.utils.actualValue
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.data.services.BeerRepository
import org.joda.time.DateTime
import java.util.*

//This viewmodel holds all the values of the fields on the addBeer screen as observable fields, these values are 2 way binded to the view
class AddBeerViewModel(private val savedBeer : Beer?) : ViewModel() {


    var name = ObservableField(savedBeer?.name?:"")
    var description = ObservableField(savedBeer?.description?:"")
    var foodPairings = ObservableField(savedBeer?.foodPairings?:"")
    var originalGravity = ObservableField(savedBeer?.originalGravity?:"")
    var alcoholByVolume = ObservableField(savedBeer?.alcoholByVolume?:"")
    var internationalBitteringUnit = ObservableField(savedBeer?.internationalBitteringUnit?:"")
    var servingTemperature = ObservableField(savedBeer?.servingTemperature?:"")
    var year = ObservableInt(savedBeer?.year?:DateTime.now().year)
    var isOrganic = ObservableBoolean(savedBeer?.isOrganic?:false)
    var isRetired = ObservableBoolean(savedBeer?.isRetired?:false)
    var selectedImage = ObservableField("")


    fun saveBeer() : String {

        //Create new beer and use all the values of the fields to fill up it's variables
        val beer = Beer()
        beer.id = savedBeer?.id?:System.currentTimeMillis().toString(16) + "." + UUID.randomUUID().toString()
        beer.name = name.get()?.actualValue() ?: "You had to enter a name ..."
        beer.description = description.get()?.actualValue()
        beer.foodPairings = foodPairings.get()?.actualValue()
        beer.originalGravity = originalGravity.get()?.actualValue()
        beer.alcoholByVolume = alcoholByVolume.get()?.actualValue()
        beer.internationalBitteringUnit = internationalBitteringUnit.get()?.actualValue()
        beer.servingTemperature = servingTemperature.get()?.actualValue()
        beer.year = year.get()
        beer.isRetired = isRetired.get()
        beer.isOrganic = isOrganic.get()
        beer.iselfMade = true
        beer.dateSaved = savedBeer?.dateSaved?:DateTime.now()
        beer.dateAdded = savedBeer?.dateAdded?:DateTime.now()

        //If we are editing an already saved beer, transfer the notes to the created beer object
        if(savedBeer != null)
            beer.notes = savedBeer.notes


        //If there is no savedBeer object, we are creating, otherwise we are editing an existing beer.
        // Delegate to the right method according to this
        if(savedBeer == null) {
            BeerRepository.getInstance().insert(beer)
        } else {
            BeerRepository.getInstance().update(beer)
        }

        //Return the beer id to our view so it can handle image creation with this id
        return beer.id
    }

}
class AddBeerViewModelFactory(private val savedBeer : Beer? = null) :  ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddBeerViewModel(savedBeer) as T
    }
}

