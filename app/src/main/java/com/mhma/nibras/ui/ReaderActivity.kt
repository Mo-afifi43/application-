package com.mhma.nibras.ui

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Layout
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.mhma.nibras.R
import com.mhma.nibras.content.Block
import com.mhma.nibras.content.Book
import com.mhma.nibras.content.BookText
import com.mhma.nibras.content.Lang
import com.mhma.nibras.content.TextNormalizer
import com.mhma.nibras.core.BaseActivity
import com.mhma.nibras.core.Prefs
import com.mhma.nibras.core.dp
import com.mhma.nibras.core.font
import com.mhma.nibras.core.localizedNumber
import com.mhma.nibras.core.onClickDebounced
import com.mhma.nibras.core.padSystemBars
import com.mhma.nibras.core.setSystemBarIconsDark
import com.mhma.nibras.core.tint
import kotlin.math.roundToInt

/** Colours of one reading theme. */
private class ReaderPalette(
    val background: Int,
    val text: Int,
    val muted: Int,
    val quote: Int,
    val accent: Int,
    val bar: Int,
    val divider: Int,
    val darkIcons: Boolean
)

/** The book reader: one chapter at a time with adjustable type and page colour. */
class ReaderActivity : BaseActivity() {

    private lateinit var book: Book
    private lateinit var lang: Lang
    private var text: BookText? = null
    private var chapterIndex = 0
    private var pendingRatio = 0f
    private var fontSp = Prefs.DEFAULT_FONT_SP
    private var themeId = Prefs.READER_AUTO
    private lateinit var palette: ReaderPalette
    private var barsVisible = true
    private val mainHandler = Handler(Looper.getMainLooper())

    private lateinit var root: FrameLayout
    private lateinit var scroll: ScrollView
    private lateinit var content: LinearLayout
    private lateinit var topBar: View
    private lateinit var bottomBar: View
    private lateinit var progressBar: ProgressBar
    private lateinit var bookTitleView: TextView
    private lateinit var chapterTitleView: TextView
    private lateinit var positionView: TextView
    private lateinit var prevButton: ImageView
    private lateinit var nextButton: ImageView
    private lateinit var bookmarkButton: ImageView
    private lateinit var tocScrim: View
    private lateinit var tocPanel: View
    private lateinit var tocList: LinearLayout
    private lateinit var settingsScrim: View
    private lateinit var settingsPanel: View
    private lateinit var fontSeek: SeekBar
    private lateinit var fontValue: TextView
    private lateinit var themeLight: View
    private lateinit var themeSepia: View
    private lateinit var themeDark: View

    private lateinit var readingFont: Typeface
    private lateinit var readingBold: Typeface
    private lateinit var arabicFont: Typeface
    private lateinit var arabicBold: Typeface
    private lateinit var uiFont: Typeface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val found = intent.getStringExtra(EXTRA_BOOK)?.let { repo.book(it) }
        if (found == null) {
            finish()
            return
        }
        book = found
        lang = intent.getStringExtra(EXTRA_LANG)?.let { Lang.of(it) } ?: prefs.bookLang(book.id) ?: uiLang
        fontSp = prefs.readerFontSp
        themeId = prefs.readerTheme
        setContentView(R.layout.activity_reader)
        bindViews()
        loadFonts()

        val savedChapter = savedInstanceState?.getInt(KEY_CHAPTER, -1) ?: -1
        val requested = intent.getIntExtra(EXTRA_CHAPTER, -1)
        val progress = prefs.progress(book.id)
        when {
            savedChapter >= 0 -> {
                chapterIndex = savedChapter
                pendingRatio = savedInstanceState?.getFloat(KEY_RATIO, 0f) ?: 0f
            }
            requested >= 0 -> {
                chapterIndex = requested
                pendingRatio = if (progress != null && progress.chapter == requested) progress.ratio else 0f
            }
            progress != null -> {
                chapterIndex = progress.chapter
                pendingRatio = progress.ratio
            }
        }

        applyTheme()
        applyKeepScreenOn()
        Thread {
            val loaded = repo.bookText(book, lang)
            mainHandler.post {
                if (isFinishing || isDestroyed) return@post
                text = loaded
                chapterIndex = chapterIndex.coerceIn(0, maxOf(0, loaded.chapters.size - 1))
                renderChapter()
                buildToc()
            }
        }.start()
    }

    private fun bindViews() {
        root = findViewById(R.id.reader_root)
        scroll = findViewById(R.id.reader_scroll)
        content = findViewById(R.id.reader_content)
        topBar = findViewById(R.id.reader_top)
        bottomBar = findViewById(R.id.reader_bottom)
        progressBar = findViewById(R.id.reader_progress)
        bookTitleView = findViewById(R.id.reader_book_title)
        chapterTitleView = findViewById(R.id.reader_chapter_title)
        positionView = findViewById(R.id.reader_position)
        prevButton = findViewById(R.id.reader_prev)
        nextButton = findViewById(R.id.reader_next)
        bookmarkButton = findViewById(R.id.reader_bookmark)
        tocScrim = findViewById(R.id.reader_toc_scrim)
        tocPanel = findViewById(R.id.reader_toc_panel)
        tocList = findViewById(R.id.reader_toc_list)
        settingsScrim = findViewById(R.id.reader_settings_scrim)
        settingsPanel = findViewById(R.id.reader_settings_panel)
        fontSeek = findViewById(R.id.font_seek)
        fontValue = findViewById(R.id.font_value)
        themeLight = findViewById(R.id.theme_light)
        themeSepia = findViewById(R.id.theme_sepia)
        themeDark = findViewById(R.id.theme_dark)

        topBar.padSystemBars(top = true)
        bottomBar.padSystemBars(bottom = true)
        tocPanel.padSystemBars(bottom = true, sides = false)
        settingsPanel.padSystemBars(bottom = true, sides = false)
        val relayout = View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> updateContentPadding() }
        root.addOnLayoutChangeListener(relayout)
        topBar.addOnLayoutChangeListener(relayout)
        bottomBar.addOnLayoutChangeListener(relayout)

        // Reading column: full width on phones, capped on tablets.
        val screenWidth = resources.displayMetrics.widthPixels
        val capped = resources.getDimensionPixelSize(R.dimen.reader_max_width)
        if (screenWidth > capped) {
            val lp = content.layoutParams as FrameLayout.LayoutParams
            lp.width = capped
            lp.gravity = Gravity.CENTER_HORIZONTAL
            content.layoutParams = lp
        }
        content.layoutDirection = if (lang.isRtl) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
        bookTitleView.text = book.title.get(uiLang)

        findViewById<View>(R.id.reader_back).setOnClickListener { finish() }
        findViewById<View>(R.id.reader_share).onClickDebounced { shareChapter() }
        bookmarkButton.setOnClickListener {
            val saved = prefs.toggleBookmark(book.id)
            bindBookmark(saved)
            Toast.makeText(this, if (saved) R.string.book_saved else R.string.book_unsaved, Toast.LENGTH_SHORT).show()
        }
        prevButton.setOnClickListener { goTo(chapterIndex - 1) }
        nextButton.setOnClickListener { goTo(chapterIndex + 1) }
        findViewById<View>(R.id.reader_contents).setOnClickListener { showToc(true) }
        findViewById<View>(R.id.reader_settings).setOnClickListener { showSettings(true) }
        tocScrim.setOnClickListener { showToc(false) }
        settingsScrim.setOnClickListener { showSettings(false) }
        tocPanel.setOnClickListener { }
        settingsPanel.setOnClickListener { }
        scroll.setOnScrollChangeListener { _, _, _, _, _ -> onScrolled() }
        (scroll.getChildAt(0) as? View)?.setOnClickListener { toggleBars() }

        fontSeek.max = Prefs.MAX_FONT_SP - Prefs.MIN_FONT_SP
        fontSeek.progress = fontSp - Prefs.MIN_FONT_SP
        fontSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) setFontSize(Prefs.MIN_FONT_SP + progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        findViewById<View>(R.id.font_decrease).setOnClickListener { setFontSize(fontSp - 1) }
        findViewById<View>(R.id.font_increase).setOnClickListener { setFontSize(fontSp + 1) }
        themeLight.setOnClickListener { setReaderTheme(Prefs.READER_LIGHT) }
        themeSepia.setOnClickListener { setReaderTheme(Prefs.READER_SEPIA) }
        themeDark.setOnClickListener { setReaderTheme(Prefs.READER_DARK) }
        val keepSwitch = findViewById<Switch>(R.id.keep_screen_switch)
        keepSwitch.isChecked = prefs.keepScreenOn
        keepSwitch.setOnCheckedChangeListener { _, checked ->
            prefs.keepScreenOn = checked
            applyKeepScreenOn()
        }
        fontValue.text = localizedNumber(fontSp)
        bindBookmark(prefs.isBookmarked(book.id))
    }

    private fun loadFonts() {
        val fallback = Typeface.DEFAULT
        readingFont = font(R.font.lora_regular) ?: fallback
        readingBold = font(R.font.lora_bold) ?: Typeface.DEFAULT_BOLD
        arabicFont = font(R.font.amiri_regular) ?: fallback
        arabicBold = font(R.font.amiri_bold) ?: Typeface.DEFAULT_BOLD
        uiFont = font(R.font.plex_medium) ?: fallback
    }

    // ------------------------------------------------------------------ theme

    private fun resolvedTheme(): Int = when (themeId) {
        Prefs.READER_LIGHT, Prefs.READER_SEPIA, Prefs.READER_DARK -> themeId
        else -> if (isNight) Prefs.READER_DARK else Prefs.READER_LIGHT
    }

    private fun paletteFor(theme: Int): ReaderPalette = when (theme) {
        Prefs.READER_SEPIA -> ReaderPalette(
            getColor(R.color.reader_sepia_bg), getColor(R.color.reader_sepia_text),
            getColor(R.color.reader_sepia_muted), getColor(R.color.reader_sepia_quote),
            getColor(R.color.gold_700), getColor(R.color.reader_sepia_bar),
            getColor(R.color.gold_200), true
        )
        Prefs.READER_DARK -> ReaderPalette(
            getColor(R.color.reader_dark_bg), getColor(R.color.reader_dark_text),
            getColor(R.color.reader_dark_muted), getColor(R.color.reader_dark_quote),
            getColor(R.color.mint_400), getColor(R.color.night_900),
            getColor(R.color.night_700), false
        )
        else -> ReaderPalette(
            getColor(R.color.reader_light_bg), getColor(R.color.reader_light_text),
            getColor(R.color.reader_light_muted), getColor(R.color.reader_light_quote),
            getColor(R.color.emerald_600), getColor(R.color.white),
            getColor(R.color.line_200), true
        )
    }

    private fun applyTheme() {
        val theme = resolvedTheme()
        palette = paletteFor(theme)
        root.setBackgroundColor(palette.background)
        topBar.setBackgroundColor(palette.bar)
        bottomBar.setBackgroundColor(palette.bar)
        progressBar.progressTintList = ColorStateList.valueOf(palette.accent)
        progressBar.progressBackgroundTintList = ColorStateList.valueOf(palette.divider)
        bookTitleView.setTextColor(palette.muted)
        chapterTitleView.setTextColor(palette.text)
        positionView.setTextColor(palette.muted)
        for (id in intArrayOf(
            R.id.reader_back, R.id.reader_share, R.id.reader_bookmark, R.id.reader_prev,
            R.id.reader_next, R.id.reader_contents, R.id.reader_settings
        )) {
            findViewById<ImageView>(id).tint(palette.text)
        }
        bindBookmark(prefs.isBookmarked(book.id))
        themeLight.isSelected = theme == Prefs.READER_LIGHT
        themeSepia.isSelected = theme == Prefs.READER_SEPIA
        themeDark.isSelected = theme == Prefs.READER_DARK
        window.setSystemBarIconsDark(palette.darkIcons)
    }

    private fun setReaderTheme(theme: Int) {
        if (theme == resolvedTheme()) return
        themeId = theme
        prefs.readerTheme = theme
        val ratio = currentRatio()
        applyTheme()
        pendingRatio = ratio
        renderChapter()
    }

    private fun setFontSize(sp: Int) {
        val clamped = sp.coerceIn(Prefs.MIN_FONT_SP, Prefs.MAX_FONT_SP)
        if (clamped == fontSp) return
        fontSp = clamped
        prefs.readerFontSp = clamped
        fontSeek.progress = clamped - Prefs.MIN_FONT_SP
        fontValue.text = localizedNumber(clamped)
        val ratio = currentRatio()
        pendingRatio = ratio
        renderChapter()
    }

    private fun applyKeepScreenOn() {
        if (prefs.keepScreenOn) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun bindBookmark(saved: Boolean) {
        bookmarkButton.setImageResource(if (saved) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_outline)
        if (this::palette.isInitialized) bookmarkButton.tint(if (saved) palette.accent else palette.text)
    }

    // -------------------------------------------------------------- chapters

    private fun goTo(index: Int) {
        val loaded = text ?: return
        if (index < 0 || index >= loaded.chapters.size) return
        saveProgress()
        chapterIndex = index
        pendingRatio = 0f
        renderChapter()
        prefs.saveProgress(book.id, chapterIndex, 0f)
    }

    private fun renderChapter() {
        val loaded = text ?: return
        if (loaded.chapters.isEmpty()) return
        val chapter = loaded.chapters[chapterIndex]
        content.removeAllViews()
        updateContentPadding()

        val label = getString(R.string.reader_chapter_label, localizedNumber(chapterIndex + 1))
        content.addView(textView(label, 12f, palette.accent, uiFont, lang.isRtl, 1.2f).apply {
            letterSpacing = if (lang.isRtl) 0f else 0.08f
        })
        content.addView(
            textView(chapter.title, fontSp + 9f, palette.text, if (lang.isRtl) arabicBold else readingBold, lang.isRtl, 1.25f)
                .withMargins(top = 6, bottom = 18)
        )
        content.addView(View(this).apply {
            setBackgroundColor(palette.accent)
            layoutParams = LinearLayout.LayoutParams(dp(40), dp(3)).also { it.bottomMargin = dp(22) }
        })

        for (block in chapter.blocks) {
            when (block) {
                is Block.Heading -> content.addView(
                    textView(block.text, fontSp + 3f, palette.text, boldFor(block.text), isArabic(block.text), 1.35f)
                        .withMargins(top = 14, bottom = 8)
                )
                is Block.Paragraph -> content.addView(paragraph(block.text).withMargins(bottom = 14))
                is Block.Quote -> content.addView(quoteCard(block).withMargins(top = 4, bottom = 18))
                is Block.Note -> content.addView(noteCard(block.text).withMargins(top = 4, bottom = 18))
                is Block.Bullets -> content.addView(bulletList(block.items).withMargins(bottom = 14))
            }
        }

        content.addView(chapterFooter(loaded).withMargins(top = 28, bottom = 8))

        chapterTitleView.text = chapter.title
        positionView.text = getString(
            R.string.reader_progress, localizedNumber(chapterIndex + 1), localizedNumber(loaded.chapters.size)
        )
        prevButton.alpha = if (chapterIndex > 0) 1f else 0.35f
        nextButton.alpha = if (chapterIndex < loaded.chapters.size - 1) 1f else 0.35f
        highlightToc()

        val ratio = pendingRatio
        pendingRatio = 0f
        scroll.scrollTo(0, 0)
        content.doOnNextLayout {
            val max = scroll.getChildAt(0).height - scroll.height
            if (ratio > 0f && max > 0) scroll.scrollTo(0, (ratio * max).roundToInt())
            onScrolled()
        }
    }

    private fun isArabic(text: String): Boolean = TextNormalizer.containsArabic(text)

    private fun fontFor(text: String): Typeface = if (isArabic(text)) arabicFont else readingFont

    private fun boldFor(text: String): Typeface = if (isArabic(text)) arabicBold else readingBold

    private fun textView(
        text: CharSequence,
        sizeSp: Float,
        color: Int,
        typeface: Typeface,
        arabic: Boolean,
        lineSpacing: Float
    ): TextView = TextView(this).apply {
        this.text = text
        textSize = sizeSp
        setTextColor(color)
        this.typeface = typeface
        setLineSpacing(0f, lineSpacing)
        layoutDirection = if (arabic) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
        textDirection = if (arabic) View.TEXT_DIRECTION_RTL else View.TEXT_DIRECTION_LTR
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        gravity = Gravity.START
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setOnClickListener { toggleBars() }
    }

    private fun paragraph(text: String): TextView {
        val arabic = isArabic(text)
        return textView(text, fontSp + (if (arabic) 1f else 0f), palette.text, fontFor(text), arabic, if (arabic) 1.85f else 1.6f)
            .apply {
                justificationMode = Layout.JUSTIFICATION_MODE_INTER_WORD
                setTextIsSelectable(true)
            }
    }

    private fun quoteCard(block: Block.Quote): View {
        val arabic = isArabic(block.text)
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = roundedCard(palette.quote, palette.accent)
            setPadding(dp(18), dp(16), dp(18), dp(16))
            layoutDirection = if (arabic) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
            setOnClickListener { toggleBars() }
        }
        card.addView(
            textView(block.text, fontSp + (if (arabic) 3f else 0.5f), palette.text, fontFor(block.text), arabic, if (arabic) 1.9f else 1.6f)
                .apply { setTextIsSelectable(true) }
        )
        val attribution = block.attribution
        if (!attribution.isNullOrBlank()) {
            card.addView(
                textView("— $attribution", fontSp - 3f, palette.muted, uiFont, isArabic(attribution), 1.3f)
                    .withMargins(top = 10)
            )
        }
        return card
    }

    private fun noteCard(text: String): View {
        val arabic = isArabic(text)
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = roundedCard(palette.quote, null)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            gravity = Gravity.TOP
            layoutDirection = if (arabic) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
            setOnClickListener { toggleBars() }
        }
        val icon = ImageView(this).apply {
            setImageResource(R.drawable.ic_info)
            tint(palette.accent)
            layoutParams = LinearLayout.LayoutParams(dp(20), dp(20)).also {
                it.marginEnd = dp(12)
                it.topMargin = dp(2)
            }
        }
        card.addView(icon)
        card.addView(
            textView(text, fontSp - 1f, palette.text, fontFor(text), arabic, if (arabic) 1.75f else 1.5f).apply {
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                setTextIsSelectable(true)
            }
        )
        return card
    }

    private fun bulletList(items: List<String>): View {
        val list = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setOnClickListener { toggleBars() }
        }
        for (item in items) {
            val arabic = isArabic(item)
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutDirection = if (arabic) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
                setPadding(0, dp(3), 0, dp(3))
            }
            row.addView(textView("•", fontSp.toFloat(), palette.accent, uiFont, arabic, if (arabic) 1.85f else 1.6f).apply {
                layoutParams = LinearLayout.LayoutParams(dp(20), ViewGroup.LayoutParams.WRAP_CONTENT)
            })
            row.addView(
                textView(item, fontSp + (if (arabic) 1f else 0f), palette.text, fontFor(item), arabic, if (arabic) 1.85f else 1.6f)
                    .apply {
                        layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                        setTextIsSelectable(true)
                    }
            )
            list.addView(row)
        }
        return list
    }

    private fun chapterFooter(loaded: BookText): View {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
        }
        val last = chapterIndex >= loaded.chapters.size - 1
        if (!last) {
            box.addView(button(getString(R.string.reader_next), primary = true) { goTo(chapterIndex + 1) })
        } else {
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                background = roundedCard(palette.quote, null)
                setPadding(dp(20), dp(24), dp(20), dp(24))
            }
            val badge = ImageView(this).apply {
                setImageResource(R.drawable.ic_check_circle)
                tint(palette.accent)
                layoutParams = LinearLayout.LayoutParams(dp(40), dp(40))
            }
            card.addView(badge)
            card.addView(
                textView(getString(R.string.reader_finished_title), 20f, palette.text, if (uiLang.isRtl) arabicBold else readingBold, uiLang.isRtl, 1.3f)
                    .apply { gravity = Gravity.CENTER; textAlignment = View.TEXT_ALIGNMENT_CENTER }
                    .withMargins(top = 12)
            )
            card.addView(
                textView(getString(R.string.reader_finished_sub), 14.5f, palette.muted, uiFont, uiLang.isRtl, 1.4f)
                    .apply { gravity = Gravity.CENTER; textAlignment = View.TEXT_ALIGNMENT_CENTER }
                    .withMargins(top = 6)
            )
            card.addView(button(getString(R.string.reader_back_library), primary = true) {
                startActivity(MainActivity.intentForLibrary(this, null))
                finish()
            }.withMargins(top = 18))
            box.addView(card)
        }
        return box
    }

    private fun button(label: String, primary: Boolean, onClick: () -> Unit): TextView = TextView(this).apply {
        text = label
        typeface = font(R.font.plex_semibold) ?: Typeface.DEFAULT_BOLD
        textSize = 15f
        gravity = Gravity.CENTER
        setTextColor(if (primary) getColor(R.color.white) else palette.text)
        background = GradientDrawable().apply {
            cornerRadius = dp(16f)
            setColor(if (primary) palette.accent else palette.quote)
        }
        minHeight = dp(52)
        setPadding(dp(24), 0, dp(24), 0)
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        isClickable = true
        isFocusable = true
        onClickDebounced { onClick() }
    }

    private fun roundedCard(fill: Int, stripe: Int?): android.graphics.drawable.Drawable {
        val body = GradientDrawable().apply {
            cornerRadius = dp(14f)
            setColor(fill)
        }
        if (stripe == null) return body
        val bar = GradientDrawable().apply {
            cornerRadius = dp(14f)
            setColor(stripe)
        }
        return LayerDrawable(arrayOf(bar, body)).apply {
            if (lang.isRtl) setLayerInset(1, 0, 0, dp(4), 0) else setLayerInset(1, dp(4), 0, 0, 0)
        }
    }

    private fun View.withMargins(top: Int = 0, bottom: Int = 0): View {
        val lp = (layoutParams as? LinearLayout.LayoutParams)
            ?: LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.topMargin = dp(top)
        lp.bottomMargin = dp(bottom)
        layoutParams = lp
        return this
    }

    private inline fun View.doOnNextLayout(crossinline action: () -> Unit) {
        addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View, l: Int, t: Int, r: Int, b: Int, ol: Int, ot: Int, or: Int, ob: Int) {
                v.removeOnLayoutChangeListener(this)
                action()
            }
        })
        requestLayout()
    }

    private fun updateContentPadding() {
        val top = topBar.height + dp(12)
        val bottom = bottomBar.height + dp(28)
        if (content.paddingTop != top || content.paddingBottom != bottom) {
            content.setPadding(content.paddingLeft, top, content.paddingRight, bottom)
        }
    }

    // -------------------------------------------------------------- progress

    private fun currentRatio(): Float {
        val child = scroll.getChildAt(0) ?: return 0f
        val max = child.height - scroll.height
        if (max <= 0) return 1f
        return (scroll.scrollY.toFloat() / max).coerceIn(0f, 1f)
    }

    private fun onScrolled() {
        val loaded = text ?: return
        val ratio = currentRatio()
        val total = loaded.chapters.size
        val overall = ((chapterIndex + ratio) / total).coerceIn(0f, 1f)
        progressBar.progress = (overall * 1000).roundToInt()
        if (chapterIndex == total - 1 && ratio >= 0.97f && !prefs.isFinished(book.id)) {
            prefs.setFinished(book.id, true)
        }
    }

    private fun saveProgress() {
        if (text == null) return
        prefs.saveProgress(book.id, chapterIndex, currentRatio())
    }

    override fun onPause() {
        saveProgress()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_CHAPTER, chapterIndex)
        outState.putFloat(KEY_RATIO, currentRatio())
    }

    // ---------------------------------------------------------------- panels

    private fun showToc(show: Boolean) = showPanel(tocScrim, tocPanel, show)

    private fun showSettings(show: Boolean) = showPanel(settingsScrim, settingsPanel, show)

    private fun showPanel(scrim: View, panel: View, show: Boolean) {
        if (show) {
            scrim.alpha = 0f
            scrim.visibility = View.VISIBLE
            scrim.animate().alpha(1f).setDuration(180).start()
            panel.translationY = dp(60f)
            panel.animate().translationY(0f).setDuration(220).start()
            setBackHandler { showPanel(scrim, panel, false) }
        } else {
            scrim.animate().alpha(0f).setDuration(160).withEndAction { scrim.visibility = View.GONE }.start()
            setBackHandler(null)
        }
    }

    private fun buildToc() {
        val loaded = text ?: return
        tocList.removeAllViews()
        val inflater = LayoutInflater.from(this)
        for (chapter in loaded.chapters) {
            val row = inflater.inflate(R.layout.item_chapter, tocList, false)
            row.findViewById<TextView>(R.id.chapter_number).text = localizedNumber(chapter.index + 1)
            val title = row.findViewById<TextView>(R.id.chapter_title)
            title.text = chapter.title
            title.textDirection = if (lang.isRtl) View.TEXT_DIRECTION_RTL else View.TEXT_DIRECTION_LTR
            row.setOnClickListener {
                showToc(false)
                goTo(chapter.index)
            }
            tocList.addView(row)
        }
        // The list scrolls inside the panel; cap it at about 60% of the screen.
        val tocScroll = findViewById<View>(R.id.reader_toc_scroll)
        val needed = loaded.chapters.size * dp(56) + dp(12)
        val cap = (resources.displayMetrics.heightPixels * 0.6f).toInt()
        tocScroll.layoutParams = tocScroll.layoutParams.also { it.height = minOf(needed, cap) }
        highlightToc()
    }

    private fun highlightToc() {
        for (i in 0 until tocList.childCount) {
            val row = tocList.getChildAt(i)
            val state = row.findViewById<ImageView>(R.id.chapter_state)
            state.visibility = if (i == chapterIndex) View.VISIBLE else View.GONE
            state.setImageResource(R.drawable.ic_play_circle)
        }
    }

    private fun toggleBars() {
        barsVisible = !barsVisible
        val topTarget = if (barsVisible) 0f else -topBar.height.toFloat()
        val bottomTarget = if (barsVisible) 0f else bottomBar.height.toFloat()
        topBar.animate().translationY(topTarget).setDuration(200).start()
        bottomBar.animate().translationY(bottomTarget).setDuration(200).start()
    }

    private fun shareChapter() {
        val loaded = text ?: return
        val chapter = loaded.chapters.getOrNull(chapterIndex) ?: return
        Binders.shareText(this, getString(R.string.reader_share_text, chapter.title, book.title.get(uiLang)))
    }

    override fun onDestroy() {
        mainHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_BOOK = "book_id"
        private const val EXTRA_LANG = "lang"
        private const val EXTRA_CHAPTER = "chapter"
        private const val KEY_CHAPTER = "state_chapter"
        private const val KEY_RATIO = "state_ratio"

        fun start(context: Context, bookId: String, lang: Lang? = null, chapter: Int = -1) {
            val intent = Intent(context, ReaderActivity::class.java)
                .putExtra(EXTRA_BOOK, bookId)
                .putExtra(EXTRA_CHAPTER, chapter)
            if (lang != null) intent.putExtra(EXTRA_LANG, lang.code)
            context.startActivity(intent)
        }
    }
}
