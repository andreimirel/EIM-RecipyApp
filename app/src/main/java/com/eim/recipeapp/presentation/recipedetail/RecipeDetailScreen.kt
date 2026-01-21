package com.eim.recipeapp.presentation.recipedetail

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmAdd
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
import com.eim.recipeapp.domain.model.MealDetail
import com.eim.recipeapp.domain.model.NutritionItem
import com.eim.recipeapp.notifications.AlarmScheduler
import com.eim.recipeapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.util.Calendar

@Composable
fun RecipeDetailScreen(navController: NavController, viewModel: RecipeDetailViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val nutritionState by viewModel.nutritionState.collectAsState()
    val context = LocalContext.current
    var dominantColor by remember { mutableStateOf(Color.Gray) }
    val animatedBackgroundColor by animateColorAsState(targetValue = dominantColor.copy(alpha = 0.4f), animationSpec = tween(700))

    LaunchedEffect(state.mealDetail?.imageUrl) {
        state.mealDetail?.imageUrl?.let { extractDominantColor(context, it) { color -> dominantColor = color } }
    }

    Box(modifier = Modifier.fillMaxSize().background(animatedBackgroundColor)) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (state.error.isNotBlank()) {
            Text(text = state.error, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
        } else if (state.mealDetail != null) {
            val meal = state.mealDetail!!

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item { Box(modifier = Modifier.fillMaxWidth().height(300.dp)) { AsyncImage(model = meal.imageUrl, contentDescription = "Image of ${meal.name}", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop) } }
                item { Column(modifier = Modifier.padding(16.dp)) { Text(text = meal.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) ; Spacer(modifier = Modifier.height(8.dp)) ; Row { Text(text = meal.category, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) ; Spacer(modifier = Modifier.width(8.dp)) ; Text(text = "•", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) ; Spacer(modifier = Modifier.width(8.dp)) ; Text(text = meal.area, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) } } }
                item { IngredientsCard(meal.ingredients) }
                item { NutritionCard(viewModel = viewModel, nutritionState = nutritionState) }
                item { InstructionsCard(meal.instructions) }
                item { Spacer(modifier = Modifier.height(120.dp)) }
            }

            FloatingActionButton(onClick = { viewModel.toggleFavorite() }, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).offset(y = (270).dp), shape = CircleShape, containerColor = MaterialTheme.colorScheme.surface) {
                Icon(imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, contentDescription = "Favorite", tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface)
            }

            ScheduleNotificationUI(mealDetail = meal)
        }
    }
}

@Composable
fun BoxScope.ScheduleNotificationUI(mealDetail: MealDetail) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                showTimePicker(context, mealDetail)
            } else {
                Toast.makeText(context, "Notification permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    FloatingActionButton(
        onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                showTimePicker(context, mealDetail)
            }
        },
        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Icon(imageVector = Icons.Filled.AlarmAdd, contentDescription = "Schedule Reminder", tint = MaterialTheme.colorScheme.onSecondaryContainer)
    }
}

private fun showTimePicker(context: Context, mealDetail: MealDetail) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            val selectedTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE, selectedMinute)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) { add(Calendar.DATE, 1) }
            }
            
            val success = AlarmScheduler.scheduleAlarm(context, selectedTime.timeInMillis, mealDetail)
            if (success) {
                Toast.makeText(context, "Reminder set for ${mealDetail.name}!", Toast.LENGTH_SHORT).show()
            } else {
                // On newer Android versions, this may fail if the user has not granted the SCHEDULE_EXACT_ALARM permission.
                // We need to guide them to the settings screen.
                 Toast.makeText(context, "Please grant permission to schedule alarms.", Toast.LENGTH_LONG).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }
            }
        },
        hour, minute, true
    ).show()
}

@Composable
fun IngredientsCard(ingredients: List<Pair<String, String>>) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Ingredients", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Spacer(modifier = Modifier.height(12.dp))
            ingredients.forEach { (ingredient, measure) -> Text("• $ingredient ($measure)", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 4.dp), color = Color.DarkGray) }
        }
    }
}

@Composable
fun NutritionCard(viewModel: RecipeDetailViewModel, nutritionState: Resource<List<NutritionItem>>?) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nutritional Values", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { viewModel.fetchNutrition() }, modifier = Modifier.fillMaxWidth()) { Text("Calculate Total Nutrition for Recipe") }
            Spacer(modifier = Modifier.height(16.dp))
            when (nutritionState) {
                is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                is Resource.Success -> {
                    val items = nutritionState.data
                    if (!items.isNullOrEmpty()) {
                        val totalCalories = items.sumOf { it.calories }; val totalServingSize = items.sumOf { it.servingSizeG }; val totalFat = items.sumOf { it.fatTotalG }; val totalSaturatedFat = items.sumOf { it.fatSaturatedG }; val totalProtein = items.sumOf { it.proteinG }; val totalSodium = items.sumOf { it.sodiumMg }; val totalPotassium = items.sumOf { it.potassiumMg }; val totalCholesterol = items.sumOf { it.cholesterolMg }; val totalCarbs = items.sumOf { it.carbohydratesTotalG }; val totalFiber = items.sumOf { it.fiberG }; val totalSugar = items.sumOf { it.sugarG }
                        val df = DecimalFormat("#,###.##")
                        Text("Aggregated Totals for the Recipe", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        NutritionInfoRow(label = "Serving Size", value = "${df.format(totalServingSize)}g")
                        NutritionInfoRow(label = "Calories", value = "${df.format(totalCalories)} kcal")
                        NutritionInfoRow(label = "Protein", value = "${df.format(totalProtein)}g")
                        NutritionInfoRow(label = "Total Fat", value = "${df.format(totalFat)}g")
                        NutritionInfoRow(label = "Saturated Fat", value = "${df.format(totalSaturatedFat)}g")
                        NutritionInfoRow(label = "Carbohydrates", value = "${df.format(totalCarbs)}g")
                        NutritionInfoRow(label = "Fiber", value = "${df.format(totalFiber)}g")
                        NutritionInfoRow(label = "Sugar", value = "${df.format(totalSugar)}g")
                        NutritionInfoRow(label = "Sodium", value = "${df.format(totalSodium)}mg")
                        NutritionInfoRow(label = "Potassium", value = "${df.format(totalPotassium)}mg")
                        NutritionInfoRow(label = "Cholesterol", value = "${df.format(totalCholesterol)}mg")
                    } else { Text("No nutritional data found for this recipe\'s ingredients.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray) }
                }
                is Resource.Error -> Text(nutritionState.message ?: "Failed to load nutritional data.", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.CenterHorizontally))
                is Resource.Idle, null -> {}
            }
        }
    }
}

@Composable
fun NutritionInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, fontWeight = FontWeight.Medium, color = Color.DarkGray)
        Text(text = value, color = Color.Black)
    }
}

@Composable
fun InstructionsCard(instructions: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Instructions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Spacer(modifier = Modifier.height(12.dp))
            instructions.split("\r\n").filter { it.isNotBlank() }.forEach { paragraph -> Text(paragraph, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 12.dp), color = Color.DarkGray) }
        }
    }
}

private suspend fun extractDominantColor(context: Context, imageUrl: String, onColorExtracted: (Color) -> Unit) {
    withContext(Dispatchers.IO) {
        try {
            val request = ImageRequest.Builder(context).data(imageUrl).scale(Scale.FILL).allowHardware(false).build()
            val result = coil.Coil.imageLoader(context).execute(request).drawable
            if (result != null) {
                val bitmap = result.toBitmap()
                Palette.from(bitmap).generate { palette ->
                    val color = palette?.getVibrantColor(Color.Gray.toArgb()) ?: palette?.getMutedColor(Color.Gray.toArgb()) ?: Color.Gray.toArgb()
                    onColorExtracted(Color(color))
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }
}
