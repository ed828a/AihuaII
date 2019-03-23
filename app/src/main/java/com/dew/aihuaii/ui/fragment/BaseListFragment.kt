package com.dew.aihuaii.ui.fragment

import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.dew.aihuaii.R
import com.dew.aihuaii.report.ErrorActivity
import com.dew.aihuaii.ui.adapter.InfoListAdapter
import com.dew.aihuaii.ui.helper.AnimationUtils.animateView
import com.dew.aihuaii.ui.helper.NavigationHelper
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.channel.ChannelInfoItem
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem
import org.schabi.newpipe.extractor.stream.StreamInfoItem


/**
 *  Created by Edward on 3/2/2019.
 */
abstract class BaseListFragment<I, N> : GeneralListFragment<I, N, InfoListAdapter>() {

    ///////////////////////////////////////////////
    //  StateSaver.WriteRead interface
    // because this interface used to save/restore infoListAdapter.itemList
    // it's better to save/restore itemList in InfoListAdapter class than here
    // Todo 3: based on the article, itemList should locate in ViewModel
    /////////////////////////////////////////////
//    override fun generateSuffix(): String {
//        // Naive solution, but it's good for now (the items don't change)
//        return ".${infoListAdapter!!.itemsList.size}.list"
//    }
//
//    override fun writeTo(objectsToSave: Queue<Any>) {
//        objectsToSave.add(infoListAdapter!!.itemsList)
//    }
//
//    @Throws(Exception::class)
//    override fun readFrom(savedObjects: Queue<Any>) {
//        infoListAdapter!!.itemsList.clear()
//
//        @Suppress("UNCHECKED_CAST")
//        infoListAdapter!!.itemsList.addAll(savedObjects.poll() as List<InfoItem>)
//    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        savedState = StateSaver.tryToSave(activity!!.isChangingConfigurations, savedState, outState, this)
//    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
//        super.onRestoreInstanceState(savedInstanceState)
//        savedState = StateSaver.tryToRestore(savedInstanceState, this)
//    }

    
    protected open fun onItemSelected(selectedItem: InfoItem) {
        Log.d(TAG, "onItemSelected() called with: selectedItem = [$selectedItem]")
        showLoading()
    }

    override fun initListeners() {
        super.initListeners()
        infoListAdapter!!.setOnStreamSelectedListener(object : OnClickGesture<StreamInfoItem>() {
            override fun selected(selectedItem: StreamInfoItem) {
                onStreamSelected(selectedItem)
            }

            override fun held(selectedItem: StreamInfoItem) {
                showStreamDialog(selectedItem)
            }
        })

        infoListAdapter!!.setOnChannelSelectedListener(object : OnClickGesture<ChannelInfoItem>() {
            override fun selected(selectedItem: ChannelInfoItem) {
                try {
                    onItemSelected(selectedItem)
                    NavigationHelper.openChannelFragment(
                        getFM(),
                        selectedItem.serviceId,
                        selectedItem.url,
                        selectedItem.name
                    )
                } catch (e: Exception) {
                    val context = getActivity()
                    context?.let {
                        ErrorActivity.reportUiError(it as AppCompatActivity, e)
                    }
                }

            }
        })

        infoListAdapter!!.setOnPlaylistSelectedListener(object : OnClickGesture<PlaylistInfoItem>() {
            override fun selected(selectedItem: PlaylistInfoItem) {
                try {
                    onItemSelected(selectedItem)
                    NavigationHelper.openPlaylistFragment(
                        getFM(),
                        selectedItem.serviceId,
                        selectedItem.url,
                        selectedItem.name
                    )
                } catch (e: Exception) {
                    val context = getActivity()
                    context?.let {
                        ErrorActivity.reportUiError(it as AppCompatActivity, e)
                    }

                }

            }
        })

        itemsList!!.clearOnScrollListeners()
        itemsList!!.addOnScrollListener(object : OnScrollBelowItemsListener() {
            override fun onScrolledDown(recyclerView: RecyclerView) {
                onScrollToBottom()
            }
        })
    }

    private fun onStreamSelected(selectedItem: StreamInfoItem) {
        Log.d(TAG, "onStreamSelected() called: autoPlay = true")
//        onItemSelected(selectedItem)
        // no last parameter: true before
//        context?.sendBroadcast(Intent(PopupVideoPlayer.ACTION_CLOSE))
        // Todo: insert directly play and store the related-videos list.
        if (selectedItem.url != null && selectedItem.name != null) {
            showLoading()
            actionOnSelectedValidStream(selectedItem)

        } else {
            Log.d(
                TAG,
                "onStreamSelected() Error: selectedItem.url = ${selectedItem.url}, selectedItem.name = ${selectedItem.name} "
            )
        }
    }

    abstract fun actionOnSelectedValidStream(selectedItem: StreamInfoItem)
    // Todo 4: on the concrete class, this function can do either play selected stream directly or show the details of the select stream and its related videos
//    NavigationHelper.openAnchorPlayer(activity!!, selectedItem.serviceId, selectedItem.url, selectedItem.name)
//        NavigationHelper.openVideoDetailFragment(getFM(), selectedItem.serviceId, selectedItem.url, selectedItem.name)

    protected fun onScrollToBottom() {
        if (hasMoreItems() && !isLoading.get()) {
            loadMoreItems()
        }
    }

    protected open fun showStreamDialog(item: StreamInfoItem) {
        val context = context
        val activity = getActivity()
        if (context == null || context.resources == null || getActivity() == null) return

        val commands = arrayOf(
            context.resources.getString(R.string.enqueue_on_background),
            context.resources.getString(R.string.enqueue_on_popup),
            context.resources.getString(R.string.append_playlist),
            context.resources.getString(R.string.share)
        )

        val actions = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> NavigationHelper.enqueueOnBackgroundPlayer(context, SinglePlayQueue(item))
                1 -> NavigationHelper.enqueueOnPopupPlayer(activity, SinglePlayQueue(item))
                2 -> if (fragmentManager != null) {
                    PlaylistAppendDialog.fromStreamInfoItems(listOf(item))
                        .show(fragmentManager!!, TAG)
                }
                3 -> shareUrl(item.name, item.url)
                else -> {
                }
            }
        }

        InfoItemDialog(getActivity()!!, item, commands, actions).show()
    }



    ///////////////////////////////////////////////////////////////////////////
    // Load and handle
    ///////////////////////////////////////////////////////////////////////////

    protected abstract fun loadMoreItems()

    protected abstract fun hasMoreItems(): Boolean

    ///////////////////////////////////////////////////////////////////////////
    // Contract
    ///////////////////////////////////////////////////////////////////////////

    override fun showLoading() {
        super.showLoading()
        animateView(itemsList!!, false, 400)
    }

    override fun hideLoading() {
        super.hideLoading()
        animateView(itemsList!!, true, 300)
    }

    override fun showError(message: String, showRetryButton: Boolean) {
        super.showError(message, showRetryButton)
        showListFooter(false)
        animateView(itemsList!!, false, 200)
    }

    override fun showEmptyState() {
        super.showEmptyState()
        showListFooter(false)
    }

    protected open fun showListFooter(show: Boolean) {
        itemsList!!.post {
            if (infoListAdapter != null && itemsList != null) {
                infoListAdapter!!.showFooter(show)
            }
        }
    }

    protected open fun handleNextItems(result: N) {
        isLoading.set(false)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Error handling
    ///////////////////////////////////////////////////////////////////////////
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == getString(R.string.list_view_mode_key)) {
            updateFlags = updateFlags or LIST_MODE_UPDATE_FLAG
        }
    }

    override fun startLoading(forceLoad: Boolean) {
        super.startLoading(forceLoad)
        resetFragment()
    }

    private fun resetFragment() {
        infoListAdapter.itemsList.clear()
    }

    protected val isGridLayout: Boolean
        get() {
            val listMode = PreferenceManager.getDefaultSharedPreferences(activity).getString(getString(R.string.list_view_mode_key), getString(R.string.list_view_mode_value))
            return when (listMode) {
                "list" -> {
                    val configuration = resources.configuration
                    configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                            configuration.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_LARGE)
                }

                "auto",
                "grid" -> true
                else -> false
            }
        }

    companion object {
        private const val TAG = "BaseListFragment"
        private const val FLAG_NO_UPDATE = 0
        private const val LIST_MODE_UPDATE_FLAG = 0x32
    }
}
