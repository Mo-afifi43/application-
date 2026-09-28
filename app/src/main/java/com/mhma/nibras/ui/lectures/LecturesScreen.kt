package com.mhma.nibras.ui.lectures

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.mhma.nibras.R
import com.mhma.nibras.content.Lang
import com.mhma.nibras.content.LectureLang
import com.mhma.nibras.content.Lectures
import com.mhma.nibras.core.LocaleHelper
import com.mhma.nibras.core.padSystemBars
import com.mhma.nibras.ui.Binders
import com.mhma.nibras.ui.MainActivity
import com.mhma.nibras.ui.Screen

class LecturesScreen(activity: MainActivity, container: ViewGroup) : Screen(activity) {

    override val root: View =
        LayoutInflater.from(activity).inflate(R.layout.screen_lectures, container, false)

    private val lang: Lang = LocaleHelper.currentLang(activity)
    private val inflater = LayoutInflater.from(activity)
    private val chipsRow: LinearLayout = root.findViewById(R.id.lectures_chips)
    private val list: LinearLayout = root.findViewById(R.id.lectures_list)
    private val chips = ArrayList<Pair<LectureLang?, TextView>>()
    private var filter: LectureLang? = null

    init {
        root.findViewById<View>(R.id.lectures_header).padSystemBars(top = true)
        addChip(null, activity.getString(R.string.chip_all))
        // Show the user's own language first.
        if (lang == Lang.AR) {
            addChip(LectureLang.AR, activity.getString(R.string.chip_arabic))
            addChip(LectureLang.EN, activity.getString(R.string.chip_english))
        } else {
            addChip(LectureLang.EN, activity.getString(R.string.chip_english))
            addChip(LectureLang.AR, activity.getString(R.string.chip_arabic))
        }
        addChip(LectureLang.RECITATION, activity.getString(R.string.chip_recitation))
        updateChips()
        render()
    }

    private fun addChip(id: LectureLang?, label: String) {
        val chip = inflater.inflate(R.layout.item_chip, chipsRow, false) as TextView
        chip.text = label
        chip.setOnClickListener {
            filter = id
            updateChips()
            render()
        }
        chipsRow.addView(chip)
        chips.add(id to chip)
    }

    private fun updateChips() {
        for ((id, chip) in chips) chip.isSelected = id == filter
    }

    private fun render() {
        list.removeAllViews()
        val items = Lectures.items.filter { filter == null || it.lang == filter }
        val ordered = if (lang == Lang.AR) {
            items.sortedBy { if (it.lang == LectureLang.AR) 0 else 1 }
        } else items
        for (lecture in ordered) {
            val item = inflater.inflate(R.layout.item_lecture, list, false)
            Binders.bindLecture(item, lecture, lang)
            list.addView(item)
        }
    }
}
