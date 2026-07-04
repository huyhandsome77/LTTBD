package com.example.appdatmon.ui.User

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.appdatmon.R
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class ScanQRActivity : AppCompatActivity() {

    private lateinit var barcodeView: DecoratedBarcodeView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_scan_qr)

        barcodeView = findViewById(R.id.barcodeScanner)

        // Kiểm tra quyền Camera
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                100
            )

        } else {

            startScanner()

        }
    }

    private fun startScanner() {

        barcodeView.decodeContinuous(callback)

    }

    private val callback = object : BarcodeCallback {

        override fun barcodeResult(result: BarcodeResult?) {

            val qrToken = result?.text ?: return

            val intent = Intent(this@ScanQRActivity, MenuActivity::class.java)
            intent.putExtra("QR_CODE", qrToken)
            startActivity(intent)

            finish()

        }

        override fun possibleResultPoints(
            resultPoints: MutableList<ResultPoint>?
        ) {
        }
    }

    override fun onResume() {

        super.onResume()

        if (::barcodeView.isInitialized) {
            barcodeView.resume()
        }
    }

    override fun onPause() {

        super.onPause()

        if (::barcodeView.isInitialized) {
            barcodeView.pause()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (
            requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {

            startScanner()

        } else {

            finish()

        }
    }
}
