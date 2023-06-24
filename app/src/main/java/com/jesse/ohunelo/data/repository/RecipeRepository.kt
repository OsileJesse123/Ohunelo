package com.jesse.ohunelo.data.repository

import com.jesse.ohunelo.data.RecipePagingSource

interface RecipeRepository {

    fun recipePagingSource(): RecipePagingSource
}