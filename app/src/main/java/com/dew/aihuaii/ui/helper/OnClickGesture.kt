package com.dew.aihuaii.ui.helper

import android.support.v7.widget.RecyclerView

/**
 *  Created by Edward on 3/2/2019.
 */
abstract class OnClickGesture<T> {

    abstract fun selected(selectedItem: T)

    open fun held(selectedItem: T) {
        // Optional gesture
    }

    open fun drag(selectedItem: T, viewHolder: RecyclerView.ViewHolder) {
        // Optional gesture
    }
}
