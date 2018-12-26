package be.verswijvelt.casper.beerio.data.deserialization.jsonModels

import java.io.Serializable

class JSONLabels(
    val contentAwareIcon: String? = null,
    val contentAwareLarge: String? = null,
    val contentAwareMedium: String? = null,
    val icon: String? = null,
    val large: String? = null,
    val medium: String? = null
) : Serializable