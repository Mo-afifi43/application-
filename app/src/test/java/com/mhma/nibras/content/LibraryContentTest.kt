package com.mhma.nibras.content

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Validates every book file shipped in assets against the catalogue: both
 * languages exist, parse strictly, have the declared number of chapters,
 * and contain no leftover markup.
 */
class LibraryContentTest {

    @Test
    fun everyBookHasBothLanguageFiles() {
        for (book in Catalog.books) {
            for (lang in Lang.values()) {
                assertTrue("Missing ${book.assetPath(lang)}", TestAssets.exists(book.assetPath(lang)))
            }
        }
    }

    @Test
    fun everyBookParsesStrictlyWithDeclaredChapterCount() {
        for (book in Catalog.books) {
            for (lang in Lang.values()) {
                val chapters = BookMarkup.parse(TestAssets.read(book.assetPath(lang)), strict = true)
                assertEquals(
                    "Chapter count of ${book.id} (${lang.code})",
                    book.chapterCount, chapters.size
                )
                for (chapter in chapters) {
                    assertTrue("Empty title in ${book.id} ${lang.code}", chapter.title.isNotBlank())
                    assertTrue(
                        "Chapter '${chapter.title}' in ${book.id} ${lang.code} is too short",
                        chapter.wordCount >= 40
                    )
                }
            }
        }
    }

    @Test
    fun arabicAndEnglishHaveTheSameStructure() {
        for (book in Catalog.books) {
            val en = BookMarkup.parse(TestAssets.read(book.assetPath(Lang.EN)), strict = true)
            val ar = BookMarkup.parse(TestAssets.read(book.assetPath(Lang.AR)), strict = true)
            assertEquals("Chapter count differs for ${book.id}", en.size, ar.size)
        }
    }

    @Test
    fun noMarkupArtifactsRemainInParsedText() {
        val badPrefixes = listOf("== ", "## ", "> ", "~ ", "! ", "- ")
        for (book in Catalog.books) {
            for (lang in Lang.values()) {
                val chapters = BookMarkup.parse(TestAssets.read(book.assetPath(lang)), strict = true)
                for (chapter in chapters) {
                    for (block in chapter.blocks) {
                        val text = block.plainText
                        for (prefix in badPrefixes) {
                            assertFalse(
                                "Markup artifact '$prefix' in ${book.id} ${lang.code} / ${chapter.title}",
                                text.lines().any { it.startsWith(prefix) }
                            )
                        }
                        if (block is Block.Quote) {
                            assertTrue("Empty quote in ${book.id} ${lang.code}", block.text.isNotBlank())
                            block.attribution?.let { assertTrue(it.isNotBlank()) }
                        }
                        if (block is Block.Bullets) {
                            assertTrue(block.items.all { it.isNotBlank() })
                        }
                    }
                }
            }
        }
    }

    @Test
    fun arabicFilesAreArabicAndEnglishFilesAreEnglish() {
        for (book in Catalog.books) {
            val ar = BookMarkup.parse(TestAssets.read(book.assetPath(Lang.AR)), strict = true)
            for (chapter in ar) {
                assertTrue("Arabic title expected in ${book.id}: ${chapter.title}", TextNormalizer.containsArabic(chapter.title))
            }
            val en = BookMarkup.parse(TestAssets.read(book.assetPath(Lang.EN)), strict = true)
            for (chapter in en) {
                assertFalse("English title expected in ${book.id}: ${chapter.title}", TextNormalizer.containsArabic(chapter.title))
            }
        }
    }

    @Test
    fun everyLineOfEveryFileIsWellFormed() {
        // Lines that start with a markup character must use the exact "X " form,
        // otherwise they would silently become paragraph text.
        for (book in Catalog.books) {
            for (lang in Lang.values()) {
                val lines = TestAssets.read(book.assetPath(lang)).lines()
                for ((n, line) in lines.withIndex()) {
                    val t = line.trimEnd()
                    if (t.isEmpty()) continue
                    val first = t[0]
                    if (first == '=' ) assertTrue("${book.id} ${lang.code}:${n + 1}", t.startsWith("== "))
                    if (first == '>') assertTrue("${book.id} ${lang.code}:${n + 1}", t == ">" || t.startsWith("> "))
                    if (first == '~') assertTrue("${book.id} ${lang.code}:${n + 1}", t.startsWith("~ "))
                    if (first == '!') assertTrue("${book.id} ${lang.code}:${n + 1}", t.startsWith("! "))
                    if (first == '#') assertTrue("${book.id} ${lang.code}:${n + 1}", t.startsWith("# ") || t.startsWith("## "))
                }
            }
        }
    }
}
