package com.eim.recipeapp.data.repository

import com.eim.recipeapp.data.local.Converters
import com.eim.recipeapp.data.local.dao.FavoriteMealDao
import com.eim.recipeapp.data.local.entities.FavoriteMealEntity
import com.eim.recipeapp.data.remote.FirebaseFavoritesManager
import com.eim.recipeapp.data.remote.MealApi
import com.eim.recipeapp.data.remote.dto.FavoriteMealFirebaseDto
import com.eim.recipeapp.domain.model.Meal
import com.eim.recipeapp.domain.model.MealDetail
import com.eim.recipeapp.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val api: MealApi,
    private val favoriteMealDao: FavoriteMealDao,
    private val firebaseFavoritesManager: FirebaseFavoritesManager // Inject Firebase Manager
) : RecipeRepository {

    private val converters = Converters()

    override suspend fun searchMeals(query: String): List<Meal> {
        return api.searchMeals(query).meals?.map { it.toMeal() } ?: emptyList()
    }

    override suspend fun getMealDetail(mealId: String): MealDetail {
        return api.lookupMealById(mealId).meals?.firstOrNull()?.toMealDetail()
            ?: throw Exception("Meal details not found for ID: $mealId")
    }

    override suspend fun addFavoriteMeal(mealDetail: MealDetail) {
        // Add to Room
        val roomEntity = FavoriteMealEntity(
            mealId = mealDetail.mealId,
            name = mealDetail.name,
            category = mealDetail.category,
            area = mealDetail.area,
            instructions = mealDetail.instructions,
            imageUrl = mealDetail.imageUrl,
            youtubeLink = mealDetail.youtubeLink,
            ingredientsJson = converters.fromIngredientsList(mealDetail.ingredients)
        )
        favoriteMealDao.insertFavoriteMeal(roomEntity)

        // Add to Firebase
        val firebaseDto = FavoriteMealFirebaseDto.fromMealDetail(mealDetail)
        firebaseFavoritesManager.addFavorite(firebaseDto)
    }

    override suspend fun removeFavoriteMeal(mealId: String) {
        // Remove from Room
        favoriteMealDao.deleteFavoriteMeal(mealId)

        // Remove from Firebase
        firebaseFavoritesManager.removeFavorite(mealId)
    }

    override fun getFavoriteMeals(): Flow<List<MealDetail>> {
        return favoriteMealDao.getAllFavoriteMeals().map {
            it.map { entity ->
                MealDetail(
                    mealId = entity.mealId,
                    name = entity.name,
                    category = entity.category,
                    area = entity.area,
                    instructions = entity.instructions,
                    imageUrl = entity.imageUrl,
                    youtubeLink = entity.youtubeLink,
                    ingredients = converters.toIngredientsList(entity.ingredientsJson)
                )
            }
        }
    }

    override suspend fun clearLocalFavorites() {
        favoriteMealDao.clearFavorites()
    }

    suspend fun syncFavoritesFromFirebaseToRoom() {
        val firebaseFavorites = firebaseFavoritesManager.getFavorites()
        favoriteMealDao.clearFavorites() // Clear local before re-populating
        firebaseFavorites.forEach { firebaseDto ->
            val roomEntity = FavoriteMealEntity(
                mealId = firebaseDto.mealId,
                name = firebaseDto.name,
                category = firebaseDto.category,
                area = firebaseDto.area,
                instructions = firebaseDto.instructions,
                imageUrl = firebaseDto.imageUrl,
                youtubeLink = firebaseDto.youtubeLink,
                ingredientsJson = converters.fromIngredientsList(firebaseDto.ingredients.map { ingredientDto ->
                    Pair(ingredientDto.ingredient, ingredientDto.measure)
                })
            )
            favoriteMealDao.insertFavoriteMeal(roomEntity)
        }
    }

    override suspend fun isMealFavorite(mealId: String): Boolean {
        // Check Room first for quick access
        return favoriteMealDao.isMealFavorite(mealId)
    }
}
