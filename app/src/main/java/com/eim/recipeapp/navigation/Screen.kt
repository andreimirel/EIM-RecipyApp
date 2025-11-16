package com.eim.recipeapp.navigation

sealed class Screen(val route: String) {
    object LoginScreen : Screen("login")
    object RegisterScreen : Screen("register")
    object HomeScreen : Screen("homeScreen")
    object RecipeSearchScreen : Screen("recipeSearchScreen")
    object FavoriteRecipesScreen : Screen("favoriteRecipesScreen")
    object RecipeDetailScreen : Screen("recipeDetailScreen") {
        fun createRoute(mealId: String): String {
            return "recipeDetailScreen/$mealId"
        }
    }
}
