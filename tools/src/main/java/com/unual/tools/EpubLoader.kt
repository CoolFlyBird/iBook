package com.unual.tools

/**
 * Created by Administrator on 2018/5/15.
 */
class EpubLoader : LoaderInterface {
    override fun loadBook(path: String) {
        startUnZip(path, path.substring(0, path.lastIndexOf(".")))
    }

    override fun loadCatalog(id: String) {

    }

    override fun loadChapter(id: String) {

    }

    fun startUnZip(path: String, savePath: String) {

    }
}