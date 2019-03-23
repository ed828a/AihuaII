package com.dew.aihuaii.ui.builder

import android.content.Context
import com.dew.aihuaii.data.model.LocalItem
import com.dew.aihuaii.ui.helper.OnClickGesture

/**
 *  Created by Edward on 3/2/2019.
 */
class LocalItemBuilder(context: Context) : GeneralItemBuilder(context){

    var onItemSelectedListener: OnClickGesture<LocalItem>? = null

}
