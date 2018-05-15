package com.unual.tools

import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * Created by Administrator on 2018/5/15.
 */
class FileUtil {
    companion object {
        fun deleteSDCardFolder(dir: File): Boolean {
            val to = File(dir.absolutePath + System.currentTimeMillis())
            dir.renameTo(to)
            if (to.isDirectory) {
                val children = to.list()
                for (i in children!!.indices) {
                    val temp = File(to, children[i])
                    if (temp.isDirectory) {
                        deleteSDCardFolder(temp)
                    } else {
                        val b = deleteSDCardFolder(temp)
                        //                    boolean b = temp.delete();
                        if (b == false) {
                            //                        Log.d("deleteSDCardFolder", "DELETE FAIL");
                            return false
                        }
                    }
                }
                return to.delete()
            } else {
                return to.delete()
            }
        }

        /**
         * 获取指定文件大小
         * @param file File
         */
        private fun getFileSize(file: File): Long {
            var fis: FileInputStream? = null
            var size: Long = 0
            try {
                if (file.exists()) {
                    fis = FileInputStream(file)
                    size = fis.available().toLong()
                }
            } catch (e: Throwable) {
            } finally {
                if (fis != null) {
                    try {
                        fis.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return size
        }

        /**
         * 获取指定文件夹
         * @param f File
         */
        fun getFileContentSize(f: File): Long {
            var size: Long = 0
            val flist = f.listFiles()
            if (flist != null) {
                for (i in flist.indices) {
                    if (flist[i].isDirectory) {
                        size = size + getFileContentSize(flist[i])
                    } else {
                        size = size + getFileSize(flist[i])
                    }
                }
            }
            return size
        }

        /**
         * The number of bytes in a kilobyte.
         */
        val ONE_KB: Long = 1024
        /**
         * The number of bytes in a megabyte.
         */
        val ONE_MB = ONE_KB * ONE_KB
        /**
         * The file copy buffer size (10 MB) （原为30MB，为更适合在手机上使用，将其改为10MB，by
         * Geek_Soledad)
         */
        private val FILE_COPY_BUFFER_SIZE = ONE_MB * 10
    }
}