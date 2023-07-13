package com.uts.homelab.network.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.uts.homelab.network.db.entity.UserSession

@Dao
interface MainDAO {

    @Insert(entity = UserSession::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserSession)
}