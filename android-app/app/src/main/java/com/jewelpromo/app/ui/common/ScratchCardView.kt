package com.jewelpromo.app.ui.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.max

class ScratchCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    private lateinit var overlayBitmap: Bitmap
    private lateinit var overlayCanvas: Canvas

    private val overlayPaint = Paint().apply {
        color = Color.parseColor("#C0C0C0")
        style = Paint.Style.FILL
    }

    private val scratchPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 70f
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private var scratchPath = Path()
    private var revealed = false
    private var onRevealListener: (() -> Unit)? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            overlayBitmap = Bitmap.createBitmap(max(1, w), max(1, h), Bitmap.Config.ARGB_8888)
            overlayCanvas = Canvas(overlayBitmap)
            resetScratch()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (::overlayBitmap.isInitialized) {
            canvas.drawBitmap(overlayBitmap, 0f, 0f, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (revealed || !::overlayBitmap.isInitialized) return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                scratchPath.moveTo(event.x, event.y)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                scratchPath.lineTo(event.x, event.y)
                overlayCanvas.drawPath(scratchPath, scratchPaint)
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                revealFully()
            }
        }
        return true
    }

    fun setOnRevealListener(listener: () -> Unit) {
        onRevealListener = listener
    }

    fun resetScratch() {
        if (!::overlayBitmap.isInitialized) return
        overlayCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        overlayCanvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)
        scratchPath = Path()
        revealed = false
        invalidate()
    }

    private fun revealFully() {
        overlayCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        revealed = true
        invalidate()
        onRevealListener?.invoke()
    }
}
