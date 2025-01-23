package com.example.wikiapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.wikiapp.data.AppDatabase
import com.example.wikiapp.data.DataStoreHelper
import com.example.wikiapp.data.SavedPageDao
import com.example.wikiapp.retrofit.WikipediaApiService
import com.example.wikiapp.retrofit.WikipediaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Define migration INSIDE the module
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add actual migration logic if you changed the schema
            // For example, if you added a new column:
            // database.execSQL("ALTER TABLE saved_pages ADD COLUMN new_column INTEGER DEFAULT 0")
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "wiki-db"
        ).addMigrations(MIGRATION_1_2)  // Add migration here
            .build()
    }

    // Keep other @Provides functions but REMOVE the duplicate provideDatabase()
    @Provides
    @Singleton
    fun provideWikipediaRepository(
        api: WikipediaApiService,
        okHttpClient: OkHttpClient,
        dao: SavedPageDao
    ): WikipediaRepository {
        return WikipediaRepository(api, okHttpClient, dao)
    }

    @Provides
    @Singleton
    fun provideDataStoreHelper(@ApplicationContext context: Context): DataStoreHelper {
        return DataStoreHelper(context)
    }

    @Provides
    @Singleton
    fun provideSavedPageDao(database: AppDatabase): SavedPageDao {
        return database.savedPageDao()
    }
}