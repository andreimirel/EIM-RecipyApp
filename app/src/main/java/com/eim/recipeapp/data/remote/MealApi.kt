package com.eim.recipeapp.data.remote

import com.eim.recipeapp.data.remote.dto.MealDetailListDto
import com.eim.recipeapp.data.remote.dto.MealListDto
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {

    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealListDto

    @GET("lookup.php")
    suspend fun lookupMealById(@Query("i") id: String): MealDetailListDto
}
