package com.jesse.ohunelo.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.jesse.ohunelo.BuildConfig
import com.jesse.ohunelo.data.local.RecipeLocalDataSource
import com.jesse.ohunelo.data.local.RecipeLocalDataSourceImpl
import com.jesse.ohunelo.data.local.database.RecipeDatabase
import com.jesse.ohunelo.data.local.database.RecipeDatabasePassphrase
import com.jesse.ohunelo.data.network.ApiKeyInterceptor
import com.jesse.ohunelo.data.network.AuthenticationService
import com.jesse.ohunelo.data.network.FirebaseAuthenticationService
import com.jesse.ohunelo.data.network.RecipeNetworkDataSource
import com.jesse.ohunelo.data.network.RecipeNetworkDataSourceImpl
import com.jesse.ohunelo.data.network.SpoonacularService
import com.jesse.ohunelo.data.repository.AuthenticationRepository
import com.jesse.ohunelo.data.repository.AuthenticationRepositoryImpl
import com.jesse.ohunelo.data.repository.RecipeRepository
import com.jesse.ohunelo.data.repository.RecipeRepositoryImpl
import com.jesse.ohunelo.domain.usecase.FormatHomeScreenDataUseCase
import com.jesse.ohunelo.domain.usecase.ValidateEmailUseCase
import com.jesse.ohunelo.domain.usecase.ValidatePasswordUseCase
import com.jesse.ohunelo.util.EmailMatcher
import com.jesse.ohunelo.util.EmailMatcherImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import net.sqlcipher.database.SupportFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://api.spoonacular.com/recipes/"

    @Provides
    fun providesMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Singleton
    @Provides
    fun providesInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(okHttpLoggingInterceptor: HttpLoggingInterceptor) =
        OkHttpClient
            .Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(okHttpLoggingInterceptor)
            .addInterceptor(ApiKeyInterceptor(BuildConfig.API_KEY))
            .build()

    @Singleton
    @Provides
    fun providesRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL)
            .client(client)
            .build()

    @Singleton
    @Provides
    fun providesSpoonacularService(retrofit: Retrofit): SpoonacularService = retrofit.create(
        SpoonacularService::class.java)

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
    fun provideFormatHomeScreenDataUseCase(recipeRepository: RecipeRepository): FormatHomeScreenDataUseCase = FormatHomeScreenDataUseCase(recipeRepository)

    @Provides
    @Singleton
    fun provideAuthenticationService(authenticationService: FirebaseAuthenticationService): AuthenticationService = authenticationService

    @Provides
    fun provideRecipeNetworkDataSource(recipeNetworkDataSourceImpl: RecipeNetworkDataSourceImpl): RecipeNetworkDataSource = recipeNetworkDataSourceImpl

    @Provides
    fun provideRecipeLocalDataSource(recipeLocalDataSourceImpl: RecipeLocalDataSourceImpl): RecipeLocalDataSource = recipeLocalDataSourceImpl

    @Provides
    @Singleton
    fun provideRecipeDatabasePassphrase(@ApplicationContext context: Context) = RecipeDatabasePassphrase(context)

    @Provides
    @Singleton
    fun provideSupportFactory(recipeDatabasePassphrase: RecipeDatabasePassphrase) = SupportFactory(recipeDatabasePassphrase.getPassphrase())

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context, supportFactory: SupportFactory): RecipeDatabase{
         return Room.databaseBuilder(context, RecipeDatabase::class.java, "recipe_database")
             .openHelperFactory(supportFactory)
             .build()
    }

    @Provides
    @Singleton
    fun provideRecipeDao(recipeDatabase: RecipeDatabase) = recipeDatabase.recipeDao()
}


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IODispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher