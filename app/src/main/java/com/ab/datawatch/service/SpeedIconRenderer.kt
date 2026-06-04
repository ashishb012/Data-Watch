package com.ab.datawatch.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import com.ab.datawatch.data.model.SpeedUnit
import com.ab.datawatch.util.FormatUtils
import kotlin.math.roundToInt

object SpeedIconRenderer {
    fun createSpeedBitmap(context: Context, speedBytesPerSec: Long, unit: SpeedUnit): Bitmap {
        val size = 96 // Default status bar icon size in pixels

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }

        val speedText = FormatUtils.formatSpeed(speedBytesPerSec, unit, showDecimals = false)
        val parts = speedText.split(" ")
        
        if (parts.size >= 2) {
            val value = parts[0]
            val unitStr = parts[1]

            // Top line: Value
            paint.textSize = size * 0.55f
            val xPos = (canvas.width / 2).toFloat()
            val yPos1 = (canvas.height / 2) - ((paint.descent() + paint.ascent()) / 2) - (size * 0.2f)
            canvas.drawText(value, xPos, yPos1, paint)

            // Bottom line: Unit
            paint.textSize = size * 0.35f
            val yPos2 = (canvas.height / 2) - ((paint.descent() + paint.ascent()) / 2) + (size * 0.3f)
            canvas.drawText(unitStr, xPos, yPos2, paint)
        } else {
            paint.textSize = size * 0.45f
            val xPos = (canvas.width / 2).toFloat()
            val yPos = (canvas.height / 2) - ((paint.descent() + paint.ascent()) / 2)
            canvas.drawText(speedText, xPos, yPos, paint)
        }

        return bitmap
    }
}
