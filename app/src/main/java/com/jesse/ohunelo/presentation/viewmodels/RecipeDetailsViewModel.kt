package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.data.repository.RecipeRepository
import com.jesse.ohunelo.presentation.uistates.RecipeDetailsUiState
import com.jesse.ohunelo.util.BottomSheetBehaviorStateWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(): ViewModel() {

    var bottomSheetBehaviorState: BottomSheetBehaviorStateWrapper =
        BottomSheetBehaviorStateWrapper.STATE_COLLAPSED
}