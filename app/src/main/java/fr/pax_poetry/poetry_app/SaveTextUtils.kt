package fr.pax_poetry.poetry_app

import android.widget.Toast

import android.R
import android.content.Context
import java.io.*
import java.lang.StringBuilder
import android.os.Environment


class SaveTextUtils {



    companion object instance{
        var separator: Char = 31.toChar()
        private fun createOrGetFile(destination: File, fileName: String, folderName: String): File {
            val folder = File(destination, folderName)
            return File(folder, fileName)
        }


        private fun readOnFile(context: Context, file: File): String? {
            var result: String? = null
            if (file.exists()) {
                val br: BufferedReader
                try {
                    br = BufferedReader(FileReader(file))
                    try {
                        val sb = StringBuilder()
                        var line: String? = br.readLine()
                        while (line != null) {
                            sb.append(line)
                            sb.append("\n")
                            line = br.readLine()
                        }
                        result = sb.toString()
                    } finally {
                        br.close()
                    }
                } catch (e: IOException) {
                    Toast.makeText(
                        context,
                        "error_happened",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            return result
        }

        private fun writeOnFile(context: Context, text: String, file: File) {
            try {
                file.parentFile.mkdirs()
                val fos = FileOutputStream(file, true)
                val w: Writer = BufferedWriter(OutputStreamWriter(fos))
                try {
                    w.write(text)
                    w.write(separator.toString())
                    w.flush()
                    fos.fd.sync()
                } finally {
                    w.close()
                    Toast.makeText(context, "saved", Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                Toast.makeText(context, "write on file: error_happened", Toast.LENGTH_LONG).show()
            }
        }

        fun getTextFromStorage( rootDestination: File?, context: Context?, fileName: String?, folderName: String?): String? {
            val file = createOrGetFile(rootDestination!!, fileName!!, folderName!!)
            return readOnFile(context!!, file)
        }

        fun setTextInStorage(rootDestination: File?,context: Context?,fileName: String?,folderName: String?,text: String?) {
            val file = createOrGetFile(rootDestination!!, fileName!!, folderName!!)
            writeOnFile(context!!, text!!, file)
        }

        // ----------------------------------
        // EXTERNAL STORAGE
        // ----------------------------------
        fun isExternalStorageWritable(): Boolean {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

        fun isExternalStorageReadable(): Boolean {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
        }
    }
}