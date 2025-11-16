package com.eim.recipeapp.domain.model

data class MealDetail(
    val mealId: String,
    val name: String,
    val category: String,
    val area: String,
    val instructions: String,
    val imageUrl: String,
    val youtubeLink: String,
    val ingredients: List<Pair<String, String>>
)
