package com.example.callapplication.ui.home

import android.Manifest
import android.app.DatePickerDialog
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.callapplication.R
import com.example.callapplication.db.AppDatabase
import com.example.callapplication.db.CallRecords
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    lateinit var textViewNumber: TextView
    lateinit var textViewName: TextView
    lateinit var textViewFollowUpDate: TextView
    lateinit var editTextRemarks: EditText
    lateinit var buttonSave: Button
    var dataBase: AppDatabase? = null
    lateinit var remarks: String
    lateinit var linearLayoutCalendar : LinearLayout

    private val REQUEST_CODE_ASK_PERMISSIONS = 1
    private val REQUIRED_SDK_PERMISSIONS = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.WRITE_CALENDAR
    )

//    val br: BroadcastReceiver = CallStateClass()
//    val filter = IntentFilter(Intent.ACTION_CALL)

    var mnth: Int = 0
    var yearrr: Int = 0
    var dayMnth: Int = 0

    //for adding number in contact list
    val ops = ArrayList<ContentProviderOperation>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
//        homeViewModel =
//                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        dataBase = Room.databaseBuilder(
            requireContext(), AppDatabase::class.java, "mydatabase"
        ).allowMainThreadQueries().build()

        textViewNumber = root.findViewById(R.id.textViewNumber)
        textViewName = root.findViewById(R.id.textViewName)
//        textViewLocation=findViewById(R.id.textViewLocation)
        textViewFollowUpDate = root.findViewById(R.id.textViewCalendar)
        editTextRemarks = root.findViewById(R.id.editTextRemarks)
        buttonSave = root.findViewById(R.id.buttonSave)
        linearLayoutCalendar = root.findViewById(R.id.linearLayoutCalendar)

        checkPermissions()

//        val serviceIntent = Intent(requireContext(), ServiceForCall::class.java)
//        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android")
//        ContextCompat.startForegroundService(requireContext(), serviceIntent)

//        requireContext().registerReceiver(br, filter)

        val preference =
            requireContext().getSharedPreferences(
                "resources.getString(R.string.app_name",
                Context.MODE_PRIVATE
            )
        val Number = preference.getString("Number", "")
        val Name = preference.getString("Name", "")

        textViewName.text = Name

        if (Number.equals("", true)) {
            textViewNumber.hint = "Number"
        } else {
            textViewNumber.text = Number
        }

        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                dayMnth = dayOfMonth
                mnth = monthOfYear
                yearrr = year

                val myFormat = "dd-MM-yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                textViewFollowUpDate.text = sdf.format(cal.time)
            }

        linearLayoutCalendar.setOnClickListener {
            val dialog = DatePickerDialog(
                requireContext(), dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )

            dialog.datePicker.minDate = System.currentTimeMillis() - 1000
            dialog.show()
        }

        ops.add(
            ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI
            )
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build()
        )


        buttonSave.setOnClickListener {

            val callerName = textViewName.text.toString()
            val callerNumber = textViewNumber.text.toString()
            val followupDate = textViewFollowUpDate.text.toString()
            remarks = editTextRemarks.text.toString()

            if (callerName.isEmpty() || callerNumber.isEmpty() || followupDate.isEmpty() || followupDate == "Calendar" || remarks.isEmpty()
            ) {
                Toast.makeText(requireContext(), "Please Fill all Empty Fields", Toast.LENGTH_SHORT).show()
            } else {

                val records = CallRecords(callerName, callerNumber, followupDate, remarks)
//                dataBase?.insertData()?.insertData(records)

                openCalendarEvent(callerNumber)

                val digitsOnly = TextUtils.isDigitsOnly(callerNumber)
                if (digitsOnly) {
                    if (callerNumber.isEmpty()) {
                        Toast.makeText(requireContext(), "Field can't be Empty.", Toast.LENGTH_LONG).show()
                    } else {
    //                        Toast.makeText(requireContext(), "field is int value", Toast.LENGTH_LONG).show();
                        saveContactInList(callerName, callerNumber)
                    }
                }else {
                    Toast.makeText(requireContext(), "Number Must be Numeric", Toast.LENGTH_LONG).show()
                }

                textViewNumber.hint = "Number"
                textViewName.hint = "Name"
                textViewFollowUpDate.text = "Calendar"
                editTextRemarks.hint = "Remarks"

            }
        }

        return root
    }

    private fun openCalendarEvent(number: String) {

        val startMillis: Long = Calendar.getInstance().run {
            set(yearrr, mnth, dayMnth, 0, 30)
            timeInMillis
        }
        val endMillis: Long = Calendar.getInstance().run {
            set(yearrr, mnth, dayMnth, 10, 30)
            timeInMillis
        }
        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
//                .putExtra(CalendarContract.Events.DTSTART,dateStart)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            .putExtra(CalendarContract.Events.TITLE, "Call Reminder")
            .putExtra(CalendarContract.Events.DESCRIPTION, remarks + " " + number)
            .putExtra(
                CalendarContract.Events.AVAILABILITY,
                CalendarContract.Events.AVAILABILITY_BUSY
            )
            .putExtra(CalendarContract.Events.ALL_DAY, true)
//                .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com")
        startActivity(intent)
    }

    private fun saveContactInList(userName: String, userNumber: String) {

        if (userName != null) {
            ops.add(
                ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI
                )
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        userName
                    ).build()
            )
        }

        if (userNumber != null) {
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, userNumber)
                    .withValue(
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                    )
                    .build()
            )
        }

        try {
            requireContext().contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
        } catch (e: Exception) {
            e.printStackTrace()
//            Toast.makeText(requireContext(), "Exception: " + e.message, Toast.LENGTH_SHORT).show()
        }

    }

    private fun checkPermissions() {
        val missingPermissions: MutableList<String> = ArrayList()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // check all required dynamic permissions
            for (permission in REQUIRED_SDK_PERMISSIONS) {
                val result = ContextCompat.checkSelfPermission(requireContext(), permission)
                if (result != PackageManager.PERMISSION_GRANTED) {
                    missingPermissions.add(permission)
                }
            }
            if (missingPermissions.isNotEmpty()) {
                // request all missing permissions
                val permissions = missingPermissions
                    .toTypedArray()
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissions,
                    REQUEST_CODE_ASK_PERMISSIONS
                )
            } else {
                val grantResults = IntArray(REQUIRED_SDK_PERMISSIONS.size)
                Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED)
                onRequestPermissionsResult(
                    REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> {
                var index = permissions.size - 1
                while (index >= 0) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(
                            requireContext(), "Required permission '" + permissions[index]
                                    + "' not granted, exiting", Toast.LENGTH_LONG
                        ).show()
//                        finish()
                        return
                    }
                    --index
                }
                // all permissions were granted

            }
        }
    }

}