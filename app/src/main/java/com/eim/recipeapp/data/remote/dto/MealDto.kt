package com.eim.recipeapp.data.remote.dto

import com.eim.recipeapp.domain.model.Meal
import com.google.gson.annotations.SerializedName

data class MealDto(
    @SerializedName("idMeal")
    val idMeal: String,
    @SerializedName("strMeal")
    val strMeal: String,
    @SerializedName("strMealThumb")
    val strMealThumb: String
)

fun MealDto.toMeal(): Meal {
    return Meal(
        mealId = idMeal,
        name = strMeal,
        imageUrl = strMealThumb
    )
}
