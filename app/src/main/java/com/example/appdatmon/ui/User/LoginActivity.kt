package com.example.futuresushi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.view.LayoutInflater

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        // LOGIN

        val btnLogin =
            findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {

            startActivity(
                Intent(this, MainActivity::class.java)
            )

            finish()

        }

        // SKIP

        val txtSkip =
            findViewById<TextView>(R.id.txtSkip)

        txtSkip.setOnClickListener {

            startActivity(
                Intent(this, MainActivity::class.java)
            )

            finish()

        }

        // register

        val txtRegister =
            findViewById<TextView>(R.id.txtRegister)

        txtRegister.setOnClickListener {

            val dialog = BottomSheetDialog(this)

            val view = LayoutInflater.from(this)
                .inflate(R.layout.bottom_register, null)

            dialog.setContentView(view)

            dialog.show()

        }

    }

}