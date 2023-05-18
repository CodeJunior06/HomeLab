package com.uts.homelab.network.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.uts.homelab.network.db.dao.HomeDAO
import com.uts.homelab.network.db.entity.UserSession

@Database(entities = [UserSession::class], version = 1, exportSchema = false)
abstract class DataBaseHome : RoomDatabase() {
    abstract fun homeDao(): HomeDAO
}