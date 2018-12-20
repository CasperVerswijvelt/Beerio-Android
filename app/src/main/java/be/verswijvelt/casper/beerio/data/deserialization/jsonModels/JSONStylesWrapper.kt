package be.verswijvelt.casper.beerio.data.deserialization.jsonModels

data class JSONStylesWrapper(
    val `data`: List<JSONStyle>,
    val message: String,
    val status: String
)