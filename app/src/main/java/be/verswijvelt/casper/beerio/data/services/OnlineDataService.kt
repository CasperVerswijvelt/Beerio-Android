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

class OnlineDataService(var apiKey: String = "4593fe9ecb788f8ce42f65d592d7de35") : IDataService {

    val baseUrl : String = "https://api.brewerydb.com/v2/"


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
                            val beer = jsonAdapter.fromJson(data)
                            completion(null)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            completion(null)
                        }
                    }
                }
            }
    }


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



    //Helper methods
    private fun apiKeyParameter() : Pair<String,Any> {
        return Pair("key",apiKey)
    }




}