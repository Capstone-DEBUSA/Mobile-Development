package com.example.debusa.views.detailhistory

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.debusa.R
import com.example.debusa.databinding.ActivityDetailHistoryBinding

class DetailHistoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var name = intent.getStringExtra("NAME")
        var deskripsi = intent.getStringExtra("DESC")
        var image = intent.getStringExtra("IMAGE")
        var manfaat = intent.getStringExtra("MANFAAT")

        binding.name.text = name
        binding.deskripsi.text = deskripsi
        binding.mandaat.text = manfaat

        Glide.with(this)
            .load(image)
            .into(binding.imageDetail)

    }
}