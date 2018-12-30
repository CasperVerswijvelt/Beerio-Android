package be.verswijvelt.casper.beerio.data.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import be.verswijvelt.casper.beerio.data.models.Beer


//This abstract class is used as our databse object for our Room Beer Database. It contains a static method that returns an instance of itself according to the
// given context
@Database(entities = [Beer::class], version = 9)
abstract  class BeerRoomDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE: BeerRoomDatabase? = null

        fun getDatabase(context: Context): BeerRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                // Create database here
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BeerRoomDatabase::class.java,
                    "beers_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }


    abstract fun beerDao() : BeerDao
}