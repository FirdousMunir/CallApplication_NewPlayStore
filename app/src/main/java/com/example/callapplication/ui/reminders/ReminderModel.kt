package com.example.callapplication.ui.reminders

data class ReminderModel (
    var Name : String ,
    var Number : String,
    var remarks :String,
    var date : String
)
{
    override fun toString(): String {
        return super.toString()
    }
}