package com.example.callapplication.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CallDataDao {
    @Insert
    fun insertData(vararg callData: CallRecords)

    @Query("SELECT * FROM CallRecords")
    fun getAll(): List<CallRecords>
}