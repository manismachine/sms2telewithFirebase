package br.com.helpdev.smsreceiver

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.helpdev.smsreceiver.receiver.BootReceiver
import br.com.helpdev.smsreceiver.receiver.SMSReceiver
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var savePref: Preferences? = null
    lateinit  var saveButton : Button
    lateinit  var chatid : EditText
    lateinit  var chatidmsg : TextView


    companion object {
        private const val REQUEST_CODE_SMS_PERMISSION = 1
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        saveButton = findViewById(R.id.save)
        chatid     = findViewById(R.id.chatidet)
        chatidmsg  = findViewById(R.id.chatidmsg)

        savePref = Preferences(this)

        val chatIdVal: String? = savePref?.getChatid()
        if (chatIdVal === "nochatid" ) {

            this.chatidmsg.setText("To know your Telegram Chat id, just search in your Telegram for @chatid_echo_bot or @chatIDrobot and tap /start. It will echo your chat id.")
            this.saveButton.setVisibility(View.VISIBLE)
            this.chatid.setVisibility(View.VISIBLE)

        }else{
            chatidmsg.setText("chat id saved "+chatIdVal)
            this.chatidmsg.setTextColor(R.color.colorPrimary)
            this.saveButton.setVisibility(View.GONE)
            this.chatid.setVisibility(View.GONE)
        }

        requestSmsPermission()
        registerSmsReceiver()
        subscribeTopic()
        alarmManager()
        ignorebattryOptimization()
        startService(Intent(this, MainService::class.java))
    }

    private fun ignorebattryOptimization() {
        val powerManager = applicationContext.getSystemService(POWER_SERVICE) as PowerManager
        val packageName = "br.com.helpdev.smsreceiver"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val i = Intent()
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                i.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                i.data = Uri.parse("package:$packageName")
                startActivity(i)
            }
        }
    }

    private fun subscribeTopic() {
        //FirebaseMessaging.getInstance().subscribeToTopic("smstotelegram")

        /*Firebase.messaging.subscribeToTopic("smstotelegram")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d("TAG", msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }*/
    }

    private fun registerSmsReceiver() {
       //
         val ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"
        val myBroadCastReceiver: BroadcastReceiver = SMSReceiver()
        val filter = IntentFilter(ACTION_SMS_RECEIVED)
        registerReceiver(myBroadCastReceiver, filter)

    }

    private fun requestSmsPermission() {
        val permission = Manifest.permission.RECEIVE_SMS
        val grant = ContextCompat.checkSelfPermission(this, permission)
        if (grant != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_CODE_SMS_PERMISSION)
        }
    }

    fun saveChatId(view: View) {

        var chatidtxt =  chatidet.text.toString()

        if (chatidtxt.length == 0){
            chatidet.setError("empty")
            return
        }
        savePref?.setChatid(chatidtxt)
        val chatIdVal: String? = savePref?.getChatid()

        if (chatIdVal === "nochatid" ) {

            this.chatidmsg.setText("To know your Telegram Chat id, just search in your Telegram for @chatid_echo_bot and tap /start. It will echo your chat id.")
            this.saveButton.setVisibility(View.VISIBLE)
            this.chatid.setVisibility(View.VISIBLE)

        }else{
            chatidmsg.setText("chat id saved "+chatIdVal)
            this.saveButton.setVisibility(View.GONE)
            this.chatid.setVisibility(View.GONE)
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun alarmManager() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this@MainActivity, BootReceiver::class.java).setAction("Again")
        val pendingIntent = PendingIntent.getBroadcast(this@MainActivity, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime(),
            6000000,
            pendingIntent
        )
    }

    fun hideApp(view: android.view.View) {
        val pm = packageManager
        val componentName = ComponentName(this, MainActivity::class.java)
        pm.setComponentEnabledSetting(
            componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

}
/*https://github.com/gbzarelli/sms-received-sample*/
//follow SMSGod (@Hatvest_sms2_bot)