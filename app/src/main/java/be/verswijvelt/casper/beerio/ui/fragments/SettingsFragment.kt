package be.verswijvelt.casper.beerio.ui.fragments

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.Preference
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.ui.NavigationController
import be.verswijvelt.casper.beerio.data.services.BeerRepository


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(be.verswijvelt.casper.beerio.R.xml.settings)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Find preference that should check if api key is valid when clicked, and set the click listener

        val preference = findPreference("checkApiKey")
        preference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            (activity as NavigationController).showLoader(true)

            //check if api key is valid, and update the summary of this preference accordingly
            BeerRepository.getInstance().isApiKeyValid { isValid ->
                activity?.runOnUiThread {
                    preference.summary = if(isValid)getString(R.string.api_key_valid) else getString(R.string.api_key_invalid)
                    (activity as NavigationController).showLoader(false)
                }
            }
            true
        }
    }
}
