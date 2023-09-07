package com.uts.homelab.di

import android.content.Context
import androidx.room.Room
import com.uts.homelab.network.db.DataBaseHome
import com.uts.homelab.network.db.dao.NurseSessionDAO
import com.uts.homelab.network.db.dao.AdminSessionDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext application: Context): DataBaseHome {
        return Room.databaseBuilder(application, DataBaseHome::class.java, "home.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    @Singleton
    @Provides
    fun provideMyNurseSessionDao(database: DataBaseHome): NurseSessionDAO {
        return database.nurseSessionDao()
    }
    @Singleton
    @Provides
    fun provideMyUserSessionDao(database: DataBaseHome): AdminSessionDAO {
        return database.adminSessionDao()
    }
}