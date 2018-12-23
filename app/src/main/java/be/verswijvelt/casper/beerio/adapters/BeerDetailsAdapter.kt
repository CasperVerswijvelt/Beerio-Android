package be.verswijvelt.casper.beerio.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import be.verswijvelt.casper.beerio.NavigationController
import be.verswijvelt.casper.beerio.data.models.Beer
import kotlinx.android.synthetic.main.row_beerdetails_simple.view.*


class BeerDetailsAdapter(private val navigationController: NavigationController) : RecyclerView.Adapter<BeerDetailsViewHolder>() {
    private var beer: Beer? = null
    private var visualBeer: BeerDetailsVisualizer.VisualBeer? = null


    fun setBeer(beer: Beer) {
        this.beer = beer
        this.visualBeer = BeerDetailsVisualizer.getVisualRepresesntation(beer)
        this.notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int {
        val cell = visualBeer!!.getCellAtIndex(position)
        var i =  when (cell.cellType) {
            BeerDetailsVisualizer.CellType.HEADER -> 0
            BeerDetailsVisualizer.CellType.SIMPLE -> 1
            BeerDetailsVisualizer.CellType.LARGE -> 2
            BeerDetailsVisualizer.CellType.IMAGE -> 3
        }
        return i
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
    }

    override fun getItemCount(): Int {
        return if (visualBeer == null) 0 else visualBeer!!.getCellCount()
    }
}

class BeerDetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val key: TextView = view.beerdetails_key
    val value: TextView? = view.beerdetails_value
}


class BeerDetailsVisualizer {
    companion object {
        fun getVisualRepresesntation(beer: Beer): VisualBeer {
            val visualBeer = VisualBeer()

            //Basic info
            var basicInfoCells: ArrayList<VisualBeerCell> = ArrayList()
            addCellIfExists(basicInfoCells, "Name", beer.name, CellType.LARGE)
            addCellIfExists(basicInfoCells, "Description", beer.description, CellType.LARGE)
            visualBeer.sections.add(VisualBeerSection("Basic Info", basicInfoCells))

            //Numbers and stuff
            var numbersCells: ArrayList<VisualBeerCell> = ArrayList()
            addCellIfExists(numbersCells, "Original Gravity", beer.originalGravity, CellType.SIMPLE)
            addCellIfExists(numbersCells, "Alcohol By Volume", beer.alcoholByVolume, CellType.SIMPLE)
            addCellIfExists(
                numbersCells,
                "International Bittering Unit",
                beer.internationalBitteringUnit,
                CellType.SIMPLE
            )
            addCellIfExists(numbersCells, "Serving temperature", beer.servingTemperature, CellType.LARGE)
            visualBeer.sections.add(VisualBeerSection("Numbers and stuff", numbersCells))

            //Random stuff
            var randomCells: ArrayList<VisualBeerCell> = ArrayList()
            addCellIfExists(randomCells, "Food Pairings", beer.foodPairings, CellType.LARGE)
            addCellIfExists(randomCells, "Is retired", beer.isRetired, CellType.SIMPLE)
            addCellIfExists(randomCells, "Is organic", beer.isOrganic, CellType.SIMPLE)
            addCellIfExists(randomCells, "Year", beer.year, CellType.SIMPLE)
            addCellIfExists(randomCells, "Bottle Label", beer.labels?.large, CellType.IMAGE)
            addCellIfExists(randomCells, "", "", CellType.HEADER)
            visualBeer.sections.add(VisualBeerSection("Other", randomCells))



            return visualBeer
        }

        fun addCellIfExists(cellList: ArrayList<VisualBeerCell>, header: String, value: Any?, cellType: CellType) {
            if (value != null) cellList.add(VisualBeerCell(header, if(value is Boolean) if(value) "Yes" else "No" else value.toString(), cellType))
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


            if (index == 0) {
                return VisualBeerCell(sections[currentSection].header, "", CellType.HEADER)
            } else {
                val cell = sections[currentSection].cells[index - 1]
                return VisualBeerCell(cell.key, cell.value, cell.cellType)
            }
        }
    }

    class VisualBeerSection(val header: String, val cells: List<VisualBeerCell>)
    class VisualBeerCell(val key: String, val value: String, val cellType: CellType)
    enum class CellType { SIMPLE, LARGE, IMAGE, HEADER }
}