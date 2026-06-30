package com.example.futuresushi

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        // BACK

        val btnBack =
            findViewById<ImageView>(R.id.btnBackProfile)

        btnBack.setOnClickListener {

            finish()

        }

        // Edit Profile

        val btnEditProfile =
            findViewById<LinearLayout>(R.id.btnEditProfile)

        btnEditProfile.setOnClickListener {

            val dialog = BottomSheetDialog(this)

            val view = LayoutInflater.from(this)
                .inflate(R.layout.bottom_edit_profile, null)

            dialog.setContentView(view)

            dialog.show()

        }

    }

}