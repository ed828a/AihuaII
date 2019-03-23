package com.dew.aihuaii.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import com.dew.aihuaii.data.remote.helper.KioskTranslator
import com.dew.aihuaii.data.remote.helper.ServiceHelper
import com.dew.aihuaii.ui.helper.Constants
import com.dew.aihuaii.ui.helper.NavigationHelper
import com.dew.aihuaii.ui.helper.ThemeHelper
import com.dew.aihuaii.R
import com.jakewharton.rxbinding2.widget.RxSearchView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.schabi.newpipe.extractor.NewPipe
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        setupMenuItemsOnDrawer(nav_view)

        if (supportFragmentManager.backStackEntryCount == 0) {
            initFragments()
        }
    }

    override fun onResume() {
        super.onResume()
        drawer_layout.closeDrawer(Gravity.LEFT, false)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences.getBoolean(Constants.KEY_THEME_CHANGE, false)) {
            Log.d(TAG, "Theme has changed, recreating activity...")
            sharedPreferences.edit().putBoolean(Constants.KEY_THEME_CHANGE, false).apply()
            // https://stackoverflow.com/questions/10844112/runtimeexception-performing-pause-of-activity-that-is-not-resumed
            // Briefly, let the activity resume properly posting the recreate call to end of the message queue
            Handler(Looper.getMainLooper()).post { this@MainActivity.recreate() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
    }

    private fun initFragments(){
        Log.d(TAG, "initFragments() called")
//        StateSaver.clearStateFiles()
        if (intent != null && intent.hasExtra(Constants.KEY_LINK_TYPE)) {
//            handleIntent(intent)
        } else { // app just started.
            NavigationHelper.gotoMainFragment(supportFragmentManager)
        }
    }

    private fun setupMenuItemsOnDrawer(navigationView: NavigationView) {

        //Tabs
        val currentServiceId = ServiceHelper.getSelectedServiceId(this)
        val service = NewPipe.getService(currentServiceId)

        val menu = navigationView.menu

        with(menu){
            removeGroup(R.id.menu_tabs_group)

            // so far, there is only one item in the service.kioskList.availableKiosks List: Trending
            for ((kioskId, ks) in service.kioskList.availableKiosks.withIndex()) {
                add(R.id.menu_tabs_group, kioskId, 0, KioskTranslator.getTranslatedKioskName(ks, this@MainActivity))
                    .setIcon(KioskTranslator.getKioskIcons(ks, this@MainActivity))
            }

            add(R.id.menu_tabs_group, ITEM_ID_SUBSCRIPTIONS, ORDER, R.string.tab_subscriptions)
                .setIcon(ThemeHelper.resolveResourceIdFromAttr(this@MainActivity, R.attr.ic_channel))

            add(R.id.menu_tabs_group, ITEM_ID_FEED, ORDER, R.string.fragment_whats_new)
            .setIcon(ThemeHelper.resolveResourceIdFromAttr(this@MainActivity, R.attr.rss))

            add(R.id.menu_tabs_group, ITEM_ID_BOOKMARKS, ORDER, R.string.tab_bookmarks)
                .setIcon(ThemeHelper.resolveResourceIdFromAttr(this@MainActivity, R.attr.ic_bookmark))

            add(R.id.menu_tabs_group, ITEM_ID_DOWNLOADS, ORDER, R.string.downloads)
                .setIcon(ThemeHelper.resolveResourceIdFromAttr(this@MainActivity, R.attr.download))

            add(R.id.menu_tabs_group, ITEM_ID_HISTORY, ORDER, R.string.action_history)
                .setIcon(ThemeHelper.resolveResourceIdFromAttr(this@MainActivity, R.attr.history))
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        val d = RxSearchView.queryTextChangeEvents(searchView)
            .skip(1)
            .throttleLast(100, TimeUnit.MILLISECONDS)
            .debounce(300, TimeUnit.MILLISECONDS)
            .filter {queryEvent ->  !TextUtils.isEmpty(queryEvent.queryText()) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { queryEvent ->
                if (queryEvent.isSubmitted){
                    contentMainTextView.text = "submitted: ${queryEvent.queryText()}"
                    searchView.setQuery("", false)
                    searchView.clearFocus()
                } else {
                    contentMainTextView.text = "Typing: ${queryEvent.queryText()}"
                }
            }

        compositeDisposable.add(d)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
//                val errorActivityTest = 35 / 0
                Log.d(TAG, "show this line")
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }

        }

//        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    companion object {
        const val TAG = "Main"

        private const val ITEM_ID_SUBSCRIPTIONS = -1
        private const val ITEM_ID_FEED = -2
        private const val ITEM_ID_BOOKMARKS = -3
        private const val ITEM_ID_DOWNLOADS = -4
        private const val ITEM_ID_HISTORY = -5
        private const val ITEM_ID_SETTINGS = 0
        private const val ITEM_ID_ABOUT = 1

        private const val ORDER = 0
    }
}
