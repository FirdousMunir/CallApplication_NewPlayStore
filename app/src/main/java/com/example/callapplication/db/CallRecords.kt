package com.example.callapplication.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class CallRecords(
    @ColumnInfo(name = "caller_name") val callerName: String?,
    @ColumnInfo(name = "caller_num") val callerNum: String?,
    @ColumnInfo(name = "followup_date") val followupDate: String?,
    @ColumnInfo(name = "remarks") val remarks: String?
) {
    @PrimaryKey(autoGenerate = true)
    var Id: Int = 0
}