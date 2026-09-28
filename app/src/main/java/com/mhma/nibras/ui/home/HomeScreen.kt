package com.mhma.nibras.ui.home

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import com.mhma.nibras.R
import com.mhma.nibras.content.Book
import com.mhma.nibras.content.DailyWisdom
import com.mhma.nibras.content.Lang
import com.mhma.nibras.content.LectureLang
import com.mhma.nibras.content.Lectures
import com.mhma.nibras.core.HijriDate
import com.mhma.nibras.core.LocaleHelper
import com.mhma.nibras.core.localizedNumber
import com.mhma.nibras.core.onClickDebounced
import com.mhma.nibras.core.padSystemBars
import com.mhma.nibras.ui.Binders
import com.mhma.nibras.ui.BookDetailActivity
import com.mhma.nibras.ui.Icons
import com.mhma.nibras.ui.MainActivity
import com.mhma.nibras.ui.ReaderActivity
import com.mhma.nibras.ui.Screen
import com.mhma.nibras.ui.widget.CoverView
import kotlin.math.roundToInt

class HomeScreen(activity: MainActivity, container: ViewGroup) : Screen(activity) {

    override val root: View =
        LayoutInflater.from(activity).inflate(R.layout.screen_home, container, false)

    private val lang: Lang = LocaleHelper.currentLang(activity)
    private val prefs = (activity.application as com.mhma.nibras.NibrasApp).prefs
    private val repo = (activity.application as com.mhma.nibras.NibrasApp).repo
    private val inflater = LayoutInflater.from(activity)

    private val continueCard: View = root.findViewById(R.id.home_continue)
    private val continueCover: CoverView = root.findViewById(R.id.home_continue_cover)
    private val continueLabel: TextView = root.findViewById(R.id.home_continue_label)
    private val continueTitle: TextView = root.findViewById(R.id.home_continue_title)
    private val continueChapter: TextView = root.findViewById(R.id.home_continue_chapter)
    private val continueProgress: ProgressBar = root.findViewById(R.id.home_continue_progress)
    private val continuePercent: TextView = root.findViewById(R.id.home_continue_percent)

    init {
        root.padSystemBars(top = true)
        root.findViewById<View>(R.id.home_hero).clipToOutline = true
        bindHero()
        bindWisdom()
        bindCategories()
        bindFeatured()
        bindLectures()
        root.findViewById<View>(R.id.home_featured_all).onClickDebounced { activity.openLibrary(null) }
        root.findViewById<View>(R.id.home_lectures_all).onClickDebounced { activity.openLectures() }
    }

    override fun onShow() {
        bindContinue()
        bindWisdom()
    }

    override fun onReselect() {
        (root as ScrollView).smoothScrollTo(0, 0)
    }

    private fun bindHero() {
        val hijri = HijriDate.hijriToday(lang)
        val gregorian = HijriDate.gregorianToday(lang)
        root.findViewById<TextView>(R.id.home_hijri).text = hijri ?: gregorian
        val gregorianView = root.findViewById<TextView>(R.id.home_gregorian)
        if (hijri == null || gregorian.isEmpty()) {
            gregorianView.visibility = View.GONE
        } else {
            gregorianView.visibility = View.VISIBLE
            gregorianView.text = gregorian
        }
    }

    private fun bindWisdom() {
        val wisdom = DailyWisdom.today()
        val text = wisdom.text.get(lang)
        val source = wisdom.source.get(lang)
        root.findViewById<TextView>(R.id.home_wisdom_text).text = text
        root.findViewById<TextView>(R.id.home_wisdom_source).text = source
        root.findViewById<View>(R.id.home_wisdom_share).onClickDebounced {
            Binders.shareText(activity, "$text\n— $source")
        }
    }

    private fun bindContinue() {
        val lastId = prefs.lastBookId
        val book = lastId?.let { repo.book(it) }
        val progress = book?.let { prefs.progress(it.id) }
        if (book != null && progress != null) {
            val readLang = prefs.bookLang(book.id) ?: lang
            continueCover.bind(book.title.get(readLang), readLang, book.palette, book.icon)
            continueLabel.setText(R.string.home_continue_title)
            continueTitle.text = book.title.get(lang)
            val chapterNumber = (progress.chapter + 1).coerceAtMost(book.chapterCount)
            continueChapter.text = activity.getString(
                R.string.home_chapter_of,
                activity.localizedNumber(chapterNumber),
                activity.localizedNumber(book.chapterCount)
            )
            val fraction = if (prefs.isFinished(book.id)) 1f
            else ((progress.chapter + progress.ratio) / book.chapterCount).coerceIn(0f, 1f)
            val percent = (fraction * 100).roundToInt()
            continueProgress.visibility = View.VISIBLE
            continueProgress.progress = percent
            continuePercent.visibility = View.VISIBLE
            continuePercent.text = activity.getString(R.string.home_progress_percent, activity.localizedNumber(percent))
            continueCard.onClickDebounced { ReaderActivity.start(activity, book.id, readLang) }
        } else {
            val suggestion: Book = repo.featured().firstOrNull() ?: repo.books.first()
            continueCover.bind(suggestion.title.get(lang), lang, suggestion.palette, suggestion.icon)
            continueLabel.setText(R.string.home_start_title)
            continueTitle.text = suggestion.title.get(lang)
            continueChapter.setText(R.string.home_start_sub)
            continueProgress.visibility = View.GONE
            continuePercent.visibility = View.GONE
            continueCard.onClickDebounced { BookDetailActivity.start(activity, suggestion.id) }
        }
    }

    private fun bindCategories() {
        val row = root.findViewById<LinearLayout>(R.id.home_categories)
        row.removeAllViews()
        for (category in repo.categories) {
            val tile = inflater.inflate(R.layout.item_category_tile, row, false)
            val color = activity.getColor(Icons.palette(category.palette)[0])
            tile.findViewById<View>(R.id.tile_icon_bg).backgroundTintList = ColorStateList.valueOf(color)
            tile.findViewById<ImageView>(R.id.tile_icon).setImageResource(Icons.forKey(category.icon))
            tile.findViewById<TextView>(R.id.tile_title).text = category.title.get(lang)
            val count = repo.booksIn(category.id).size
            tile.findViewById<TextView>(R.id.tile_count).text = activity.resources.getQuantityString(
                R.plurals.books_count, count, activity.localizedNumber(count)
            )
            tile.onClickDebounced { activity.openLibrary(category.id) }
            row.addView(tile)
        }
    }

    private fun bindFeatured() {
        val row = root.findViewById<LinearLayout>(R.id.home_featured)
        row.removeAllViews()
        for (book in repo.featured()) {
            val item = inflater.inflate(R.layout.item_book_cover, row, false)
            item.findViewById<CoverView>(R.id.cover_art).bind(book.title.get(lang), lang, book.palette, book.icon)
            item.findViewById<TextView>(R.id.cover_title).text = book.title.get(lang)
            item.findViewById<TextView>(R.id.cover_meta).text =
                repo.category(book.categoryId)?.title?.get(lang) ?: ""
            item.onClickDebounced { BookDetailActivity.start(activity, book.id) }
            row.addView(item)
        }
    }

    private fun bindLectures() {
        val list = root.findViewById<LinearLayout>(R.id.home_lectures)
        list.removeAllViews()
        val preferred = if (lang == Lang.AR) LectureLang.AR else LectureLang.EN
        val picks = Lectures.items.filter { it.lang == preferred }.take(2)
        for (lecture in picks) {
            val item = inflater.inflate(R.layout.item_lecture, list, false)
            Binders.bindLecture(item, lecture, lang)
            list.addView(item)
        }
    }
}
