package com.mhma.nibras.core

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import com.mhma.nibras.NibrasApp
import com.mhma.nibras.R
import com.mhma.nibras.content.ContentRepository
import com.mhma.nibras.content.Lang

/**
 * Base class for every screen. It applies the chosen language and theme to
 * the activity context, enables edge-to-edge drawing and recreates itself
 * when those settings change while it is in the background.
 */
abstract class BaseActivity : Activity() {

    protected val app: NibrasApp get() = application as NibrasApp
    protected val prefs: Prefs get() = app.prefs
    protected val repo: ContentRepository get() = app.repo

    /** Interface language of this activity's resources. */
    protected lateinit var uiLang: Lang
        private set

    protected val isNight: Boolean get() = LocaleHelper.isNight(this)

    private var configKey: String = ""
    private var backCallback: OnBackInvokedCallback? = null

    /** Whether content should draw behind transparent system bars. */
    protected open val drawBehindSystemBars: Boolean = true

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrap(newBase, Prefs(newBase)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiLang = LocaleHelper.currentLang(this)
        configKey = LocaleHelper.configKey(prefs)
        if (drawBehindSystemBars) {
            window.drawBehindSystemBars()
        } else {
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT < 35) {
                window.statusBarColor = themeColor(R.attr.nbBackground)
                window.navigationBarColor = themeColor(R.attr.nbBackground)
            }
        }
        window.setSystemBarIconsDark(!isNight)
    }

    override fun onResume() {
        super.onResume()
        if (configKey != LocaleHelper.configKey(prefs)) {
            recreate()
        }
    }

    /**
     * Registers (or clears) an in-app back handler. Uses the predictive back
     * dispatcher on Android 13+ and the classic callback before that.
     */
    protected fun setBackHandler(handler: (() -> Unit)?) {
        legacyBackHandler = handler
        if (Build.VERSION.SDK_INT >= 33) {
            backCallback?.let { onBackInvokedDispatcher.unregisterOnBackInvokedCallback(it) }
            backCallback = null
            if (handler != null) {
                val cb = OnBackInvokedCallback { handler() }
                onBackInvokedDispatcher.registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT, cb
                )
                backCallback = cb
            }
        }
    }

    private var legacyBackHandler: (() -> Unit)? = null

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        val handler = legacyBackHandler
        if (Build.VERSION.SDK_INT < 33 && handler != null) {
            handler()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (Build.VERSION.SDK_INT >= 33) {
            backCallback?.let { onBackInvokedDispatcher.unregisterOnBackInvokedCallback(it) }
            backCallback = null
        }
        super.onDestroy()
    }

    protected val windowOrNull: Window? get() = window
}
