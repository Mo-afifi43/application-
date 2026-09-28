package com.mhma.nibras.core

import android.icu.text.DateFormat
import android.icu.util.Calendar
import android.icu.util.ULocale
import com.mhma.nibras.content.Lang
import java.util.Date
import java.util.Locale

/** Today's date in the Umm al-Qura Hijri calendar and in the Gregorian calendar. */
object HijriDate {

    fun hijriToday(lang: Lang): String? = try {
        val locale = ULocale(lang.code + "@calendar=islamic-umalqura")
        val calendar = Calendar.getInstance(locale)
        val format = DateFormat.getDateInstance(calendar, DateFormat.LONG, locale)
        format.format(calendar.time)
    } catch (e: Exception) {
        null
    }

    fun gregorianToday(lang: Lang): String = try {
        val locale = Locale.forLanguageTag(lang.code)
        java.text.DateFormat.getDateInstance(java.text.DateFormat.LONG, locale).format(Date())
    } catch (e: Exception) {
        ""
    }
}
