package be.verswijvelt.casper.beerio.ui.fragments

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.Preference
import be.verswijvelt.casper.beerio.ui.NavigationController
import be.verswijvelt.casper.beerio.data.services.BeerRepository


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(be.verswijvelt.casper.beerio.R.xml.settings)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val button = findPreference("checkApiKey")
        button.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            (activity as NavigationController).showLoader(true)


            BeerRepository.getInstance().isApiKeyValid { isValid ->
                activity?.runOnUiThread {
                    button.summary = if(isValid)"This API key is valid" else "This API key is not valid"
                    (activity as NavigationController).showLoader(false)
                }
            }
            true
        }
    }
}
