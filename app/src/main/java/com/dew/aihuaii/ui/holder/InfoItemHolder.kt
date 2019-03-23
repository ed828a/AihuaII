package com.dew.aihuaii.ui.holder

import android.view.ViewGroup
import com.dew.aihuaii.ui.builder.InfoItemBuilder

/**
 *  Created by Edward on 3/2/2019.
 */

abstract class InfoItemHolder(
    itemBuilder: InfoItemBuilder,
    layoutId: Int,
    parent: ViewGroup
) : GeneralItemHolder<InfoItemBuilder>(itemBuilder, layoutId, parent)
