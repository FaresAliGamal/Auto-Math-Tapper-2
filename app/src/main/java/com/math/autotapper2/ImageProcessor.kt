
package com.math.autotapper2

import android.graphics.Bitmap
import android.graphics.Color

object ImageProcessor {

    // Convert to grayscale
    fun toGrayscale(src: Bitmap): Bitmap {
        val out = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        for (y in 0 until src.height) {
            for (x in 0 until src.width) {
                val c = src.getPixel(x, y)
                val g = ((Color.red(c) + Color.green(c) + Color.blue(c)) / 3)
                out.setPixel(x, y, Color.rgb(g, g, g))
            }
        }
        return out
    }

    // Threshold keeping bright (white) digits
    fun thresholdWhite(srcGray: Bitmap, thr: Int = 200): Bitmap {
        val out = Bitmap.createBitmap(srcGray.width, srcGray.height, Bitmap.Config.ARGB_8888)
        for (y in 0 until srcGray.height) {
            for (x in 0 until srcGray.width) {
                val c = srcGray.getPixel(x, y)
                val g = Color.red(c)
                val v = if (g >= thr) 255 else 0
                out.setPixel(x, y, Color.rgb(v, v, v))
            }
        }
        return out
    }

}
