package com.demo.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.collection.LruCache
import java.lang.Exception
import android.graphics.BitmapFactory
import java.io.*


class MyCache {
    companion object {
        fun getLru(): LruCache<Any?, Any?>? {
            var  lru = LruCache<Any?, Any?>(100024)
            return lru
        }
        fun saveBitmapToCahche(key: String?, bitmap: Bitmap?,context : Context) {

            val cacheDir: File = context.getCacheDir()
            val f = File(cacheDir, key)

            try {
                val out = FileOutputStream(
                    f
                )
                bitmap!!.compress(
                    Bitmap.CompressFormat.JPEG,
                    100, out
                )
                out.flush()
                out.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        fun retrieveBitmapFromCache(key: String?,context : Context): Bitmap? {

            val cacheDir: File = context.getCacheDir()
            val f = File(cacheDir, key)
            var fis: FileInputStream? = null
            try {
                fis = FileInputStream(f)
            } catch (e: FileNotFoundException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
                return null
            }
            val bitmap = BitmapFactory.decodeStream(fis)
            return bitmap
        }

    }




}