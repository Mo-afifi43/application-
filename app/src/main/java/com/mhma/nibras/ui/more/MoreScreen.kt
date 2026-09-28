package com.mhma.nibras.ui.more

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.mhma.nibras.BuildConfig
import com.mhma.nibras.NibrasApp
import com.mhma.nibras.R
import com.mhma.nibras.core.Prefs
import com.mhma.nibras.core.localizedNumber
import com.mhma.nibras.core.onClickDebounced
import com.mhma.nibras.core.padSystemBars
import com.mhma.nibras.ui.AboutActivity
import com.mhma.nibras.ui.Binders
import com.mhma.nibras.ui.LicensesActivity
import com.mhma.nibras.ui.MainActivity
import com.mhma.nibras.ui.Screen
import com.mhma.nibras.ui.library.LibraryScreen
import com.mhma.nibras.ui.widget.Option
import com.mhma.nibras.ui.widget.OptionSheet

class MoreScreen(activity: MainActivity, container: ViewGroup) : Screen(activity) {

    override val root: View =
        LayoutInflater.from(activity).inflate(R.layout.screen_more, container, false)

    private val prefs: Prefs = (activity.application as NibrasApp).prefs

    private val rowLanguage: View = root.findViewById(R.id.row_language)
    private val rowAppearance: View = root.findViewById(R.id.row_appearance)

    init {
        root.padSystemBars(top = true)
        bindRow(rowLanguage, R.drawable.ic_language, R.string.more_language)
        bindRow(rowAppearance, R.drawable.ic_palette, R.string.more_appearance)
        bindRow(root.findViewById(R.id.row_saved), R.drawable.ic_bookmark_filled, R.string.more_saved_books)
        bindRow(root.findViewById(R.id.row_reset), R.drawable.ic_refresh, R.string.more_reset_progress)
        bindRow(root.findViewById(R.id.row_about), R.drawable.ic_info, R.string.more_about)
        bindRow(root.findViewById(R.id.row_share), R.drawable.ic_share, R.string.more_share)
        bindRow(root.findViewById(R.id.row_rate), R.drawable.ic_star, R.string.more_rate)
        bindRow(root.findViewById(R.id.row_licenses), R.drawable.ic_scroll, R.string.more_licenses)
        root.findViewById<TextView>(R.id.more_version).text =
            activity.getString(R.string.more_version, BuildConfig.VERSION_NAME)

        rowLanguage.onClickDebounced { showLanguagePicker() }
        rowAppearance.onClickDebounced { showAppearancePicker() }
        root.findViewById<View>(R.id.row_saved).onClickDebounced {
            activity.openLibrary(LibraryScreen.FILTER_SAVED)
        }
        root.findViewById<View>(R.id.row_reset).onClickDebounced { confirmReset() }
        root.findViewById<View>(R.id.row_about).onClickDebounced {
            activity.startActivity(Intent(activity, AboutActivity::class.java))
        }
        root.findViewById<View>(R.id.row_share).onClickDebounced {
            Binders.shareText(activity, activity.getString(R.string.more_share_text))
        }
        root.findViewById<View>(R.id.row_rate).onClickDebounced { openStore() }
        root.findViewById<View>(R.id.row_licenses).onClickDebounced {
            activity.startActivity(Intent(activity, LicensesActivity::class.java))
        }
        refreshValues()
    }

    override fun onShow() = refreshValues()

    override fun onReselect() {
        (root as ScrollView).smoothScrollTo(0, 0)
    }

    private fun bindRow(row: View, icon: Int, title: Int) {
        row.findViewById<ImageView>(R.id.row_icon).setImageResource(icon)
        row.findViewById<TextView>(R.id.row_title).setText(title)
    }

    private fun setRowValue(row: View, value: String) {
        val view = row.findViewById<TextView>(R.id.row_value)
        view.text = value
        view.visibility = View.VISIBLE
    }

    private fun refreshValues() {
        root.findViewById<TextView>(R.id.stat_started).text = activity.localizedNumber(prefs.startedCount())
        root.findViewById<TextView>(R.id.stat_finished).text = activity.localizedNumber(prefs.finishedCount())
        root.findViewById<TextView>(R.id.stat_saved).text = activity.localizedNumber(prefs.bookmarks().size)
        setRowValue(rowLanguage, languageLabels()[languageIndex()])
        setRowValue(rowAppearance, themeLabels()[themeIndex()])
    }

    private fun languageLabels(): List<String> = listOf(
        activity.getString(R.string.more_language_system),
        activity.getString(R.string.lang_english),
        activity.getString(R.string.lang_arabic)
    )

    private fun languageIndex(): Int = when (prefs.language) {
        Prefs.LANG_EN -> 1
        Prefs.LANG_AR -> 2
        else -> 0
    }

    private fun themeLabels(): List<String> = listOf(
        activity.getString(R.string.more_theme_system),
        activity.getString(R.string.more_theme_light),
        activity.getString(R.string.more_theme_dark)
    )

    private fun themeIndex(): Int = when (prefs.theme) {
        Prefs.THEME_LIGHT -> 1
        Prefs.THEME_DARK -> 2
        else -> 0
    }

    private fun showLanguagePicker() {
        val labels = languageLabels()
        val options = listOf(
            Option(labels[0], null, R.drawable.ic_language),
            Option(labels[1], null, R.drawable.ic_translate),
            Option(labels[2], null, R.drawable.ic_translate)
        )
        OptionSheet.show(activity, activity.getString(R.string.more_language), options, languageIndex()) { index ->
            val value = when (index) {
                1 -> Prefs.LANG_EN
                2 -> Prefs.LANG_AR
                else -> Prefs.LANG_SYSTEM
            }
            if (value != prefs.language) {
                prefs.language = value
                activity.recreate()
            }
        }
    }

    private fun showAppearancePicker() {
        val labels = themeLabels()
        val options = listOf(
            Option(labels[0], null, R.drawable.ic_palette),
            Option(labels[1], null, R.drawable.ic_light_mode),
            Option(labels[2], null, R.drawable.ic_dark_mode)
        )
        OptionSheet.show(activity, activity.getString(R.string.more_appearance), options, themeIndex()) { index ->
            val value = when (index) {
                1 -> Prefs.THEME_LIGHT
                2 -> Prefs.THEME_DARK
                else -> Prefs.THEME_SYSTEM
            }
            if (value != prefs.theme) {
                prefs.theme = value
                activity.recreate()
            }
        }
    }

    private fun confirmReset() {
        AlertDialog.Builder(activity)
            .setTitle(R.string.more_reset_confirm_title)
            .setMessage(R.string.more_reset_confirm_msg)
            .setNegativeButton(R.string.action_cancel, null)
            .setPositiveButton(R.string.action_reset) { _, _ ->
                prefs.resetProgress()
                refreshValues()
                Toast.makeText(activity, R.string.more_reset_done, Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun openStore() {
        val id = activity.packageName
        try {
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$id")))
        } catch (e: ActivityNotFoundException) {
            Binders.openUrl(activity, "https://play.google.com/store/apps/details?id=$id")
        }
    }
}
