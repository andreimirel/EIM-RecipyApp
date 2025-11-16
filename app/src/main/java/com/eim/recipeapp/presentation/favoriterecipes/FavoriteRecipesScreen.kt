package com.eim.recipeapp.presentation.favoriterecipes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.eim.recipeapp.R
import com.eim.recipeapp.domain.model.MealDetail
import com.eim.recipeapp.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteRecipesScreen(
    navController: NavController,
    viewModel: FavoriteRecipesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.search_background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize().blur(radius = 8.dp),
            contentScale = ContentScale.Crop
        )
        // Scrim layer
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)))

        Column(modifier = Modifier.fillMaxSize()) {
            // TopAppBar
            TopAppBar(
                title = { Text("Favorite Recipes", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else if (state.error.isNotBlank()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.error, color = MaterialTheme.colorScheme.error)
                    }
                } else if (state.favoriteMeals.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No favorite recipes yet.", color = Color.White)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.favoriteMeals) { meal ->
                            FavoriteMealItem(
                                meal = meal,
                                onItemClick = { clickedMealId ->
                                    navController.navigate("${Screen.RecipeDetailScreen.route}/$clickedMealId")
                                },
                                onRemoveClick = { mealIdToRemove ->
                                    viewModel.removeFavorite(mealIdToRemove)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteMealItem(
    meal: MealDetail,
    onItemClick: (String) -> Unit,
    onRemoveClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onItemClick(meal.mealId) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                AsyncImage(
                    model = meal.imageUrl,
                    contentDescription = "Image of ${meal.name}",
                    modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = meal.name, style = MaterialTheme.typography.titleMedium)
            }
            IconButton(onClick = { onRemoveClick(meal.mealId) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove from favorites")
            }
        }
    }
}
