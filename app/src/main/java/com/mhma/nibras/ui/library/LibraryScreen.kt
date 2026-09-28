package com.mhma.nibras.ui.library

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.mhma.nibras.NibrasApp
import com.mhma.nibras.R
import com.mhma.nibras.content.Book
import com.mhma.nibras.content.Lang
import com.mhma.nibras.core.LocaleHelper
import com.mhma.nibras.core.onClickDebounced
import com.mhma.nibras.core.padSystemBars
import com.mhma.nibras.ui.Binders
import com.mhma.nibras.ui.BookDetailActivity
import com.mhma.nibras.ui.MainActivity
import com.mhma.nibras.ui.Screen
import com.mhma.nibras.ui.SearchActivity

class LibraryScreen(activity: MainActivity, container: ViewGroup) : Screen(activity) {

    override val root: View =
        LayoutInflater.from(activity).inflate(R.layout.screen_library, container, false)

    private val lang: Lang = LocaleHelper.currentLang(activity)
    private val prefs = (activity.application as NibrasApp).prefs
    private val repo = (activity.application as NibrasApp).repo
    private val inflater = LayoutInflater.from(activity)

    private val chipsRow: LinearLayout = root.findViewById(R.id.library_chips)
    private val list: LinearLayout = root.findViewById(R.id.library_list)
    private val empty: TextView = root.findViewById(R.id.library_empty)
    private val scroll: ScrollView = root.findViewById(R.id.library_scroll)

    /** null = all books, [FILTER_SAVED] = bookmarks, otherwise a category id. */
    private var filter: String? = null
    private val chips = ArrayList<Pair<String?, TextView>>()

    init {
        root.findViewById<View>(R.id.library_header).padSystemBars(top = true)
        root.findViewById<View>(R.id.library_search).onClickDebounced {
            activity.startActivity(Intent(activity, SearchActivity::class.java))
        }
        buildChips()
        render()
    }

    override fun onShow() = render()

    override fun onReselect() {
        scroll.smoothScrollTo(0, 0)
    }

    fun setFilter(newFilter: String?) {
        filter = newFilter
        updateChips()
        render()
        scroll.scrollTo(0, 0)
    }

    private fun buildChips() {
        chipsRow.removeAllViews()
        chips.clear()
        addChip(null, activity.getString(R.string.chip_all))
        addChip(FILTER_SAVED, activity.getString(R.string.chip_saved))
        for (category in repo.categories) addChip(category.id, category.title.get(lang))
        updateChips()
    }

    private fun addChip(id: String?, label: String) {
        val chip = inflater.inflate(R.layout.item_chip, chipsRow, false) as TextView
        chip.text = label
        chip.setOnClickListener { setFilter(id) }
        chipsRow.addView(chip)
        chips.add(id to chip)
    }

    private fun updateChips() {
        for ((id, chip) in chips) chip.isSelected = id == filter
    }

    private fun render() {
        val books: List<Book> = when (val f = filter) {
            null -> repo.books
            FILTER_SAVED -> {
                val saved = prefs.bookmarks()
                repo.books.filter { saved.contains(it.id) }
            }
            else -> repo.booksIn(f)
        }
        list.removeAllViews()
        empty.visibility = if (books.isEmpty() && filter == FILTER_SAVED) View.VISIBLE else View.GONE
        for (book in books) {
            val row = inflater.inflate(R.layout.item_book_row, list, false)
            val categoryTitle = repo.category(book.categoryId)?.title?.get(lang) ?: ""
            Binders.bindBookRow(row, book, lang, prefs, categoryTitle)
            row.onClickDebounced { BookDetailActivity.start(activity, book.id) }
            val bookmark = row.findViewById<ImageView>(R.id.row_bookmark)
            bookmark.setOnClickListener {
                val saved = prefs.toggleBookmark(book.id)
                Binders.bindBookmarkIcon(bookmark, saved)
                Toast.makeText(
                    activity, if (saved) R.string.book_saved else R.string.book_unsaved, Toast.LENGTH_SHORT
                ).show()
                if (filter == FILTER_SAVED && !saved) render()
            }
            list.addView(row)
        }
    }

    companion object {
        const val FILTER_SAVED = "saved"
    }
}
