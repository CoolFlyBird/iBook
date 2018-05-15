package com.unual.tools

import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * Created by Administrator on 2018/5/15.
 */
class FileUtil {
    companion object {
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
    }
}