package com.eim.recipeapp.presentation.recipedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eim.recipeapp.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val repository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeDetailState())
    val state: StateFlow<RecipeDetailState> = _state.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    init {
        savedStateHandle.get<String>("mealId")?.let { mealId ->
            getMealDetail(mealId)
        }
    }

    private fun getMealDetail(mealId: String) {
        viewModelScope.launch {
            _state.value = RecipeDetailState(isLoading = true)
            try {
                val mealDetail = repository.getMealDetail(mealId)
                _state.value = RecipeDetailState(mealDetail = mealDetail)
                checkIfFavorite(mealId) // Check if this meal is a favorite
            } catch (e: Exception) {
                _state.value = RecipeDetailState(error = e.message ?: "An unexpected error occurred")
            }
        }
    }

    private fun checkIfFavorite(mealId: String) {
        viewModelScope.launch {
            _isFavorite.value = repository.isMealFavorite(mealId)
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _state.value.mealDetail?.let { mealDetail ->
                if (_isFavorite.value) {
                    repository.removeFavoriteMeal(mealDetail.mealId)
                } else {
                    repository.addFavoriteMeal(mealDetail)
                }
                _isFavorite.value = !_isFavorite.value // Toggle the state
            }
        }
    }
}
