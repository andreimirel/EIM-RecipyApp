package com.eim.recipeapp.data.remote.dto

import com.eim.recipeapp.domain.model.MealDetail
import com.google.gson.annotations.SerializedName

data class MealDetailDto(
    @SerializedName("meals")
    val meals: List<MealDetailItemDto>?
)

data class MealDetailItemDto(
    @SerializedName("idMeal") val idMeal: String,
    @SerializedName("strMeal") val strMeal: String,
    @SerializedName("strCategory") val strCategory: String,
    @SerializedName("strArea") val strArea: String,
    @SerializedName("strInstructions") val strInstructions: String,
    @SerializedName("strMealThumb") val strMealThumb: String,
    @SerializedName("strYoutube") val strYoutube: String?,
    val strIngredient1: String?, val strIngredient2: String?, val strIngredient3: String?,
    val strIngredient4: String?, val strIngredient5: String?, val strIngredient6: String?,
    val strIngredient7: String?, val strIngredient8: String?, val strIngredient9: String?,
    val strIngredient10: String?, val strIngredient11: String?, val strIngredient12: String?,
    val strIngredient13: String?, val strIngredient14: String?, val strIngredient15: String?,
    val strIngredient16: String?, val strIngredient17: String?, val strIngredient18: String?,
    val strIngredient19: String?, val strIngredient20: String?,
    val strMeasure1: String?, val strMeasure2: String?, val strMeasure3: String?,
    val strMeasure4: String?, val strMeasure5: String?, val strMeasure6: String?,
    val strMeasure7: String?, val strMeasure8: String?, val strMeasure9: String?,
    val strMeasure10: String?, val strMeasure11: String?, val strMeasure12: String?,
    val strMeasure13: String?, val strMeasure14: String?, val strMeasure15: String?,
    val strMeasure16: String?, val strMeasure17: String?, val strMeasure18: String?,
    val strMeasure19: String?, val strMeasure20: String?
)

fun MealDetailItemDto.toMealDetail(): MealDetail {
    val ingredients = mutableListOf<Pair<String, String>>()
    // Helper function to add ingredient if not null/blank
    fun addIngredient(ingredient: String?, measure: String?) {
        if (!ingredient.isNullOrBlank()) {
            ingredients.add(Pair(ingredient, measure.orEmpty()))
        }
    }

    addIngredient(strIngredient1, strMeasure1)
    addIngredient(strIngredient2, strMeasure2)
    addIngredient(strIngredient3, strMeasure3)
    addIngredient(strIngredient4, strMeasure4)
    addIngredient(strIngredient5, strMeasure5)
    addIngredient(strIngredient6, strMeasure6)
    addIngredient(strIngredient7, strMeasure7)
    addIngredient(strIngredient8, strMeasure8)
    addIngredient(strIngredient9, strMeasure9)
    addIngredient(strIngredient10, strMeasure10)
    addIngredient(strIngredient11, strMeasure11)
    addIngredient(strIngredient12, strMeasure12)
    addIngredient(strIngredient13, strMeasure13)
    addIngredient(strIngredient14, strMeasure14)
    addIngredient(strIngredient15, strMeasure15)
    addIngredient(strIngredient16, strMeasure16)
    addIngredient(strIngredient17, strMeasure17)
    addIngredient(strIngredient18, strMeasure18)
    addIngredient(strIngredient19, strMeasure19)
    addIngredient(strIngredient20, strMeasure20)

    return MealDetail(
        mealId = idMeal,
        name = strMeal,
        category = strCategory,
        area = strArea,
        instructions = strInstructions,
        imageUrl = strMealThumb,
        youtubeLink = strYoutube ?: "",
        ingredients = ingredients
    )
}
