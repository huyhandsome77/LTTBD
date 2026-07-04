package com.example.appdatmon.ui.User

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.appdatmon.R

class ChefActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chef)

        findViewById<ImageView>(R.id.btnBackChef)
            .setOnClickListener {

                finish()

            }
    }

}
