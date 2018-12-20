package be.verswijvelt.casper.beerio.data.deserialization.jsonModels

data class JSONBeer(
    val abv: String,
    val available: JSONAvailable,
    val availableId: Int,
    val createDate: String,
    val description: String,
    val foodPairings: String,
    val glass: JSONGlass,
    val glasswareId: Int,
    val ibu: String,
    val id: String,
    val isOrganic: String,
    val isRetired: String,
    val labels: JSONLabels,
    val name: String,
    val nameDisplay: String,
    val originalGravity: String,
    val servingTemperature: String,
    val servingTemperatureDisplay: String,
    val srmId: Int,
    val status: String,
    val statusDisplay: String,
    val style: JSONStyle,
    val styleId: Int,
    val updateDate: String,
    val year: Int
)