package be.verswijvelt.casper.beerio.data.deserialization.jsonModels

//Generated data class according to the JSON text that is returned from the API.
// This way we never have problems with wrongly parsed json data, and we have the freedom of converting these objects to our own implementation
data class JSONGlass(
    val createDate: String,
    val id: Int,
    val name: String
)