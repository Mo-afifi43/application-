package com.mhma.nibras.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.mhma.nibras.R
import com.mhma.nibras.content.Lang
import com.mhma.nibras.core.BaseActivity
import com.mhma.nibras.core.LocaleHelper
import com.mhma.nibras.core.onClickDebounced
import com.mhma.nibras.core.padSystemBars
import com.mhma.nibras.core.setSystemBarIcons

/** First-launch screen: introduces the app and lets the user pick a language. */
class OnboardingActivity : BaseActivity() {

    private var selected: Lang = Lang.EN
    private lateinit var optionEnglish: View
    private lateinit var optionArabic: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        findViewById<View>(R.id.onboarding_content).padSystemBars(top = true)
        findViewById<View>(R.id.onboarding_sheet).padSystemBars(bottom = true)
        // Dark hero at the top, light sheet at the bottom.
        window.setSystemBarIcons(statusDark = false, navDark = !isNight)

        optionEnglish = findViewById(R.id.option_english)
        optionArabic = findViewById(R.id.option_arabic)
        selected = savedInstanceState?.getString(KEY_LANG)?.let { Lang.of(it) }
            ?: LocaleHelper.resolveLang(prefs)
        applySelection()

        optionEnglish.setOnClickListener { selected = Lang.EN; applySelection() }
        optionArabic.setOnClickListener { selected = Lang.AR; applySelection() }
        findViewById<View>(R.id.btn_continue).onClickDebounced {
            prefs.language = selected.code
            prefs.onboarded = true
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun applySelection() {
        optionEnglish.isSelected = selected == Lang.EN
        optionArabic.isSelected = selected == Lang.AR
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_LANG, selected.code)
    }

    companion object {
        private const val KEY_LANG = "selected_lang"
    }
}
