package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.jesse.ohunelo.util.BottomSheetBehaviorStateWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(): ViewModel() {

    var bottomSheetBehaviorState: BottomSheetBehaviorStateWrapper =
        BottomSheetBehaviorStateWrapper.STATE_COLLAPSED

}