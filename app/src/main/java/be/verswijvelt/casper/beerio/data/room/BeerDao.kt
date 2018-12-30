package be.verswijvelt.casper.beerio.data.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import be.verswijvelt.casper.beerio.data.models.Beer

//This interface acts as a Data Access Object for our Beer database, and defines the methods that can be executed to our database.
// These include the standard insert, detel and update queries, but also custom SQL queries such as getAllBeers, findById and deleteById

@Dao
interface BeerDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(beer:Beer)

    @Delete
    fun delete(beer:Beer)

    @Update
    fun update(beer:Beer)

    @Query("SELECT * from Beer ORDER BY dateSaved ASC")
    fun getAllBeers(): LiveData<List<Beer>>

    @Query("SELECT * FROM Beer WHERE id = :id ")
    fun findById(id: String): LiveData<Beer?>

    @Query("DELETE FROM Beer WHERE id = :id ")
    fun deleteById(id: String)
}