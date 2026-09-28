package com.mhma.nibras.ui.widget

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path

/**
 * Draws a lattice of eight-point stars — the classic "star and cross"
 * geometry of Islamic art — as thin strokes. Used as a subtle overlay on
 * hero surfaces and book covers.
 */
object IslamicPattern {

    private val path = Path()

    /**
     * @param cell size of one star cell in pixels
     * @param paint stroke paint (colour and alpha decide the intensity)
     */
    fun draw(canvas: Canvas, width: Float, height: Float, cell: Float, paint: Paint) {
        if (cell <= 0f || width <= 0f || height <= 0f) return
        val half = cell / 2f
        val inner = half * 0.72f
        var cy = half
        var row = 0
        while (cy - half < height) {
            val offset = if (row % 2 == 0) 0f else half
            var cx = half + offset
            while (cx - half < width + half) {
                // Diamond (square rotated 45°)
                path.reset()
                path.moveTo(cx, cy - half)
                path.lineTo(cx + half, cy)
                path.lineTo(cx, cy + half)
                path.lineTo(cx - half, cy)
                path.close()
                canvas.drawPath(path, paint)
                // Axis-aligned square
                path.reset()
                path.moveTo(cx - inner, cy - inner)
                path.lineTo(cx + inner, cy - inner)
                path.lineTo(cx + inner, cy + inner)
                path.lineTo(cx - inner, cy + inner)
                path.close()
                canvas.drawPath(path, paint)
                cx += cell
            }
            cy += half
            row++
        }
    }
}
