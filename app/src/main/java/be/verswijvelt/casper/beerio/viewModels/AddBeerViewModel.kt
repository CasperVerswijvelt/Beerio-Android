package be.verswijvelt.casper.beerio.viewModels

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider
import android.databinding.Bindable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import be.verswijvelt.casper.beerio.data.models.Beer
import org.joda.time.DateTime

class AddBeerViewModel(val savedBeer : Beer?) : ViewModel() {


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

}
class AddBeerViewModelFactory(private val savedBeer : Beer? = null) :  ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddBeerViewModel(savedBeer) as T
    }
}