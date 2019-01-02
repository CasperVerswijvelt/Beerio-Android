package be.verswijvelt.casper.beerio.ui.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONStyle
import android.arch.lifecycle.ViewModelProvider
import be.verswijvelt.casper.beerio.data.services.BeerRepository


class StylesViewModel(private val categoryId : Int, val categoryName:String, val cateogryDescription:String?) : ReloadableViewModel<List<JSONStyle>>()  {

    private val styles: MutableLiveData<List<JSONStyle>> by lazy {
        MutableLiveData<List<JSONStyle>>()
    }


    //Loaddata function so we can later reload the data aswell (by swipe down to refresh)
    override fun loadData() {
        isLoadingData.postValue(true)
        BeerRepository.getInstance().fetchStyles(categoryId) {
            styles.postValue(it)
            isLoadingData.postValue(false)
        }
    }

    override fun hasData(): Boolean {
        return styles.value != null
    }

    override fun getObservableData(): LiveData<List<JSONStyle>> {
        return styles
    }
}


class StylesViewModelFactory(private val categoryId: Int, private val categoryName:String, private val cateogryDescription:String?) :  ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StylesViewModel(categoryId,categoryName,cateogryDescription) as T
    }
}