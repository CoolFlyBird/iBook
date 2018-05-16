package com.unual.tools

import org.apache.tools.zip.ZipEntry
import org.apache.tools.zip.ZipFile
import org.apache.tools.zip.ZipOutputStream
import java.io.*
import java.util.*

/**
 * Created by Administrator on 2018/5/15.
 */
class FileUtil {
    companion object {
        /*************************** 文件操作 ***************************************/
        /**
         * 删除指定文件夹
         * @param file File
         */
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
         * 获取指定文件夹大小
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

        /*************************** Zip压缩 ***************************************/
        enum class ZIPMODE {
            COVER, // 覆盖
            UPDATE; // 更新
        }

        /**
         * 压缩文件-由于out要在递归调用外,所以封装一个方法用来
         * 调用zipFiles(ZipOutputStream out,String path,File... srcFiles)
         * @param zip
         * @param path
         * @param srcFiles
         * @throws java.io.IOException
         * @author isea533
         */
        @Throws(IOException::class, RuntimeException::class)
        fun zipFiles(zip: File, path: String, vararg srcFiles: File) {
            val out = ZipOutputStream(FileOutputStream(zip))
            zipFiles(out, path, *srcFiles)
            out.close()
        }

        /**
         * 压缩文件-File
         * @param out      输入流
         * @param srcFiles 被压缩源文件
         */
        fun zipFiles(out: ZipOutputStream, path: String, vararg srcFiles: File) {
            var path = path
            path = path.replace("\\*".toRegex(), "/")
            if (!path.endsWith("/")) {
                path += "/"
            }
            val buf = ByteArray(1024)
            try {
                for (i in srcFiles.indices) {
                    if (srcFiles[i].isDirectory) {
                        val files = srcFiles[i].listFiles()
                        var srcPath = srcFiles[i].name
                        srcPath = srcPath.replace("\\*".toRegex(), "/")
                        if (!srcPath.endsWith("/")) {
                            srcPath += "/"
                        }
                        out.putNextEntry(ZipEntry(path + srcPath))
                        zipFiles(out, path + srcPath, *files)
                    } else {
                        val inStream = FileInputStream(srcFiles[i])
                        out.putNextEntry(ZipEntry(path + srcFiles[i].name))
                        var len: Int
                        while (true) {
                            len = inStream.read(buf)
                            if (len <= 0) break
                            out.write(buf, 0, len)
                        }
                        out.closeEntry()
                        inStream.close()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        /**************************** ��ѹzip�� **************************************/
        /**
         * 解压到指定目录
         *
         * @param zipPath 文件路径名称
         * @param descDir 解压路径名称
         */
        @Throws(IOException::class, RuntimeException::class)
        fun unZipFiles(zipPath: String, descDir: String): Boolean {
            return unZipFiles(File(zipPath), descDir, *arrayOf())
        }

        /**
         * @param zipPath 文件路径名称
         * @param descDir 解压路径名称
         * @param zipMode 解压模式
         */
        @Throws(IOException::class, RuntimeException::class)
        fun unZipFiles(zipPath: String, descDir: String, zipMode: ZIPMODE): Boolean {
            return unZipFiles(File(zipPath), descDir, zipMode, *arrayOf<String>())
        }

        @Throws(IOException::class, RuntimeException::class)
        fun unZipFiles(zipPath: String, descDir: String, zipMode: ZIPMODE, vararg exceptFiles: String): Boolean {
            return unZipFiles(File(zipPath), descDir, zipMode, *exceptFiles)
        }

        @Throws(IOException::class, RuntimeException::class)
        fun unZipFiles(zipPath: String, descDir: String, vararg exceptFiles: String): Boolean {
            return unZipFiles(File(zipPath), descDir, *exceptFiles)
        }

        @Throws(IOException::class, RuntimeException::class)
        fun unZipFiles(zipFile: File, descDir: String, vararg exceptFileNames: String): Boolean {
            return unZipFiles(zipFile, descDir, ZIPMODE.COVER, *exceptFileNames)
        }

        @Synchronized
        @Throws(IOException::class, RuntimeException::class)
        fun unZipFiles(zipFile: File, descDir: String, zipMode: ZIPMODE, vararg exceptFileNames: String): Boolean {
            var descDir = descDir
            val pathFile = File(descDir)
            if (!pathFile.exists()) {
                pathFile.mkdirs()
            }
            if (!descDir.endsWith(File.separator)) {
                descDir = descDir + File.separator
            }
            val zip = ZipFile(zipFile.path, "gbk")

            val entries = zip.getEntries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement() as ZipEntry

                val zipEntryName = entry.getName()
                if (exceptFileNames != null) {
                    val exceptFileNameList = Arrays.asList(*exceptFileNames)
                    if (exceptFileNameList.contains(zipEntryName)) {
                        continue
                    }
                }
                var `in` = zip.getInputStream(entry)
                val outPath = (descDir + zipEntryName).replace("\\*".toRegex(), "/")
                // 判断路径是否存在,不存在则创建文件路径
                val file = File(outPath.substring(0, outPath.lastIndexOf('/')))
                if (!file.exists()) {
                    file.mkdirs()
                }
                // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if (File(outPath).isDirectory) {
                    continue
                }
                if (zipMode == ZIPMODE.UPDATE && File(outPath).exists()) continue
                if (zipMode == ZIPMODE.COVER) {
                }

                // 输出文件路径信息
                var out: OutputStream? = FileOutputStream(outPath)
                val buf1 = ByteArray(4096)
                var len: Int
                while (true) {
                    len = `in`!!.read(buf1)
                    if (len <= 0) break
                    out!!.write(buf1, 0, len)
                }
                `in`!!.close()
                out!!.close()
                `in` = null
                out = null
            }
            return true

        }

        @Synchronized
        @Throws(IOException::class)
        fun unZipAssignFiles(zipFile: File, descDir: String, vararg assignFileNames: String): Boolean {
            var descDir = descDir
            val pathFile = File(descDir)
            if (!pathFile.exists()) {
                pathFile.mkdirs()
            }
            if (!descDir.endsWith(File.separator)) {
                descDir = descDir + File.separator
            }
            val zip = ZipFile(zipFile.path, "gbk")
            var assignFileNameList: List<String>? = null
            if (assignFileNames != null) {
                assignFileNameList = Arrays.asList(*assignFileNames)
            }
            if (assignFileNameList == null || assignFileNameList.isEmpty()) {
                return false
            }
            val entries = zip.getEntries()
            while (entries.hasMoreElements()) {

                val entry = entries.nextElement() as ZipEntry
                val zipEntryName = entry.getName()
                for (i in assignFileNameList.indices) {
                    val assignFileName = assignFileNameList[i]
                    if (assignFileName != zipEntryName && !zipEntryName.contains(assignFileName)) {
                        continue
                    }
                    var `in` = zip.getInputStream(entry)
                    val outPath = (descDir + zipEntryName).replace("\\*".toRegex(), "/")
                    val file = File(outPath.substring(0, outPath.lastIndexOf('/')))
                    if (!file.exists()) {
                        file.mkdirs()
                    }
                    if (File(outPath).isDirectory) {

                        continue
                    }
                    var out: OutputStream? = FileOutputStream(outPath)
                    val buf1 = ByteArray(4096)
                    var len: Int
                    while (true) {
                        len = `in`!!.read(buf1)
                        if (len <= 0) break
                        out!!.write(buf1, 0, len)
                    }
                    `in`!!.close()
                    out!!.close()
                    `in` = null
                    out = null
                }
            }
            return true

        }

        val UTF8_BOM = "\uFEFF"
        private fun removeUTF8BOM(s: String): String {
            var s = s
            if (s.startsWith(UTF8_BOM)) {
                s = s.substring(1)
            }
            return s
        }

        @Synchronized
        @Throws(IOException::class, RuntimeException::class)
        fun unZipAssignFile(zipFile: File, assignFileName: String): InputStream? {
            val zip = ZipFile(zipFile.path, "gbk")
            val entries = zip.getEntries()
            while (entries.hasMoreElements()) {

                val entry = entries.nextElement() as ZipEntry
                val zipEntryName = entry.getName()
                if (assignFileName != zipEntryName) {
                    continue
                }
                return zip.getInputStream(entry)
            }
            return null
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