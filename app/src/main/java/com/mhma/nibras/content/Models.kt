package com.mhma.nibras.content

/** The two content languages of the library. */
enum class Lang(val code: String) {
    EN("en"),
    AR("ar");

    val isRtl: Boolean get() = this == AR

    companion object {
        fun of(code: String?): Lang = if (code != null && code.startsWith("ar")) AR else EN
    }
}

/** A string available in both languages. */
class Localized(val en: String, val ar: String) {
    fun get(lang: Lang): String = if (lang == Lang.AR) ar else en
}

class Category(
    val id: String,
    val title: Localized,
    val subtitle: Localized,
    /** Icon key resolved to a drawable by the UI layer. */
    val icon: String,
    /** Index into the cover palettes (0..7). */
    val palette: Int
)

class Book(
    val id: String,
    val categoryId: String,
    val title: Localized,
    val author: Localized,
    val description: Localized,
    val icon: String,
    val palette: Int,
    /** Number of chapters in each language file (verified by unit tests). */
    val chapterCount: Int,
    val featured: Boolean = false
) {
    /** Path of the text file inside the assets folder. */
    fun assetPath(lang: Lang): String = "books/$id.${lang.code}.txt"
}

/** A piece of a chapter, in reading order. */
sealed class Block {
    class Heading(val text: String) : Block()
    class Paragraph(val text: String) : Block()
    class Quote(val text: String, val attribution: String?) : Block()
    class Note(val text: String) : Block()
    class Bullets(val items: List<String>) : Block()

    /** Plain text of this block, used for searching and word counts. */
    val plainText: String
        get() = when (this) {
            is Heading -> text
            is Paragraph -> text
            is Quote -> if (attribution == null) text else "$text\n$attribution"
            is Note -> text
            is Bullets -> items.joinToString("\n")
        }
}

class Chapter(val index: Int, val title: String, val blocks: List<Block>) {
    val wordCount: Int by lazy {
        var count = 0
        for (block in blocks) {
            count += block.plainText.split(WHITESPACE).count { it.isNotBlank() }
        }
        count += title.split(WHITESPACE).count { it.isNotBlank() }
        count
    }

    companion object {
        private val WHITESPACE = Regex("\\s+")
    }
}

/** The full text of one book in one language. */
class BookText(val bookId: String, val lang: Lang, val chapters: List<Chapter>) {
    val wordCount: Int get() = chapters.sumOf { it.wordCount }

    /** Rough reading time at a relaxed pace. */
    val readingMinutes: Int get() = maxOf(1, (wordCount + 179) / 180)
}

/** A search result pointing into a chapter. */
class SearchHit(
    val book: Book,
    val lang: Lang,
    val chapterIndex: Int,
    val chapterTitle: String,
    val snippet: String,
    val highlightStart: Int,
    val highlightEnd: Int
)
