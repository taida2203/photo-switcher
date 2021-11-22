package com.example.photoswitcher.ui.view

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.photoswitcher.R
import com.example.photoswitcher.ui.base.ViewModelFactory
import com.example.photoswitcher.ui.viewmodel.MainViewModel
import com.example.photoswitcher.ui.viewmodel.MainViewModel.Companion.PROGRESS_MAX
import com.example.photoswitcher.utils.FileHelper
import com.example.photoswitcher.utils.PrefHelper
import com.example.photoswitcher.utils.Status

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel

    private lateinit var progressBar: ProgressBar
    private lateinit var imageView: ImageView
    private lateinit var tvNoData: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUI()
        setupViewModel()
        setupObserver()

        mainViewModel.fetchPhotos(this)
    }

    private fun setupObserver() {
        mainViewModel.getPhotos().observe(this, Observer {
            progressBar.max = PROGRESS_MAX
            when (it.status) {
                Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    Glide.with(this).load(it.data).into(imageView)
                }
                Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    progressBar.visibility = View.GONE
                    tvNoData.visibility = View.VISIBLE
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(PrefHelper.getInstance(this), FileHelper.getInstance(this))
        ).get(MainViewModel::class.java)
    }

    private fun setupUI() {
        this.progressBar = findViewById(R.id.progressBar)
        this.imageView = findViewById(R.id.imageView)
        this.tvNoData = findViewById(R.id.tvNoData)
    }
}