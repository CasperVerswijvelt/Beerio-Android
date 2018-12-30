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

    //We hold a list of both beers and all our saved beers, so we can compare and display checkmark when a beer is already saved
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
        //Checking if the beer for this viewholder is already saved, and if yes, displaying a checkmark, else nothing
        if(savedBeers.find { it.id == beer.id } != null) {
            holder.isSavedImage.setImageResource(R.drawable.ic_check_black_24dp)
        } else
            holder.isSavedImage.setImageDrawable(null)
        //Loading icon image in icon imageview trough picasso
        Picasso.get()
            .load(beer.labels?.icon)
            .placeholder(R.drawable.beer)
            .error(R.drawable.beer)
            .into(holder.imageView)
        //viewholder tapped listener
        holder.view.setOnClickListener {
            navigationController.showBeer(beer)
        }
    }

    override fun getItemCount(): Int {
        return beers.size
    }

    //Beers changed, notify data changed to reload list
    fun setBeers(list : List<Beer>) {
        this.beers = list
        notifyDataSetChanged()
    }

    //saved beers changed, notify data changed to recalculate for each viewholder whether beer is already saved or not
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