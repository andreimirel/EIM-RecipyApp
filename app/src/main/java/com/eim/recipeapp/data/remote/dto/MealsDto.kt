package com.eim.recipeapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MealsDto(
    @SerializedName("meals")
    val meals: List<MealDto>?
)
