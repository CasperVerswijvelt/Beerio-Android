package be.verswijvelt.casper.beerio.ui.adapters

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import be.verswijvelt.casper.beerio.ui.NavigationController
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONStyle
import kotlinx.android.synthetic.main.row_style.view.*

//General adapter, should be pretty self explanatory
class StyleAdapter(val navigationController: NavigationController) : Adapter<StyleHolder>(){

    private var styles : List<JSONStyle> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, p: Int): StyleHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.row_style,parent,false)
        return StyleHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: StyleHolder, position: Int) {
        val style = styles[position]
        holder.name.text = style.name

        holder.view.setOnClickListener {
            navigationController.showBeers(style)
        }
    }

    override fun getItemCount(): Int {
        return styles.size
    }


    fun setStyles(list : List<JSONStyle>) {
        this.styles = list
        notifyDataSetChanged()
    }
}

class StyleHolder(val view : View) : RecyclerView.ViewHolder(view) {
    val name = view.styleName!!
}