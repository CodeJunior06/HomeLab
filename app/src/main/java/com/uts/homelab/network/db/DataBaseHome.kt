package com.uts.homelab.network.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.uts.homelab.network.dataclass.NurseRegister
import com.uts.homelab.network.db.dao.UserSessionDAO
import com.uts.homelab.network.db.dao.NurseSessionDAO

import com.uts.homelab.network.db.entity.UserSession

@Database(entities = [UserSession::class,NurseRegister::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)

abstract class DataBaseHome : RoomDatabase() {
    abstract fun nurseSessionDao(): NurseSessionDAO
    abstract fun userSessionDao() : UserSessionDAO
}