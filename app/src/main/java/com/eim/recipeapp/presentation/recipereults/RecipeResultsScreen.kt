package com.eim.recipeapp.presentation.recipereults

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // Import added
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.eim.recipeapp.domain.model.Meal
import com.eim.recipeapp.presentation.recipesearch.RecipeSearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeResultsScreen(
    navController: NavController,
    searchQuery: String,
    viewModel: RecipeSearchViewModel = hiltViewModel()
) {
    // Automatically trigger search when the screen is composed
    LaunchedEffect(key1 = searchQuery) {
        viewModel.searchMeals(searchQuery)
    }

    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.search_background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize().blur(radius = 8.dp),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)))

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Results for \"$searchQuery\"", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (state.error.isNotBlank()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.error, color = MaterialTheme.colorScheme.error)
                }
            } else if (state.meals.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No recipes found.", color = Color.White)
                }
            } else {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(state.meals) { meal ->
                        MealResultItem(meal) { clickedMealId ->
                            navController.navigate("recipeDetailScreen/$clickedMealId")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MealResultItem(meal: Meal, onItemClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { onItemClick(meal.mealId) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.85f) // White, semi-transparent background
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = meal.imageUrl,
                contentDescription = "Image of ${meal.name}",
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = meal.name, style = MaterialTheme.typography.titleMedium, color = Color.Black)
        }
    }
}
