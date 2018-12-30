package be.verswijvelt.casper.beerio.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import be.verswijvelt.casper.beerio.ui.NavigationController
import be.verswijvelt.casper.beerio.data.models.Beer
import be.verswijvelt.casper.beerio.data.services.BeerRepository
import kotlinx.android.synthetic.main.row_beerdetails_simple.view.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.io.File


class BeerDetailsAdapter(private val navigationController: NavigationController) : RecyclerView.Adapter<BeerDetailsViewHolder>() {
    private var beer: Beer? = null
    private var savedBeer: Beer? = null

    private var visualBeer: BeerDetailsVisualizer.VisualBeer? = null


    fun setBeer(beer: Beer) {
        this.beer = beer
        updateBeer()
    }

    fun setSavedBeer(beer: Beer?) {
        this.savedBeer = beer
        updateBeer()
    }

    fun updateBeer() {

        val file = File(navigationController.getFilesDirectory().absolutePath +"/"+ this.beer!!.id +".png")
        this.visualBeer = BeerDetailsVisualizer.getVisualRepresesntation(this.beer!!, this.savedBeer, if(file.exists()) "file://"+file.absolutePath else null)
        this.notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int {
        val cell = visualBeer!!.getCellAtIndex(position)
        return when (cell.cellType) {
            BeerDetailsVisualizer.CellType.HEADER -> 0
            BeerDetailsVisualizer.CellType.SIMPLE -> 1
            BeerDetailsVisualizer.CellType.LARGE -> 2
            BeerDetailsVisualizer.CellType.IMAGE -> 3
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerDetailsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow: View
        cellForRow = when (viewType) {
            0 -> layoutInflater.inflate(be.verswijvelt.casper.beerio.R.layout.section, parent, false)
            1 -> layoutInflater.inflate(be.verswijvelt.casper.beerio.R.layout.row_beerdetails_simple, parent, false)
            2 -> layoutInflater.inflate(be.verswijvelt.casper.beerio.R.layout.row_beerdetails_subtitle, parent, false)
            3 -> layoutInflater.inflate(be.verswijvelt.casper.beerio.R.layout.row_beerdetails_image, parent, false)
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
        holder.itemView.setOnClickListener {
            if(cell.cellType == BeerDetailsVisualizer.CellType.IMAGE) {
                navigationController.showImage("Bottle label: "+beer!!.name,cell.value)
            }
        }
        holder.isDeletable = cell.isDeletable
    }

    override fun getItemCount(): Int {
        return if (visualBeer == null) 0 else visualBeer!!.getCellCount()
    }

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

class BeerDetailsViewHolder(view: View, var isDeletable: Boolean=false) : RecyclerView.ViewHolder(view) {
    val key: TextView = view.beerdetails_key
    val value: TextView? = view.beerdetails_value
}


class BeerDetailsVisualizer {
    companion object {
        fun getVisualRepresesntation(beer: Beer, savedBeer: Beer?, overrideImageUrl: String? = null): VisualBeer {
            val visualBeer = VisualBeer()
            val beerToUse = savedBeer?:beer

            //Basic info
            val basicInfoCells: ArrayList<VisualBeerCell> = ArrayList()
            addCellIfExists(basicInfoCells, "Name", beerToUse.name, CellType.LARGE)
            addCellIfExists(basicInfoCells, "Description", beerToUse.description, CellType.LARGE)
            visualBeer.sections.add(VisualBeerSection("Basic Info", basicInfoCells))

            //Numbers and stuff
            val numbersCells: ArrayList<VisualBeerCell> = ArrayList()
            addCellIfExists(numbersCells, "Original Gravity", beerToUse.originalGravity, CellType.SIMPLE)
            addCellIfExists(numbersCells, "Alcohol By Volume", beerToUse.alcoholByVolume, CellType.SIMPLE)
            addCellIfExists(
                numbersCells,
                "International Bittering Unit",
                beer.internationalBitteringUnit,
                CellType.SIMPLE
            )
            addCellIfExists(numbersCells, "Serving temperature", beerToUse.servingTemperature, CellType.LARGE)
            visualBeer.sections.add(VisualBeerSection("Numbers and stuff", numbersCells))

            //Random stuff
            val randomCells: ArrayList<VisualBeerCell> = ArrayList()
            addCellIfExists(randomCells, "Food Pairings", beerToUse.foodPairings, CellType.LARGE)
            addCellIfExists(randomCells, "Is retired", beerToUse.isRetired, CellType.SIMPLE)
            addCellIfExists(randomCells, "Is organic", beerToUse.isOrganic, CellType.SIMPLE)
            addCellIfExists(randomCells, "Year", beerToUse.year, CellType.SIMPLE)
            addCellIfExists(randomCells, "Bottle Label", overrideImageUrl?:beer.labels?.large, CellType.IMAGE)
            if(savedBeer != null) {
                addCellIfExists(randomCells, "Saved on", beerToUse.dateSaved,CellType.SIMPLE)
            }
            visualBeer.sections.add(VisualBeerSection("Other", randomCells))

            if(savedBeer != null) {
                val notes: ArrayList<VisualBeerCell> = ArrayList()
                beerToUse.notes.forEachIndexed { index, it ->
                    addCellIfExists(notes,it.text,it.dateWritten,CellType.LARGE,true,index)
                }
                visualBeer.sections.add(VisualBeerSection("Notes", notes))
            }


            for(i in visualBeer.sections.size - 1 downTo 0) {
                if(visualBeer.sections[i].cells.isEmpty()) visualBeer.sections.removeAt(i)
            }



            addCellIfExists(visualBeer.sections.last().cells, "", "", CellType.HEADER)




            return visualBeer
        }

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

    class VisualBeer(sections: List<VisualBeerSection>? = null) {
        var sections: ArrayList<VisualBeerSection> = if (sections == null) ArrayList() else ArrayList(sections)


        fun getCellCount(): Int {
            var counter: Int = sections.size
            sections.forEach {
                counter += it.cells.size
            }
            return counter
        }

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

    class VisualBeerSection(val header: String, val cells: ArrayList<VisualBeerCell>)
    class VisualBeerCell(val key: String, val value: String, val cellType: CellType, val isDeletable: Boolean = false, val noteIndex : Int?=null)
    enum class CellType { SIMPLE, LARGE, IMAGE, HEADER }
}