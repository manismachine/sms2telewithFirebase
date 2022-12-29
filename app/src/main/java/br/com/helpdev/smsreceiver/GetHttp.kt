package br.com.helpdev.smsreceiver

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class GetHttp(applicationContext: Context) {
    var savePref: Preferences? = Preferences(applicationContext)

    @RequiresApi(Build.VERSION_CODES.N)
    fun sendGet(result: URL) {

        val r = Runnable {
            with(result.openConnection() as HttpURLConnection) {
                requestMethod = "GET"  // optional default is GET
                Log.e("cmdreceived", "response $responseCode")
                var mainline = ""
                inputStream.bufferedReader().use {
                     it.lines().forEach { line ->
                         mainline += line
                     }

                    val jsonObj = JSONObject(mainline)
                    val resultJson = jsonObj.getJSONArray("result")
                    for (i in 0..resultJson!!.length() - 1) {
                        val msg = resultJson.getJSONObject(i).getString("message")
                        val msgobj = JSONObject(msg)
                        val cmd = msgobj.getString("text")
                        savePref?.setCMD(cmd)
                        //println(cmd)
                    }

                }
            }
        }
        Thread(r).start()
    }
}