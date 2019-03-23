package com.dew.aihuaii.ui.tab

/**
 *  Created by Edward on 3/20/2019.
 */

enum class TabType(val tab: Tab) {
    BLANK(BlankTab());
//    SUBSCRIPTIONS(Tab.SubscriptionsTab()),
//    FEED(Tab.FeedTab()),
//    BOOKMARKS(Tab.BookmarksTab()),
//    HISTORY(Tab.HistoryTab()),
//    KIOSK(Tab.KioskTab()),
//    CHANNEL(Tab.ChannelTab());

    val tabId: Int
        get() = tab.tabId

    companion object {
        const val BLANK_TAB_ID = 0
        const val SUBSCRIPTION_TAB_ID = 1
        const val FEED_TAB_ID = 2
        const val BOOKMARK_TAB_ID = 3
        const val HISTORY_TAB_ID = 4
        const val KIOSK_TAB_ID = 5
        const val CHANNEL_TAB_ID = 6
    }
}