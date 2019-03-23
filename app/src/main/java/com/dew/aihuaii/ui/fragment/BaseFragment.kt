package com.dew.aihuaii.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.dew.aihuaii.App
import com.nostra13.universalimageloader.core.ImageLoader
import icepick.Icepick
import icepick.State

/**
 *  Created by Edward on 3/2/2019.
 */

/**
 *  Created by Edward on 2/23/2019.
 */

abstract class BaseFragment : Fragment() {

    protected var activity: AppCompatActivity? = null

    //These values are used for controlling framgents when they are part of the frontpage
    @State
    @JvmField
    var isUsedAsFrontPage = false

    private var mIsVisibleToUser = false

    protected fun getFM(): FragmentManager? =
        if (parentFragment == null)
            fragmentManager
        else
            parentFragment!!.fragmentManager


    ///////////////////////////////////////////////////////////////////////////
    // Fragment's Lifecycle
    ///////////////////////////////////////////////////////////////////////////

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as AppCompatActivity?
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate() called with: savedInstanceState = [$savedInstanceState]")
        super.onCreate(savedInstanceState)

        onRestoreInstanceState(savedInstanceState)
    }

    override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)
        Log.d(TAG, "onViewCreated() called with: rootView = [$rootView], savedInstanceState = [$savedInstanceState]")

        initViews(rootView, savedInstanceState)
        initListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
    }

    protected open fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        Icepick.restoreInstanceState(this, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()

        val refWatcher = App.getRefWatcher(getActivity()!!)
        refWatcher?.watch(this)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mIsVisibleToUser = isVisibleToUser
    }

    ///////////////////////////////////////////////////////////////////////////
    // Init
    ///////////////////////////////////////////////////////////////////////////

    protected open fun initViews(rootView: View, savedInstanceState: Bundle?) {}

    protected open fun initListeners() {}

    ///////////////////////////////////////////////////////////////////////////
    // Utils
    ///////////////////////////////////////////////////////////////////////////

    open fun setTitle(title: String) {
        Log.d(TAG, "setTitle() called with: title = [$title]")
        if ((!isUsedAsFrontPage || mIsVisibleToUser) && activity != null && activity!!.supportActionBar != null) {
            activity!!.supportActionBar!!.title = title
        }
    }

    companion object {
        private val TAG = BaseFragment::class.java.simpleName
        val imageLoader = ImageLoader.getInstance()!!
    }
}
