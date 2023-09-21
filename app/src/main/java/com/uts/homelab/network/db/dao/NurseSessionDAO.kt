package com.uts.homelab.network.db.dao

import androidx.room.*
import com.uts.homelab.network.dataclass.NurseRegister
import com.uts.homelab.network.dataclass.WorkingDayNurse

@Dao
interface NurseSessionDAO {
    @Insert(entity = NurseRegister::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNurseSession(nurseRegister: NurseRegister)

    @Query("SELECT * FROM nurseSession")
    suspend fun getUserAuth() : NurseRegister
    @Update(entity = NurseRegister::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNurseSession(nurseRegister: NurseRegister)
    @Delete(entity = NurseRegister::class)
    suspend fun deleteNurseSession(nurseDelete:NurseRegister)

    @Insert(entity = WorkingDayNurse::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNurseWorkingDay(workingDayNurse : WorkingDayNurse)

    @Query("SELECT * FROM workingDay limit 1")
    suspend fun getNurseWorkingDay() : WorkingDayNurse

    @Delete(entity = WorkingDayNurse::class)
    suspend fun deleteWorkingDay(workingDay:WorkingDayNurse)
}