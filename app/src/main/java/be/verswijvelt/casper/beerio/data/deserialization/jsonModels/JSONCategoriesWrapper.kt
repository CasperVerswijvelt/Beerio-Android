package be.verswijvelt.casper.beerio.data.deserialization.jsonModels

data class JSONCategoriesWrapper(
    val `data`: List<JSONCategory>,
    val message: String,
    val status: String
)