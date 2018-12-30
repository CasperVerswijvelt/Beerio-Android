package be.verswijvelt.casper.beerio.ui.adapters

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import be.verswijvelt.casper.beerio.ui.NavigationController
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.data.models.Beer
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_beer.view.*

class BeerAdapter(val navigationController: NavigationController) : Adapter<BeerHolder>(){

    private var beers : List<Beer> = ArrayList()
    private var savedBeers : List<Beer> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, p: Int): BeerHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.row_beer,parent,false)
        return BeerHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: BeerHolder, position: Int) {
        val beer = beers[position]
        holder.name.text = beer.name
        if(savedBeers.find { it.id == beer.id } != null) {
            holder.isSavedImage.setImageResource(R.drawable.ic_check_black_24dp)
        } else
            holder.isSavedImage.setImageDrawable(null)
        Picasso.get()
            .load(beer.labels?.icon)
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


    fun setBeers(list : List<Beer>) {
        this.beers = list
        notifyDataSetChanged()
    }

    fun setSavedBeers(list : List<Beer>) {
        this.savedBeers = list
        notifyDataSetChanged()
    }

}

class BeerHolder(val view : View) : RecyclerView.ViewHolder(view) {
    val imageView = view.imageView!!
    val name = view.beerName!!
    val isSavedImage = view.accessoryImage!!
}