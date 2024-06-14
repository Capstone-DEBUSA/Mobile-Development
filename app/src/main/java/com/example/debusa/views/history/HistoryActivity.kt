package com.example.debusa.views.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.debusa.R
import com.example.debusa.data.local.entity.HistoryAnalyzeEntity
import com.example.debusa.databinding.ActivityHistoryBinding
import com.example.debusa.views.ViewModelFactory
import com.example.debusa.views.adapter.HistoryListAdapter
import com.example.debusa.views.detailhistory.DetailHistoryActivity
import com.example.debusa.views.result.ResultViewModel
import com.google.android.material.appbar.MaterialToolbar

class HistoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryListAdapter
    private val viewModel by viewModels<ResultViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<MaterialToolbar>(R.id.menuActivity)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        adapter = HistoryListAdapter(object : HistoryListAdapter.OnItemClickListener {
            override fun onItemClick(
                item: HistoryAnalyzeEntity,
                sharedElements: List<Pair<View, String>>
            ) {
                val intent = Intent(this@HistoryActivity, DetailHistoryActivity::class.java)
                intent.putExtra("ID", item.id)
                intent.putExtra("IMAGE", item.image)
                intent.putExtra("NAME", item.result)
                intent.putExtra("MANFAAT", item.manfaat)
                intent.putExtra("DESC", item.deskripsi)
                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@HistoryActivity,
                    *sharedElements.map { androidx.core.util.Pair.create(it.first, it.second) }.toTypedArray()
                )
                startActivity(intent, optionsCompat.toBundle())
            }

        })
        binding.rvHistory.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        binding.rvHistory.layoutManager = layoutManager

        binding.fabDelete.setOnClickListener {
            viewModel.deleteHistory()
        }

        viewModel.getAllHistoryAnalyze().observe(this){historylist ->
            val history = arrayListOf<HistoryAnalyzeEntity>()
            historylist.map {
                val historyAnalyze = HistoryAnalyzeEntity(result = it.result, deskripsi = it.deskripsi, manfaat = it.manfaat, image = it.image)
                history.add(historyAnalyze)
            }
            history.reverse()
            adapter.submitList(history)
        }
    }
}