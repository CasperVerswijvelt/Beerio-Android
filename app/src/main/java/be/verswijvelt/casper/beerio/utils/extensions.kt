package be.verswijvelt.casper.beerio.utils

//Extension function on a string, thtat returns null if the trimmed string is empty
fun String.actualValue(): String? {
    return if (trim().isEmpty())
        null
    else
        this
}