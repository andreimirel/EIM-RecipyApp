package com.eim.recipeapp.presentation.recipedetail

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun RecipeDetailScreen(navController: NavController, viewModel: RecipeDetailViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val context = LocalContext.current

    var dominantColor by remember { mutableStateOf(Color.Gray) }
    val animatedBackgroundColor by animateColorAsState(
        targetValue = dominantColor.copy(alpha = 0.4f),
        animationSpec = tween(700)
    )

    LaunchedEffect(state.mealDetail?.imageUrl) {
        state.mealDetail?.imageUrl?.let {
            extractDominantColor(context, it) { color ->
                dominantColor = color
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBackgroundColor)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (state.error.isNotBlank()) {
            Text(
                text = state.error, 
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (state.mealDetail != null) {
            val meal = state.mealDetail!!

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                        AsyncImage(
                            model = meal.imageUrl,
                            contentDescription = "Image of ${meal.name}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = meal.name,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface // Use theme color for contrast
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Text(
                                text = meal.category,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = meal.area,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.85f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Ingredients",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            meal.ingredients.forEach { (ingredient, measure) ->
                                Text(
                                    text = "• $ingredient ($measure)",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(bottom = 4.dp),
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.85f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Instructions",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            meal.instructions.split("\r\n").filter { it.isNotBlank() }.forEach { paragraph ->
                                Text(
                                    text = paragraph,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(bottom = 12.dp),
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { viewModel.toggleFavorite() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .offset(y = (270).dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private suspend fun extractDominantColor(context: Context, imageUrl: String, onColorExtracted: (Color) -> Unit) {
    withContext(Dispatchers.IO) {
        try {
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .scale(Scale.FILL)
                .allowHardware(false)
                .build()
            
            val result = coil.Coil.imageLoader(context).execute(request).drawable
            if (result != null) {
                val bitmap = result.toBitmap()
                Palette.from(bitmap).generate { palette ->
                    val color = palette?.getVibrantColor(Color.Gray.toArgb()) 
                                ?: palette?.getMutedColor(Color.Gray.toArgb()) 
                                ?: Color.Gray.toArgb()
                    onColorExtracted(Color(color))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
