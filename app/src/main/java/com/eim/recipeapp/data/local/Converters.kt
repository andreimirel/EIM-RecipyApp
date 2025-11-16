package com.eim.recipeapp.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromIngredientsList(ingredients: List<Pair<String, String>>): String {
        val gson = Gson()
        return gson.toJson(ingredients)
    }

    @TypeConverter
    fun toIngredientsList(ingredientsJson: String): List<Pair<String, String>> {
        val gson = Gson()
        val type = object : TypeToken<List<Pair<String, String>>>() {}.type
        return gson.fromJson(ingredientsJson, type)
    }
}
