package com.uts.homelab.network.db.dao

import androidx.room.*
import com.uts.homelab.network.dataclass.UserRegister

@Dao
interface UserSessionDAO {

    @Insert(entity = UserRegister::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNurseSession(nurseRegister: UserRegister)

    @Query("SELECT * FROM userSession")
    suspend fun getUserAuth(): UserRegister

    @Update(entity = UserRegister::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUserSession(nurseRegister: UserRegister)

    @Delete(entity = UserRegister::class)
    suspend fun deleteUserSession(nurseDelete: UserRegister)
}