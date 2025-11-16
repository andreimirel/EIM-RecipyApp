package com.eim.recipeapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.eim.recipeapp.data.local.entities.FavoriteMealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteMeal(meal: FavoriteMealEntity)

    @Query("DELETE FROM favorite_meals WHERE mealId = :mealId")
    suspend fun deleteFavoriteMeal(mealId: String)

    @Query("SELECT * FROM favorite_meals")
    fun getAllFavoriteMeals(): Flow<List<FavoriteMealEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_meals WHERE mealId = :mealId LIMIT 1)")
    suspend fun isMealFavorite(mealId: String): Boolean

    @Query("DELETE FROM favorite_meals")
    suspend fun clearFavorites()
}
