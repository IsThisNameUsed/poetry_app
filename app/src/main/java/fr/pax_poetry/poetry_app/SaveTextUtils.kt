package fr.pax_poetry.poetry_app

import android.widget.Toast

import android.R
import android.content.Context
import java.io.*
import java.lang.StringBuilder
import android.os.Environment
import android.util.Log
import java.util.regex.Pattern


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

        fun deleteFromFile(context: Context?, file: File, textToDelete: String){
            var br = BufferedReader(FileReader(file))
            var line = br.readLine()
            var text : String = ""
            var lineCount = 0
            var startLine = 0

            while(line != null)
            {
                Log.d("D","deleteFromFile")
                lineCount += 1
                text+=line
                if(line.contains(separator))
                {
                    text = text.dropLast(1)
                    if(text.equals(textToDelete))
                    {
                        br = BufferedReader(FileReader(file))
                        var lines = br.readLines()
                        val startList = lines.take(startLine)
                        val endList = lines.drop(startLine+lineCount)
                        lines = startList + endList
                        var textToSave = ""
                        for(string in lines)
                        {
                            textToSave += string +"\n"
                        }
                        //lines = lines?.take(start) + lines?.drop(start+lineCount-1)
                        writeOnFile(context, textToSave, file, false)
                        break
                    }
                    else
                    {
                        startLine = startLine + lineCount
                        lineCount = 0
                        text = ""
                        line = br.readLine()
                    }
                }
            }
        }

        private fun writeOnFile(context: Context?, text: String, file: File, append:Boolean=true) {
            try {
                file.parentFile.mkdirs()
                val fos = FileOutputStream(file, append)
                val w: Writer = BufferedWriter(OutputStreamWriter(fos))
                try {
                    w.write(text)
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

        fun deleteTextFromStorage( rootDestination: File?, context: Context?, fileName: String?, folderName: String?, textToDelete: String){
            val file = createOrGetFile(rootDestination!!, fileName!!, folderName!!)
            return deleteFromFile(context!!, file, textToDelete)
        }

        fun setTextInStorage(rootDestination: File?,context: Context?,fileName: String?,folderName: String?,text: String?, append:Boolean=true) {
            val file = createOrGetFile(rootDestination!!, fileName!!, folderName!!)
            writeOnFile(context!!, text!!, file, append)
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

        fun cleanStringWhithPattern(sample:String, regex:String ):String {

            if (sample != null && regex != null) {
                val pattern = Pattern.compile(regex)
                val matcher = pattern.matcher(sample)

                if (matcher.find()) {
                    return matcher.replaceAll("");
                }
                return sample
            }
            return sample
        }
    }
}