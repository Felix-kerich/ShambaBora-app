package com.app.shamba_bora.di

import android.content.Context
import com.app.shamba_bora.data.db.AppDatabase
import com.app.shamba_bora.data.db.dao.SavedAdviceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideSavedAdviceDao(database: AppDatabase): SavedAdviceDao {
        return database.savedAdviceDao()
    }
}
