package be.verswijvelt.casper.beerio.ui.adapters

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import be.verswijvelt.casper.beerio.ui.NavigationController
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONCategory
import kotlinx.android.synthetic.main.row_category.view.*

class CategoryAdapter(val navigationController: NavigationController) : Adapter<CategoryHolder>(){

    private var categories : List<JSONCategory> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, p: Int): CategoryHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.row_category,parent,false)
        return CategoryHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val category = categories[position]
        holder.name.text = category.name
        holder.description.text = category.description
        holder.description.visibility = if (category.description == null ) View.GONE else View.VISIBLE

        holder.view.setOnClickListener {
            navigationController.showCategory(category)
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }


    fun setCategories(list : List<JSONCategory>) {
        this.categories = list
        notifyDataSetChanged()
    }
}

class CategoryHolder(val view : View) : RecyclerView.ViewHolder(view) {
    val name = view.categoryName!!
    val description = view.categoryDescription!!
}