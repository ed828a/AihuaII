package com.dew.aihuaii.ui.holder

import android.text.TextUtils
import android.view.ViewGroup
import android.widget.TextView
import com.dew.aihuaii.R
import com.dew.aihuaii.ui.builder.InfoItemBuilder
import com.dew.aihuaii.ui.helper.Localization
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.stream.StreamInfoItem


/**
 *  Created by Edward on 3/2/2019.
 */
class StreamInfoItemHolder(infoItemBuilder: InfoItemBuilder, parent: ViewGroup) : StreamMiniInfoItemHolder(infoItemBuilder, R.layout.list_stream_item, parent) {

    val itemAdditionalDetails: TextView = itemView.findViewById(R.id.itemAdditionalDetails)

    override fun updateFromItem(infoItem: InfoItem) {
        super.updateFromItem(infoItem)

        if (infoItem !is StreamInfoItem) return

        itemAdditionalDetails.text = getStreamInfoDetailLine(infoItem)
    }

    private fun getStreamInfoDetailLine(infoItem: StreamInfoItem): String {
        var viewsAndDate = ""
        if (infoItem.viewCount >= 0) {
            viewsAndDate = Localization.shortViewCount(itemBuilder.context, infoItem.viewCount)
        }
        if (!TextUtils.isEmpty(infoItem.uploadDate)) {
            if (viewsAndDate.isEmpty()) {
                viewsAndDate = infoItem.uploadDate
            } else {
                viewsAndDate += " • " + infoItem.uploadDate
            }
        }
        return viewsAndDate
    }


}
