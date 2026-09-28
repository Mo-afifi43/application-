package com.mhma.nibras.ui

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.mhma.nibras.R
import com.mhma.nibras.content.SearchHit
import com.mhma.nibras.core.BaseActivity
import com.mhma.nibras.core.localizedNumber
import com.mhma.nibras.core.onClickDebounced
import com.mhma.nibras.core.padSystemBars
import com.mhma.nibras.core.themeColor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/** Full-text search across every book, in either language. */
class SearchActivity : BaseActivity() {

    override val drawBehindSystemBars: Boolean
        get() = Build.VERSION.SDK_INT >= 30

    private lateinit var input: EditText
    private lateinit var clear: ImageView
    private lateinit var status: TextView
    private lateinit var results: LinearLayout
    private lateinit var empty: View
    private val mainHandler = Handler(Looper.getMainLooper())
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private var generation = 0
    private var pendingSearch: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        input = findViewById(R.id.search_input)
        clear = findViewById(R.id.search_clear)
        status = findViewById(R.id.search_status)
        results = findViewById(R.id.search_results)
        empty = findViewById(R.id.search_empty)

        findViewById<View>(R.id.search_header).padSystemBars(top = drawBehindSystemBars)
        findViewById<ScrollView>(R.id.search_scroll).padSystemBars(bottom = drawBehindSystemBars, ime = true)

        findViewById<View>(R.id.search_back).setOnClickListener { finish() }
        clear.setOnClickListener { input.setText("") }
        input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                clear.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE
                scheduleSearch(query)
            }
        })
        input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                runSearch(input.text.toString())
                true
            } else false
        }

        val suggestions = findViewById<LinearLayout>(R.id.search_suggestions)
        val inflater = LayoutInflater.from(this)
        for (res in intArrayOf(
            R.string.search_suggestion_1, R.string.search_suggestion_2,
            R.string.search_suggestion_3, R.string.search_suggestion_4
        )) {
            val chip = inflater.inflate(R.layout.item_chip, suggestions, false) as TextView
            chip.setText(res)
            chip.setOnClickListener {
                input.setText(chip.text)
                input.setSelection(input.text.length)
            }
            suggestions.addView(chip)
        }

        input.requestFocus()
        input.post {
            (getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)
                ?.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun scheduleSearch(query: String) {
        pendingSearch?.let { mainHandler.removeCallbacks(it) }
        val task = Runnable { runSearch(query) }
        pendingSearch = task
        mainHandler.postDelayed(task, 250)
    }

    private fun runSearch(query: String) {
        val trimmed = query.trim()
        val gen = ++generation
        if (trimmed.length < 2) {
            showResults(emptyList(), trimmed)
            return
        }
        executor.execute {
            val hits = repo.search(trimmed)
            mainHandler.post {
                if (gen == generation && !isFinishing && !isDestroyed) showResults(hits, trimmed)
            }
        }
    }

    private fun showResults(hits: List<SearchHit>, query: String) {
        results.removeAllViews()
        if (query.length < 2) {
            empty.visibility = View.VISIBLE
            status.visibility = View.GONE
            return
        }
        empty.visibility = View.GONE
        status.visibility = View.VISIBLE
        status.text = if (hits.isEmpty()) getString(R.string.search_no_results, query)
        else resources.getQuantityString(R.plurals.search_results, hits.size, localizedNumber(hits.size))

        val inflater = LayoutInflater.from(this)
        val highlight = themeColor(R.attr.nbPrimary)
        for (hit in hits) {
            val row = inflater.inflate(R.layout.item_search_hit, results, false)
            val bookView = row.findViewById<TextView>(R.id.hit_book)
            val chapterView = row.findViewById<TextView>(R.id.hit_chapter)
            val snippetView = row.findViewById<TextView>(R.id.hit_snippet)
            val rtl = hit.lang.isRtl
            for (view in arrayOf(chapterView, snippetView)) {
                view.textDirection = if (rtl) View.TEXT_DIRECTION_RTL else View.TEXT_DIRECTION_LTR
            }
            if (hit.chapterTitle.isEmpty()) {
                bookView.text = repo.category(hit.book.categoryId)?.title?.get(hit.lang) ?: ""
                chapterView.text = hit.book.title.get(hit.lang)
                snippetView.text = hit.book.description.get(hit.lang)
            } else {
                bookView.text = hit.book.title.get(hit.lang)
                chapterView.text = hit.chapterTitle
                val span = SpannableString(hit.snippet)
                if (hit.highlightEnd > hit.highlightStart && hit.highlightEnd <= span.length) {
                    span.setSpan(StyleSpan(android.graphics.Typeface.BOLD), hit.highlightStart, hit.highlightEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    span.setSpan(ForegroundColorSpan(highlight), hit.highlightStart, hit.highlightEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                snippetView.text = span
            }
            row.onClickDebounced { ReaderActivity.start(this, hit.book.id, hit.lang, hit.chapterIndex) }
            results.addView(row)
        }
    }

    override fun onDestroy() {
        mainHandler.removeCallbacksAndMessages(null)
        executor.shutdownNow()
        super.onDestroy()
    }
}
