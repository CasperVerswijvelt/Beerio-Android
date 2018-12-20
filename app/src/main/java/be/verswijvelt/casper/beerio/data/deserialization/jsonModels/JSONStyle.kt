package be.verswijvelt.casper.beerio.data.deserialization.jsonModels

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