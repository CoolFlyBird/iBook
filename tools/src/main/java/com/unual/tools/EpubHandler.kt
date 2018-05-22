package com.unual.tools

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Log
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler

/**
 * Created by unual on 2018/5/16.
 */
//解析opf路径
class ContainerSAXHandler : DefaultHandler() {
    private var containerFullPath: String? = null

    @Throws(SAXException::class)
    override fun startDocument() {
        super.startDocument()
    }

    @Throws(SAXException::class)
    override fun endDocument() {
        super.endDocument()
    }

    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        if (localName.equals("rootfile", ignoreCase = true)) {
            containerFullPath = attributes.getValue("full-path")
        }
        super.startElement(uri, localName, qName, attributes)
    }

    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        super.endElement(uri, localName, qName)
    }

    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        super.characters(ch, start, length)
    }

    fun getContainerFullPath(): String? {
        return containerFullPath
    }
}

//解析书本和目录信息
class ContentOpfSAXHandler : DefaultHandler() {
    private var tagName: String? = null
    lateinit var book: Book

    @Throws(SAXException::class)
    override fun startDocument() {
        book = Book()
        super.startDocument()
    }

    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        tagName = qName
        if ("item".equals(tagName!!, ignoreCase = true)) {
            if ("ncx".equals(attributes.getValue("id"), ignoreCase = true)) {
                book!!.ncxPath = attributes.getValue("href")
            }
        }
//        else if ("itemref".equals(tagName!!, ignoreCase = true)) {
//            var catalog = Catalog()
//            catalog.name = attributes.getValue("idref") + ".html"
//        }
        else if ("reference".equals(tagName!!, ignoreCase = true)) {//目录信息
            var catalog = Catalog()
            catalog.name = attributes.getValue("title")
            catalog.urlShort = attributes.getValue("href")
            book.catalogs.add(catalog)
        }
        super.startElement(uri, localName, qName, attributes)
    }

    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        val data = String(ch, start, length)
        if (!TextUtils.isEmpty(data.trim { it <= ' ' })) {
            if ("dc:title".equals(tagName!!, ignoreCase = true)) {
                book!!.bookName = data
            } else if ("dc:creator".equals(tagName!!, ignoreCase = true)) {
                book!!.author = data
            } else if ("dc:identifier".equals(tagName!!, ignoreCase = true)) {
                book!!.ISBN = data
            } else if ("dc:language".equals(tagName!!, ignoreCase = true)) {
                book!!.language = data
            } else if ("dc:date".equals(tagName!!, ignoreCase = true)) {
                book!!.date = data
            } else if ("dc:publisher".equals(tagName!!, ignoreCase = true)) {
                book!!.publisher = data
            }
        }
        super.characters(ch, start, length)
    }

    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        super.endElement(uri, localName, qName)
    }

    @Throws(SAXException::class)
    override fun endDocument() {
        super.endDocument()
    }
}

//解析章节
class OpfSAXHandler(catalogs: ArrayList<Catalog>) : DefaultHandler() {
    var catalogs: ArrayList<Catalog> = ArrayList()
    var isNavMap = false
    var isText = false
    var tagName = ""
    var curChapterPathName = ""
    var curData = ""

    init {
        this.catalogs = catalogs
    }

    @Throws(SAXException::class)
    override fun startDocument() {
        super.startDocument()
    }

    @Throws(SAXException::class)
    override fun endDocument() {
        super.endDocument()
    }

    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        tagName = qName
        if (qName == "navMap") {
            isNavMap = true
        }
        if (qName == "text") {
            isText = true
        }
        if (isNavMap && qName == "content") {
            curChapterPathName = attributes.getValue("src")
            setTocNcxChapterEntityData(curData, curChapterPathName)
        }
        super.startElement(uri, localName, qName, attributes)
    }


    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        super.endElement(uri, localName, qName)
        if (qName == "navMap" && isNavMap) {
            isNavMap = false
        }
        if (qName == "text" && isNavMap) {
            isText = false
        }
    }

    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        if (isNavMap && isText && tagName == "text") {
            curData = String(ch, start, length)
        }
        super.characters(ch, start, length)
    }

    private fun setTocNcxChapterEntityData(data: String, keyString: String) {
        for (chapter in catalogs) {
            if (keyString == chapter.urlShort) {
                chapter.name = data
                break
            }
        }
    }
}

//解析内容
class ChapterHandler(catalog: Catalog) : DefaultHandler() {
    private lateinit var chapter: Chapter
    lateinit var tag: TagContent
    var c = catalog

    override fun startDocument() {
        super.startDocument()
        chapter = Chapter()
        c.chapter = chapter
    }

    override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes) {
        tag = TagContent()
        chapter.tags.add(tag)
        tag.tagName = localName ?: ""
        if (tag.tagName.trim() == "img") {
            var value = attributes.getValue("src")
            tag.content = c.path + value
        }
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        if (tag.tagName.trim() == "img") {
        } else {
            var s = String(ch, start, length)
            tag.content = tag.content + s
        }
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
//        Log.e("TAG", "${tag.tagName} - ${localName} ${(tag.tagName == localName)} ${tag.content}")
//        chapter.tags.add(tag)
    }
}

class ContentParse(tagContents: ArrayList<TagContent>, w: Int, h: Int) {
    var pages: ArrayList<Page> = ArrayList()
    var tags = tagContents
    var width = w
    var height = h

    fun parse(callback: (Page) -> Unit) {
        for (tag in tags) {
            var bitmap = BitmapFactory.decodeFile(tag.content, BitmapFactory.Options())
            var h = bitmap.height
            var w = bitmap.width
        }
    }
}