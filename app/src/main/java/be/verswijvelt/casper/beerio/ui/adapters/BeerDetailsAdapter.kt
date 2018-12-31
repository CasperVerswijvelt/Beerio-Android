package be.verswijvelt.casper.beerio.ui.adapters

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.ui.NavigationController
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.data.services.BeerRepository
import kotlinx.android.synthetic.main.row_beerdetails_simple.view.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.io.File


class BeerDetailsAdapter(private val navigationController: NavigationController) : RecyclerView.Adapter<BeerDetailsViewHolder>() {
    //We hold an object of both the beer object that was picked, and an optional parameter for savedBeer
    private var beer: Beer? = null
    private var savedBeer: Beer? = null

    //The latest generated visual representation in the form of sections and cells, for the given beer
    private var visualBeer: BeerDetailsVisualizer.VisualBeer? = null


    fun setBeer(beer: Beer) {
        this.beer = beer
        updateBeer()
    }

    fun setSavedBeer(beer: Beer?) {
        this.savedBeer = beer
        updateBeer()
    }

    //Gets called each time the beer or savedBeer is updated
    fun updateBeer() {
        val file = File(navigationController.getFilesDirectory().absolutePath +"/"+ this.beer!!.id +".png")
        //Generates the visual representation of the beer, with - if a local file for the image exists - a path to that image
        this.visualBeer = BeerDetailsVisualizer.getVisualRepresesntation(this.beer!!
            , this.savedBeer
            , if(file.exists()) "file://"+file.absolutePath else null
            ,(navigationController as Activity).applicationContext)
        this.notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int {
        val cell = visualBeer!!.getCellAtIndex(position)
        //Return itemType based on the cellType of the VisualBeerCell, so our 'onCreateViewHolder' method can react to this and load the right xml layout
        return when (cell.cellType) {
            BeerDetailsVisualizer.CellType.HEADER -> 0
            BeerDetailsVisualizer.CellType.SIMPLE -> 1
            BeerDetailsVisualizer.CellType.LARGE -> 2
            BeerDetailsVisualizer.CellType.IMAGE -> 3
            BeerDetailsVisualizer.CellType.FOOTER -> 4
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerDetailsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow: View
        //Load the right xml layout according to the given itemViewType, as seen in in previous method
        cellForRow = when (viewType) {
            0 -> layoutInflater.inflate(be.verswijvelt.casper.beerio.R.layout.row_beerdetails_header, parent, false)
            1 -> layoutInflater.inflate(be.verswijvelt.casper.beerio.R.layout.row_beerdetails_simple, parent, false)
            2 -> layoutInflater.inflate(be.verswijvelt.casper.beerio.R.layout.row_beerdetails_subtitle, parent, false)
            3 -> layoutInflater.inflate(be.verswijvelt.casper.beerio.R.layout.row_beerdetails_image, parent, false)
            4 -> layoutInflater.inflate(be.verswijvelt.casper.beerio.R.layout.row_beerdetails_footer, parent, false)
            else -> {
                layoutInflater.inflate(be.verswijvelt.casper.beerio.R.layout.row_beerdetails_simple, parent, false)
            }
        }
        return BeerDetailsViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: BeerDetailsViewHolder, position: Int) {
        val cell = visualBeer!!.getCellAtIndex(position)
        holder.key.text = cell.key
        holder.value?.text = cell.value
        //If celltype is image, set click listener to call navigationController and show image with the value of the cell
        if(cell.cellType == BeerDetailsVisualizer.CellType.IMAGE) {
            holder.itemView.setOnClickListener {
                navigationController.showImage(holder.itemView.context.getString(R.string.bottle_label_prefix)+" "+beer!!.name,cell.value)
            }
        }
        holder.isDeletable = cell.isDeletable
    }

    override fun getItemCount(): Int {
        return if (visualBeer == null) 0 else visualBeer!!.getCellCount()
    }


    //Function called from fragment when a cell is swiped to delete on the recyclerview
    fun removeAt(position: Int) {
        val index = visualBeer?.getCellAtIndex(position)?.noteIndex
        if(index != null) {
            beer!!.notes.removeAt(index)
            BeerRepository.getInstance().update(beer!!)
            notifyItemRemoved(position)
        } else {
            notifyItemChanged(position)
        }
    }
}


//Viewholder class for this adapter, holds a key and a value, and a value indiciating whether this viewholder is deletable (by swipe)
class BeerDetailsViewHolder(view: View, var isDeletable: Boolean=false) : RecyclerView.ViewHolder(view) {
    val key: TextView = view.beerdetails_key
    val value: TextView? = view.beerdetails_value
}

//Class that handles visualisation of a beer object by partitioning it in sections and cells of the right type
class BeerDetailsVisualizer {
    companion object {
        fun getVisualRepresesntation(beer: Beer, savedBeer: Beer?, overrideImageUrl: String? = null, context:Context): VisualBeer {
            val visualBeer = VisualBeer()
            val beerToUse = savedBeer?:beer

            //Section: Basic info
            val basicInfoCells: ArrayList<VisualBeerCell> = ArrayList()
            addCellIfExists(basicInfoCells, context.getString(R.string.name), beerToUse.name, CellType.LARGE)
            addCellIfExists(basicInfoCells, context.getString(R.string.description), beerToUse.description, CellType.LARGE)
            visualBeer.sections.add(VisualBeerSection(context.getString(R.string.basic_info), basicInfoCells))

            //Section: Numbers and stuff
            val numbersCells: ArrayList<VisualBeerCell> = ArrayList()
            addCellIfExists(numbersCells, context.getString(R.string.original_gravity), beerToUse.originalGravity, CellType.SIMPLE)
            addCellIfExists(numbersCells, context.getString(R.string.alcohol_by_volume), beerToUse.alcoholByVolume, CellType.SIMPLE)
            addCellIfExists(numbersCells, context.getString(R.string.international_bittering_unit), beer.internationalBitteringUnit, CellType.SIMPLE)
            addCellIfExists(numbersCells, context.getString(R.string.serving_temperature), beerToUse.servingTemperature, CellType.LARGE)
            visualBeer.sections.add(VisualBeerSection(context.getString(R.string.numbers_and_stuff), numbersCells))

            //Section: Random stuff
            val randomCells: ArrayList<VisualBeerCell> = ArrayList()
            addCellIfExists(randomCells, context.getString(R.string.food_pairings), beerToUse.foodPairings, CellType.LARGE)
            addCellIfExists(randomCells, context.getString(R.string.is_retired), beerToUse.isRetired, CellType.SIMPLE)
            addCellIfExists(randomCells, context.getString(R.string.is_organic), beerToUse.isOrganic, CellType.SIMPLE)
            addCellIfExists(randomCells, context.getString(R.string.year), beerToUse.year, CellType.SIMPLE)
            addCellIfExists(randomCells, context.getString(R.string.bottle_label), overrideImageUrl?:beer.labels?.large, CellType.IMAGE)
            if(savedBeer != null) {
                addCellIfExists(randomCells, context.getString(R.string.saved_on), beerToUse.dateSaved,CellType.SIMPLE)
            }
            visualBeer.sections.add(VisualBeerSection(context.getString(R.string.other), randomCells))

            //Section: Notes (only if there is a savedBeer)
            if(savedBeer != null) {
                val notes: ArrayList<VisualBeerCell> = ArrayList()
                beerToUse.notes.forEachIndexed { index, it ->
                    addCellIfExists(notes,it.text,it.dateWritten,CellType.LARGE,true,index)
                }
                visualBeer.sections.add(VisualBeerSection(context.getString(R.string.notes), notes))
            }


            //Remove all empty sections (sections that contain no cells)
            for(i in visualBeer.sections.size - 1 downTo 0) {
                if(visualBeer.sections[i].cells.isEmpty()) visualBeer.sections.removeAt(i)
            }


            //Add empty header cell to the last row_beerdetails_header to create some space at the end, feels less crammed
            addCellIfExists(visualBeer.sections.last().cells, if(beerToUse.iselfMade) "" else context.getString(R.string.data_courtesy_brewerydb), "", CellType.FOOTER)

            return visualBeer
        }

        //Private function that adds a cell with the given information to the given row_beerdetails_header, only if the value is not null
        // Also handles displaying the value in a correct way, e.g. showing a date righ, showing boolean by "Yes", or "No"
        private fun addCellIfExists(cellList: ArrayList<VisualBeerCell>, header: String, value: Any?, cellType: CellType, deletable: Boolean = false, noteIndex : Int?=null) {
            if (value != null) cellList.add(
                VisualBeerCell(header,
                    if(value is Boolean)
                        if(value)
                            "Yes"
                        else
                            "No"
                    else if(value is DateTime)
                        value.toString(DateTimeFormat.mediumDateTime())
                    else
                        value.toString()
                    , cellType,deletable,noteIndex))
        }
    }

    //Represents the visual representation of 1 beer object
    class VisualBeer(sections: List<VisualBeerSection>? = null) {
        var sections: ArrayList<VisualBeerSection> = if (sections == null) ArrayList() else ArrayList(sections)

        //Returns the amount of cells this visualBeer will take up
        fun getCellCount(): Int {
            var counter: Int = sections.size
            sections.forEach {
                counter += it.cells.size
            }
            return counter
        }

        //Returns the cell for given position
        fun getCellAtIndex(index: Int): VisualBeerCell {
            var index = index
            var currentSection = 0

            for (i in 0 until sections.size) {
                currentSection = i
                if (index <= sections[currentSection].cells.size) {
                    break
                }
                else
                    index -= sections[currentSection].cells.size+1
            }


            return if (index == 0) {
                VisualBeerCell(sections[currentSection].header, "", CellType.HEADER)
            } else {
                sections[currentSection].cells[index - 1]
            }
        }
    }

    //Classes for sections and cells, and enum indicating the type of cell
    class VisualBeerSection(val header: String, val cells: ArrayList<VisualBeerCell>)
    class VisualBeerCell(val key: String, val value: String, val cellType: CellType, val isDeletable: Boolean = false, val noteIndex : Int?=null)
    enum class CellType { SIMPLE, LARGE, IMAGE, HEADER, FOOTER }
}