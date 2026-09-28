package com.mhma.nibras.core

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.os.SystemClock
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageView
import android.widget.TextView
import com.mhma.nibras.R
import com.mhma.nibras.content.Lang
import kotlin.math.roundToInt

/** Small view helpers shared by every screen. */

fun Context.dp(value: Int): Int =
    (value * resources.displayMetrics.density).roundToInt()

fun Context.dp(value: Float): Float = value * resources.displayMetrics.density

/** Resolves a colour attribute (for example `R.attr.nbPrimary`) against the current theme. */
fun Context.themeColor(attr: Int): Int {
    val tv = TypedValue()
    theme.resolveAttribute(attr, tv, true)
    return if (tv.resourceId != 0) getColor(tv.resourceId) else tv.data
}

fun Context.font(resId: Int): Typeface? = try {
    resources.getFont(resId)
} catch (e: Exception) {
    null
}

/** The display typeface for a given content language. */
fun Context.headingFont(lang: Lang): Typeface? =
    font(if (lang == Lang.AR) R.font.amiri_bold else R.font.lora_bold)

/** The long-form reading typeface for a given content language. */
fun Context.readingFont(lang: Lang, bold: Boolean = false): Typeface? =
    font(
        when {
            lang == Lang.AR && bold -> R.font.amiri_bold
            lang == Lang.AR -> R.font.amiri_regular
            bold -> R.font.lora_bold
            else -> R.font.lora_regular
        }
    )

fun ImageView.tint(color: Int) {
    imageTintList = ColorStateList.valueOf(color)
}

/** Click listener that ignores rapid double taps (protects against double navigation). */
fun View.onClickDebounced(action: (View) -> Unit) {
    var last = 0L
    setOnClickListener {
        val now = SystemClock.elapsedRealtime()
        if (now - last > 450L) {
            last = now
            action(it)
        }
    }
}

fun TextView.setTextOrHide(text: CharSequence?) {
    if (text.isNullOrBlank()) {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE
        this.text = text
    }
}

/** Formats an integer with the digits of the current locale (Arabic-Indic when Arabic). */
fun Context.localizedNumber(value: Int): String {
    val locale = resources.configuration.locales[0]
    return String.format(locale, "%d", value)
}

// ---------------------------------------------------------------------------
// Edge-to-edge helpers
// ---------------------------------------------------------------------------

/** Lets the content draw behind transparent system bars on every supported version. */
fun Window.drawBehindSystemBars() {
    if (Build.VERSION.SDK_INT >= 30) {
        @Suppress("DEPRECATION")
        setDecorFitsSystemWindows(false)
    } else {
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = decorView.systemUiVisibility or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }
    if (Build.VERSION.SDK_INT < 35) {
        @Suppress("DEPRECATION")
        statusBarColor = 0
        @Suppress("DEPRECATION")
        navigationBarColor = 0
    }
    if (Build.VERSION.SDK_INT >= 29) {
        isNavigationBarContrastEnforced = false
        @Suppress("DEPRECATION")
        isStatusBarContrastEnforced = false
    }
}

/** Chooses dark or light icons for the status and navigation bars. */
fun Window.setSystemBarIconsDark(dark: Boolean) = setSystemBarIcons(dark, dark)

/** Chooses the icon colour of each system bar independently. */
fun Window.setSystemBarIcons(statusDark: Boolean, navDark: Boolean) {
    if (Build.VERSION.SDK_INT >= 30) {
        val mask = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
        val value = (if (statusDark) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0) or
            (if (navDark) WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS else 0)
        insetsController?.setSystemBarsAppearance(value, mask)
    } else {
        @Suppress("DEPRECATION")
        var flags = decorView.systemUiVisibility
        @Suppress("DEPRECATION")
        val statusFlag = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        @Suppress("DEPRECATION")
        val navFlag = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        flags = if (statusDark) flags or statusFlag else flags and statusFlag.inv()
        flags = if (navDark) flags or navFlag else flags and navFlag.inv()
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = flags
    }
}

/** Returns system bar + cutout insets as [left, top, right, bottom]. */
fun systemBarInsets(insets: WindowInsets): IntArray {
    return if (Build.VERSION.SDK_INT >= 30) {
        val i = insets.getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
        intArrayOf(i.left, i.top, i.right, i.bottom)
    } else {
        @Suppress("DEPRECATION")
        intArrayOf(
            insets.systemWindowInsetLeft, insets.systemWindowInsetTop,
            insets.systemWindowInsetRight, insets.systemWindowInsetBottom
        )
    }
}

/** Bottom inset of the on-screen keyboard (0 on versions that cannot report it). */
fun imeInset(insets: WindowInsets): Int =
    if (Build.VERSION.SDK_INT >= 30) insets.getInsets(WindowInsets.Type.ime()).bottom else 0

/**
 * Adds the system bar insets to this view's padding. The padding the view had
 * when this was called is preserved, so the listener is idempotent.
 */
fun View.padSystemBars(
    top: Boolean = false,
    bottom: Boolean = false,
    sides: Boolean = true,
    ime: Boolean = false
) {
    val pl = paddingLeft
    val pt = paddingTop
    val pr = paddingRight
    val pb = paddingBottom
    setOnApplyWindowInsetsListener { v, insets ->
        val s = systemBarInsets(insets)
        val keyboard = if (ime) imeInset(insets) else 0
        val bottomInset = if (bottom) maxOf(s[3], keyboard) else keyboard
        v.setPadding(
            pl + (if (sides) s[0] else 0),
            pt + (if (top) s[1] else 0),
            pr + (if (sides) s[2] else 0),
            pb + bottomInset
        )
        insets
    }
    requestApplyInsets()
}
