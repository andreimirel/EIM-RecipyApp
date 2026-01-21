package com.eim.recipeapp.presentation.recipedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eim.recipeapp.domain.model.NutritionItem
import com.eim.recipeapp.domain.repository.RecipeRepository
import com.eim.recipeapp.utils.Resource
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

    private val _nutritionState = MutableStateFlow<Resource<List<NutritionItem>>?>(null)
    val nutritionState: StateFlow<Resource<List<NutritionItem>>?> = _nutritionState.asStateFlow()

    init {
        savedStateHandle.get<String>("mealId")?.let {
            getMealDetail(it)
        }
    }

    private fun createGramBasedQuery(ingredients: List<Pair<String, String>>): String {
        val conversionMap = mapOf(
            "kg" to 1000.0,
            "cup" to 240.0,  // Standard approximation
            "tbsp" to 15.0,
            "tsp" to 5.0
        )

        return ingredients.mapNotNull { (ingredient, measure) ->
            val measureLower = measure.lowercase()
                .replace("¼", "0.25")
                .replace("½", "0.5")
                .replace("¾", "0.75")

            if (measureLower.contains("to taste")) return@mapNotNull null

            val regex = "([\\d.]+)\\s*(\\w+)?".toRegex()
            val match = regex.find(measureLower)

            if (match != null) {
                val (valueStr, unit) = match.destructured
                val value = valueStr.toDoubleOrNull() ?: return@mapNotNull null

                val conversionFactor = conversionMap[unit] ?: 1.0
                val grams = value * conversionFactor
                
                // Clean up the ingredient name from residual descriptions
                val cleanedIngredient = ingredient.replace(Regex("(?i)dried|chopped|sliced|minced"), "").trim()
                
                "${grams.toInt()}g $cleanedIngredient"
            } else {
                // If no specific unit, send as is (e.g., "2 Green chilli")
                "$measure $ingredient"
            }
        }.joinToString(" and ")
    }

    fun fetchNutrition() {
        viewModelScope.launch {
            _state.value.mealDetail?.let { mealDetail ->
                val query = createGramBasedQuery(mealDetail.ingredients)

                if (query.isNotBlank()) {
                    _nutritionState.value = Resource.Loading
                    _nutritionState.value = repository.getNutritionInfo(query)
                }
            }
        }
    }

    private fun getMealDetail(mealId: String) {
        viewModelScope.launch {
            _state.value = RecipeDetailState(isLoading = true)
            try {
                val mealDetail = repository.getMealDetail(mealId)
                _state.value = RecipeDetailState(mealDetail = mealDetail)
                checkIfFavorite(mealId)
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
            state.value.mealDetail?.let { mealDetail ->
                if (isFavorite.value) {
                    repository.removeFavoriteMeal(mealDetail.mealId)
                } else {
                    repository.addFavoriteMeal(mealDetail)
                }
                _isFavorite.value = !_isFavorite.value
            }
        }
    }
}
