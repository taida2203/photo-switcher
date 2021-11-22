package com.example.photoswitcher.ui.view

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.photoswitcher.R
import com.example.photoswitcher.utils.Status
import com.example.photoswitcher.ui.base.ViewModelFactory
import com.example.photoswitcher.ui.viewmodel.MainViewModel
import com.example.photoswitcher.utils.FileHelper
import com.example.photoswitcher.utils.PhotoStorages
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

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
    }

    private fun setupObserver() {
        mainViewModel.getPhotos().observe(this, Observer {
            val nextImage = PhotoStorages.getInstance(this).getNextImage()
            if (nextImage.isBlank()) {
                tvNoData.visibility = View.VISIBLE
            }
            progressBar.max = PhotoStorages.PROGRESS_MAX
            PhotoStorages.getInstance(this).downloadImage(nextImage, { progress ->
                if (progress < PhotoStorages.PROGRESS_MAX) {
                    progressBar.progress = progress
                } else {
                    progressBar.visibility = View.GONE
                    val unzipped =
                        FileHelper.unzip(File(baseContext.filesDir.absolutePath + "/" + nextImage))
                    val firstFile = unzipped.firstOrNull()
                    firstFile?.let {
                        val bmp = BitmapFactory.decodeByteArray(it.content, 0, it.content.size)
                        imageView.setImageBitmap(bmp)
                    } ?: run {
                        tvNoData.visibility = View.VISIBLE
                    }
                }
            }, {
                Toast.makeText(this, it, Toast.LENGTH_SHORT)
                    .show()
            })

            when (it.status) {
                Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                }
                Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory()
        ).get(MainViewModel::class.java)
    }

    private fun setupUI() {
        this.progressBar = findViewById(R.id.progressBar)
        this.imageView = findViewById(R.id.imageView)
        this.tvNoData = findViewById(R.id.tvNoData)
    }
}