package be.verswijvelt.casper.beerio.data.deserialization.jsonModels

import java.io.Serializable

//Generated data class according to the JSON text that is returned from the API.
// This way we never have problems with wrongly parsed json data, and we have the freedom of converting these objects to our own implementation
class JSONLabels(
    val icon: String? = null,
    val large: String? = null
) : Serializable