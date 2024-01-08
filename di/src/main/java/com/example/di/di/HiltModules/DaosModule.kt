package com.example.di.di.hiltModules

import android.content.Context
import androidx.room.Room
import com.example.data.room.AppDatabase
import com.example.data.room.ChannelDao
import com.example.data.room.EpgDao
import com.example.data.room.FavoriteChannelDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DaosModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(appContext, AppDatabase::class.java, "database.db").build()
    }

    @Provides
    @Singleton
    fun provideEpgDao(appDatabase: AppDatabase): EpgDao {
        return appDatabase.getEpgDao()
    }

    @Provides
    @Singleton
    fun provideChannelDao(appDatabase: AppDatabase): ChannelDao {
        return appDatabase.getChannelDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteChannelDao(appDatabase: AppDatabase): FavoriteChannelDao {
        return appDatabase.getFavoriteChannelDao()
    }
}