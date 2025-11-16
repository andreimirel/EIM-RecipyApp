package com.eim.recipeapp.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eim.recipeapp.data.repository.RecipeRepositoryImpl
import com.eim.recipeapp.domain.repository.RecipeRepository
import com.eim.recipeapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val recipeRepository: RecipeRepositoryImpl,
    private val application: Application 
) : ViewModel() {

    private val _signInState = MutableStateFlow<Resource<FirebaseUser?>>(Resource.Idle)
    val signInState: StateFlow<Resource<FirebaseUser?>> = _signInState

    private val _signUpState = MutableStateFlow<Resource<FirebaseUser?>>(Resource.Idle)
    val signUpState: StateFlow<Resource<FirebaseUser?>> = _signUpState

    private val _isUserAuthenticated = MutableStateFlow(auth.currentUser != null)
    val isUserAuthenticated: StateFlow<Boolean> = _isUserAuthenticated

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing

    init {
        if (auth.currentUser != null) {
            _isUserAuthenticated.value = true
            syncFavorites()
        } else {
            _isUserAuthenticated.value = false
        }
    }

    private fun syncFavorites() = viewModelScope.launch {
        _isSyncing.value = true
        recipeRepository.syncFavoritesFromFirebaseToRoom()
        _isSyncing.value = false
    }

    fun signInWithEmailAndPassword(email: String, password: String) = viewModelScope.launch {
        _signInState.value = Resource.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _signInState.value = Resource.Success(it.user)
                _isUserAuthenticated.value = true 
                syncFavorites()
            }
            .addOnFailureListener {
                _signInState.value = Resource.Error(it.message ?: "An unexpected error occurred")
                _isUserAuthenticated.value = false 
            }
    }

    fun createUserWithEmailAndPassword(email: String, password: String) = viewModelScope.launch {
        _signUpState.value = Resource.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _signUpState.value = Resource.Success(it.user)
                _isUserAuthenticated.value = true
                syncFavorites()
            }
            .addOnFailureListener {
                _signUpState.value = Resource.Error(it.message ?: "An unexpected error occurred")
                _isUserAuthenticated.value = false
            }
    }

    fun signOut() = viewModelScope.launch {
        try {
            auth.signOut()
            _isUserAuthenticated.value = false
            recipeRepository.clearLocalFavorites()
        } catch (e: Exception) {
            println("Sign out error: ${e.message}")
        }
    }
}
