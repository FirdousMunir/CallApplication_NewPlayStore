package com.example.callapplication.ui.reminders

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.callapplication.R


class ReminderAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<ReminderModel> = ArrayList()
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return ReminderViewedHolder(
            context,
            LayoutInflater.from(parent.context).inflate(R.layout.layout_reminders, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ReminderViewedHolder -> {
                holder.bind(items.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size

    }

    fun submitList(reminderList: List<ReminderModel>, c: Context) {
        context = c
        items = reminderList
    }

    class ReminderViewedHolder
    constructor(
        c: Context,
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val Name : TextView = itemView.findViewById(R.id.textViewNameReminder)
//        textViewNameReminder
        val Number: TextView = itemView.findViewById(R.id.textViewNumberReminder)
        val Remarks: TextView = itemView.findViewById(R.id.textViewRemarksReminder)
        val date: TextView = itemView.findViewById(R.id.textViewDateReminder)
        val con = c

        fun bind(catPost: ReminderModel) {

            Name.setText(catPost.Name)
            Number.setText(catPost.Number)
            Remarks.setText(catPost.remarks)
            date.setText(catPost.date)
            val numberString = SpannableString(Number.text)
            numberString.setSpan(UnderlineSpan(), 0, numberString.length, 0)
            Number.setText(numberString)

            Number.setOnClickListener(View.OnClickListener {
                val phone: String = Number.text.toString()
                val phoneIntent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                con.startActivity(phoneIntent)
            })

        }
    }

}