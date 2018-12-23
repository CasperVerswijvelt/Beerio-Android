package be.verswijvelt.casper.beerio.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONCategory
import be.verswijvelt.casper.beerio.data.services.IDataService

class CategoriesViewModel : ViewModel() {
    private val categories: MutableLiveData<List<JSONCategory>> by lazy {
        MutableLiveData<List<JSONCategory>>()
    }

    init {
        IDataService.getInstance().fetchCategories {
            if(it != null) {
                categories.postValue(it)
            }
        }
    }


    fun getCategories() : LiveData<List<JSONCategory>> {
        return categories
    }

}