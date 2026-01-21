package com.eim.recipeapp.data.remote.dto.nutrition

import com.eim.recipeapp.domain.model.NutritionItem
import com.google.gson.annotations.SerializedName

// This class now represents the root of the JSON response
data class NutritionResponseDto(
    @SerializedName("items")
    val items: List<NutritionItemDto>
)

// This class represents a single item in the list
data class NutritionItemDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("calories")
    val calories: Double, // Back to Double
    @SerializedName("serving_size_g")
    val servingSizeG: Double,
    @SerializedName("fat_total_g")
    val fatTotalG: Double,
    @SerializedName("fat_saturated_g")
    val fatSaturatedG: Double,
    @SerializedName("protein_g")
    val proteinG: Double, // Back to Double
    @SerializedName("sodium_mg")
    val sodiumMg: Int,
    @SerializedName("potassium_mg")
    val potassiumMg: Int,
    @SerializedName("cholesterol_mg")
    val cholesterolMg: Int,
    @SerializedName("carbohydrates_total_g")
    val carbohydratesTotalG: Double,
    @SerializedName("fiber_g")
    val fiberG: Double,
    @SerializedName("sugar_g")
    val sugarG: Double
)

// The mapping function remains the same, but now it's more reliable
fun NutritionItemDto.toNutritionItem(): NutritionItem {
    return NutritionItem(
        name = name.replaceFirstChar { it.uppercase() },
        calories = calories,
        servingSizeG = servingSizeG,
        fatTotalG = fatTotalG,
        fatSaturatedG = fatSaturatedG,
        proteinG = proteinG,
        sodiumMg = sodiumMg,
        potassiumMg = potassiumMg,
        cholesterolMg = cholesterolMg,
        carbohydratesTotalG = carbohydratesTotalG,
        fiberG = fiberG,
        sugarG = sugarG
    )
}
