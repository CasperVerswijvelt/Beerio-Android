package be.verswijvelt.casper.beerio.ui.fragments

import android.arch.lifecycle.Observer
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import be.verswijvelt.casper.beerio.R
import be.verswijvelt.casper.beerio.ui.viewModels.ReloadableViewModel
import kotlinx.android.synthetic.main.empty_dataset_placeholder.*
import kotlinx.android.synthetic.main.error_placeholder.*

abstract class ReloadableFragment : BaseFragment() {
    private var _reloadableViewModel : ReloadableViewModel<List<*>>? = null

    var reloadableViewModel: ReloadableViewModel<List<*>>?
        get() = _reloadableViewModel
        set(value) {
            this._reloadableViewModel = value
            observeViewModel()
        }

    var swipeRefreshLayout : SwipeRefreshLayout? = null


    override fun onResume() {
        super.onResume()
        observeViewModel()
        reloadDataConditionally()
    }

    override fun showLoader(show:Boolean) {
        super.showLoader(show)

        if (show) {
            //These 2 views should always be hidden when the loader is shown
            error_placeholder?.visibility = View.GONE
            emptyDataSetPlaceHolder?.visibility = View.GONE

            //Disable swipe down to refresh
            swipeRefreshLayout?.isEnabled = false
        } else {
            swipeRefreshLayout?.isRefreshing = false
            swipeRefreshLayout?.isEnabled = true
        }
    }

    private fun observeViewModel() {
        _reloadableViewModel?.isLoadingData?.observe(this, Observer {
            if(_reloadableViewModel?.isLoadingData!!.value == false) {
                showLoader(false)
            }
        })
        _reloadableViewModel?.getObservableData()?.observe(this, Observer {
            error_placeholder?.visibility = if(it==null) View.VISIBLE else View.GONE
            emptyDataSetPlaceHolder?.visibility =  if(it!=null && it.isEmpty()) View.VISIBLE else View.GONE
        })
    }

    //If a reloadableViewModel is set, use it and ask it to load it's data, the fragment will listen to the data and handle the rest
    fun loadData() {
        if (reloadableViewModel == null)
            return
        reloadableViewModel?.loadData()
    }


    fun reloadDataConditionally(showWifiNotification : Boolean = false) {
        val it = reloadableViewModel
        if (isVisible && it?.isLoadingData?.value == false && !it.hasData()) {
            showLoader(true)
            loadData()
            if(showWifiNotification) navigationController.notify(getString(R.string.wifi_connected_reloading))
        }

    }

    fun connectedStateChanged(connected : Boolean) {
        if (connected) {
            reloadDataConditionally(true)
        }
    }
}