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

    data object Granted: PermissionStatus

    class Denied(val shouldShowRationale: Boolean, val shouldGuideUserToAppSettings: Boolean): PermissionStatus
}

class PermissionRequest (
    private val fragment: Fragment,
    private val permission: String,
    private val shouldGuideUserToAppSettings: () -> Boolean,
    /** Once the user denies permission twice, permission has to be granted manually from the app settings.
     This has to be tracked so as to let the user know that this is the case and grant them the ability
     to go to app settings screen from the app.

     This lambda is responsible for updating denial count.
     **/
    private val updateDenialCount:() -> Unit,
    private val resetDenialCount:() -> Unit
){

    private val _status: MutableStateFlow<PermissionStatus?> = MutableStateFlow<PermissionStatus?>(null).also {
        status ->
        fragment.lifecycleScope.launch {
            fragment.repeatOnLifecycle(Lifecycle.State.STARTED){
                status.value = fragment.requireActivity().checkPermissionStatus(permission, shouldGuideUserToAppSettings(), resetDenialCount)
            }
        }
    }
    val status get() = _status.asStateFlow()

    private val launcher = fragment.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        granted ->
        _status.value = if(granted){
            resetDenialCount()
            PermissionStatus.Granted
        } else {
            updateDenialCount()
            PermissionStatus.Denied(ActivityCompat.shouldShowRequestPermissionRationale(fragment.requireActivity(),permission), shouldGuideUserToAppSettings = shouldGuideUserToAppSettings())
        }
    }

    fun launch(){
        launcher.launch(permission)
    }
}

private fun Activity.checkPermissionStatus(permission: String, shouldGuideUserToAppSettings: Boolean, resetDenialCount: () -> Unit): PermissionStatus{
    val check = ContextCompat.checkSelfPermission(this, permission)
    return if(check == PackageManager.PERMISSION_GRANTED){
        resetDenialCount()
        PermissionStatus.Granted
    } else {
        PermissionStatus.Denied(ActivityCompat.shouldShowRequestPermissionRationale(this, permission), shouldGuideUserToAppSettings)
    }
}