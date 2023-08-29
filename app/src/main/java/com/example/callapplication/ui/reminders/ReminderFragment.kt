package com.example.callapplication.ui.reminders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.callapplication.R
import com.example.callapplication.db.AppDatabase
import com.example.callapplication.db.CallRecords

class ReminderFragment : Fragment() {

    private lateinit var reminderRecyclerView: RecyclerView
    private lateinit var reminderAdapter: ReminderAdapter
    var dataBase: AppDatabase? = null
    lateinit var textViewCatchyLine : TextView
    lateinit var linearLayoutCatchy : LinearLayout

    companion object {
        fun newInstance() = ReminderFragment()
    }

    private lateinit var viewModel: ReminderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val root= inflater.inflate(R.layout.fragment_reminder, container, false)

        reminderRecyclerView= root.findViewById(R.id.remindersRecyclerView)
        textViewCatchyLine=root.findViewById(R.id.textViewCatchyLine)
        linearLayoutCatchy=root.findViewById(R.id.linearLayoutCatchy)

        dataBase = Room.databaseBuilder(
            requireContext(), AppDatabase::class.java, "mydatabase"
        ).allowMainThreadQueries().build()

        reminderRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            reminderAdapter = ReminderAdapter()
            adapter = reminderAdapter
        }

        displayAllReminders()

        return root
    }

    fun displayAllReminders(){

        val reminderList = ArrayList<ReminderModel>()
        var list : List<CallRecords>? = dataBase?.insertData()?.getAll()

        if (list != null) {
            linearLayoutCatchy.visibility=View.VISIBLE
            reminderRecyclerView.visibility=View.VISIBLE
            for (rem : CallRecords in list){

                var name = rem.callerName.toString()
                var num = rem.callerNum.toString()
                var rmrks = rem.remarks.toString()
                var datee = rem.followupDate.toString()

                if(name!!.isEmpty() && num!!.isEmpty() && rmrks!!.isEmpty()){
//                    Toast.makeText(requireContext(), "data nhi h", Toast.LENGTH_SHORT).show()
                    textViewCatchyLine.visibility=View.VISIBLE
                }else{
                    linearLayoutCatchy.visibility=View.GONE
                    reminderRecyclerView.visibility=View.VISIBLE
                    reminderList.add(
                        ReminderModel(
                            name,
                            num,
                            rmrks,
                            datee
                        )
                    )
                }
            }
        }else{
//            Toast.makeText(requireContext(), "No data", Toast.LENGTH_SHORT).show()
        }
        reminderAdapter?.submitList(reminderList, requireContext())
        reminderAdapter.notifyDataSetChanged()
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProviders.of(this).get(ReminderViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}