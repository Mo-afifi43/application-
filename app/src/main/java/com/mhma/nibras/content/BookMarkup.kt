package com.mhma.nibras.content

/**
 * Parser for the lightweight markup used by the book files in `assets/books`.
 *
 * ```
 * # Book title            (optional, ignored: titles come from the catalog)
 * == Chapter title        starts a new chapter
 * ## Heading              a heading inside the chapter
 * > Quoted text           a quote; consecutive "> " lines keep their line breaks
 * ~ Attribution           source of the preceding quote
 * ! Note                  an information card
 * - Item                  a bullet list item
 * Plain lines             paragraphs; a blank line ends a paragraph
 * ```
 *
 * The parser is pure Kotlin so it can be unit tested on the JVM.
 */
object BookMarkup {

    /**
     * Parses a whole file into chapters.
     *
     * @param strict when true, content before the first chapter or an empty
     *   chapter raises an exception (used by the content tests). At runtime
     *   the parser is lenient and simply skips such content.
     */
    fun parse(raw: String, strict: Boolean = false): List<Chapter> {
        val chapters = ArrayList<Chapter>()
        var title: String? = null
        var blocks = ArrayList<Block>()
        val paragraph = StringBuilder()
        val quote = StringBuilder()
        val bullets = ArrayList<String>()

        fun flushParagraph() {
            if (paragraph.isNotEmpty()) {
                blocks.add(Block.Paragraph(paragraph.toString().trim()))
                paragraph.setLength(0)
            }
        }

        fun flushQuote(attribution: String? = null) {
            if (quote.isNotEmpty()) {
                blocks.add(Block.Quote(quote.toString().trim(), attribution))
                quote.setLength(0)
            }
        }

        fun flushBullets() {
            if (bullets.isNotEmpty()) {
                blocks.add(Block.Bullets(ArrayList(bullets)))
                bullets.clear()
            }
        }

        fun flushAll() {
            flushParagraph()
            flushQuote()
            flushBullets()
        }

        fun endChapter() {
            flushAll()
            val t = title
            if (t != null) {
                if (blocks.isEmpty()) {
                    if (strict) throw IllegalStateException("Chapter '$t' has no content")
                } else {
                    chapters.add(Chapter(chapters.size, t, ArrayList(blocks)))
                }
            } else if (blocks.isNotEmpty() && strict) {
                throw IllegalStateException("Content found before the first chapter heading")
            }
            blocks = ArrayList()
            title = null
        }

        for (rawLine in raw.lineSequence()) {
            val line = rawLine.trimEnd()
            when {
                line.startsWith("== ") -> {
                    endChapter()
                    title = line.substring(3).trim()
                }
                line.startsWith("## ") -> {
                    flushAll()
                    blocks.add(Block.Heading(line.substring(3).trim()))
                }
                line == ">" || line.startsWith("> ") -> {
                    flushParagraph()
                    flushBullets()
                    if (quote.isNotEmpty()) quote.append('\n')
                    quote.append(line.substring(1).trim())
                }
                line.startsWith("~ ") -> {
                    flushParagraph()
                    flushBullets()
                    val attribution = line.substring(2).trim()
                    if (quote.isNotEmpty()) {
                        flushQuote(attribution)
                    } else {
                        val last = blocks.lastOrNull()
                        if (last is Block.Quote && last.attribution == null) {
                            blocks[blocks.size - 1] = Block.Quote(last.text, attribution)
                        } else {
                            blocks.add(Block.Paragraph(attribution))
                        }
                    }
                }
                line.startsWith("! ") -> {
                    flushAll()
                    blocks.add(Block.Note(line.substring(2).trim()))
                }
                line.startsWith("- ") -> {
                    flushParagraph()
                    flushQuote()
                    bullets.add(line.substring(2).trim())
                }
                line.isBlank() -> flushAll()
                line.startsWith("# ") && title == null && chapters.isEmpty() && blocks.isEmpty() -> {
                    // Book title line: informational only.
                }
                else -> {
                    flushQuote()
                    flushBullets()
                    if (paragraph.isNotEmpty()) paragraph.append(' ')
                    paragraph.append(line.trim())
                }
            }
        }
        endChapter()
        return chapters
    }
}
