package com.example.pd_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pd_project.R
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.app.Activity
import android.provider.MediaStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_SELECT_PHOTO = 1
        const val EXTRA_PHOTO_URI = "EXTRA_PHOTO_URI"
        const val EXTRA_UPLOAD_DATE = "EXTRA_UPLOAD_DATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val linkToAuth: TextView = findViewById(R.id.contact_button)

        linkToAuth.setOnClickListener {
            val intentC = Intent(this, contacts::class.java)
            startActivity(intentC)
        }

        val GoHistory: TextView = findViewById(R.id.history_button)

        GoHistory.setOnClickListener {
            val intentH = Intent(this, history::class.java)
            startActivity(intentH)
        }

        val button: Button = findViewById(R.id.button)

        button.setOnClickListener {
            val url = "https://github.com/Sooty001/Pd-project/blob/main/contacts.html"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        val headerTitleTextView: TextView = findViewById(R.id.header_title)
        val headerTitleText: String = headerTitleTextView.text.toString()
        val spannableString = SpannableString(headerTitleText)
        val startIndex = headerTitleText.indexOf("Пером")

        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.orange)),
            startIndex,
            startIndex + "Пером".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        headerTitleTextView.text = spannableString



        val loadButton: Button = findViewById(R.id.load_button)

        loadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_SELECT_PHOTO)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_PHOTO && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                val uploadDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                val intent = Intent(this, history::class.java).apply {
                    putExtra(EXTRA_PHOTO_URI, selectedImageUri.toString())
                    putExtra(EXTRA_UPLOAD_DATE, uploadDate)
                }
                startActivity(intent)
            }
        }
    }
}
