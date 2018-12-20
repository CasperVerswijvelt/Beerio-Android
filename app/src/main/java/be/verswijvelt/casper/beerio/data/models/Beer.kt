package be.verswijvelt.casper.beerio.data.models

import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONLabels
import org.joda.time.DateTime
import java.time.LocalDateTime
import java.util.*

/*@objc dynamic var id: String=""
@objc dynamic var name: String=""
@objc dynamic var beerDescription: String?
@objc dynamic var foodPairings: String?
@objc dynamic var originalGravity: String?
@objc dynamic var alcoholByVolume: String? //abv
@objc dynamic var internationalBitteringUnit: String? //ibu
@objc dynamic var labels : Labels?
@objc dynamic var servingTemperature : String? //servingTemperatureDisplay
@objc dynamic var status : String?
@objc dynamic var dateAdded : Date?
@objc dynamic var isSelfMade = false;
var notes : List<Note> = List<Note>()

//Realm can't save 'Int?' and 'Bool?', so we make realmoptionals
var year : RealmOptional<Int> = RealmOptional<Int>()
var isRetired: RealmOptional<Bool> = RealmOptional<Bool>()
var isOrganic: RealmOptional<Bool> = RealmOptional<Bool>()*/

class Beer {
    var name: String = ""
    var description: String? = null
    var foodPairings: String? = null
    var originalGravity: String? = null
    var alcoholByVolume: String? = null
    var internationalBitteringUnit: String? = null
    var labels: JSONLabels? = null
    var servingTemperature: String? = null
    var status: String? = null
    var dateAdded: DateTime? = null
    var iselfMade: Boolean = false
    var year: Int? = null
    var isRetired: Boolean? = null
    var isOrganic: Boolean? = null

    //TODO Notes
}