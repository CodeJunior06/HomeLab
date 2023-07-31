package com.uts.homelab.network.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uts.homelab.network.dataclass.NurseRegister

@Dao
interface NurseSessionDAO {
    @Insert(entity = NurseRegister::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNurseSession(nurseRegister: NurseRegister)

    @Query("SELECT * FROM nurseSession")
    suspend fun getUserAuth() : NurseRegister
}