package be.verswijvelt.casper.beerio.data.services

import android.content.SharedPreferences
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONBeer
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONCategory
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONStyle
import be.verswijvelt.casper.beerio.data.models.Beer

interface IDataService{
    companion object {
        private var instance: IDataService? = null
        fun getInstance(): IDataService {
            if (instance == null) {
                instance = OnlineDataService()
            }
            return instance!!
        }
    }
    fun setSharedPreferences(preferences:SharedPreferences)

    fun fetchCategories(completion : (List<JSONCategory>?) -> Unit)

    fun fetchStyles(categoryId: Int, completion : (List<JSONStyle>?) -> Unit )

    fun fetchBeers(styleId: Int, completion: (List<Beer>?) -> Unit)

    fun isApiKeyValid(completion: (Boolean) -> Unit)
}

