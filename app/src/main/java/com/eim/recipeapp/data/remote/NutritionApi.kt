package com.eim.recipeapp.data.remote

import com.eim.recipeapp.data.remote.dto.nutrition.NutritionResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NutritionApi {

    @GET("v1/nutrition")
    suspend fun getNutritionInfo(
        @Query("query") query: String
    ): NutritionResponseDto // Changed to reflect the new API structure

}
