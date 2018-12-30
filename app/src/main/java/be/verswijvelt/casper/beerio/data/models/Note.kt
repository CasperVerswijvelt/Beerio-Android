package be.verswijvelt.casper.beerio.data.models

import android.arch.persistence.room.TypeConverters
import be.verswijvelt.casper.beerio.data.converters.TimeStampConverter
import org.joda.time.DateTime
import java.io.Serializable

@TypeConverters(TimeStampConverter::class)
class Note(var text:String,
           var dateWritten : DateTime = DateTime.now()) : Serializable