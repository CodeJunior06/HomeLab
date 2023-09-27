package com.uts.homelab.network.db.dao

import androidx.room.*
import com.uts.homelab.network.dataclass.AdminSession

@Dao
interface AdminSessionDAO {

    @Query("SELECT * FROM adminSession")
    suspend fun getAdminAuth() : AdminSession

    @Query("DELETE FROM adminSession WHERE id = :idUserSession")
    suspend fun deleteAdminAuth(idUserSession: String)
    @Insert(entity = AdminSession::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdmin(user: AdminSession)
    @Update(entity = AdminSession::class,OnConflictStrategy.REPLACE)
    suspend fun updateAdmin(user:AdminSession)
}