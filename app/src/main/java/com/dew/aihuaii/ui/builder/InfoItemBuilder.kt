package com.dew.aihuaii.ui.builder

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.dew.aihuaii.ui.helper.OnClickGesture
import com.dew.aihuaii.ui.holder.InfoItemHolder
import com.dew.aihuaii.ui.holder.StreamInfoItemHolder
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.channel.ChannelInfoItem
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem
import org.schabi.newpipe.extractor.stream.StreamInfoItem

/**
 *  Created by Edward on 3/2/2019.
 */
class InfoItemBuilder(context: Context) : GeneralItemBuilder(context) {

    var onStreamSelectedListener: OnClickGesture<StreamInfoItem>? = null
    var onChannelSelectedListener: OnClickGesture<ChannelInfoItem>? = null
    var onPlaylistSelectedListener: OnClickGesture<PlaylistInfoItem>? = null

    fun buildView(parent: ViewGroup, infoItem: InfoItem, useMiniVariant: Boolean = false): View {
        Log.d(TAG, "buildView(): infoItem.InfoType = ${infoItem.infoType}")
        val holder = holderFromInfoType(parent, infoItem.infoType, useMiniVariant)
        holder.updateFromItem(infoItem)
        return holder.itemView
    }

    private fun holderFromInfoType(parent: ViewGroup, infoType: InfoItem.InfoType, useMiniVariant: Boolean): InfoItemHolder {
        Log.d(TAG, "holderFromInfoType(): InfoType = $infoType, useMiniVariant = $useMiniVariant")
        return when (infoType) {
            InfoItem.InfoType.STREAM -> StreamInfoItemHolder(this, parent)
//            InfoItem.InfoType.CHANNEL -> ChannelInfoItemHolder(this, parent)
//            InfoItem.InfoType.PLAYLIST -> PlaylistInfoItemHolder(this, parent)
            else -> {
                Log.e(TAG, "Wrong infoType: $infoType")
                throw RuntimeException("InfoType not expected = ${infoType.name}")
            }
        }
    }

    companion object {
        private val TAG = InfoItemBuilder::class.java.toString()
    }

}
