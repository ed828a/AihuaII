package com.dew.aihuaii.ui.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.view.ViewGroup
import com.dew.aihuaii.ui.fragment.BaseFragment
import com.dew.aihuaii.ui.fragment.BlankFragment
import com.dew.aihuaii.ui.tab.Tab
import com.dew.aihuaii.R
import com.dew.aihuaii.report.ErrorActivity
import com.dew.aihuaii.report.ErrorInfo
import com.dew.aihuaii.report.UserAction
import org.schabi.newpipe.extractor.exceptions.ExtractionException

/**
 *  Created by Edward on 3/20/2019.
 */
class SelectedTabsPagerAdapter(
    fragmentManager: FragmentManager,
    private val tabsList: List<Tab>,
    private val context: Context?
) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        val tab = tabsList[position]

        return try {
            val fragment = tab.fragment
            if (fragment is BaseFragment) {
                fragment.isUsedAsFrontPage = true
            }

            fragment

        } catch (throwable: ExtractionException) {
            context?.let {
                ErrorActivity.reportError(it, throwable, it.javaClass, null,
                    ErrorInfo.make(UserAction.UI_ERROR, "none", "", R.string.app_ui_crash))
            }

            BlankFragment()
        }
    }

    override fun getItemPosition(`object`: Any): Int {
        // Causes adapter to reload all Fragments when
        // notifyDataSetChanged is called
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int = tabsList.size

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        tabsList[position].fragment
            .childFragmentManager
            .beginTransaction()
            .remove(`object` as Fragment)
            .commitNowAllowingStateLoss()
    }
}