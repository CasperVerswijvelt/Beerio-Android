package be.verswijvelt.casper.beerio.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONStyle
import be.verswijvelt.casper.beerio.data.services.IDataService
import android.arch.lifecycle.ViewModelProvider
import android.util.Log


class StylesViewModel(private val categoryId : Int, val categoryName:String, val cateogryDescription:String?) : ViewModel() {

    private val styles: MutableLiveData<List<JSONStyle>> by lazy {
        MutableLiveData<List<JSONStyle>>()
    }

    init {
        loadData()
    }

    fun loadData() {
        IDataService.getInstance().fetchStyles(categoryId) {
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