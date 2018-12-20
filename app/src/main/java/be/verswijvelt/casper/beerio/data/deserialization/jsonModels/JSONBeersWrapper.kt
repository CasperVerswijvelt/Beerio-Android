package be.verswijvelt.casper.beerio.data.deserialization.jsonModels

data class JSONBeersWrapper(
    val currentPage: Int,
    val `data`: List<JSONBeer>,
    val numberOfPages: Int,
    val status: String,
    val totalResults: Int
)