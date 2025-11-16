package com.eim.recipeapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MealListDto(
    @SerializedName("meals")
    val meals: List<MealDto>?
)
