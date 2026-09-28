package com.mhma.nibras.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.mhma.nibras.BuildConfig
import com.mhma.nibras.R
import com.mhma.nibras.core.BaseActivity
import com.mhma.nibras.core.padSystemBars

class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        findViewById<View>(R.id.about_toolbar).padSystemBars(top = true)
        findViewById<View>(R.id.about_root).padSystemBars(bottom = true)
        findViewById<View>(R.id.about_back).setOnClickListener { finish() }
        findViewById<TextView>(R.id.about_version).text =
            getString(R.string.more_version, BuildConfig.VERSION_NAME)
    }
}
