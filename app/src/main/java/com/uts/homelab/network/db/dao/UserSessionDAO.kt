package com.uts.homelab.network.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.uts.homelab.network.db.entity.UserSession

@Dao
interface UserSessionDAO {

    @Query("SELECT * FROM userSession")
    suspend fun getUserAuth() : UserSession

    @Query("DELETE FROM userSession WHERE id = :idUserSession")
    suspend fun deleteUserAuth(idUserSession: String)
}