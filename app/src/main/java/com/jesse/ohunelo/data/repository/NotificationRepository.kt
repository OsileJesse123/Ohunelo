package com.jesse.ohunelo.data.repository

import com.jesse.ohunelo.data.model.Notification
import com.jesse.ohunelo.data.network.models.OhuneloResult
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    suspend fun getRandomFoodJoke(): OhuneloResult<Flow<List<Notification>>>

    suspend fun getRandomFoodTrivia(): OhuneloResult<Flow<List<Notification>>>

}