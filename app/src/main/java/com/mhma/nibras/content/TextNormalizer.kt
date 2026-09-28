package com.mhma.nibras.content

/**
 * Normalises text for searching: lower-cases Latin letters, removes Arabic
 * diacritics (tashkeel) and the tatweel, and folds the common letter
 * variants (أ إ آ → ا, ى → ي, ة → ه) so a query matches regardless of how the
 * word was spelled.
 *
 * Every transformation keeps the string length unchanged except for removed
 * diacritics, which is why it is applied to both the query and the text.
 */
object TextNormalizer {

    fun normalize(input: String): String {
        val sb = StringBuilder(input.length)
        for (ch in input) {
            when {
                isDiacritic(ch) -> Unit
                ch == 'أ' || ch == 'إ' || ch == 'آ' || ch == 'ٱ' -> sb.append('ا') // أ إ آ ٱ -> ا
                ch == 'ى' -> sb.append('ي') // ى -> ي
                ch == 'ة' -> sb.append('ه') // ة -> ه
                ch == 'ـ' -> Unit // tatweel
                else -> sb.append(ch.lowercaseChar())
            }
        }
        return sb.toString()
    }

    fun containsArabic(text: String): Boolean = text.any { it in '؀'..'ۿ' }

    private fun isDiacritic(ch: Char): Boolean =
        ch in 'ً'..'ٟ' || ch == 'ٰ' || ch in 'ۖ'..'ۭ'
}
