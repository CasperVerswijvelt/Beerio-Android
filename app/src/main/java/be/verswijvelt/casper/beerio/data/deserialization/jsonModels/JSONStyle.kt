package be.verswijvelt.casper.beerio.data.deserialization.jsonModels

//Generated data class according to the JSON text that is returned from the API.
// This way we never have problems with wrongly parsed json data, and we have the freedom of converting these objects to our own implementation
data class JSONStyle(
    val abvMax: String,
    val abvMin: String,
    val category: JSONCategory,
    val categoryId: Int,
    val createDate: String,
    val description: String,
    val fgMax: String,
    val fgMin: String,
    val ibuMax: String,
    val ibuMin: String,
    val id: Int,
    val name: String,
    val ogMax: String,
    val ogMin: String,
    val shortName: String,
    val srmMax: String,
    val srmMin: String,
    val updateDate: String
)