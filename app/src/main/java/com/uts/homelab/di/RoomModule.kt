package com.uts.homelab.di

import android.app.Application
import androidx.room.Room
import com.uts.homelab.network.db.DataBaseHome
import com.uts.homelab.network.db.dao.HomeDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@InstallIn(SingletonComponent::class)
@Module

object RoomModule {
    @Provides
    @Singleton
    fun provideDatabase(application: Application): DataBaseHome {
        return Room.databaseBuilder(application, DataBaseHome::class.java, "homelab.db")
            .build()
    }

    @Provides
    fun provideMyDao(database: DataBaseHome): HomeDAO {
        return database.homeDao()
    }
}