
package com.math.autotapper2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class MatchResult(val symbol: String, val score: Double, val rect: Rect)

object TemplateMatcher {

    // Load template bitmap from a stored URI (user provided)
    private fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? =
        context.contentResolver.openInputStream(uri)?.use { ins ->
            BitmapFactory.decodeStream(ins)
        }

    // Very basic normalized SAD-based matching (multi-scale)
    fun findSymbols(context: Context, screen: Bitmap, region: Rect): List<MatchResult> {
        val roi = Bitmap.createBitmap(screen, region.left, region.top, region.width(), region.height())
        val candidates = mutableListOf<MatchResult>()

        for (symbol in TemplateStore.listSymbols()) {
            val uri = TemplateStore.loadSymbol(context, symbol) ?: continue
            val templ = loadBitmapFromUri(context, uri) ?: continue
            candidates += matchTemplateMultiScale(roi, templ, symbol).map {
                // shift rect by region offset
                it.copy(rect = Rect(it.rect.left + region.left, it.rect.top + region.top, it.rect.right + region.left, it.rect.bottom + region.top))
            }
        }
        return candidates.sortedByDescending { it.score }
    }

    private fun matchTemplateMultiScale(src: Bitmap, templ: Bitmap, symbol: String): List<MatchResult> {
        val scales = listOf(0.75f, 1.0f, 1.25f)
        val results = mutableListOf<MatchResult>()
        for (s in scales) {
            val tw = max(4, (templ.width * s).toInt())
            val th = max(4, (templ.height * s).toInt())
            val tScaled = Bitmap.createScaledBitmap(templ, tw, th, false)
            results += slideAndScore(src, tScaled, symbol)
        }
        return results
    }

    private fun slideAndScore(src: Bitmap, templ: Bitmap, symbol: String): List<MatchResult> {
        val res = mutableListOf<MatchResult>()
        val step = max(1, min(templ.width, templ.height) / 3) // coarse step to speed up
        val maxX = src.width - templ.width
        val maxY = src.height - templ.height
        if (maxX <= 0 || maxY <= 0) return res

        var bestScore = -1.0
        for (y in 0..maxY step step) {
            for (x in 0..maxX step step) {
                val score = nccScore(src, templ, x, y)
                if (score > 0.85) {
                    res.add(MatchResult(symbol, score, Rect(x, y, x + templ.width, y + templ.height)))
                }
                if (score > bestScore) bestScore = score
            }
        }
        // Keep top-N results
        return res.sortedByDescending { it.score }.take(20)
    }

    // Simplified Normalized Cross Correlation on grayscale binary-like content
    private fun nccScore(src: Bitmap, templ: Bitmap, sx: Int, sy: Int): Double {
        var sumSrc = 0L; var sumTpl = 0L; var sumSrc2 = 0L; var sumTpl2 = 0L; var sumMul = 0L
        val w = templ.width; val h = templ.height
        for (j in 0 until h) {
            for (i in 0 until w) {
                val cS = src.getPixel(sx + i, sy + j) and 0xFF
                val cT = templ.getPixel(i, j) and 0xFF
                sumSrc += cS; sumTpl += cT
                sumSrc2 += cS * cS; sumTpl2 += cT * cT
                sumMul += cS * cT
            }
        }
        val n = (w * h).toDouble()
        val num = sumMul - (sumSrc * sumTpl) / n
        val denLeft = sumSrc2 - (sumSrc * sumSrc) / n
        val denRight = sumTpl2 - (sumTpl * sumTpl) / n
        val den = kotlin.math.sqrt(denLeft * denRight)
        return if (den <= 1e-9) 0.0 else num / den
    }
}
