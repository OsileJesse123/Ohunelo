package com.jesse.ohunelo.di

import com.jesse.ohunelo.data.repository.RecipeRepository
import com.jesse.ohunelo.data.repository.RecipeRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesRecipeRepository(recipeRepositoryImpl: RecipeRepositoryImpl): RecipeRepository =
        recipeRepositoryImpl
}