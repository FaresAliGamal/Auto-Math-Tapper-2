
package com.math.autotapper2

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private var roi: Rect = Rect(100, 200, 1000, 600) // simple default ROI; could be edited in OverlayView later

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)

        findViewById<Button>(R.id.btnUploadSymbols).setOnClickListener {
            startActivity(Intent(this, SymbolUploaderActivity::class.java))
        }

        findViewById<Button>(R.id.btnPickRoi).setOnClickListener {
            // Placeholder for a proper overlay-based ROI picker.
            Toast.makeText(this, "ROI picker not implemented (using default).", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnStartAnalyze).setOnClickListener {
            tvStatus.text = "Analyzing screen ROI..."
            // Here you would actually capture the screen via MediaProjection/Accessibility.
            // For MVP, this is a placeholder demonstrating the flow.
            // You can integrate real capture later.

            // After capture+process, pretend we detected expression "76+57"
            val expr = "76+57"
            val result = MathEngine.evaluate(expr)
            tvStatus.text = "Expr: $expr = $result"

            // Auto tap service would locate the result on screen and tap it.
            // AutoTapService.performTapAt( ... ) would be called after OCR/matching.
        }
    }
}
