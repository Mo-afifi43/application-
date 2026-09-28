package com.mhma.nibras

import android.app.Application
import com.mhma.nibras.content.ContentRepository
import com.mhma.nibras.core.Prefs

/**
 * Application singleton. Holds the preference store and the content
 * repository so every screen shares one in-memory cache of parsed books.
 */
class NibrasApp : Application() {

    lateinit var prefs: Prefs
        private set

    lateinit var repo: ContentRepository
        private set

    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(this)
        repo = ContentRepository { path ->
            assets.open(path).bufferedReader(Charsets.UTF_8).use { it.readText() }
        }
    }
}
