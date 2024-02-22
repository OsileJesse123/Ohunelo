package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.data.model.AuthUser
import com.jesse.ohunelo.data.repository.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
): ViewModel() {


    private val _user: MutableStateFlow<AuthUser?> = MutableStateFlow(null)
    val user = _user.asStateFlow()

    init {
        updateProfile()
    }
    private fun updateProfile(){
        viewModelScope.launch {
            _user.update {
                authenticationRepository.getUser()
            }
        }
    }

    fun logout(){
        viewModelScope.launch {
            authenticationRepository.logout()
        }
    }
}