package com.example.hifive.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val mIsLoading = MutableStateFlow(true)
    val isLoading = mIsLoading.asStateFlow()

    init {
        viewModelScope.launch {
            mIsLoading.value = false
        }
    }
}