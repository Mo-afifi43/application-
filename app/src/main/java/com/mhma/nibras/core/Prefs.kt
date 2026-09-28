package com.mhma.nibras.core

import android.content.Context
import android.content.SharedPreferences
import com.mhma.nibras.content.Lang

/** Reading position inside a book: chapter index and scroll ratio (0..1). */
class Progress(val chapter: Int, val ratio: Float)

/**
 * Thin, typed wrapper around SharedPreferences. Everything the app remembers
 * about the user lives here: language, appearance, reader settings,
 * bookmarks and reading progress.
 */
class Prefs(context: Context) {

    private val sp: SharedPreferences =
        context.applicationContext.getSharedPreferences("nibras_prefs", Context.MODE_PRIVATE)

    // ---- first launch -------------------------------------------------------

    var onboarded: Boolean
        get() = sp.getBoolean(KEY_ONBOARDED, false)
        set(value) = sp.edit().putBoolean(KEY_ONBOARDED, value).apply()

    // ---- language & appearance ---------------------------------------------

    /** One of [LANG_SYSTEM], [LANG_EN], [LANG_AR]. */
    var language: String
        get() = sp.getString(KEY_LANGUAGE, LANG_SYSTEM) ?: LANG_SYSTEM
        set(value) = sp.edit().putString(KEY_LANGUAGE, value).apply()

    /** One of [THEME_SYSTEM], [THEME_LIGHT], [THEME_DARK]. */
    var theme: String
        get() = sp.getString(KEY_THEME, THEME_SYSTEM) ?: THEME_SYSTEM
        set(value) = sp.edit().putString(KEY_THEME, value).apply()

    // ---- reader ----------------------------------------------------------------

    var readerFontSp: Int
        get() = sp.getInt(KEY_READER_FONT, DEFAULT_FONT_SP).coerceIn(MIN_FONT_SP, MAX_FONT_SP)
        set(value) = sp.edit().putInt(KEY_READER_FONT, value.coerceIn(MIN_FONT_SP, MAX_FONT_SP)).apply()

    /** [READER_AUTO], [READER_LIGHT], [READER_SEPIA] or [READER_DARK]. */
    var readerTheme: Int
        get() = sp.getInt(KEY_READER_THEME, READER_AUTO)
        set(value) = sp.edit().putInt(KEY_READER_THEME, value).apply()

    var keepScreenOn: Boolean
        get() = sp.getBoolean(KEY_KEEP_SCREEN_ON, false)
        set(value) = sp.edit().putBoolean(KEY_KEEP_SCREEN_ON, value).apply()

    /** Language the user chose to read a particular book in, if any. */
    fun bookLang(bookId: String): Lang? =
        sp.getString(KEY_BOOK_LANG_PREFIX + bookId, null)?.let { Lang.of(it) }

    fun setBookLang(bookId: String, lang: Lang) =
        sp.edit().putString(KEY_BOOK_LANG_PREFIX + bookId, lang.code).apply()

    // ---- bookmarks ------------------------------------------------------------

    fun bookmarks(): Set<String> = sp.getStringSet(KEY_BOOKMARKS, emptySet()) ?: emptySet()

    fun isBookmarked(bookId: String): Boolean = bookmarks().contains(bookId)

    /** Toggles the bookmark and returns the new state. */
    fun toggleBookmark(bookId: String): Boolean {
        val set = HashSet(bookmarks())
        val added = if (set.contains(bookId)) {
            set.remove(bookId); false
        } else {
            set.add(bookId); true
        }
        sp.edit().putStringSet(KEY_BOOKMARKS, set).apply()
        return added
    }

    // ---- reading progress -----------------------------------------------------

    var lastBookId: String?
        get() = sp.getString(KEY_LAST_BOOK, null)
        set(value) = sp.edit().putString(KEY_LAST_BOOK, value).apply()

    fun progress(bookId: String): Progress? {
        val raw = sp.getString(KEY_PROGRESS_PREFIX + bookId, null) ?: return null
        val parts = raw.split(':')
        if (parts.size != 2) return null
        val chapter = parts[0].toIntOrNull() ?: return null
        val ratio = parts[1].toFloatOrNull() ?: return null
        return Progress(chapter.coerceAtLeast(0), ratio.coerceIn(0f, 1f))
    }

    fun saveProgress(bookId: String, chapter: Int, ratio: Float) {
        sp.edit()
            .putString(KEY_PROGRESS_PREFIX + bookId, "$chapter:${ratio.coerceIn(0f, 1f)}")
            .putString(KEY_LAST_BOOK, bookId)
            .apply()
    }

    fun isFinished(bookId: String): Boolean = sp.getBoolean(KEY_FINISHED_PREFIX + bookId, false)

    fun setFinished(bookId: String, finished: Boolean) =
        sp.edit().putBoolean(KEY_FINISHED_PREFIX + bookId, finished).apply()

    /** Number of books the user has opened at least once. */
    fun startedCount(): Int = sp.all.keys.count { it.startsWith(KEY_PROGRESS_PREFIX) }

    fun finishedCount(): Int =
        sp.all.entries.count { it.key.startsWith(KEY_FINISHED_PREFIX) && it.value == true }

    /** Clears progress, finished flags and the last-opened book (keeps settings & bookmarks). */
    fun resetProgress() {
        val editor = sp.edit()
        for (key in sp.all.keys) {
            if (key.startsWith(KEY_PROGRESS_PREFIX) || key.startsWith(KEY_FINISHED_PREFIX)) {
                editor.remove(key)
            }
        }
        editor.remove(KEY_LAST_BOOK).apply()
    }

    companion object {
        const val LANG_SYSTEM = "system"
        const val LANG_EN = "en"
        const val LANG_AR = "ar"

        const val THEME_SYSTEM = "system"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"

        const val READER_AUTO = -1
        const val READER_LIGHT = 0
        const val READER_SEPIA = 1
        const val READER_DARK = 2

        const val MIN_FONT_SP = 14
        const val MAX_FONT_SP = 26
        const val DEFAULT_FONT_SP = 18

        private const val KEY_ONBOARDED = "onboarded"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_THEME = "theme"
        private const val KEY_READER_FONT = "reader_font_sp"
        private const val KEY_READER_THEME = "reader_theme"
        private const val KEY_KEEP_SCREEN_ON = "keep_screen_on"
        private const val KEY_BOOKMARKS = "bookmarks"
        private const val KEY_LAST_BOOK = "last_book"
        private const val KEY_PROGRESS_PREFIX = "progress."
        private const val KEY_FINISHED_PREFIX = "finished."
        private const val KEY_BOOK_LANG_PREFIX = "book_lang."
    }
}
