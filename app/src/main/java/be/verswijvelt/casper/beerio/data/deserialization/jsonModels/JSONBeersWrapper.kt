package be.verswijvelt.casper.beerio.data.deserialization.jsonModels

//Generated data class according to the JSON text that is returned from the API.
// This way we never have problems with wrongly parsed json data, and we have the freedom of converting these objects to our own implementation
data class JSONBeersWrapper(
    val currentPage: Int,
    val `data`: List<JSONBeer>?,
    val numberOfPages: Int,
    val status: String,
    val totalResults: Int
)