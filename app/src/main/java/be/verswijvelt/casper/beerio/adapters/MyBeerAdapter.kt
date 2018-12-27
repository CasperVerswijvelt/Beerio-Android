package be.verswijvelt.casper.beerio.adapters

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import be.verswijvelt.casper.beerio.NavigationController
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.data.models.Beer
import kotlinx.android.synthetic.main.row_my_beer.view.*

class MyBeerAdapter(val navigationController: NavigationController) : Adapter<MyBeerHolder>(){

    private var beers : List<Beer> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, p: Int): MyBeerHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.row_my_beer,parent,false)
        return MyBeerHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: MyBeerHolder, position: Int) {
        val beer = beers[position]
        holder.name.text = beer.name
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
        navigationController.deleteBeer(beers[position].id)
        notifyItemRemoved(position)
    }
}

class MyBeerHolder(val view : View) : RecyclerView.ViewHolder(view) {
    val name = view.beerName
}