package com.eim.recipeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eim.recipeapp.data.local.dao.FavoriteMealDao
import com.eim.recipeapp.data.local.entities.FavoriteMealEntity

@Database(entities = [FavoriteMealEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun favoriteMealDao(): FavoriteMealDao
}
