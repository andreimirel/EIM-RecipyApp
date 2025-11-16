package com.eim.recipeapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MealDetailListDto(
    @SerializedName("meals")
    val meals: List<MealDetailDto>?
)
