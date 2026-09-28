package com.mhma.nibras.content

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BookMarkupTest {

    @Test
    fun parsesChaptersHeadingsParagraphsQuotesNotesAndBullets() {
        val text = """
            # Title line is ignored
            == First chapter
            First paragraph line one
            continues on line two.

            ## A heading
            > A quoted line
            > second quoted line
            ~ Source of the quote
            ! A note
            - one
            - two
            == Second chapter
            Only paragraph.
        """.trimIndent()

        val chapters = BookMarkup.parse(text, strict = true)
        assertEquals(2, chapters.size)

        val first = chapters[0]
        assertEquals("First chapter", first.title)
        assertEquals(0, first.index)
        assertEquals(5, first.blocks.size)

        val paragraph = first.blocks[0] as Block.Paragraph
        assertEquals("First paragraph line one continues on line two.", paragraph.text)
        assertEquals("A heading", (first.blocks[1] as Block.Heading).text)

        val quote = first.blocks[2] as Block.Quote
        assertEquals("A quoted line\nsecond quoted line", quote.text)
        assertEquals("Source of the quote", quote.attribution)

        assertEquals("A note", (first.blocks[3] as Block.Note).text)
        assertEquals(listOf("one", "two"), (first.blocks[4] as Block.Bullets).items)

        val second = chapters[1]
        assertEquals("Second chapter", second.title)
        assertEquals(1, second.index)
        assertEquals("Only paragraph.", (second.blocks[0] as Block.Paragraph).text)
    }

    @Test
    fun attributionAfterBlankLineStillAttachesToPrecedingQuote() {
        val text = "== C\n> quote\n\n~ later attribution\n"
        val chapters = BookMarkup.parse(text, strict = true)
        val quote = chapters[0].blocks[0] as Block.Quote
        assertEquals("later attribution", quote.attribution)
    }

    @Test
    fun quoteWithoutAttributionHasNullAttribution() {
        val chapters = BookMarkup.parse("== C\n> quote only\nnext paragraph\n", strict = true)
        val quote = chapters[0].blocks[0] as Block.Quote
        assertNull(quote.attribution)
        assertTrue(chapters[0].blocks[1] is Block.Paragraph)
    }

    @Test
    fun lenientModeSkipsContentBeforeFirstChapter() {
        val chapters = BookMarkup.parse("stray text\n== C\nbody\n")
        assertEquals(1, chapters.size)
        assertEquals("C", chapters[0].title)
    }

    @Test(expected = IllegalStateException::class)
    fun strictModeRejectsContentBeforeFirstChapter() {
        BookMarkup.parse("stray text\n== C\nbody\n", strict = true)
    }

    @Test(expected = IllegalStateException::class)
    fun strictModeRejectsEmptyChapter() {
        BookMarkup.parse("== Empty\n== Next\nbody\n", strict = true)
    }

    @Test
    fun windowsLineEndingsAreHandled() {
        val chapters = BookMarkup.parse("== C\r\nline one\r\nline two\r\n", strict = true)
        assertEquals("line one line two", (chapters[0].blocks[0] as Block.Paragraph).text)
    }

    @Test
    fun wordCountAndReadingMinutes() {
        val chapters = BookMarkup.parse("== Chapter\n" + "word ".repeat(360).trim() + "\n", strict = true)
        val text = BookText("x", Lang.EN, chapters)
        assertEquals(361, text.wordCount)
        assertEquals(3, text.readingMinutes)
    }
}
