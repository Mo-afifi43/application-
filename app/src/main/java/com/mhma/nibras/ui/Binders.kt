package com.mhma.nibras.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.mhma.nibras.R
import com.mhma.nibras.content.Book
import com.mhma.nibras.content.Lang
import com.mhma.nibras.content.Lecture
import com.mhma.nibras.core.Prefs
import com.mhma.nibras.core.localizedNumber
import com.mhma.nibras.core.onClickDebounced
import com.mhma.nibras.core.tint
import com.mhma.nibras.core.themeColor
import com.mhma.nibras.ui.widget.CoverView

/** View binding helpers shared by several screens. */
object Binders {

    fun bindLecture(view: View, lecture: Lecture, lang: Lang) {
        val context = view.context
        val name = lecture.name.get(lang)
        view.findViewById<TextView>(R.id.lecture_name).text = name
        view.findViewById<TextView>(R.id.lecture_desc).text = lecture.description.get(lang)
        view.findViewById<TextView>(R.id.lecture_topics).text = lecture.topics.get(lang)
        val avatar = view.findViewById<View>(R.id.lecture_avatar)
        avatar.backgroundTintList = ColorStateList.valueOf(context.getColor(Icons.palette(lecture.palette)[0]))
        view.findViewById<TextView>(R.id.lecture_initial).text = initialOf(name)
        view.onClickDebounced { openUrl(context as Activity, lecture.url) }
    }

    /** First letter of the first word that is not a title (Dr., Sheikh, …). */
    private fun initialOf(name: String): String {
        val skip = setOf("dr.", "dr", "sheikh", "shaykh", "the", "د.", "الشيخ", "الدكتور", "د")
        val word = name.split(' ').firstOrNull { it.isNotBlank() && it.lowercase() !in skip }
            ?: name.trim()
        return if (word.isEmpty()) "" else word.substring(0, 1)
    }

    fun openUrl(activity: Activity, url: String) {
        try {
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, R.string.open_failed, Toast.LENGTH_SHORT).show()
        }
    }

    fun shareText(activity: Activity, text: String) {
        val intent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, text)
        try {
            activity.startActivity(Intent.createChooser(intent, null))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, R.string.open_failed, Toast.LENGTH_SHORT).show()
        }
    }

    /** Binds a library row: cover, titles, metadata, status badge and bookmark toggle. */
    fun bindBookRow(view: View, book: Book, lang: Lang, prefs: Prefs, categoryTitle: String) {
        val context = view.context
        view.findViewById<CoverView>(R.id.row_cover)
            .bind(book.title.get(lang), lang, book.palette, book.icon)
        view.findViewById<TextView>(R.id.row_category).text = categoryTitle
        view.findViewById<TextView>(R.id.row_title).text = book.title.get(lang)
        view.findViewById<TextView>(R.id.row_author).text =
            context.getString(R.string.library_by, book.author.get(lang))
        view.findViewById<TextView>(R.id.row_meta).text = context.resources.getQuantityString(
            R.plurals.chapters_count, book.chapterCount, context.localizedNumber(book.chapterCount)
        )
        val status = view.findViewById<TextView>(R.id.row_status)
        when {
            prefs.isFinished(book.id) -> {
                status.visibility = View.VISIBLE
                status.setText(R.string.book_finished)
            }
            prefs.progress(book.id) != null -> {
                status.visibility = View.VISIBLE
                status.setText(R.string.book_in_progress)
            }
            else -> status.visibility = View.GONE
        }
        bindBookmarkIcon(view.findViewById(R.id.row_bookmark), prefs.isBookmarked(book.id))
    }

    fun bindBookmarkIcon(icon: ImageView, saved: Boolean) {
        val context = icon.context
        icon.setImageResource(if (saved) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_outline)
        icon.tint(context.themeColor(if (saved) R.attr.nbPrimary else R.attr.nbTextMuted))
    }
}
