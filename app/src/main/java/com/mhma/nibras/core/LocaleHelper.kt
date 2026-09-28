package com.mhma.nibras.core

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.mhma.nibras.content.Lang
import java.util.Locale

/**
 * Applies the user's language and appearance choices to a Context.
 *
 * The app supports exactly two interface languages (English and Arabic).
 * When the preference is "system" the device language decides; any language
 * other than Arabic falls back to English so the layout direction and the
 * strings always agree.
 */
object LocaleHelper {

    /** Interface language that should be used right now. */
    fun resolveLang(prefs: Prefs): Lang = when (prefs.language) {
        Prefs.LANG_AR -> Lang.AR
        Prefs.LANG_EN -> Lang.EN
        else -> Lang.of(systemLocale().language)
    }

    /** Night-mode bits that should be used for the given theme preference. */
    private fun resolveNightMode(prefs: Prefs, base: Configuration): Int = when (prefs.theme) {
        Prefs.THEME_LIGHT -> Configuration.UI_MODE_NIGHT_NO
        Prefs.THEME_DARK -> Configuration.UI_MODE_NIGHT_YES
        else -> base.uiMode and Configuration.UI_MODE_NIGHT_MASK
    }

    /** A short signature of the settings that require an Activity to be recreated. */
    fun configKey(prefs: Prefs): String = prefs.language + "|" + prefs.theme

    /** Wraps [base] so its resources use the chosen language, direction and night mode. */
    fun wrap(base: Context, prefs: Prefs): Context {
        val lang = resolveLang(prefs)
        val locale = Locale.forLanguageTag(lang.code)
        Locale.setDefault(locale)

        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        val night = resolveNightMode(prefs, base.resources.configuration)
        config.uiMode = (config.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or night
        return base.createConfigurationContext(config)
    }

    fun isNight(context: Context): Boolean =
        (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES

    fun currentLang(context: Context): Lang {
        val locales = context.resources.configuration.locales
        val locale = if (locales.isEmpty) Locale.getDefault() else locales[0]
        return Lang.of(locale.language)
    }

    private fun systemLocale(): Locale {
        val locales = Resources.getSystem().configuration.locales
        return if (locales.isEmpty) Locale.getDefault() else locales[0]
    }
}
