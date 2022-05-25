package com.example.tabexample.ui.main

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class PageViewModel : ViewModel() {

    val description = MutableLiveData<String>()
    val imageSrc = MutableLiveData<Int>()
}