package be.verswijvelt.casper.beerio.data.services

import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONCategory
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONStyle
import be.verswijvelt.casper.beerio.data.models.Beer

//This is an interface that defines the method that an implementation should offer, for the browse online section of this application
interface IOnlineDataService{

    fun fetchCategories(completion : (List<JSONCategory>?) -> Unit)

    fun fetchStyles(categoryId: Int, completion : (List<JSONStyle>?) -> Unit )

    fun fetchBeers(styleId: Int, completion: (List<Beer>?) -> Unit)

    fun isApiKeyValid(completion: (Boolean) -> Unit)
}

