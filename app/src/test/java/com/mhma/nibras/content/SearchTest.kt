package com.mhma.nibras.content

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchTest {

    private val repo = TestAssets.repository()

    @Test
    fun normalizerFoldsArabicVariantsAndCase() {
        assertEquals("الصلاه", TextNormalizer.normalize("الصَّلاةُ"))
        assertEquals("ابراهيم", TextNormalizer.normalize("إبراهيم"))
        assertEquals("موسي", TextNormalizer.normalize("موسى"))
        assertEquals("patience", TextNormalizer.normalize("PATIENCE"))
        assertEquals("محمد", TextNormalizer.normalize("مـحـمـد"))
        assertTrue(TextNormalizer.containsArabic("صبر"))
        assertTrue(!TextNormalizer.containsArabic("sabr"))
    }

    @Test
    fun englishSearchFindsBodyAndTitleMatches() {
        val hits = repo.search("patience")
        assertTrue(hits.isNotEmpty())
        assertTrue(hits.all { it.lang == Lang.EN })
        assertTrue(hits.any { it.book.id == "heart" })
        for (hit in hits) {
            if (hit.chapterTitle.isNotEmpty() && hit.highlightEnd > hit.highlightStart) {
                val marked = hit.snippet.substring(hit.highlightStart, hit.highlightEnd)
                assertEquals("patience", marked)
            }
        }
    }

    @Test
    fun arabicSearchIgnoresDiacritics() {
        val hits = repo.search("الصبر")
        assertTrue(hits.isNotEmpty())
        assertTrue(hits.all { it.lang == Lang.AR })
    }

    @Test
    fun bookTitleMatchIsReturnedFirst() {
        val hits = repo.search("Forty Hadith")
        assertTrue(hits.isNotEmpty())
        assertEquals("nawawi40", hits[0].book.id)
        assertEquals("", hits[0].chapterTitle)
    }

    @Test
    fun shortAndUnknownQueriesReturnNothing() {
        assertTrue(repo.search("a").isEmpty())
        assertTrue(repo.search("qzxjvwq").isEmpty())
    }

    @Test
    fun resultsAreCapped() {
        assertTrue(repo.search("the", limit = 5).size <= 5)
    }

    @Test
    fun repositoryCachesParsedBooks() {
        val book = Catalog.bookById("prophets")!!
        assertTrue(!repo.isLoaded(book, Lang.EN))
        val first = repo.bookText(book, Lang.EN)
        assertTrue(repo.isLoaded(book, Lang.EN))
        assertTrue(first === repo.bookText(book, Lang.EN))
        assertEquals(book.chapterCount, first.chapters.size)
        assertTrue(first.readingMinutes > 5)
    }
}
