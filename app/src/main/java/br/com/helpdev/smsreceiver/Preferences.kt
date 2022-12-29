package br.com.helpdev.smsreceiver

import android.content.Context
import android.content.SharedPreferences

class Preferences() {

    var savepref: SharedPreferences? = null
    var mContext: Context? = null

    constructor (context: Context?) : this() {
        mContext = context
        savepref = mContext!!.getSharedPreferences("savepref", 0)
    }


    fun setChatid(encIMEIString: String?) {
        val editor = savepref!!.edit()
        editor.putString("Chatid", encIMEIString)
        editor.apply()
    }

    fun getChatid(): String? {
        return savepref!!.getString("Chatid", "nochatid")
    }

    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    fun getbrodTime(): Long {
        return savepref!!.getLong("brodLastTime", 0)
    }

    fun setbrodTime(res: Long) {
        val editor = savepref!!.edit()
        editor.putLong("brodLastTime", res)
        editor.apply()
    }

    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

    fun setCMD(encIMEIString: String?) {
        val editor = savepref!!.edit()
        editor.putString("CMD", encIMEIString)
        editor.apply()
    }

    fun getCMD(): String? {
        return savepref!!.getString("CMD", "/startsms")
    }

    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^


}