package com.uts.homelab.network.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.uts.homelab.network.db.dao.UserSessionDAO
import com.uts.homelab.network.db.dao.MainDAO

import com.uts.homelab.network.db.entity.UserSession

@Database(entities = [UserSession::class], version = 1, exportSchema = false)
abstract class DataBaseHome : RoomDatabase() {
    abstract fun mainDao(): MainDAO
    abstract fun userSessionDao() : UserSessionDAO
}