package com.example.callapplication

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.provider.Contacts
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat

class CallStateClass : BroadcastReceiver() {

    var mContext: Context?=null
    var log_Tag="CustomPhoneStateListener"
    lateinit var lookupUri: Uri
    var contactNumber:String?=null
    var NameNumber:String?=null
    var userName:String?=null
    var checkNum:Boolean?=false
    var numList: MutableList<String> = mutableListOf<String>()
    val CHANNEL_ID = "ForegroundServiceChannel"
    val app_id = Context::getPackageName

    override fun onReceive(context: Context?, intent: Intent?) {

        mContext=context

        val bundle = intent!!.extras
        if (bundle == null)
            return
        val state = bundle.getString(TelephonyManager.EXTRA_STATE);
        if ((state != null) && (state.equals(TelephonyManager.EXTRA_STATE_RINGING))) {

//            Toast.makeText(context, "Ringing", Toast.LENGTH_SHORT).show()

        } else if (state == null) {

//            Toast.makeText(context, "outgoing", Toast.LENGTH_SHORT).show()

        } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

//            Toast.makeText(context, "callState:OFFHOOK", Toast.LENGTH_SHORT).show()

        } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

            val num = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
//            Toast.makeText(context, "nO:   $num", Toast.LENGTH_SHORT).show()

            NameNumber=num
            contactNumber = num

            contactNumber=normalizeMsisdn(contactNumber.toString())

            getContactList()

            startActivity()

            val preference = context!!.getSharedPreferences(
                "resources.getString(R.string.app_name", Context.MODE_PRIVATE
            )
            val editor = preference.edit()
            editor.putString("Number", num)
            editor.putString("Name" , userName)
            editor.commit()

//            val records = CallRecords(userName, num, "followupDate", "remarks")
//            database?.saveData()?.insertData(records)
//            Toast.makeText(context, "DataRecords:    $records", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("Range")
    private fun getContactList() {
        val cr: ContentResolver = mContext!!.getContentResolver()
        val cur = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )
        if (cur?.count ?: 0 > 0) {
            while (cur != null && cur.moveToNext()) {
                val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                    val pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null)

                    while (pCur!!.moveToNext()) {
                        var phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        phoneNo= normalizeMsisdn(phoneNo)
                        numList.add(phoneNo)
                    }
                    pCur.close()
                }
            }
        }
        cur?.close()

        if(contactNumber in numList){
//            Toast.makeText(mContext, "number h ", Toast.LENGTH_SHORT).show()
            userName=getContactName(NameNumber)
//            Toast.makeText(mContext, "name against Contact Number: $userName", Toast.LENGTH_SHORT).show()
        }else{
//            Toast.makeText(mContext, "number not available in Contact List", Toast.LENGTH_SHORT).show()
        }
//        Toast.makeText(mContext, "number list  Contains Result:    ${numList.size}", Toast.LENGTH_SHORT).show()

    }

    private fun normalizeMsisdn(msisdn: String) : String {
        if (msisdn == null) {
            return msisdn;
        }
        if (msisdn.startsWith("3") && msisdn.length == 10) {
            return msisdn;
        } else if (msisdn.startsWith("03") && msisdn.length == 11) {
            return msisdn.substring(1);
        } else if (msisdn.startsWith("92") && msisdn.length == 12) {
            return msisdn.substring(2);
        } else if (msisdn.startsWith("+92") && msisdn.length == 13) {
            return msisdn.substring(3);
        } else if (msisdn.startsWith("0092") && msisdn.length == 14) {
            return msisdn.substring(4);
        } else {
            return msisdn;
        }
    }

    private fun getContactName(phoneNumber: String?): String? {
        val uri: Uri
        var projection: Array<String?>
        var mBaseUri = Contacts.Phones.CONTENT_FILTER_URL
        projection = arrayOf(Contacts.People.NAME)
        try {
            val c = Class.forName("android.provider.ContactsContract\$PhoneLookup")
            mBaseUri = c.getField("CONTENT_FILTER_URI")[mBaseUri] as Uri
            projection = arrayOf("display_name")
        } catch (e: Exception) {
        }
        uri = Uri.withAppendedPath(mBaseUri, Uri.encode(phoneNumber))
        var cursor: Cursor? = mContext!!.getContentResolver().query(uri, projection, null, null, null)
        var contactName = ""
        if (cursor!!.moveToFirst()) {
            contactName = cursor.getString(0)
        }
        cursor.close()
//        cursor = null
        return contactName
    }

    private fun startActivity() {

        val sound: Uri = Uri.parse("android.resource://" + "com.example.callfollowupservice")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val notificationManager = mContext!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val attributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            val CHANNEL_ID = "$app_id  _notification_id"
            val CHANNEL_NAME = "$app_id  _notification_name"
            assert(notificationManager != null)
            var mChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (mChannel == null) {
                mChannel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
                mChannel.setSound(sound, attributes)
                notificationManager.createNotificationChannel(mChannel)
            }
            val builder = mContext?.let { NotificationCompat.Builder(it, CHANNEL_ID) }
            builder!!.setSmallIcon(R.drawable.icon)
                .setContentTitle("Call Follow Up Service")
                .setContentText("Phone Call Reminder")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(openScreen(5), true)
//                .setFullScreenIntent(openScreen(Constants.NOTIFICATION_ID), true)
                .setAutoCancel(true)
                .setOngoing(true)
            val notification: Notification = builder.build()
            notificationManager.notify(5,notification)

//            notificationManager.notify(Constants.NOTIFICATION_ID, notification)
        } else {
            mContext!!.startActivity(
                Intent(mContext, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    private fun openScreen(notificationId: Int): PendingIntent? {
        val fullScreenIntent = Intent(mContext, MainActivity::class.java)
        fullScreenIntent.putExtra("7",notificationId)

//        fullScreenIntent.putExtra(Constants.NOTIFICATION_IDS, notificationId)
        return PendingIntent.getActivity(mContext, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

}