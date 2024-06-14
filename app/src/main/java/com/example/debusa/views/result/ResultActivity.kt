package com.example.debusa.views.result

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.debusa.R
import com.example.debusa.databinding.ActivityResultBinding
import com.example.debusa.views.ViewModelFactory
import com.example.debusa.views.history.HistoryActivity
import com.google.android.material.appbar.MaterialToolbar

class ResultActivity : AppCompatActivity() {
    lateinit var binding: ActivityResultBinding
    private val viewModel by viewModels<ResultViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<MaterialToolbar>(R.id.menuActivity)

        toolbar.setNavigationOnClickListener {
            finish()
        }


        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        val result = intent.getStringExtra("HASIL")
        val desc = intent.getStringExtra("DESC")
        val manfaat = intent.getStringExtra("MANFAAT")
        val url = intent.getStringExtra("URL")
        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            Glide.with(binding.root)
                .load(it)
                .apply(RequestOptions().transform(RoundedCorners(70)))
                .into(binding.resultImage)
        }

        binding.resultText.text = result

        binding.deskripsi.text = desc

        binding.manfaat.text = manfaat

        binding.search.setOnClickListener { gotoWiki(url.toString()) }

        binding.btnSave.setOnClickListener {
            viewModel.saveAnalyze(
                result = result.toString(),
                deskripsi = desc.toString(),
                manfaat = manfaat.toString(),
                imageUri = imageUri.toString()
            )
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

    }

    private fun gotoWiki(url:String){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }


    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}