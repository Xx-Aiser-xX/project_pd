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
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge


class history : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history)

        val linkToReg: TextView = findViewById(R.id.recognize_button_history)

        linkToReg.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val linkToAuth: TextView = findViewById(R.id.contact_button_history)

        linkToAuth.setOnClickListener{
            val intent = Intent(this, contacts::class.java)
            startActivity(intent)
        }

        val button: Button = findViewById(R.id.button)

        button.setOnClickListener {
            val url = "https://github.com/Sooty001/Pd-project/blob/main/contacts.html"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        val photoUriString = intent.getStringExtra(MainActivity.EXTRA_PHOTO_URI)
        val uploadDate = intent.getStringExtra(MainActivity.EXTRA_UPLOAD_DATE)

        if (photoUriString != null && uploadDate != null) {
            val photoUri = Uri.parse(photoUriString)

            val newEntryLayout = layoutInflater.inflate(R.layout.history_entry, null) as LinearLayout

            val imageView = newEntryLayout.findViewById<ImageView>(R.id.history_image)
            val dateTextView = newEntryLayout.findViewById<TextView>(R.id.history_date)
            val openButton = newEntryLayout.findViewById<Button>(R.id.history_open_button)

            imageView.setImageURI(photoUri)
            dateTextView.text = "Uploaded on: $uploadDate"

            openButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = photoUri
                startActivity(intent)
            }

            val contentListView: LinearLayout = findViewById(R.id.content_list_view_history)
            contentListView.addView(newEntryLayout)
        }

    }
}
//ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//    insets
//}