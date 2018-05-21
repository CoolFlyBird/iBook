package com.unual.tools

import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.xml.parsers.SAXParserFactory
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream


/**
 * Created by Administrator on 2018/5/15.
 */
class EpubLoader : LoaderInterface {
    override fun loadBook(path: String, callback: (Book) -> Unit) {
        unZip(path, callback)
    }

    override fun loadCatalog(id: String) {

    }

    override fun loadChapter(id: String) {

    }

    fun unZip(path: String, callback: (Book) -> Unit?) {
        Observable.just(path)
                .subscribeOn(Schedulers.computation())
                .map { path: String ->
                    Log.e("TAG", "map1 on -> ${Thread.currentThread().name}")
                    val zipFile = File(path)
                    val savePath = path.substring(0, path.lastIndexOf("."))
                    val unZipFile = File(savePath)
                    val unZipFileSize = FileUtil.getFileContentSize(unZipFile)
                    val zipFileSize = FileUtil.getFileContentSize(zipFile)
                    if (!unZipFile.exists() || unZipFileSize < zipFileSize) {
                        FileUtil.unZipFiles(path, savePath)
                    }
                    savePath
                }
                .map { unZipFilePath: String ->
                    Log.e("TAG", "map2 on -> ${Thread.currentThread().name}")
                    val factory = SAXParserFactory.newInstance()
                    val parser = factory.newSAXParser()
                    //解析opf路径
                    val metaInfPathFile = File(unZipFilePath + File.separator + "META-INF" + File.separator + "container.xml")
                    val containerSAXHandler = ContainerSAXHandler()
                    parser.parse(metaInfPathFile, containerSAXHandler)
                    val containerFullPath = containerSAXHandler.getContainerFullPath()
                    val contentOpfPath = unZipFilePath + File.separator + containerFullPath
                    Log.e("TAG", "contentOpfPath:$contentOpfPath \n")
                    //解析书名，章节名
                    val contentOpfFile = File(contentOpfPath)
                    val contentOpfSAXHandler = ContentOpfSAXHandler()
                    parser.parse(contentOpfFile, contentOpfSAXHandler)
                    val book = contentOpfSAXHandler.book
                    val opsDirPath = File(contentOpfPath).parent + File.separator
                    book.ncxPath = opsDirPath + book.ncxPath
                    //解析章节
                    val opfSAXHandler = OpfSAXHandler(book.chapterEntities)
                    parser.parse(File(book.ncxPath), opfSAXHandler)
//                    book.getChapterEntities().remove(0)
                    for (chapter in book.chapterEntities) {
                        chapter.url = opsDirPath + chapter.urlShort
                    }
                    book
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { book: Book ->
                    Log.e("TAG", "subscribe on -> ${Thread.currentThread().name}")
                    Log.e("TAG", "book->${book.bookName} chapter ${book.chapterEntities.size}")
                    for (chapter in book.chapterEntities) {
                        Log.e("TAG", "chapter.name->${chapter.name} -- ${chapter.urlShort} -- ${chapter.url}")
                    }
                    callback.invoke(book)
                }
    }


    @Throws(Exception::class)
    fun readGuidePic(file: String): Bitmap? {
        val fileName = file.substring(0, file.length - 4)
        val zf = ZipFile(file)
        val `in` = BufferedInputStream(FileInputStream(file))
        val zin = ZipInputStream(`in`)
        var ze: ZipEntry
        while (true) {
            ze = zin.nextEntry
            if (ze == null) break
            if (ze.isDirectory()) {
                //Do nothing
            } else {
                Log.i("tag", "file - " + ze.getName() + " : " + ze.getSize() + " bytes")
                if (ze.getName().equals(fileName + "/pic/haha.png")) {
                    val `is` = zf.getInputStream(ze)
                    return BitmapFactory.decodeStream(`is`)
                }
            }
        }
        zin.closeEntry()
        return null
    }
}