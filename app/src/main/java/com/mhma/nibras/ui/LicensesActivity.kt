package com.mhma.nibras.ui

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.mhma.nibras.R
import com.mhma.nibras.core.BaseActivity
import com.mhma.nibras.core.dp
import com.mhma.nibras.core.padSystemBars
import com.mhma.nibras.core.themeColor

/** Lists the open-source typefaces bundled with the app and their licence text. */
class LicensesActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_licenses)
        findViewById<View>(R.id.licenses_toolbar).padSystemBars(top = true)
        findViewById<View>(R.id.licenses_root).padSystemBars(bottom = true)
        findViewById<View>(R.id.licenses_back).setOnClickListener { finish() }

        val list = findViewById<LinearLayout>(R.id.licenses_list)
        addLicense(list, getString(R.string.license_amiri), "licenses/amiri_ofl.txt")
        addLicense(list, getString(R.string.license_plex), "licenses/plex_ofl.txt")
        addLicense(list, getString(R.string.license_lora), "licenses/lora_ofl.txt")
    }

    private fun addLicense(list: LinearLayout, title: String, assetPath: String) {
        val body = try {
            assets.open(assetPath).bufferedReader(Charsets.UTF_8).use { it.readText() }
        } catch (e: Exception) {
            ""
        }
        val heading = TextView(this).apply {
            text = title
            setTextAppearance(R.style.TextAppearance_Nibras_SectionTitle)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.topMargin = dp(24) }
        }
        val text = TextView(this).apply {
            this.text = body.trim()
            textSize = 12f
            typeface = Typeface.MONOSPACE
            setTextColor(themeColor(R.attr.nbTextSecondary))
            setLineSpacing(0f, 1.3f)
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            textDirection = View.TEXT_DIRECTION_LTR
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.topMargin = dp(8) }
        }
        list.addView(heading)
        list.addView(text)
    }
}
