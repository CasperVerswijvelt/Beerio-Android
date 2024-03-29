package be.verswijvelt.casper.beerio.data.services

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONCategory
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONStyle
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.data.room.BeerDao
import org.jetbrains.anko.doAsync
import org.joda.time.DateTime
import java.lang.IllegalArgumentException

//This class functions as a repository, a single point of access for all data needed within the Beerio application.
// It retrieve's it's data from 2 kinds of sources: a beerDao and an onlineDataService.
// Both can be injected into this object, and the Repository can be retrieved everywhere in the application by using the singleton pattern.
// If dependencies are not injected before running methods on the singleton instance, a fitting exception is thrown.
class BeerRepository {

    private var beerDao: BeerDao? = null
    private var onlineDataService: IOnlineDataService? = null

    companion object {
        private var instance: BeerRepository? = null
        fun getInstance(): BeerRepository {
            if (instance == null) {
                instance = BeerRepository()
            }
            return instance!!
        }
    }


    //Dependency injection
    fun setBeerDao(beerDao: BeerDao) {
        this.beerDao = beerDao
    }

    fun setOnlineDataService(onlineDataService: IOnlineDataService) {
        this.onlineDataService = onlineDataService
    }

    //Private helper methods
    private fun checkBeerDaoNotNull() {
        if (beerDao == null)
            throw IllegalArgumentException("beerDao has not been set yet!")
    }

    private fun checkOnlineDataServiceNotNull() {
        if (onlineDataService == null)
            throw IllegalArgumentException("onlineDataService has not been set yet!")
    }


    //Room Database Beers
    val allSavedBeers: LiveData<List<Beer>> by lazy {
        checkBeerDaoNotNull()
        beerDao!!.getAllBeers()
    }

    @WorkerThread
    fun insert(beer: Beer) {
        doAsync {
            checkBeerDaoNotNull()
            beer.dateSaved = DateTime.now()
            beerDao!!.insert(beer)
        }
    }

    @WorkerThread
    fun findById(id: String): LiveData<Beer?> {
        checkBeerDaoNotNull()
        return beerDao!!.findById(id)
    }

    @WorkerThread
    fun delete(id: String) {
        doAsync {
            checkBeerDaoNotNull()
            beerDao!!.deleteById(id)
        }

    }

    @WorkerThread
    fun update(beer: Beer) {
        doAsync {
            beerDao!!.update(beer)
        }

    }

    //Online beers
    fun fetchCategories(completion: (List<JSONCategory>?) -> Unit) {
        checkOnlineDataServiceNotNull()
        onlineDataService!!.fetchCategories(completion)
    }

    fun fetchStyles(categoryId: Int, completion: (List<JSONStyle>?) -> Unit) {
        checkOnlineDataServiceNotNull()
        onlineDataService!!.fetchStyles(categoryId,completion)
    }

    fun fetchBeers(styleId: Int, completion: (List<Beer>?) -> Unit) {
        checkOnlineDataServiceNotNull()
        onlineDataService!!.fetchBeers(styleId,completion)
    }

    fun isApiKeyValid(completion: (Boolean) -> Unit) {
        checkOnlineDataServiceNotNull()
        onlineDataService!!.isApiKeyValid(completion)
    }

}