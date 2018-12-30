package be.verswijvelt.casper.beerio.data.deserialization.adapters

import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONBeersWrapper
import be.verswijvelt.casper.beerio.data.models.Beer
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat


class JSONBeerAdapter {
    @FromJson
    fun beerFromJson(beerJsonWrapper: JSONBeersWrapper): List<Beer> {
        //In this method we build up the actual domain objects according to our json models. Had to do it this way since somehow our models and json didn't align enough and I couldn't find the cause.
        val list:ArrayList<Beer> = ArrayList()
        beerJsonWrapper.data.forEach { beerJson ->

            val beer = Beer()

            beer.id = beerJson.id
            beer.name = beerJson.name
            beer.description = beerJson.description
            beer.alcoholByVolume = beerJson.abv
            beer.foodPairings = beerJson.foodPairings
            beer.dateAdded = DateTime.parse(beerJson.createDate,DateTimeFormat.forPattern("YYYY-mm-DD HH:mm:ss"))
            beer.internationalBitteringUnit = beerJson.ibu
            beer.isOrganic = if (beerJson.isOrganic == "Y") true else if (beerJson.isOrganic == "N") false else null
            beer.isRetired = if (beerJson.isRetired == "Y") true else if (beerJson.isRetired == "N") false else null
            beer.originalGravity = beerJson.originalGravity
            beer.labels = beerJson.labels
            beer.year = if(beerJson.year==0) null else beerJson.year

            list.add(beer)
        }

        return list

    }

    //For some reason won't work without this method, even if the implementation sucks. Don't question it.
    @ToJson
    fun studentToJson(beers : List<Beer>): JSONBeersWrapper? {
        return null
    }
}