package com.dew.aihuaii.data.model

/**
 *  Created by Edward on 3/2/2019.
 */
interface LocalItem : GeneralItem {
    enum class LocalItemType {
        PLAYLIST_LOCAL_ITEM,
        PLAYLIST_REMOTE_ITEM,

        PLAYLIST_STREAM_ITEM,
        STATISTIC_STREAM_ITEM
    }
}
