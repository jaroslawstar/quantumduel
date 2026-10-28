package pl.student.quantumduel.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.student.quantumduel.BuildConfig
import pl.student.quantumduel.data.local.AppDatabase
import pl.student.quantumduel.data.local.RoundDao
import pl.student.quantumduel.data.remote.AnuQrngApi
import pl.student.quantumduel.data.remote.KotlinxJsonConverterFactory
import pl.student.quantumduel.data.remote.OutshiftQrngApi
import pl.student.quantumduel.data.repository.EntropyRepositoryImpl
import pl.student.quantumduel.data.repository.HistoryRepositoryImpl
import pl.student.quantumduel.data.repository.SettingsRepositoryImpl
import pl.student.quantumduel.domain.entropy.EntropyRepository
import pl.student.quantumduel.domain.history.HistoryRepository
import pl.student.quantumduel.domain.settings.SettingsRepository
import pl.student.quantumduel.domain.usecase.GetStatsUseCase
import pl.student.quantumduel.domain.usecase.PlayRoundUseCase
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/** Zasięg korutyn żyjący tak długo jak aplikacja (pobieranie entropii w tle). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object CoroutinesModule {

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                        redactHeader("x-api-key")
                        redactHeader("x-id-api-key")
                    },
                )
            }
        }
        .build()

    @Provides
    @Singleton
    fun provideAnuQrngApi(client: OkHttpClient, json: Json): AnuQrngApi = Retrofit.Builder()
        .baseUrl(AnuQrngApi.BASE_URL)
        .client(client)
        .addConverterFactory(KotlinxJsonConverterFactory(json))
        .build()
        .create(AnuQrngApi::class.java)

    @Provides
    @Singleton
    fun provideOutshiftQrngApi(client: OkHttpClient, json: Json): OutshiftQrngApi = Retrofit.Builder()
        .baseUrl(OutshiftQrngApi.BASE_URL)
        .client(client)
        .addConverterFactory(KotlinxJsonConverterFactory(json))
        .build()
        .create(OutshiftQrngApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NAME).build()

    @Provides
    fun provideRoundDao(database: AppDatabase): RoundDao = database.roundDao()

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(produceFile = { context.preferencesDataStoreFile("settings") })
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEntropyRepository(impl: EntropyRepositoryImpl): EntropyRepository

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(impl: HistoryRepositoryImpl): HistoryRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}

/** Warstwa domeny nie zna Hilta – przypadki użycia tworzymy tutaj. */
@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    fun providePlayRoundUseCase(entropyRepository: EntropyRepository): PlayRoundUseCase =
        PlayRoundUseCase(entropyRepository)

    @Provides
    fun provideGetStatsUseCase(): GetStatsUseCase = GetStatsUseCase()
}
