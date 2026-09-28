package com.mhma.nibras.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.mhma.nibras.R
import com.mhma.nibras.core.BaseActivity
import com.mhma.nibras.core.padSystemBars
import com.mhma.nibras.ui.home.HomeScreen
import com.mhma.nibras.ui.lectures.LecturesScreen
import com.mhma.nibras.ui.library.LibraryScreen
import com.mhma.nibras.ui.more.MoreScreen

/** A tab of the main screen. Each tab keeps its view alive while the activity lives. */
abstract class Screen(val activity: MainActivity) {
    abstract val root: View

    /** Called every time the tab becomes visible (and on resume). */
    open fun onShow() {}

    /** Called when the user taps the tab that is already selected. */
    open fun onReselect() {}
}

/** The main shell: four tabs behind a bottom navigation bar. */
class MainActivity : BaseActivity() {

    private lateinit var content: FrameLayout
    private val navItems = arrayOfNulls<View>(TAB_COUNT)
    private val screens = arrayOfNulls<Screen>(TAB_COUNT)
    private var current = -1
    private var pendingLibraryFilter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!prefs.onboarded) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_main)
        content = findViewById(R.id.content)
        findViewById<View>(R.id.bottom_nav).padSystemBars(bottom = true)

        setupNavItem(R.id.nav_home, TAB_HOME, R.drawable.ic_home, R.string.nav_home)
        setupNavItem(R.id.nav_library, TAB_LIBRARY, R.drawable.ic_menu_book, R.string.nav_library)
        setupNavItem(R.id.nav_lectures, TAB_LECTURES, R.drawable.ic_play_circle, R.string.nav_lectures)
        setupNavItem(R.id.nav_more, TAB_MORE, R.drawable.ic_apps, R.string.nav_more)

        pendingLibraryFilter = intent.getStringExtra(EXTRA_LIBRARY_FILTER)
        val tab = savedInstanceState?.getInt(KEY_TAB) ?: intent.getIntExtra(EXTRA_TAB, TAB_HOME)
        select(tab.coerceIn(0, TAB_COUNT - 1))
    }

    private fun setupNavItem(id: Int, index: Int, icon: Int, label: Int) {
        val item = findViewById<View>(id)
        item.findViewById<ImageView>(R.id.nav_icon).setImageResource(icon)
        item.findViewById<TextView>(R.id.nav_label).setText(label)
        item.setOnClickListener { select(index) }
        navItems[index] = item
    }

    fun select(index: Int) {
        if (index == current) {
            screens[index]?.onReselect()
            return
        }
        for (i in 0 until TAB_COUNT) navItems[i]?.isSelected = i == index
        val screen = screens[index] ?: createScreen(index).also {
            screens[index] = it
            content.addView(it.root)
        }
        for (i in 0 until TAB_COUNT) {
            screens[i]?.root?.visibility = if (i == index) View.VISIBLE else View.GONE
        }
        current = index
        screen.onShow()
        setBackHandler(if (index == TAB_HOME) null else { { select(TAB_HOME) } })
    }

    private fun createScreen(index: Int): Screen = when (index) {
        TAB_HOME -> HomeScreen(this, content)
        TAB_LIBRARY -> LibraryScreen(this, content).also { screen ->
            pendingLibraryFilter?.let { screen.setFilter(it) }
            pendingLibraryFilter = null
        }
        TAB_LECTURES -> LecturesScreen(this, content)
        else -> MoreScreen(this, content)
    }

    /** Switches to the library tab, optionally filtered by category id or [LibraryScreen.FILTER_SAVED]. */
    fun openLibrary(filter: String?) {
        val screen = screens[TAB_LIBRARY] as? LibraryScreen
        if (screen != null) screen.setFilter(filter) else pendingLibraryFilter = filter
        select(TAB_LIBRARY)
    }

    fun openLectures() = select(TAB_LECTURES)

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (current < 0) return
        val filter = intent.getStringExtra(EXTRA_LIBRARY_FILTER)
        val tab = intent.getIntExtra(EXTRA_TAB, -1)
        when {
            tab == TAB_LIBRARY -> openLibrary(filter)
            tab in 0 until TAB_COUNT -> select(tab)
        }
    }

    override fun onResume() {
        super.onResume()
        if (current >= 0) screens[current]?.onShow()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_TAB, current)
    }

    companion object {
        const val TAB_HOME = 0
        const val TAB_LIBRARY = 1
        const val TAB_LECTURES = 2
        const val TAB_MORE = 3
        private const val TAB_COUNT = 4
        private const val KEY_TAB = "tab"
        const val EXTRA_TAB = "extra_tab"
        const val EXTRA_LIBRARY_FILTER = "extra_library_filter"

        fun intentForLibrary(context: Context, filter: String?): Intent =
            Intent(context, MainActivity::class.java)
                .putExtra(EXTRA_TAB, TAB_LIBRARY)
                .putExtra(EXTRA_LIBRARY_FILTER, filter)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }
}
