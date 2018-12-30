package be.verswijvelt.casper.beerio.data.converters

import android.arch.persistence.room.TypeConverter
import be.verswijvelt.casper.beerio.data.deserialization.adapters.JSONNoteListAdapter
import be.verswijvelt.casper.beerio.data.models.Note
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types



//This class handles the conversion to and from, json in string format to an ArrayList of Notes, using a Moshi adapter
class NoteListConverter {
    @TypeConverter
    fun jsonNotesToList(json : String): ArrayList<Note> ?  {
        val moshi = Moshi.Builder()
            .add(JSONNoteListAdapter())
            .build()

        val jsonAdapter : JsonAdapter<ArrayList<Note>> = moshi.adapter(Types.newParameterizedType(ArrayList::class.java, Note::class.java))

        return jsonAdapter.fromJson(json)
    }



    @TypeConverter
    fun notesToString(notes : ArrayList<Note>): String {
        val moshi = Moshi.Builder()
            .add(JSONNoteListAdapter())
            .build()

        val jsonAdapter : JsonAdapter<ArrayList<Note>> = moshi.adapter(Types.newParameterizedType(ArrayList::class.java, Note::class.java))

        return jsonAdapter.toJson(notes)

    }

}