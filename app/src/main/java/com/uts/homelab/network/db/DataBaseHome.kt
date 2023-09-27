package com.uts.homelab.network.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.uts.homelab.network.dataclass.NurseRegister
import com.uts.homelab.network.dataclass.UserRegister
import com.uts.homelab.network.dataclass.WorkingDayNurse
import com.uts.homelab.network.db.dao.AdminSessionDAO
import com.uts.homelab.network.db.dao.NurseSessionDAO
import com.uts.homelab.network.db.dao.UserSessionDAO

import com.uts.homelab.network.dataclass.AdminSession

@Database(entities = [AdminSession::class,NurseRegister::class,UserRegister::class,WorkingDayNurse::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)

abstract class DataBaseHome : RoomDatabase() {
    abstract fun nurseSessionDao(): NurseSessionDAO
    abstract fun adminSessionDao() : AdminSessionDAO
    abstract fun userSessionDao() : UserSessionDAO
}