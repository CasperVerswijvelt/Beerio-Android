package be.verswijvelt.casper.beerio.data.services

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.data.room.BeerDao
import org.jetbrains.anko.doAsync
import org.joda.time.DateTime

class BeerRepository(private val beerDao: BeerDao) {
    val allBeers: LiveData<List<Beer>> = beerDao.getAllBeers()

    @WorkerThread
    fun insert(beer: Beer) : Boolean{
        try {
            beer.dateSaved = DateTime.now()
            beerDao.insert(beer)
            return true
        }catch(e:Exception) {
            e.printStackTrace()
            return false
        }
    }

    @WorkerThread
    fun findById(id: String): LiveData<Beer?> {
        return beerDao.findById(id)
    }

    @WorkerThread
    fun delete(id: String) {
        doAsync {
            beerDao.deleteById(id)
        }

    }

    @WorkerThread
    fun update(beer:Beer) {
        doAsync {
            beerDao.update(beer)
        }

    }
}