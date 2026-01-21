package com.eim.recipeapp.presentation.recipesearch

import app.cash.turbine.test
import com.eim.recipeapp.domain.model.Meal
import com.eim.recipeapp.domain.repository.RecipeRepository
import com.eim.recipeapp.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class RecipeSearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: RecipeRepository = mockk()
    private lateinit var viewModel: RecipeSearchViewModel

    @Before
    fun setUp() {
        // Initialize the ViewModel before each test
        viewModel = RecipeSearchViewModel(repository)
    }

    @Test
    fun `searchMeals success - emits loading then success state`() = runTest {
        // Given
        val query = "chicken"
        val mockMeals = listOf(
            Meal("1", "Chicken Soup", "url1"),
            Meal("2", "Fried Chicken", "url2")
        )
        coEvery { repository.searchMeals(query) } returns mockMeals

        // When & Then
        viewModel.state.test {
            // The first item is the initial state, which we can skip or assert
            assertEquals(RecipeSearchState(), awaitItem()) 

            // Trigger the search inside the test block
            viewModel.searchMeals(query)

            // Expect the loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            // Expect the success state
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals(mockMeals, successState.meals)
            assertTrue(successState.error.isBlank())

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `searchMeals error - emits loading then error state`() = runTest {
        // Given
        val query = "invalid"
        val errorMessage = "An unexpected error occurred"
        coEvery { repository.searchMeals(query) } throws RuntimeException(errorMessage)

        // When & Then
        viewModel.state.test {
             // Assert and consume the initial state
            assertEquals(RecipeSearchState(), awaitItem())

            // Trigger the search
            viewModel.searchMeals(query)

            // Expect the loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            // Expect the error state
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertTrue(errorState.meals.isEmpty())
            assertEquals(errorMessage, errorState.error)

            cancelAndConsumeRemainingEvents()
        }
    }
}
