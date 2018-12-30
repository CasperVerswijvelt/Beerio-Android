package be.verswijvelt.casper.beerio.data.converters

import android.arch.persistence.room.TypeConverter
import org.joda.time.DateTime
import java.lang.Exception

//This class handles the conversion from and to, date in string to an actual DateTime object, using the .parse and .toString methods on DateTime
class TimeStampConverter {
    @TypeConverter
    fun fromTimestamp(value: String?): DateTime? {
        if (value != null) {
            try {
                return DateTime.parse(value)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        } else {
            return null
        }
    }

    @TypeConverter
    fun toTimestamp(value: DateTime?): String? {
        if (value != null) {
            try {
                return value.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        } else {
            return null
        }
    }
}