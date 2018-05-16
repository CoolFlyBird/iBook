package com.unual.tools

import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.schedulers.Schedulers
import org.apache.tools.zip.ZipUtil
import java.io.File
import java.util.*
import javax.xml.parsers.SAXParserFactory

/**
 * Created by Administrator on 2018/5/15.
 */
class EpubLoader : LoaderInterface {
    override fun loadBook(path: String) {
        startUnZip(path)
    }

    override fun loadCatalog(id: String) {

    }

    override fun loadChapter(id: String) {

    }

    fun startUnZip(path: String) {
        Observable.just(path)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .filter { a: String ->
                    val zipFile = File(a)
                    val savePath = a.substring(0, a.lastIndexOf("."))
                    val unZipFile = File(savePath)
                    val unZipFileSize = FileUtil.getFileContentSize(unZipFile)
                    val zipFileSize = FileUtil.getFileContentSize(zipFile)
                    var a = (!unZipFile.exists() || unZipFileSize < zipFileSize)
                    Log.e("TAG", "have unzip->" + a)
                    a
                }
                .map { path: String ->
                    val savePath = path.substring(0, path.lastIndexOf("."))
                    FileUtil.unZipFiles(path, savePath)
                    savePath
                }
                .map { unZipFilePath: String ->
                    val factory = SAXParserFactory.newInstance()
                    val parser = factory.newSAXParser()
                    val metaInfPathFile = File(unZipFilePath + File.separator + "META-INF" + File.separator + "container.xml")
                    val containerSAXHandler = ContainerSAXHandler()
                    parser.parse(metaInfPathFile, containerSAXHandler)
                    val containerFullPath = containerSAXHandler.getContainerFullPath()
                    val contentOpfPath = unZipFilePath + File.separator + containerFullPath
                    Log.e("TAG", "contentOpfPath:" + contentOpfPath)
                    val contentOpfFile = File(contentOpfPath)
                    val contentOpfSAXHandler = ContentOpfSAXHandler()
                    parser.parse(contentOpfFile, contentOpfSAXHandler)
                    val opfContent = contentOpfSAXHandler.getOpfContent()
                    val opsDirPath = File(contentOpfPath).parent + File.separator
                    Log.e("TAG", "opsDirPath:" + opsDirPath)
//                    for (i in 0 until opfContent.getChapterEntities().size()) {
//                        Log.e("TAG", "ncxTocs:" + opfContent.getChapterEntities().get(i).toString())
//                    }
//                    opfContent.setNcxPath(opsDirPath + opfContent.getNcxPath())
//                    Log.e("TAG", "opfContent.getNcxPath():" + opfContent.getNcxPath())
                    val opfSAXHandler = OpfSAXHandler(opfContent.getChapterEntities())
                    parser.parse(File(opfContent.getNcxPath()), opfSAXHandler)
                    opfContent.getChapterEntities().remove(0)
//                    for (i in 0 until opfContent.getChapterEntities().size()) {
//                        Log.e("TAG", "ncxTocs:" + opfContent.getChapterEntities().get(i).chapter_Title + "--" + opfContent.getChapterEntities().get(i).chapter_shortPath)
//                    }
                    for (entity in opfContent.getChapterEntities()) {
                        entity.chapter_FullPath = opsDirPath + entity.chapter_shortPath
                    }
                    Log.e("TAG", "opfContent.toString:" + opfContent.toString())
                }
    }
}