package com.uts.homelab.network.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uts.homelab.network.dataclass.NurseRegister
import com.uts.homelab.network.db.entity.UserSession

@Dao
interface UserSessionDAO {

    @Query("SELECT * FROM userSession")
    suspend fun getUserAuth() : UserSession

    @Query("DELETE FROM userSession WHERE id = :idUserSession")
    suspend fun deleteUserAuth(idUserSession: String)
    @Insert(entity = UserSession::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserSession)
}