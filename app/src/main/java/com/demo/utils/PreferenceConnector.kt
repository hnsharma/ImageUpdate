package com.demo.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.HashMap

class PreferenceConnector {

    companion object {
        val PREF_NAME = "demoimage"
        val MODE = Context.MODE_PRIVATE
        private var stringStringHashMap = HashMap<String, String?>()
        private var booleanHashMap = HashMap<String, Boolean?>()
        private var langnHashMap = HashMap<String, Long?>()
        private var sharedPreferences: SharedPreferences? = null
        private var editor: SharedPreferences.Editor? = null
        val LAST_IMAGE_URL = "last.image.url"


        fun resetData() {
            stringStringHashMap = HashMap()
            booleanHashMap = HashMap()
            langnHashMap = HashMap()
        }

        fun writeBoolean(context: Context, key: String, value: Boolean) {
            booleanHashMap[key] = value
            getEditor(context)!!.putBoolean(key, value).commit()
        }

        fun readBoolean(
            context: Context, key: String,
            defValue: Boolean
        ): Boolean {
            return if (booleanHashMap[key] != null) {
                booleanHashMap[key]!!
            } else {
                val b = getPreferences(context)!!.getBoolean(key, defValue)
                booleanHashMap[key] = b
                b
            }
        }

        fun writeInteger(context: Context, key: String?, value: Int) {
            getEditor(context)!!.putInt(key, value).commit()
        }

        fun readInteger(context: Context, key: String?, defValue: Int): Int {
            return getPreferences(context)!!.getInt(key, defValue)
        }

        fun writeString(context: Context, key: String, value: String?) {
            stringStringHashMap[key] = value
            getEditor(context)!!.putString(key, value).commit()
        }

        fun readString(context: Context, key: String, defValue: String?): String? {
            return if (stringStringHashMap[key] != null) {
                stringStringHashMap[key]
            } else {
                val s = getPreferences(context)!!.getString(key, defValue)
                stringStringHashMap[key] = s
                s
            }
        }

        fun writeFloat(context: Context, key: String?, value: Float) {
            getEditor(context)!!.putFloat(key, value).commit()
        }

        fun readFloat(context: Context, key: String?, defValue: Float): Float {
            return getPreferences(context)!!.getFloat(key, defValue)
        }

        fun writeLong(context: Context, key: String, value: Long) {
            langnHashMap[key] = value
            getEditor(context)!!.putLong(key, value).commit()
        }

        fun readLong(context: Context, key: String, defValue: Long): Long {
            return if (langnHashMap[key] != null) {
                langnHashMap[key]!!
            } else {
                val s = getPreferences(context)!!.getLong(key, defValue)
                langnHashMap[key] = s
                s
            }
            //return getPreferences(context).getLong(key, defValue);
        }

        fun clearPrefrence(context: Context) {
            getPreferences(context)!!.edit().clear().commit()
        }

        fun getPreferences(context: Context): SharedPreferences? {
            return if (sharedPreferences != null) {
                sharedPreferences
            } else {
                sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE)
                sharedPreferences
            }
        }

        fun getEditor(context: Context): SharedPreferences.Editor? {
            return if (editor != null) {
                editor
            } else {
                editor = getPreferences(context)!!.edit()
                editor
            }
        }
    }
}