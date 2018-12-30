package be.verswijvelt.casper.beerio.data.deserialization.adapters

import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONNote
import be.verswijvelt.casper.beerio.data.deserialization.jsonModels.JSONNoteList
import be.verswijvelt.casper.beerio.data.models.Note
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.joda.time.DateTime

//This class is used by moshi to convert a JSONNoteList object to my actual domain objects, and vice versa
class JSONNoteListAdapter {
    @ToJson
    fun notesToJson(list: ArrayList<Note>) : JSONNoteList  {
        val res = ArrayList<JSONNote>()
        list.forEach {
            res.add(JSONNote(it.text,it.dateWritten.toString()))
        }
        return JSONNoteList(res)
    }

    @FromJson
    fun jsonNoteListToNotes(json: JSONNoteList) : ArrayList<Note>  {
        val list = ArrayList<Note>()
        json.notes.forEach {
            list.add(Note(it.text, DateTime.parse(it.dateWritten)))
        }
        return list
    }
}