package be.verswijvelt.casper.beerio.data.services

import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONCategory
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONStyle
import be.verswijvelt.casper.beerio.data.models.Beer

interface IOnlineDataService{

    fun fetchCategories(completion : (List<JSONCategory>?) -> Unit)

    fun fetchStyles(categoryId: Int, completion : (List<JSONStyle>?) -> Unit )

    fun fetchBeers(styleId: Int, completion: (List<Beer>?) -> Unit)

    fun isApiKeyValid(completion: (Boolean) -> Unit)
}

