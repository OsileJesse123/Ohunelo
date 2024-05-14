package com.jesse.ohunelo.util

import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PermissionStatus{

    object Granted: PermissionStatus

    class Denied(val shouldShowRationale: Boolean): PermissionStatus
}

class PermissionRequest (
    private val fragment: Fragment,
    private val permission: String
){

    private val _status: MutableStateFlow<PermissionStatus?> = MutableStateFlow<PermissionStatus?>(null).also {
        status ->
        fragment.lifecycleScope.launch {
            fragment.repeatOnLifecycle(Lifecycle.State.STARTED){
                status.value = fragment.requireActivity().checkPermissionStatus(permission)
            }
        }
    }
    val status get() = _status.asStateFlow()

    private val launcher = fragment.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        granted ->
        _status.value = if(granted){
            PermissionStatus.Granted
        } else {
            PermissionStatus.Denied(ActivityCompat.shouldShowRequestPermissionRationale(fragment.requireActivity(),permission))
        }
    }

    fun launch(){
        launcher.launch(permission)
    }
}

private fun Activity.checkPermissionStatus(permission: String): PermissionStatus{
    val check = ContextCompat.checkSelfPermission(this, permission)
    return if(check == PackageManager.PERMISSION_GRANTED){
        PermissionStatus.Granted
    } else {
        PermissionStatus.Denied(ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
    }
}