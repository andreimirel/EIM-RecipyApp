package com.eim.recipeapp.data.remote.dto

import com.eim.recipeapp.domain.model.MealDetail

data class FavoriteMealFirebaseDto(
    val mealId: String = "",
    val name: String = "",
    val category: String = "",
    val area: String = "",
    val instructions: String = "",
    val imageUrl: String = "",
    val youtubeLink: String = "",
    val ingredients: List<IngredientFirebaseDto> = emptyList()
) {
    fun toMealDetail(): MealDetail {
        return MealDetail(
            mealId = mealId,
            name = name,
            category = category,
            area = area,
            instructions = instructions,
            imageUrl = imageUrl,
            youtubeLink = youtubeLink,
            ingredients = ingredients.map { Pair(it.ingredient, it.measure) }
        )
    }

    companion object {
        fun fromMealDetail(mealDetail: MealDetail): FavoriteMealFirebaseDto {
            return FavoriteMealFirebaseDto(
                mealId = mealDetail.mealId,
                name = mealDetail.name,
                category = mealDetail.category,
                area = mealDetail.area,
                instructions = mealDetail.instructions,
                imageUrl = mealDetail.imageUrl,
                youtubeLink = mealDetail.youtubeLink,
                ingredients = mealDetail.ingredients.map { IngredientFirebaseDto(it.first, it.second) }
            )
        }
    }
}
