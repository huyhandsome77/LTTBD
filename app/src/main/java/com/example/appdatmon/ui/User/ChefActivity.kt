package com.example.appdatmon

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

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
