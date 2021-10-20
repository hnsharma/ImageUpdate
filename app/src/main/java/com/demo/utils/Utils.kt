package com.demo.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import java.math.BigInteger
import java.security.MessageDigest

class Utils {
    companion object {
        fun isOnline(context: Context?): Boolean {
            if (context == null) {
                return false
            }
            val conMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (conMgr != null) {
                val networkInfo = conMgr.activeNetworkInfo
                if (networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected) {
                    return true
                }
            }
            return false
        }
        fun showToast(context: Context?,message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun md5(string:String): String {

            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(string.toByteArray())).toString(16).padStart(32, '0')
        }
    }
}