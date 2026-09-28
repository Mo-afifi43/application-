package com.mhma.nibras.content

/**
 * Loads and caches book texts and provides full-text search.
 *
 * The repository is independent from Android: it only needs a function that
 * returns the contents of an asset file, which keeps it unit-testable.
 */
class ContentRepository(private val readAsset: (String) -> String) {

    private val cache = HashMap<String, BookText>()

    val categories: List<Category> get() = Catalog.categories
    val books: List<Book> get() = Catalog.books

    fun book(id: String): Book? = Catalog.bookById(id)

    fun category(id: String): Category? = Catalog.categoryById(id)

    fun booksIn(categoryId: String): List<Book> = books.filter { it.categoryId == categoryId }

    fun featured(): List<Book> = books.filter { it.featured }

    /** Returns the parsed text of a book, reading it from assets on first use. */
    @Synchronized
    fun bookText(book: Book, lang: Lang): BookText {
        val key = book.id + "." + lang.code
        cache[key]?.let { return it }
        val chapters = try {
            BookMarkup.parse(readAsset(book.assetPath(lang)))
        } catch (e: Exception) {
            emptyList()
        }
        val text = BookText(book.id, lang, chapters)
        cache[key] = text
        return text
    }

    fun isLoaded(book: Book, lang: Lang): Boolean = synchronized(this) {
        cache.containsKey(book.id + "." + lang.code)
    }

    /**
     * Searches titles, chapter titles and body text.
     *
     * The language is chosen from the query: Arabic letters search the Arabic
     * texts, anything else searches the English texts. Results are ordered
     * title matches first, then by book order.
     */
    fun search(query: String, limit: Int = 60): List<SearchHit> {
        val q = TextNormalizer.normalize(query.trim())
        if (q.length < 2) return emptyList()
        val lang = if (TextNormalizer.containsArabic(q)) Lang.AR else Lang.EN
        val hits = ArrayList<SearchHit>()
        val bodyHits = ArrayList<SearchHit>()

        for (book in books) {
            val title = book.title.get(lang)
            if (TextNormalizer.normalize(title).contains(q)) {
                hits.add(SearchHit(book, lang, 0, "", title, 0, 0))
            }
            val text = bookText(book, lang)
            for (chapter in text.chapters) {
                val normTitle = TextNormalizer.normalize(chapter.title)
                val titleIdx = normTitle.indexOf(q)
                if (titleIdx >= 0) {
                    hits.add(
                        SearchHit(
                            book, lang, chapter.index, chapter.title,
                            normTitle, titleIdx, titleIdx + q.length
                        )
                    )
                    continue
                }
                var found = false
                for (block in chapter.blocks) {
                    if (found) break
                    val norm = TextNormalizer.normalize(block.plainText)
                    val idx = norm.indexOf(q)
                    if (idx >= 0) {
                        val start = maxOf(0, idx - 60)
                        val end = minOf(norm.length, idx + q.length + 90)
                        val prefix = if (start > 0) "…" else ""
                        val suffix = if (end < norm.length) "…" else ""
                        val snippet = prefix + norm.substring(start, end).replace('\n', ' ') + suffix
                        val hs = prefix.length + (idx - start)
                        bodyHits.add(
                            SearchHit(book, lang, chapter.index, chapter.title, snippet, hs, hs + q.length)
                        )
                        found = true
                    }
                }
                if (hits.size + bodyHits.size >= limit) break
            }
            if (hits.size + bodyHits.size >= limit) break
        }
        hits.addAll(bodyHits)
        return if (hits.size > limit) hits.subList(0, limit) else hits
    }
}
