package com.mhma.nibras.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.mhma.nibras.R
import com.mhma.nibras.content.OnlineBook
import com.mhma.nibras.content.OnlineLibrary
import com.mhma.nibras.core.BaseActivity
import com.mhma.nibras.core.onClickDebounced
import com.mhma.nibras.core.padSystemBars
import com.mhma.nibras.ui.widget.CoverView

/**
 * The classical library: the great works of the scholars, read in full on
 * Jami' al-Kutub al-Islamiyya. Every card opens the book's page in the browser.
 */
class ClassicalLibraryActivity : BaseActivity() {

    private lateinit var chipsRow: LinearLayout
    private lateinit var list: LinearLayout
    private lateinit var scroll: ScrollView
    private val chips = ArrayList<Pair<String?, TextView>>()

    /** null = every work, otherwise a category id. */
    private var filter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classical)
        findViewById<View>(R.id.classical_toolbar).padSystemBars(top = true)
        findViewById<View>(R.id.classical_root).padSystemBars(bottom = true)
        findViewById<View>(R.id.classical_back).setOnClickListener { finish() }
        findViewById<View>(R.id.classical_hero).clipToOutline = true

        chipsRow = findViewById(R.id.classical_chips)
        list = findViewById(R.id.classical_list)
        scroll = findViewById(R.id.classical_scroll)

        findViewById<TextView>(R.id.classical_source_label).text = OnlineLibrary.siteName.get(uiLang)
        findViewById<View>(R.id.classical_source).onClickDebounced { Binders.openUrl(this, OnlineLibrary.HOME_URL) }
        findViewById<View>(R.id.classical_quran).onClickDebounced { Binders.openUrl(this, OnlineLibrary.QURAN_URL) }
        findViewById<View>(R.id.classical_narrators).onClickDebounced {
            Binders.openUrl(this, OnlineLibrary.NARRATORS_URL)
        }

        // After a configuration change the saved filter wins, even when it is
        // "all", so the category the screen was opened with is not re-applied.
        val requested = if (savedInstanceState != null) savedInstanceState.getString(KEY_FILTER)
        else intent.getStringExtra(EXTRA_CATEGORY)
        filter = requested?.takeIf { id -> OnlineLibrary.items.any { it.categoryId == id } }
        buildChips()
        render()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_FILTER, filter)
    }

    private fun buildChips() {
        val inflater = LayoutInflater.from(this)
        chipsRow.removeAllViews()
        chips.clear()
        addChip(inflater, null, getString(R.string.chip_all))
        for (id in OnlineLibrary.categoryIds()) {
            val category = repo.category(id) ?: continue
            addChip(inflater, id, category.title.get(uiLang))
        }
        updateChips()
    }

    private fun addChip(inflater: LayoutInflater, id: String?, label: String) {
        val chip = inflater.inflate(R.layout.item_chip, chipsRow, false) as TextView
        chip.text = label
        chip.setOnClickListener {
            filter = id
            updateChips()
            render()
        }
        chipsRow.addView(chip)
        chips.add(id to chip)
    }

    private fun updateChips() {
        for ((id, chip) in chips) chip.isSelected = id == filter
    }

    private fun render() {
        val inflater = LayoutInflater.from(this)
        val items: List<OnlineBook> = when (val f = filter) {
            null -> OnlineLibrary.items
            else -> OnlineLibrary.itemsIn(f)
        }
        list.removeAllViews()
        for (book in items) {
            val row = inflater.inflate(R.layout.item_online_book, list, false)
            row.findViewById<CoverView>(R.id.online_cover)
                .bind(book.title.get(uiLang), uiLang, book.palette, book.icon)
            row.findViewById<TextView>(R.id.online_category).text =
                repo.category(book.categoryId)?.title?.get(uiLang) ?: ""
            row.findViewById<TextView>(R.id.online_title).text = book.title.get(uiLang)
            row.findViewById<TextView>(R.id.online_author).text = book.author.get(uiLang)
            row.findViewById<TextView>(R.id.online_desc).text = book.description.get(uiLang)
            row.onClickDebounced { Binders.openUrl(this, book.url) }
            list.addView(row)
        }
    }

    companion object {
        private const val EXTRA_CATEGORY = "category_id"
        private const val KEY_FILTER = "filter"

        /** Opens the classical library, optionally filtered to one category. */
        fun start(context: Context, categoryId: String? = null) {
            context.startActivity(
                Intent(context, ClassicalLibraryActivity::class.java).putExtra(EXTRA_CATEGORY, categoryId)
            )
        }
    }
}
