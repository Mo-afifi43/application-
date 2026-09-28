package com.mhma.nibras.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import com.mhma.nibras.content.Lang
import com.mhma.nibras.core.dp
import com.mhma.nibras.core.headingFont
import com.mhma.nibras.ui.Icons
import kotlin.math.max
import kotlin.math.min

/**
 * Procedurally drawn book cover: a two-tone gradient, the geometric
 * lattice, a glyph for the category, a spine on the opening edge and the
 * title set in the display face of the book's language.
 */
class CoverView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var title: String = ""
    private var lang: Lang = Lang.EN
    private var colorA: Int = Color.parseColor("#0E5A48")
    private var colorB: Int = Color.parseColor("#1E9678")
    private var icon: Drawable? = null

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val patternPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = context.dp(1f)
        color = Color.WHITE
        alpha = 34
    }
    private val spinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        alpha = 46
    }
    private val accentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E0B84F")
    }
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
    }
    private val clip = Path()
    private val rect = RectF()
    private var titleLayout: StaticLayout? = null
    private var layoutWidth = 0

    init {
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                val r = min(view.width, view.height) * 0.09f
                outline.setRoundRect(0, 0, view.width, view.height, r)
            }
        }
    }

    fun bind(title: String, lang: Lang, palette: Int, iconKey: String) {
        this.title = title
        this.lang = lang
        val colors = Icons.palette(palette)
        colorA = context.getColor(colors[0])
        colorB = context.getColor(colors[1])
        icon = context.getDrawable(Icons.forKey(iconKey))?.mutate()?.apply {
            setTint(Color.WHITE)
            alpha = 225
        }
        textPaint.typeface = context.headingFont(lang)
        titleLayout = null
        fillPaint.shader = null
        contentDescription = title
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        fillPaint.shader = null
        titleLayout = null
    }

    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return
        val radius = min(w, h) * 0.09f

        if (fillPaint.shader == null) {
            fillPaint.shader = LinearGradient(0f, 0f, w, h, colorA, colorB, Shader.TileMode.CLAMP)
            glowPaint.shader = RadialGradient(
                w * 0.2f, h * 0.1f, w * 0.9f,
                Color.argb(70, 255, 255, 255), Color.TRANSPARENT, Shader.TileMode.CLAMP
            )
        }

        rect.set(0f, 0f, w, h)
        clip.reset()
        clip.addRoundRect(rect, radius, radius, Path.Direction.CW)
        canvas.save()
        canvas.clipPath(clip)

        canvas.drawRect(rect, fillPaint)
        IslamicPattern.draw(canvas, w, h, w * 0.42f, patternPaint)
        canvas.drawRect(rect, glowPaint)

        // Spine on the opening edge: left for English, right for Arabic.
        val spine = max(context.dp(3f), w * 0.035f)
        if (lang.isRtl) canvas.drawRect(w - spine, 0f, w, h, spinePaint)
        else canvas.drawRect(0f, 0f, spine, h, spinePaint)

        // Glyph
        val pad = w * 0.11f
        val iconSize = (w * 0.24f).toInt()
        icon?.let {
            val left = if (lang.isRtl) (w - pad - iconSize).toInt() else pad.toInt()
            val top = (h * 0.09f).toInt()
            it.setBounds(left, top, left + iconSize, top + iconSize)
            it.draw(canvas)
        }

        // Title
        val textWidth = (w - pad * 2).toInt()
        if (titleLayout == null || layoutWidth != textWidth) {
            textPaint.textSize = max(context.dp(10f), w * 0.115f)
            val available = h - h * 0.44f - pad * 0.8f
            val lineHeight = textPaint.fontMetrics.let { it.descent - it.ascent }
            val maxLines = max(1, (available / lineHeight).toInt())
            titleLayout = StaticLayout.Builder.obtain(title, 0, title.length, textPaint, max(1, textWidth))
                .setAlignment(if (lang.isRtl) Layout.Alignment.ALIGN_NORMAL else Layout.Alignment.ALIGN_NORMAL)
                .setTextDirection(
                    if (lang.isRtl) android.text.TextDirectionHeuristics.RTL
                    else android.text.TextDirectionHeuristics.LTR
                )
                .setMaxLines(maxLines)
                .setEllipsize(TextUtils.TruncateAt.END)
                .setIncludePad(false)
                .build()
            layoutWidth = textWidth
        }
        titleLayout?.let { layout ->
            val ornamentY = h * 0.44f
            val ornamentW = w * 0.22f
            val ornamentH = max(context.dp(2f), h * 0.012f)
            if (lang.isRtl) canvas.drawRect(w - pad - ornamentW, ornamentY, w - pad, ornamentY + ornamentH, accentPaint)
            else canvas.drawRect(pad, ornamentY, pad + ornamentW, ornamentY + ornamentH, accentPaint)
            canvas.save()
            canvas.translate(pad, ornamentY + ornamentH + pad * 0.5f)
            layout.draw(canvas)
            canvas.restore()
        }
        canvas.restore()
    }
}
