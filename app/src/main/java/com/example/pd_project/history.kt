package com.example.pd_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            val url = "https://github.com/Xx-Aiser-xX/project_pd"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        for (i in 0..3) {
            val historyImageView: ImageView = findViewById(
                resources.getIdentifier("history_image_${i+1}", "id", packageName)
            )
            val historyDate: TextView = findViewById(
                resources.getIdentifier("history_date_${i+1}", "id", packageName)
            )
            val savedImagePath = getSavedImagePath(i)
            if (savedImagePath != null) {
                val imageFile = File(savedImagePath)
                if (imageFile.exists()) {
                    val imageUri = Uri.fromFile(imageFile)
                    historyImageView.setImageURI(imageUri)
                }
            }
            val timestampMillis = getSavedImageTimestamp(i)
            if (timestampMillis != -1L) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val dateTime = dateFormat.format(Date(timestampMillis))
                historyDate.text = "Загружено: $dateTime"
            }
        }
    }

    private fun getSavedImagePath(index: Int): String? {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("IMAGE_PATH_$index", null)
    }

    private fun getSavedImageTimestamp(index: Int): Long {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("IMAGE_TIMESTAMP_$index", -1)
    }
    private fun setImageViewWithImage(imageView: ImageView, imagePath: String) {
        val imageFile = File(imagePath)
        if (imageFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true)
            imageView.setImageBitmap(scaledBitmap)
        }
    }

}
