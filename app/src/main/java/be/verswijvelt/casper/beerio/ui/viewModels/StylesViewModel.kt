package be.verswijvelt.casper.beerio.ui.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONStyle
import android.arch.lifecycle.ViewModelProvider
import be.verswijvelt.casper.beerio.data.services.BeerRepository


class StylesViewModel(private val categoryId : Int, val categoryName:String, val cateogryDescription:String?) : ViewModel() {

    private val styles: MutableLiveData<List<JSONStyle>> by lazy {
        MutableLiveData<List<JSONStyle>>()
    }

    init {
        loadData()
    }

    //Loaddata function so we can later reload the data aswell (by swipe down to refresh)
    fun loadData() {
        BeerRepository.getInstance().fetchStyles(categoryId) {
            styles.postValue(it)
        }
    }

    fun getStyles() : LiveData<List<JSONStyle>> {
        return styles
    }
}


class StylesViewModelFactory(private val categoryId: Int, private val categoryName:String, private val cateogryDescription:String?) :  ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StylesViewModel(categoryId,categoryName,cateogryDescription) as T
    }
}