package com.dew.aihuaii.ui.holder

import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.dew.aihuaii.R
import com.dew.aihuaii.ui.builder.InfoItemBuilder
import com.dew.aihuaii.ui.helper.ImageDisplayConstants
import com.dew.aihuaii.ui.helper.Localization
import de.hdodenhof.circleimageview.CircleImageView
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import org.schabi.newpipe.extractor.stream.StreamType

/**
 *  Created by Edward on 3/2/2019.
 */
open class StreamMiniInfoItemHolder(infoItemBuilder: InfoItemBuilder, layoutId: Int, parent: ViewGroup) :
    InfoItemHolder(infoItemBuilder, layoutId, parent) {

    val itemThumbnailView: ImageView = itemView.findViewById(R.id.itemThumbnailView)
    val itemVideoTitleView: TextView = itemView.findViewById(R.id.itemVideoTitleView)
    val itemUploaderView: TextView = itemView.findViewById(R.id.itemUploaderView)
    val itemDurationView: TextView = itemView.findViewById(R.id.itemDurationView)
    private val itemUploaderThumbnail: CircleImageView? = itemView.findViewById(R.id.detail_uploader_thumbnail_view)

    override fun updateFromItem(infoItem: InfoItem) {
        if (infoItem !is StreamInfoItem) return
        Log.d(TAG, "updateFromItem() called: infoItem = $infoItem")

        itemVideoTitleView.text = infoItem.name
        itemUploaderView.text = infoItem.uploaderName

        itemUploaderThumbnail?.let {itemUploaderThumbnail ->
            if (infoItem is StreamInfo && !TextUtils.isEmpty(infoItem.uploaderAvatarUrl)) {
                itemBuilder.displayImage(
                    infoItem.uploaderAvatarUrl, itemUploaderThumbnail,
                    ImageDisplayConstants.DISPLAY_AVATAR_OPTIONS
                )
            } else {
                if (!TextUtils.isEmpty(infoItem.thumbnailUrl)) {
                    itemBuilder.displayImage(
                        infoItem.thumbnailUrl, itemUploaderThumbnail,
                        ImageDisplayConstants.DISPLAY_AVATAR_OPTIONS
                    )
                }
            }
        }


        when {
            infoItem.duration > 0 -> {
                itemDurationView.text = Localization.getDurationString(infoItem.duration)
                itemDurationView.setBackgroundColor(
                    ContextCompat.getColor(
                        itemBuilder.context,
                        R.color.duration_background_color
                    )
                )
                itemDurationView.visibility = View.VISIBLE
            }
            infoItem.streamType == StreamType.LIVE_STREAM -> {
                itemDurationView.setText(R.string.duration_live)
                itemDurationView.setBackgroundColor(
                    ContextCompat.getColor(
                        itemBuilder.context,
                        R.color.live_duration_background_color
                    )
                )
                itemDurationView.visibility = View.VISIBLE
            }
            else -> itemDurationView.visibility = View.GONE
        }

        // Default thumbnail is shown on error, while loading and if the url is empty
        itemBuilder.displayImage(
                infoItem.thumbnailUrl,
                itemThumbnailView,
                ImageDisplayConstants.DISPLAY_THUMBNAIL_OPTIONS
            )

        val listener: (view: View) -> Unit = { view ->
            val fragment =
                (view.context as FragmentActivity).supportFragmentManager.findFragmentById(R.id.fragment_holder)

//            if (fragment is VideoDetailFragment) {
//                Log.d(TAG, "itemView onClick(): fragment is  VideoDetailFragment")
//            } else {
//                view.context.applicationContext.sendBroadcast(Intent(ACTION_CLOSE))
//            }

            Log.d(
                TAG,
                "itemView onClicked() called: itemBuilder.onStreamSelectedListener  = ${itemBuilder.onStreamSelectedListener}"
            )
            if (itemBuilder.onStreamSelectedListener != null) {
                itemBuilder.onStreamSelectedListener?.selected(infoItem)
            }
        }
        itemVideoTitleView.setOnClickListener(listener)
        itemThumbnailView.setOnClickListener(listener)

        val listener2: (view: View) -> Unit = { view ->
            if (TextUtils.isEmpty(infoItem.uploaderUrl)) {
                Log.w(TAG, "Can't open channel because we got no channel URL")
            } else {
//                try {
//                    NavigationHelper.openChannelFragment(
//                        (view.context as FragmentActivity).supportFragmentManager,
//                        infoItem.serviceId,
//                        infoItem.uploaderUrl,
//                        infoItem.uploaderName
//                    )
//                } catch (e: Exception) {
//                    val context = itemView.context
//                    context?.let {
//                        ErrorActivity.reportUiError(it as AppCompatActivity, e)
//                    }
//                }
            }
        }
        itemUploaderThumbnail?.setOnClickListener(listener2)
        itemUploaderView.setOnClickListener(listener2)

        when (infoItem.streamType) {
            StreamType.AUDIO_STREAM, StreamType.VIDEO_STREAM, StreamType.LIVE_STREAM, StreamType.AUDIO_LIVE_STREAM -> enableLongClick(
                infoItem
            )
            StreamType.FILE, StreamType.NONE -> disableLongClick()
            else -> disableLongClick()
        }
    }


    private fun enableLongClick(item: StreamInfoItem) {
//        itemView.isLongClickable = true
//        itemView.setOnLongClickListener {
//            if (itemBuilder.onStreamSelectedListener != null) {
//                itemBuilder.onStreamSelectedListener?.held(item)
//            }
//            true
//        }

        itemThumbnailView.isLongClickable = true
        itemThumbnailView.setOnLongClickListener {
            if (itemBuilder.onStreamSelectedListener != null) {
                itemBuilder.onStreamSelectedListener?.held(item)
            }
            true
        }
    }

    private fun disableLongClick() {
//        itemView.isLongClickable = false
//        itemView.setOnLongClickListener(null)
        itemThumbnailView.isLongClickable = false
        itemThumbnailView.setOnLongClickListener(null)
    }


    companion object {
        const val TAG = "InfoItemHolder"
    }

}
