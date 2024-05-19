package com.example.pd_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.WindowManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File

class MainActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var currentPhotoPath: String
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
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
            val url = "https://github.com/Xx-Aiser-xX/project_pd"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        val loadButton: Button = findViewById(R.id.load_button)
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        loadButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        // Получаем текст из TextView
        val headerTitleTextView: TextView = findViewById(R.id.header_title)
        val headerTitleText: String = headerTitleTextView.text.toString()

        // Создаем SpannableString для изменения цвета
        val spannableString = SpannableString(headerTitleText)

        // Индекс, с которого начинается слово "Пером"
        val startIndex = headerTitleText.indexOf("Пером")

        // Устанавливаем красный цвет для слова "Пером"
        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.orange)),
            startIndex,
            startIndex + "Пером".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Устанавливаем измененный текст в TextView
        headerTitleTextView.text = spannableString
    }

    private fun dispatchTakePictureIntent() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                saveImageToAppDirectory(imageUri)
            } else {
                Toast.makeText(this, "Failed to retrieve image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageToAppDirectory(imageUri: Uri) {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(
            "JPEG_${timeStamp}_", ".jpg", storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
        contentResolver.openInputStream(imageUri).use { inputStream ->
            imageFile.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
        }
        saveImagePathAndTimestamp(currentPhotoPath, System.currentTimeMillis())
    }

    private fun saveImagePathAndTimestamp(path: String, timestampMillis: Long) {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val currentIndex = sharedPreferences.getInt("HISTORY_INDEX", 0)
        val editor = sharedPreferences.edit()
        editor.putString("IMAGE_PATH_$currentIndex", path)
        editor.putLong("IMAGE_TIMESTAMP_$currentIndex", timestampMillis)
        val nextIndex = (currentIndex + 1) % 4
        editor.putInt("HISTORY_INDEX", nextIndex)
        editor.apply()
    }
}
