package com.mhma.nibras.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.mhma.nibras.R
import com.mhma.nibras.core.dp

/**
 * A decorative view that paints the [IslamicPattern] lattice. Place it on
 * top of a gradient to give hero surfaces their signature texture.
 *
 * XML attributes: `patternColor`, `patternAlpha` (0..1) and `patternCell`.
 */
class PatternView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = context.dp(1f)
        strokeJoin = Paint.Join.ROUND
    }

    private var cell: Float = context.dp(56f)

    init {
        var color = Color.WHITE
        var alpha = 0.12f
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.PatternView)
            try {
                color = a.getColor(R.styleable.PatternView_patternColor, color)
                alpha = a.getFloat(R.styleable.PatternView_patternAlpha, alpha)
                cell = a.getDimension(R.styleable.PatternView_patternCell, cell)
            } finally {
                a.recycle()
            }
        }
        paint.color = color
        paint.alpha = (alpha.coerceIn(0f, 1f) * 255).toInt()
    }

    fun setPattern(color: Int, alpha: Float, cellPx: Float) {
        paint.color = color
        paint.alpha = (alpha.coerceIn(0f, 1f) * 255).toInt()
        cell = cellPx
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        IslamicPattern.draw(canvas, width.toFloat(), height.toFloat(), cell, paint)
    }
}
