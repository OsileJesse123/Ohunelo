package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SharedViewModel @Inject constructor(): ViewModel() {

    private val _shouldKeepShowingSplashScreen: MutableSharedFlow<Boolean> = MutableStateFlow(true)
    val shouldKeepShowingSplashScreen get() = _shouldKeepShowingSplashScreen.asSharedFlow()

    fun stopShowingSplashScreen(){
        viewModelScope.launch {
            _shouldKeepShowingSplashScreen.emit(false)
        }
    }
}