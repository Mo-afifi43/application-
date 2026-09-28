package com.mhma.nibras.ui

import com.mhma.nibras.R

/** Maps the icon keys used by the content catalogue to drawable resources. */
object Icons {
    fun forKey(key: String): Int = when (key) {
        "book_open" -> R.drawable.ic_book_open
        "scroll" -> R.drawable.ic_scroll
        "kaaba" -> R.drawable.ic_kaaba
        "crescent" -> R.drawable.ic_crescent
        "people" -> R.drawable.ic_people
        "tasbih" -> R.drawable.ic_tasbih
        "mosque" -> R.drawable.ic_mosque
        "history" -> R.drawable.ic_history
        "star8" -> R.drawable.ic_star8
        "sparkle" -> R.drawable.ic_sparkle
        "quote" -> R.drawable.ic_quote
        "favorite" -> R.drawable.ic_favorite
        "lantern" -> R.drawable.ic_lantern
        else -> R.drawable.ic_menu_book
    }

    /** Start and end colours of the eight cover palettes. */
    val palettes: List<IntArray> = listOf(
        intArrayOf(R.color.cover_0_a, R.color.cover_0_b),
        intArrayOf(R.color.cover_1_a, R.color.cover_1_b),
        intArrayOf(R.color.cover_2_a, R.color.cover_2_b),
        intArrayOf(R.color.cover_3_a, R.color.cover_3_b),
        intArrayOf(R.color.cover_4_a, R.color.cover_4_b),
        intArrayOf(R.color.cover_5_a, R.color.cover_5_b),
        intArrayOf(R.color.cover_6_a, R.color.cover_6_b),
        intArrayOf(R.color.cover_7_a, R.color.cover_7_b)
    )

    fun palette(index: Int): IntArray = palettes[((index % palettes.size) + palettes.size) % palettes.size]
}
