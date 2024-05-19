package com.example.pd_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class contacts : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contacts)

        val linkToReg: TextView = findViewById(R.id.recognize_button_contacts)

        linkToReg.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val GoHistory: TextView = findViewById(R.id.history_button_contacts)

        GoHistory.setOnClickListener{
            val intent = Intent(this, history::class.java)
            startActivity(intent)
        }

        val button: Button = findViewById(R.id.button)

        button.setOnClickListener {
            val url = "https://github.com/Xx-Aiser-xX/project_pd"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

    }
}