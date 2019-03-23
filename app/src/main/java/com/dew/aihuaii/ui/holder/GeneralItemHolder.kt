package com.dew.aihuaii.ui.holder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dew.aihuaii.ui.builder.GeneralItemBuilder
import org.schabi.newpipe.extractor.InfoItem

/**
 *  Created by Edward on 3/2/2019.
 */

abstract class GeneralItemHolder<T: GeneralItemBuilder> (
    protected val itemBuilder: T,
    layoutId: Int,
    parent: ViewGroup
) : RecyclerView.ViewHolder(LayoutInflater.from(itemBuilder.context).inflate(layoutId, parent, false)) {

    abstract fun updateFromItem(infoItem: InfoItem)


}
