package com.eim.recipeapp.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.eim.recipeapp.presentation.favoriterecipes.FavoriteRecipesScreen
import com.eim.recipeapp.presentation.recipedetail.RecipeDetailScreen
import com.eim.recipeapp.presentation.recipereults.RecipeResultsScreen
import com.eim.recipeapp.presentation.recipesearch.RecipeSearchScreen
import com.eim.recipeapp.presentation.screens.LoginScreen
import com.eim.recipeapp.presentation.screens.RegisterScreen
import com.eim.recipeapp.presentation.viewmodel.AuthViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RecipeAppNavigation(authViewModel: AuthViewModel = hiltViewModel()) {
    val navController = rememberAnimatedNavController()
    val isUserAuthenticated by authViewModel.isUserAuthenticated.collectAsState()

    val startDestination = if (isUserAuthenticated) "recipeSearchScreen" else "login"

    AnimatedNavHost(
        navController = navController, 
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(700)) },
        exitTransition = { fadeOut(animationSpec = tween(700)) }
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }
        composable("recipeSearchScreen") {
            RecipeSearchScreen(
                navController = navController,
                authViewModel = authViewModel,
                onNavigateToFavorites = { navController.navigate(Screen.FavoriteRecipesScreen.route) },
                onSignOut = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(
            route = "recipeResultsScreen/{searchQuery}",
            arguments = listOf(navArgument("searchQuery") { type = NavType.StringType })
        ) {
            val searchQuery = it.arguments?.getString("searchQuery") ?: ""
            RecipeResultsScreen(navController = navController, searchQuery = searchQuery)
        }
        composable(
            route = "recipeDetailScreen/{mealId}",
            arguments = listOf(navArgument("mealId") { type = NavType.StringType })
        ) {
            RecipeDetailScreen(navController = navController)
        }
        composable(Screen.FavoriteRecipesScreen.route) {
            FavoriteRecipesScreen(navController = navController)
        }
    }
}
