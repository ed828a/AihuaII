package com.dew.aihuaii.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dew.aihuaii.R
import com.dew.aihuaii.ui.adapter.SelectedTabsPagerAdapter
import com.dew.aihuaii.ui.tab.Tab
import com.dew.aihuaii.ui.tab.TabsManager
import kotlinx.android.synthetic.main.fragment_main.*

/**
 *  Created by Edward on 3/2/2019.
 */
class MainFragment : BaseFragment(), TabLayout.OnTabSelectedListener {
    private lateinit var pagerAdapter: SelectedTabsPagerAdapter

    private val tabsList = ArrayList<Tab>()
    private lateinit var tabsManager: TabsManager

    private var hasTabsChanged = false

    ///////////////////////////////////////////////////////////////////////////
    // Fragment's LifeCycle
    ///////////////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        tabsManager = TabsManager.getTabsManager(activity as Context)
        tabsManager.setSavedTabsChangeListener(object : TabsManager.SavedTabsChangeListener {
            override fun onTabsChanged() {
                Log.d(TAG, "TabsManager.SavedTabsChangeListener: onTabsChanged called, isResumed = $isResumed")

                if (isResumed) {
                    updateTabs()
                } else {
                    hasTabsChanged = true
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun initViews(rootView: View, savedInstanceState: Bundle?) {
        super.initViews(rootView, savedInstanceState)

        /*  Nested fragment, use child fragment here to maintain backstack in view pager. */
        pagerAdapter = SelectedTabsPagerAdapter(childFragmentManager, tabsList, context)
        viewPagerLayout.adapter = pagerAdapter

        mainTabLayout.setupWithViewPager(viewPagerLayout)
        mainTabLayout.addOnTabSelectedListener(this)
        updateTabs()
    }

    override fun onResume() {
        super.onResume()

        if (hasTabsChanged) {
            hasTabsChanged = false
            updateTabs()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabsManager.unsetSavedTabsListener()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Menu
    ///////////////////////////////////////////////////////////////////////////

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        Log.d(TAG, "onCreateOptionsMenu() called with: menu = [$menu], inflater = [$inflater]")
//        inflater.inflate(R.menu.main_fragment_menu, menu)
//
//        val supportActionBar = activity?.supportActionBar
//        supportActionBar?.setDisplayHomeAsUpEnabled(false)
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.action_search -> {
//                try {
//                    NavigationHelper.openSearchFragment(
//                        fragmentManager,
//                        ServiceHelper.getSelectedServiceId(activity!!),
//                        "")
//                } catch (e: Exception) {
//                    val context = getActivity()
//                    context?.let {
//                        ErrorActivity.reportUiError(it as AppCompatActivity, e)
//                    }
//                }
//
//                return true
//            }
//
//        }
//        return super.onOptionsItemSelected(item)
//    }

    ///////////////////////////////////////////////////////////////////////////
    // Tabs
    ///////////////////////////////////////////////////////////////////////////

    fun updateTabs() {
        tabsList.clear()
        tabsList.addAll(tabsManager.getTabs())
        pagerAdapter.notifyDataSetChanged()

        viewPagerLayout.offscreenPageLimit = pagerAdapter.count
        updateTabsIcon()
        updateCurrentTitle()
    }

    private fun updateTabsIcon() {
        tabsList.forEach {
            mainTabLayout.getTabAt(tabsList.indexOf(it))?.setIcon(it.getTabIconRes(requireContext()))
        }
    }

    private fun updateCurrentTitle() {
        setTitle(tabsList[viewPagerLayout.currentItem].getTabName(requireContext()))
    }

    ////////////////////////////////////////////////////////////////////////////
    // TabLayout.OnTabSelectedListener methods
    ////////////////////////////////////////////////////////////////////////////

    override fun onTabSelected(selectedTab: TabLayout.Tab) {
        Log.d(TAG, "onTabSelected() called with: selectedTab = [$selectedTab]")
        updateCurrentTitle()
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {}

    override fun onTabReselected(tab: TabLayout.Tab) {
        Log.d(TAG, "onTabReselected() called with: tab = [$tab]")
        updateCurrentTitle()
    }


    companion object {
        private val TAG = MainFragment::class.java.simpleName
    }
}