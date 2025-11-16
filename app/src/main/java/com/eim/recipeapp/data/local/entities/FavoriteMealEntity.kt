package com.eim.recipeapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.eim.recipeapp.domain.model.MealDetail

@Entity(tableName = "favorite_meals")
data class FavoriteMealEntity(
    @PrimaryKey
    val mealId: String,
    val name: String,
    val category: String,
    val area: String,
    val instructions: String,
    val imageUrl: String,
    val youtubeLink: String,
    val ingredientsJson: String // Store ingredients as JSON string
) {
    fun toMealDetail(): MealDetail {
        // This conversion will be handled with a TypeConverter or manually parsed JSON
        // For now, let's keep it simple and assume we'll parse ingredientsJson later.
        // Or, if not needed for simple display, just pass empty list.
        return MealDetail(
            mealId = mealId,
            name = name,
            category = category,
            area = area,
            instructions = instructions,
            imageUrl = imageUrl,
            youtubeLink = youtubeLink,
            ingredients = emptyList() // Placeholder for now
        )
    }
}
