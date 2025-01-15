package com.example.wikiapp.di

import android.content.Context
import com.example.wikiapp.data.DataStoreHelper
import com.example.wikiapp.retrofit.WikipediaApiService
import com.example.wikiapp.retrofit.WikipediaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWikipediaApi(retrofit: Retrofit): WikipediaApiService {
        return retrofit.create(WikipediaApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWikipediaRepository(api: WikipediaApiService): WikipediaRepository {
        return WikipediaRepository(api)
    }


    @Provides
    @Singleton
    fun provideDataStoreHelper(@ApplicationContext context: Context): DataStoreHelper {
        return DataStoreHelper(context)
    }
}
