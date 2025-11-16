package com.eim.recipeapp.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.eim.recipeapp.R
import com.eim.recipeapp.presentation.viewmodel.AuthViewModel
import com.eim.recipeapp.utils.Resource

@Composable
fun RegisterScreen(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val signUpState by authViewModel.signUpState.collectAsState()

    LaunchedEffect(signUpState) {
        val currentSignUpState = signUpState
        when (currentSignUpState) {
            is Resource.Success -> {
                Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                navController.navigate("recipeSearchScreen") {
                    popUpTo("register") { inclusive = true }
                }
            }
            is Resource.Error -> {
                Toast.makeText(context, currentSignUpState.message, Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize().blur(radius = 10.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Register", style = MaterialTheme.typography.displaySmall, color = Color.White)
            Spacer(modifier = Modifier.height(24.dp))
            
            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.8f),
                    disabledContainerColor = Color.Gray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.DarkGray,
                    unfocusedPlaceholderColor = Color.DarkGray,
                    focusedPlaceholderColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                trailingIcon = {
                    val imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = imageVector, contentDescription = "Toggle password visibility")
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.8f),
                    disabledContainerColor = Color.Gray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.DarkGray,
                    unfocusedPlaceholderColor = Color.DarkGray,
                    focusedPlaceholderColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = { Text("Confirm Password") },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                trailingIcon = {
                    val imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = imageVector, contentDescription = "Toggle confirm password visibility")
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.8f),
                    disabledContainerColor = Color.Gray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.DarkGray,
                    unfocusedPlaceholderColor = Color.DarkGray,
                    focusedPlaceholderColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    if (password == confirmPassword) {
                        authViewModel.createUserWithEmailAndPassword(email, password)
                    } else {
                        Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_LONG).show()
                    }
                },
                enabled = signUpState !is Resource.Loading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (signUpState is Resource.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Register", style = MaterialTheme.typography.bodyLarge)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Already have an account? Login", color = Color.White)
            }
        }
    }
}
