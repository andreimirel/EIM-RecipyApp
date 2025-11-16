package com.eim.recipeapp.presentation.favoriterecipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eim.recipeapp.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteRecipesViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteRecipesState())
    val state: StateFlow<FavoriteRecipesState> = _state.asStateFlow()

    init {
        getFavoriteMeals()
    }

    private fun getFavoriteMeals() {
        viewModelScope.launch {
            repository.getFavoriteMeals()
                .onEach { meals ->
                    _state.value = FavoriteRecipesState(favoriteMeals = meals)
                }
                .launchIn(viewModelScope)
        }
    }

    fun removeFavorite(mealId: String) {
        viewModelScope.launch {
            repository.removeFavoriteMeal(mealId)
        }
    }
}
