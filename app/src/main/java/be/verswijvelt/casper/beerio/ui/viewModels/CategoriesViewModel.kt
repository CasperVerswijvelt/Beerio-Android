package be.verswijvelt.casper.beerio.ui.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONCategory
import be.verswijvelt.casper.beerio.data.services.BeerRepository

class CategoriesViewModel : ViewModel() {
    private val categories: MutableLiveData<List<JSONCategory>> by lazy {
        MutableLiveData<List<JSONCategory>>()
    }

    init {
        loadData()
    }

    //Loaddata function so we can later reload the data aswell (by swipe down to refresh)
    fun loadData() {
        BeerRepository.getInstance().fetchCategories {
            categories.postValue(it)
        }
    }


    fun getCategories(): LiveData<List<JSONCategory>> {
        return categories
    }

}