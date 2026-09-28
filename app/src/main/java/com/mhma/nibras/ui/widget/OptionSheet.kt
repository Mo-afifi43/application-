package com.mhma.nibras.ui.widget

import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.mhma.nibras.R
import com.mhma.nibras.core.padSystemBars

class Option(val title: String, val subtitle: String?, val iconRes: Int)

/** A bottom sheet with a single-choice list. */
object OptionSheet {

    fun show(
        activity: Activity,
        title: String,
        options: List<Option>,
        selectedIndex: Int,
        onSelect: (Int) -> Unit
    ) {
        val dialog = Dialog(activity, R.style.Theme_Nibras_Sheet)
        val inflater = LayoutInflater.from(dialog.context)
        val view = inflater.inflate(R.layout.sheet_options, null, false)
        view.findViewById<TextView>(R.id.sheet_title).text = title
        val container = view.findViewById<LinearLayout>(R.id.sheet_options)
        for ((index, option) in options.withIndex()) {
            val row = inflater.inflate(R.layout.item_option, container, false)
            row.findViewById<ImageView>(R.id.option_icon).setImageResource(option.iconRes)
            row.findViewById<TextView>(R.id.option_title).text = option.title
            val subtitle = row.findViewById<TextView>(R.id.option_subtitle)
            if (option.subtitle.isNullOrEmpty()) {
                subtitle.visibility = View.GONE
            } else {
                subtitle.visibility = View.VISIBLE
                subtitle.text = option.subtitle
            }
            row.findViewById<View>(R.id.option_check).visibility =
                if (index == selectedIndex) View.VISIBLE else View.INVISIBLE
            row.setOnClickListener {
                dialog.dismiss()
                onSelect(index)
            }
            container.addView(row)
        }
        view.padSystemBars(bottom = true, sides = false)
        dialog.setContentView(view)
        dialog.window?.let { window ->
            window.setBackgroundDrawable(ColorDrawable(0))
            window.setGravity(Gravity.BOTTOM)
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        dialog.show()
    }
}
