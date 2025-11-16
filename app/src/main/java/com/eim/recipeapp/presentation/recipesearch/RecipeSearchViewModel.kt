package com.eim.recipeapp.presentation.recipesearch

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
class RecipeSearchViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeSearchState())
    val state: StateFlow<RecipeSearchState> = _state.asStateFlow()

    fun searchMeals(query: String) {
        viewModelScope.launch {
            _state.value = RecipeSearchState(isLoading = true)
            try {
                val meals = repository.searchMeals(query)
                _state.value = RecipeSearchState(meals = meals)
            } catch (e: Exception) {
                _state.value = RecipeSearchState(error = e.message ?: "An unexpected error occurred")
            }
        }
    }
}
