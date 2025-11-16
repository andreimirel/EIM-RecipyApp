package com.eim.recipeapp.data.remote

import com.eim.recipeapp.data.remote.dto.FavoriteMealFirebaseDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseFavoritesManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private fun getFavoritesCollectionRef() = auth.currentUser?.uid?.let { uid ->
        firestore.collection("users").document(uid).collection("favorites")
    }

    suspend fun addFavorite(meal: FavoriteMealFirebaseDto) {
        getFavoritesCollectionRef()?.document(meal.mealId)?.set(meal)?.await()
    }

    suspend fun removeFavorite(mealId: String) {
        getFavoritesCollectionRef()?.document(mealId)?.delete()?.await()
    }

    suspend fun getFavorites(): List<FavoriteMealFirebaseDto> {
        val snapshot = getFavoritesCollectionRef()?.get()?.await()
        return snapshot?.documents?.mapNotNull { it.toObject(FavoriteMealFirebaseDto::class.java) } ?: emptyList()
    }

    suspend fun isFavorite(mealId: String): Boolean {
        val doc = getFavoritesCollectionRef()?.document(mealId)?.get()?.await()
        return doc?.exists() ?: false
    }
}
