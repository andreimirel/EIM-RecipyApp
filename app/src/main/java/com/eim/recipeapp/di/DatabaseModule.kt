package com.eim.recipeapp.di

import android.app.Application
import androidx.room.Room
import com.eim.recipeapp.data.local.RecipeDatabase
import com.eim.recipeapp.data.local.dao.FavoriteMealDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): RecipeDatabase {
        return Room.databaseBuilder(
            app,
            RecipeDatabase::class.java,
            "recipe_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFavoriteMealDao(db: RecipeDatabase): FavoriteMealDao {
        return db.favoriteMealDao()
    }
}
