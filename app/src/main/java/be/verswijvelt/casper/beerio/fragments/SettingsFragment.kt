package be.verswijvelt.casper.beerio.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.Preference
import be.verswijvelt.casper.beerio.NavigationController
import be.verswijvelt.casper.beerio.data.services.IDataService


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SettingsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(be.verswijvelt.casper.beerio.R.xml.settings)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val button = findPreference("checkApiKey")
        button.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            (activity as NavigationController).showLoader(true)


            IDataService.getInstance().isApiKeyValid {
                activity?.runOnUiThread {
                    button.summary = if(it)"This API key is valid" else "This API key is not valid"
                    (activity as NavigationController).showLoader(false)
                }
            }
            true
        }
    }
}
