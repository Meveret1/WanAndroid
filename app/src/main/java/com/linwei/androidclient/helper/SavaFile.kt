package com.linwei.androidclient.helper

import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.lang.Exception

class SavaFile {
    fun savastring(str:String){
        val path=Environment.getExternalStorageDirectory().absolutePath+"/AAA/Json"
        val pathfile="${path}/json.txt"
        try {
            val file = File(path)
            val f = File(pathfile)
            if (!file.exists()) {
                file.mkdirs()
            }
            if (!f.exists()) {
                f.createNewFile()
            }
            val osw = OutputStreamWriter(FileOutputStream(f), "UTF-8")
            osw.write(str)
            osw.flush()
            osw.close()
        }catch (e:Exception){
            Log.e("Expersion",e.toString())
        }

    }
}