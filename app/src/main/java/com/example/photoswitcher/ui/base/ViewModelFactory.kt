package com.example.photoswitcher.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.photoswitcher.data.repository.MainRepository
import com.example.photoswitcher.ui.viewmodel.MainViewModel

class ViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(MainRepository()) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}