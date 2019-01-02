package be.verswijvelt.casper.beerio.ui.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONCategory
import be.verswijvelt.casper.beerio.data.services.BeerRepository

class CategoriesViewModel : ReloadableViewModel<List<JSONCategory>>()  {


    private val categories: MutableLiveData<List<JSONCategory>> by lazy {
        MutableLiveData<List<JSONCategory>>()
    }

    //Loaddata function so we can later reload the data aswell (by swipe down to refresh)
    override fun loadData() {
        isLoadingData.postValue(true)
        BeerRepository.getInstance().fetchCategories {
            categories.postValue(it)
            isLoadingData.postValue(false)
        }
    }

    override fun hasData(): Boolean {
        return categories.value != null
    }

    override fun getObservableData(): LiveData<List<JSONCategory>> {
        return categories
    }
}