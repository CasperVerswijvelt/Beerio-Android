package be.verswijvelt.casper.beerio.data.services

import be.verswijvelt.casper.beerio.data.deserialization.adapters.JSONBeerAdapter
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.*
import be.verswijvelt.casper.beerio.data.models.Beer
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.Exception
import android.content.SharedPreferences

//This is an implementation class of our IOnlineDataService interface.
// It handles all network calls to the BreweryDB API, to fulfill all data needs for the 'Browse Online' row_beerdetails_header of this application.
class OnlineDataService(private var preferences : SharedPreferences) : IOnlineDataService {

    //base Url for our API
    private val baseUrl : String = "https://api.brewerydb.com/v2/"

    //Api key, retrieved from our preferences object that is injected trough the constructor
    private val apiKey : String
        get() {
            if(preferences == null) return ""
            return preferences.getString("apiKey","")!!
        }

    //Fetches all beer categories
    override fun fetchCategories(completion: (List<JSONCategory>?) -> Unit) {
        val url = baseUrl + "categories"
        url.httpGet(listOf(apiKeyParameter()))
            .responseString { _, _, result ->
                when (result) {
                    is Result.Failure -> {
                        val ex = result.getException()
                        completion(null)
                        ex.printStackTrace(System.out)
                    }
                    is Result.Success -> {

                        val moshi = Moshi.Builder()
                            .build()

                        val data = result.get()
                        val jsonAdapter: JsonAdapter<JSONCategoriesWrapper> =
                            moshi.adapter(JSONCategoriesWrapper::class.java)
                        try {
                            val categories = jsonAdapter.fromJson(data)
                            completion(categories?.data)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            completion(null)
                        }

                    }
                }
            }
    }

    //Fetches all beer styles for a specific beer category
    override fun fetchStyles(categoryId: Int, completion: (List<JSONStyle>?) -> Unit) {
        val url = baseUrl + "styles"
        url.httpGet(listOf(apiKeyParameter()))
            .responseString { _, _, result ->
                when (result) {
                    is Result.Failure -> {
                        val ex = result.getException()
                        completion(null)
                        ex.printStackTrace(System.out)
                    }
                    is Result.Success -> {

                        val moshi = Moshi.Builder()
                            .build()

                        val data = result.get()
                        val jsonAdapter: JsonAdapter<JSONStylesWrapper> =
                            moshi.adapter(JSONStylesWrapper::class.java)
                        try {
                            val styles = jsonAdapter.fromJson(data)
                            completion(styles?.data?.filter {
                                it.categoryId == categoryId
                            })
                        } catch (e: Exception) {
                            e.printStackTrace()
                            completion(null)
                        }

                    }
                }
            }
    }

    //Fetches all beers for a specific beer style
    override fun fetchBeers(styleId: Int, completion: (List<Beer>?) -> Unit) {
        val url = baseUrl + "beers"
        url.httpGet(listOf(apiKeyParameter(), Pair("styleId",styleId)))
            .responseString { _, _, result ->
                when (result) {
                    is Result.Failure -> {
                        val ex = result.getException()
                        completion(null)
                        ex.printStackTrace(System.out)
                    }
                    is Result.Success -> {

                        val moshi = Moshi.Builder()
                            .add(JSONBeerAdapter())
                            .build()

                        val data = result.get()
                        val jsonAdapter : JsonAdapter<List<Beer>> = moshi.adapter(Types.newParameterizedType(List::class.java, Beer::class.java))
                        try {
                            val beers = jsonAdapter.fromJson(data)
                            completion(beers)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            completion(null)
                        }
                    }
                }
            }
    }


    //Checks wether the api key from the preferences is a valid one
    override fun isApiKeyValid( completion: (Boolean) -> Unit) {
        val url = baseUrl + "beers"
        url.httpGet(listOf(apiKeyParameter()))
            .responseString { _, _, result ->
                when (result) {
                    is Result.Failure -> {
                        val ex = result.getException()
                        completion(false)
                    }
                    is Result.Success -> {
                        val data = result.get()
                        completion(!data.contains("API key could not be found",true))
                    }
                }
            }
    }



    //Helper method for making a pair to use as query parameter in the url with the api key
    private fun apiKeyParameter() : Pair<String,Any> {
        return Pair("key",apiKey)
    }




}