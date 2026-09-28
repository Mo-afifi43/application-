package com.mhma.nibras.ui

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.mhma.nibras.R
import com.mhma.nibras.content.Book
import com.mhma.nibras.content.BookText
import com.mhma.nibras.content.Lang
import com.mhma.nibras.core.BaseActivity
import com.mhma.nibras.core.localizedNumber
import com.mhma.nibras.core.onClickDebounced
import com.mhma.nibras.core.padSystemBars
import com.mhma.nibras.core.setSystemBarIcons
import com.mhma.nibras.core.themeColor
import com.mhma.nibras.core.tint
import com.mhma.nibras.ui.widget.CoverView

/** Book page: cover, description, reading-language switch and the chapter list. */
class BookDetailActivity : BaseActivity() {

    private lateinit var book: Book
    private lateinit var readLang: Lang
    private var text: BookText? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var loadGeneration = 0

    private lateinit var cover: CoverView
    private lateinit var meta: TextView
    private lateinit var readButton: TextView
    private lateinit var bookmarkButton: ImageView
    private lateinit var chaptersList: LinearLayout
    private lateinit var segEn: TextView
    private lateinit var segAr: TextView
    private lateinit var toolbar: View
    private lateinit var backButton: ImageView
    private lateinit var shareButton: ImageView
    private var toolbarSolid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val found = intent.getStringExtra(EXTRA_BOOK)?.let { repo.book(it) }
        if (found == null) {
            finish()
            return
        }
        book = found
        readLang = prefs.bookLang(book.id) ?: uiLang
        setContentView(R.layout.activity_book_detail)

        cover = findViewById(R.id.detail_cover)
        meta = findViewById(R.id.detail_meta)
        readButton = findViewById(R.id.detail_read)
        bookmarkButton = findViewById(R.id.detail_bookmark)
        chaptersList = findViewById(R.id.detail_chapters)
        segEn = findViewById(R.id.seg_en)
        segAr = findViewById(R.id.seg_ar)
        toolbar = findViewById(R.id.detail_toolbar)
        backButton = findViewById(R.id.detail_back)
        shareButton = findViewById(R.id.detail_share)

        val hero = findViewById<View>(R.id.detail_hero)
        val palette = Icons.palette(book.palette)
        hero.background = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(getColor(palette[0]), getColor(palette[1]))
        )
        hero.padSystemBars(top = true)
        toolbar.padSystemBars(top = true)
        val scroll = findViewById<ScrollView>(R.id.detail_scroll)
        scroll.padSystemBars(bottom = true)
        scroll.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val threshold = hero.height - toolbar.height
            setToolbarSolid(scrollY >= threshold && threshold > 0)
        }
        setToolbarSolid(false)

        findViewById<TextView>(R.id.detail_category).text =
            repo.category(book.categoryId)?.title?.get(uiLang) ?: ""
        findViewById<TextView>(R.id.detail_title).text = book.title.get(uiLang)
        findViewById<TextView>(R.id.detail_author).text =
            getString(R.string.library_by, book.author.get(uiLang))
        findViewById<TextView>(R.id.detail_description).text = book.description.get(uiLang)

        backButton.setOnClickListener { finish() }
        shareButton.onClickDebounced {
            Binders.shareText(
                this,
                book.title.get(uiLang) + "\n" + book.description.get(uiLang) + "\n\n" +
                    getString(R.string.more_share_text)
            )
        }
        bookmarkButton.setOnClickListener {
            val saved = prefs.toggleBookmark(book.id)
            bindBookmark(saved)
            Toast.makeText(this, if (saved) R.string.book_saved else R.string.book_unsaved, Toast.LENGTH_SHORT).show()
        }
        segEn.setOnClickListener { setReadLang(Lang.EN) }
        segAr.setOnClickListener { setReadLang(Lang.AR) }
        readButton.onClickDebounced { openReader(startChapter()) }

        bindLanguage()
        loadText()
    }

    override fun onResume() {
        super.onResume()
        bindProgress()
        bindBookmark(prefs.isBookmarked(book.id))
    }

    private fun setToolbarSolid(solid: Boolean) {
        if (solid == toolbarSolid && toolbar.background != null) return
        toolbarSolid = solid
        if (solid) {
            toolbar.setBackgroundColor(themeColor(R.attr.nbSurface))
            val iconColor = themeColor(R.attr.nbTextPrimary)
            backButton.tint(iconColor)
            shareButton.tint(iconColor)
            backButton.setBackgroundResource(R.drawable.bg_icon_button)
            shareButton.setBackgroundResource(R.drawable.bg_icon_button)
            window.setSystemBarIcons(statusDark = !isNight, navDark = !isNight)
        } else {
            toolbar.setBackgroundColor(0)
            backButton.tint(getColor(R.color.white))
            shareButton.tint(getColor(R.color.white))
            backButton.setBackgroundResource(R.drawable.bg_icon_button_on_hero)
            shareButton.setBackgroundResource(R.drawable.bg_icon_button_on_hero)
            window.setSystemBarIcons(statusDark = false, navDark = !isNight)
        }
    }

    private fun setReadLang(lang: Lang) {
        if (lang == readLang) return
        readLang = lang
        prefs.setBookLang(book.id, lang)
        bindLanguage()
        loadText()
    }

    private fun bindLanguage() {
        segEn.isSelected = readLang == Lang.EN
        segAr.isSelected = readLang == Lang.AR
        cover.bind(book.title.get(readLang), readLang, book.palette, book.icon)
        bindMeta()
    }

    private fun bindMeta() {
        val chapters = resources.getQuantityString(
            R.plurals.chapters_count, book.chapterCount, localizedNumber(book.chapterCount)
        )
        val minutes = text?.let { getString(R.string.book_minutes, localizedNumber(it.readingMinutes)) }
        meta.text = if (minutes == null) chapters else "$chapters  ·  $minutes"
    }

    private fun bindBookmark(saved: Boolean) {
        bookmarkButton.setImageResource(if (saved) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_outline)
    }

    private fun bindProgress() {
        val progress = prefs.progress(book.id)
        readButton.setText(
            when {
                prefs.isFinished(book.id) -> R.string.book_read_again
                progress != null -> R.string.book_continue
                else -> R.string.book_start
            }
        )
        renderChapters()
    }

    /** Chapter to open from the main button. */
    private fun startChapter(): Int {
        if (prefs.isFinished(book.id)) return 0
        return prefs.progress(book.id)?.chapter?.coerceIn(0, book.chapterCount - 1) ?: 0
    }

    private fun openReader(chapter: Int) {
        ReaderActivity.start(this, book.id, readLang, chapter)
    }

    private fun loadText() {
        val generation = ++loadGeneration
        val lang = readLang
        text = null
        chaptersList.removeAllViews()
        Thread {
            val loaded = repo.bookText(book, lang)
            mainHandler.post {
                if (isFinishing || isDestroyed || generation != loadGeneration) return@post
                text = loaded
                bindMeta()
                renderChapters()
            }
        }.start()
    }

    private fun renderChapters() {
        val loaded = text ?: return
        chaptersList.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val progress = prefs.progress(book.id)
        val finished = prefs.isFinished(book.id)
        for (chapter in loaded.chapters) {
            val row = inflater.inflate(R.layout.item_chapter, chaptersList, false)
            row.findViewById<TextView>(R.id.chapter_number).text = localizedNumber(chapter.index + 1)
            val title = row.findViewById<TextView>(R.id.chapter_title)
            title.text = chapter.title
            title.textDirection = if (lang().isRtl) View.TEXT_DIRECTION_RTL else View.TEXT_DIRECTION_LTR
            val state = row.findViewById<ImageView>(R.id.chapter_state)
            when {
                finished || (progress != null && chapter.index < progress.chapter) -> {
                    state.visibility = View.VISIBLE
                    state.setImageResource(R.drawable.ic_check_circle)
                }
                progress != null && chapter.index == progress.chapter -> {
                    state.visibility = View.VISIBLE
                    state.setImageResource(R.drawable.ic_play_circle)
                }
                else -> state.visibility = View.GONE
            }
            row.onClickDebounced { openReader(chapter.index) }
            chaptersList.addView(row)
        }
    }

    private fun lang(): Lang = readLang

    override fun onDestroy() {
        mainHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_BOOK = "book_id"

        fun start(context: Context, bookId: String) {
            context.startActivity(Intent(context, BookDetailActivity::class.java).putExtra(EXTRA_BOOK, bookId))
        }
    }
}
