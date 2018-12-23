package be.verswijvelt.casper.beerio.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONStyle
import be.verswijvelt.casper.beerio.data.services.IDataService
import android.arch.lifecycle.ViewModelProvider
import android.util.Log


class StylesViewModel(private val categoryId : Int) : ViewModel() {

    private val styles: MutableLiveData<List<JSONStyle>> by lazy {
        MutableLiveData<List<JSONStyle>>()
    }

    init {
        IDataService.getInstance().fetchStyles(categoryId) {
            if(it != null) {
                styles.postValue(it)
            }
        }
    }

    fun getStyles() : LiveData<List<JSONStyle>> {
        return styles
    }
}


class StylesViewModelFactory(private val categoryId: Int) :  ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StylesViewModel(categoryId) as T
    }
}