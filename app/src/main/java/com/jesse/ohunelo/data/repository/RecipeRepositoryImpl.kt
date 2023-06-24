package com.jesse.ohunelo.data.repository

import com.jesse.ohunelo.data.RecipePagingSource
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(): RecipeRepository {

    override fun recipePagingSource() = RecipePagingSource()
}