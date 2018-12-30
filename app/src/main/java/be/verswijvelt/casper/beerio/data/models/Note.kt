package be.verswijvelt.casper.beerio.data.models

import android.arch.persistence.room.TypeConverters
import be.verswijvelt.casper.beerio.data.converters.TimeStampConverter
import org.joda.time.DateTime
import java.io.Serializable

//This is a class that represents a 'Note' object that is used in a Beer object.
// TimeStampConverter is used to convert the DateTime object to a string before saving to databse
@TypeConverters(TimeStampConverter::class)
class Note(var text:String,
           var dateWritten : DateTime = DateTime.now()) : Serializable