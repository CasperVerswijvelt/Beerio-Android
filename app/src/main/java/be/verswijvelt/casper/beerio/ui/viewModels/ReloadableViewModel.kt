package be.verswijvelt.casper.beerio.ui.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

//Defining 2 functions in an interface that a viewmodel should supply when confirming to be 'Reloadable'
abstract class ReloadableViewModel<T> : ViewModel() {
    val isLoadingData = MutableLiveData<Boolean>()
    init {
        isLoadingData.value = false
    }

    abstract fun loadData()
    abstract fun hasData() : Boolean
    abstract fun getObservableData() : LiveData<T>
}