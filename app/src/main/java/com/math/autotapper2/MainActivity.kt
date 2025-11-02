package com.math.autotapper2

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private var roi: Rect = Rect()

    private val pickRoiLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            roi = ROIStore.load(this)
            tvStatus.text = "ROI: (${roi.left},${roi.top})-(${roi.right},${roi.bottom})"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        roi = ROIStore.load(this)
        tvStatus.text = "ROI: (${roi.left},${roi.top})-(${roi.right},${roi.bottom})"

        findViewById<Button>(R.id.btnUploadSymbols).setOnClickListener {
            startActivity(Intent(this, SymbolUploaderActivity::class.java))
        }

        findViewById<Button>(R.id.btnPickRoi).setOnClickListener {
            pickRoiLauncher.launch(Intent(this, PickRoiActivity::class.java))
        }

        findViewById<Button>(R.id.btnStartAnalyze).setOnClickListener {
            // هنا مكان دمج التقاط الشاشة والتحليل.. مثال افتراضي:
            val expr = "76+57"
            val result = MathEngine.evaluate(expr)
            tvStatus.text = "Expr: $expr = $result\nROI: (${roi.left},${roi.top})-(${roi.right},${roi.bottom})"
        }
    }
}
