package com.andlife.invitation_card.editor.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan
import androidx.core.graphics.withSave

class CenteredImageSpan(
    private val drawable: Drawable,
    private val containerWidth: Int,
    val imageSource: String,
) : ImageSpan(drawable) {

    override fun getSize(
        paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?
    ): Int {
        val rect = drawable.bounds

        if (fm != null) {
            fm.ascent = -rect.height()
            fm.descent = 0
            fm.top = fm.ascent
            fm.bottom = 0
        }

        return containerWidth
    }

    override fun draw(
        canvas: Canvas, text: CharSequence?, start: Int, end: Int,
        x: Float, top: Int, y: Int, bottom: Int, paint: Paint
    ) {
        canvas.withSave {

            val imageWidth = drawable.bounds.width()
            val imageHeight = drawable.bounds.height()

            val centerX = (containerWidth - imageWidth) / 2f
            val transY = y - imageHeight.toFloat()

            translate(centerX, transY)
            drawable.draw(this)
        }
    }
}
