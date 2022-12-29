package br.com.helpdev.smsreceiver.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import br.com.helpdev.smsreceiver.Preferences
import java.net.HttpURLConnection
import java.net.URL


class SMSReceiver : BroadcastReceiver() {
    var savePref: Preferences? = null


    companion object {
        private val TAG by lazy { SMSReceiver::class.java.simpleName }

    }

    override fun onReceive(context: Context?, intent: Intent?) {
        savePref = Preferences(context)

        val chatIdVal: String? = savePref?.getChatid()
        val cmd: String? = savePref?.getCMD()
        if (chatIdVal == "nochatid" || cmd == "/stopsms") {
            println("not sending sms")
        } else {

            //if (!intent?.action.equals(ACTION_SMS_RECEIVED)) return
            val extractMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            extractMessages.forEach { smsMessage ->
                Log.e("smsreceived", smsMessage.displayMessageBody)

                //if (smsMessage.displayMessageBody . contains("test123")){
                var token = "5777738217:AAFpYHCaPcYvgOdsQAdkf0DoYJKhdyrPWGg" //
                //var chatid = "10digit";
                var chatid = chatIdVal;
                var body = smsMessage.displayMessageBody.toString().replace("#", "hash")
                var turl = "https://api.telegram.org/bot" + token + "/sendMessage?chat_id=";
                var result = turl + chatid + "&text=" + body;
                var url = URL(result)
                sendGet(url);
            }

            // }
         }
    }

    private fun sendGet(result: URL) {

        val r = Runnable {
            with(result.openConnection() as HttpURLConnection) {
                requestMethod = "GET"  // optional default is GET
                Log.e("smsreceived", "response $responseCode")
                inputStream.bufferedReader().use {
                    /* it.lines().forEach { line ->
                         println(line)
                     }*/
                }
            }
        }
        Thread(r).start()
    }
}