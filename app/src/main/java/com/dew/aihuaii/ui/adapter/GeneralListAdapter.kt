package com.dew.aihuaii.ui.adapter

import android.app.Activity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.dew.aihuaii.data.model.GeneralItem
import com.dew.aihuaii.ui.holder.FallbackViewHolder
import java.util.*

/**
 *  Created by Edward on 3/2/2019.
 */
abstract class GeneralListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val itemsList: ArrayList<GeneralItem> = ArrayList()

    private var showFooter = false
    private var useGridVariant = false
    private var header: View? = null
    private var footer: View? = null

    fun setGridItemVariants(useGridVariant: Boolean) {
        this.useGridVariant = useGridVariant
    }

    fun addItems(data: List<GeneralItem>?) {
        data?.let{items ->

            Log.d(TAG, "addItems() before > localItems.size() = ${itemsList.size}, data.size() = ${data.size}")

            val offsetStart = sizeConsideringHeader()
            itemsList.addAll(items)

            Log.d(TAG, "addItems() after > offsetStart = $offsetStart, localItems.size() = ${itemsList.size}, header = $header, footer = $footer, showFooter = $showFooter")

            notifyItemRangeInserted(offsetStart, data.size)

            if (footer != null && showFooter) {
                val footerNow = sizeConsideringHeader()
                notifyItemMoved(offsetStart, footerNow)

                Log.d(TAG, "addItems() footer getTabFrom $offsetStart to $footerNow")
            }
        }
    }


    fun clearStreamItemList() {
        if (itemsList.isEmpty()) {
            return
        }
        itemsList.clear()
        notifyDataSetChanged()
    }


    fun setHeader(header: View?) {
        val changed = header != this.header
        this.header = header
        if (changed) notifyDataSetChanged()
    }

    fun setFooter(view: View) {
        this.footer = view
    }

    fun showFooter(show: Boolean) {
        Log.d(TAG, "showFooter() called with: show = [$show]")
        if (show == showFooter) return

        showFooter = show
        if (show)
            notifyItemInserted(sizeConsideringHeader())
        else
            notifyItemRemoved(sizeConsideringHeader())
    }

    private fun sizeConsideringHeader(): Int {
        return itemsList.size + if (header != null) 1 else 0
    }

    override fun getItemCount(): Int {
        var count = itemsList.size
        if (header != null) count++
        if (footer != null && showFooter) count++

        Log.d(TAG, "getItemCount() called, count = $count, localItems.size() = ${itemsList.size}, header = $header, footer = $footer, showFooter = $showFooter")

        return count
    }

    override fun getItemViewType(pos: Int): Int {
        var position = pos
        Log.d(TAG, "getItemViewType() called with: position = [$position]")

        if (header != null && position == 0) {
            return HEADER_TYPE
        } else if (header != null) {
            position--
        }
        if (footer != null && position == itemsList.size && showFooter) {
            return FOOTER_TYPE
        }
        val item = itemsList[position]

        return viewTypeMaker(item.itemType)
    }

    abstract fun viewTypeMaker(type: Int): Int
//    {
//        // Todo: subclass must override this function, and can't call super function
//        return GENERAL_TYPE
//    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
        Log.d(TAG, "onCreateViewHolder() called with: parent = [$parent], type = [$type]")

        return viewHolderMaker(parent, type)
    }

    abstract fun viewHolderMaker(parent: ViewGroup, type: Int): RecyclerView.ViewHolder
//    {
//        // Todo: subclass must override this function, and can't call super function
//        return FallbackViewHolder(View(parent.context))
//    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        Log.d(TAG, "onBindViewHolder() called with: holder = [${holder.javaClass.simpleName}], position = [$pos]")

        bindItem(holder, itemsList[pos])
    }

    abstract  fun bindItem(holder: RecyclerView.ViewHolder, item: GeneralItem)
//    {
//        //Todo: subclass must override this function
//    }

    fun getSpanSizeLookup(spanCount: Int): GridLayoutManager.SpanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            val type = getItemViewType(position)
            return if (type == HEADER_TYPE || type == FOOTER_TYPE) spanCount else 1
        }
    }


    companion object {

        private val TAG = GeneralListAdapter::class.java.simpleName

        private const val HEADER_TYPE = 0
        private const val FOOTER_TYPE = 1
        private const val GENERAL_TYPE = 2

    }
}
