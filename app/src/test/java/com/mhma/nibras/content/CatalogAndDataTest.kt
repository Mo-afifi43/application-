package com.mhma.nibras.content

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogAndDataTest {

    @Test
    fun bookIdsAndCategoryIdsAreUnique() {
        assertEquals(Catalog.books.size, Catalog.books.map { it.id }.toSet().size)
        assertEquals(Catalog.categories.size, Catalog.categories.map { it.id }.toSet().size)
    }

    @Test
    fun everyBookBelongsToAnExistingCategoryAndEveryCategoryHasBooks() {
        for (book in Catalog.books) {
            assertNotNull("Category ${book.categoryId} for ${book.id}", Catalog.categoryById(book.categoryId))
        }
        for (category in Catalog.categories) {
            assertTrue("Category ${category.id} has no books", Catalog.books.any { it.categoryId == category.id })
        }
    }

    @Test
    fun catalogTextsAreBilingualAndNonEmpty() {
        for (book in Catalog.books) {
            for (localized in listOf(book.title, book.author, book.description)) {
                assertTrue(localized.en.isNotBlank())
                assertTrue(localized.ar.isNotBlank())
                assertTrue(TextNormalizer.containsArabic(localized.ar))
            }
            assertTrue(book.palette in 0..7)
            assertTrue(book.chapterCount > 0)
        }
        for (category in Catalog.categories) {
            assertTrue(category.title.en.isNotBlank() && category.title.ar.isNotBlank())
            assertTrue(category.subtitle.en.isNotBlank() && category.subtitle.ar.isNotBlank())
        }
    }

    @Test
    fun thereAreFeaturedBooks() {
        assertTrue(Catalog.books.count { it.featured } >= 3)
    }

    @Test
    fun dailyWisdomIsBilingualAndRotates() {
        assertTrue(DailyWisdom.items.size >= 20)
        for (item in DailyWisdom.items) {
            assertTrue(item.text.en.isNotBlank() && item.text.ar.isNotBlank())
            assertTrue(item.source.en.isNotBlank() && item.source.ar.isNotBlank())
            assertTrue(TextNormalizer.containsArabic(item.text.ar))
        }
        assertNotNull(DailyWisdom.today())
    }

    @Test
    fun lecturesHaveValidLinksAndBothLanguages() {
        assertTrue(Lectures.items.size >= 10)
        val urls = Lectures.items.map { it.url }
        assertEquals(urls.size, urls.toSet().size)
        for (lecture in Lectures.items) {
            assertTrue(lecture.url, lecture.url.startsWith("https://www.youtube.com/"))
            assertTrue(lecture.name.en.isNotBlank() && lecture.name.ar.isNotBlank())
            assertTrue(lecture.description.en.isNotBlank() && lecture.description.ar.isNotBlank())
            assertTrue(lecture.topics.en.isNotBlank() && lecture.topics.ar.isNotBlank())
            assertTrue(lecture.palette in 0..7)
        }
        assertTrue(Lectures.items.any { it.lang == LectureLang.EN })
        assertTrue(Lectures.items.any { it.lang == LectureLang.AR })
        assertTrue(Lectures.items.any { it.lang == LectureLang.RECITATION })
    }

    @Test
    fun langResolution() {
        assertEquals(Lang.AR, Lang.of("ar"))
        assertEquals(Lang.AR, Lang.of("ar-EG"))
        assertEquals(Lang.EN, Lang.of("en"))
        assertEquals(Lang.EN, Lang.of("fr"))
        assertEquals(Lang.EN, Lang.of(null))
    }
}
