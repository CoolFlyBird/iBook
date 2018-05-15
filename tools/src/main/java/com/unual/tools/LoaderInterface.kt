package com.unual.tools

/**
 * Created by Administrator on 2018/5/15.
 */
interface LoaderInterface {
    fun loadBook(path: String)
    fun loadCatalog(id: String)
    fun loadChapter(id: String)
}