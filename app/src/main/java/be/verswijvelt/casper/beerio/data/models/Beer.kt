package be.verswijvelt.casper.beerio.data.models

import android.arch.persistence.room.*
import be.verswijvelt.casper.beerio.data.converters.NoteListConverter
import be.verswijvelt.casper.beerio.data.converters.TimeStampConverter
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONLabels
import org.joda.time.DateTime
import java.io.Serializable


@Entity
@TypeConverters(NoteListConverter::class,TimeStampConverter::class)
class Beer : Serializable{

    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var description: String? = null
    var foodPairings: String? = null
    var originalGravity: String? = null
    var alcoholByVolume: String? = null
    var internationalBitteringUnit: String? = null
    @Ignore
    var labels: JSONLabels? = null
    var servingTemperature: String? = null
    var status: String? = null
    var iselfMade: Boolean = false
    var year: Int? = null
    var isRetired: Boolean? = null
    var isOrganic: Boolean? = null

    var dateAdded: DateTime? = null
    var dateSaved: DateTime? = null

    var notes : ArrayList<Note> = ArrayList()
}