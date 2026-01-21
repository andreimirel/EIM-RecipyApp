package com.eim.recipeapp.di

import com.eim.recipeapp.BuildConfig
import com.eim.recipeapp.data.remote.MealApi
import com.eim.recipeapp.data.remote.NutritionApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val MEAL_BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
    private const val NUTRITION_BASE_URL = "https://api.calorieninjas.com/"

    @Provides
    @Singleton
    @Named("MealOkHttpClient")
    fun provideMealOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("NutritionOkHttpClient")
    fun provideNutritionOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-Api-Key", BuildConfig.NINJAS_API_KEY)
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    @Named("MealRetrofit")
    fun provideMealRetrofit(@Named("MealOkHttpClient") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(MEAL_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("NutritionRetrofit")
    fun provideNutritionRetrofit(@Named("NutritionOkHttpClient") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NUTRITION_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideMealApi(@Named("MealRetrofit") retrofit: Retrofit): MealApi {
        return retrofit.create(MealApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNutritionApi(@Named("NutritionRetrofit") retrofit: Retrofit): NutritionApi {
        return retrofit.create(NutritionApi::class.java)
    }
}
