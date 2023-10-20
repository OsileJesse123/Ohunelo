package com.jesse.ohunelo.di

import com.jesse.ohunelo.data.network.AuthenticationService
import com.jesse.ohunelo.data.network.FirebaseAuthenticationService
import com.jesse.ohunelo.data.network.RecipeNetworkDataSource
import com.jesse.ohunelo.data.network.RecipeNetworkDataSourceImpl
import com.jesse.ohunelo.data.repository.AuthenticationRepository
import com.jesse.ohunelo.data.repository.AuthenticationRepositoryImpl
import com.jesse.ohunelo.data.repository.RecipeRepository
import com.jesse.ohunelo.data.repository.RecipeRepositoryImpl
import com.jesse.ohunelo.domain.usecase.ValidateEmailUseCase
import com.jesse.ohunelo.domain.usecase.ValidatePasswordUseCase
import com.jesse.ohunelo.util.EmailMatcher
import com.jesse.ohunelo.util.EmailMatcherImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesRecipeRepository(recipeRepositoryImpl: RecipeRepositoryImpl): RecipeRepository =
        recipeRepositoryImpl

    @Provides
    @Singleton
    fun provideAuthenticationRepository(authenticationRepositoryImpl: AuthenticationRepositoryImpl):
            AuthenticationRepository = authenticationRepositoryImpl

    @Provides
    @Singleton
    fun providesEmailMatcher(emailMatcherImpl: EmailMatcherImpl): EmailMatcher = emailMatcherImpl

    @IODispatcher
    @Provides
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @DefaultDispatcher
    @Provides
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    fun provideValidateEmailUseCase(emailMatcher: EmailMatcher):
            ValidateEmailUseCase = ValidateEmailUseCase(emailMatcher)

    @Provides
    fun provideValidatePasswordUseCase(): ValidatePasswordUseCase = ValidatePasswordUseCase()

    @Provides
    @Singleton
    fun provideAuthenticationService(authenticationService: FirebaseAuthenticationService): AuthenticationService = authenticationService

    @Provides
    fun provideRecipeNetworkDataSource(recipeNetworkDataSourceImpl: RecipeNetworkDataSourceImpl): RecipeNetworkDataSource = recipeNetworkDataSourceImpl
}


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IODispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher