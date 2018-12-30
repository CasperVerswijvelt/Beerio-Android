package be.verswijvelt.casper.beerio.ui.adapters

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import be.verswijvelt.casper.beerio.ui.NavigationController
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.data.services.BeerRepository
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_beer.view.*
import java.io.File

class MyBeerAdapter(val navigationController: NavigationController) : Adapter<MyBeerHolder>(){

    private var beers : List<Beer> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, p: Int): MyBeerHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.row_beer,parent,false)
        return MyBeerHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: MyBeerHolder, position: Int) {
        val beer = beers[position]
        holder.name.text = beer.name
        if(beer.iselfMade) {
            holder.beerSourceImage.setImageDrawable(null)
        } else
            holder.beerSourceImage.setImageResource(R.drawable.globe)

        val file = File(navigationController.getFilesDirectory().absolutePath +"/"+ beer.id +".png")
        Picasso.get()
            .load("file://"+file.absolutePath)
            .placeholder(R.drawable.beer)
            .error(R.drawable.beer)
            .into(holder.imageView)


        holder.view.setOnClickListener {
            navigationController.showBeer(beer)
        }
    }

    override fun getItemCount(): Int {
        return beers.size
    }


    fun setMyBeers(list : List<Beer>) {
        this.beers = list
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        BeerRepository.getInstance().delete(beers[position].id)
        notifyItemRemoved(position)
    }
}

class MyBeerHolder(val view : View) : RecyclerView.ViewHolder(view) {
    val imageView = view.imageView!!
    val name = view.beerName!!
    val beerSourceImage = view.accessoryImage!!

}