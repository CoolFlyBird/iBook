package com.unual.tools

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
    //    lateinit var map: HashMap<String, String>
    lateinit var book: Book

    @Throws(SAXException::class)
    override fun startDocument() {
//        map = HashMap()
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
//            map.put(attributes.getValue("id"), attributes.getValue("href"))
        }
//        else if ("itemref".equals(tagName!!, ignoreCase = true)) {
//            var catalog = Catalog()
//            catalog.name = attributes.getValue("idref") + ".html"
//        }
        else if ("reference".equals(tagName!!, ignoreCase = true)) {//目录信息
            var catalog = Catalog()
            catalog.name = attributes.getValue("title")
            catalog.urlShort = attributes.getValue("href")
            book.chapterEntities.add(catalog)
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
class OpfSAXHandler(chapterEntities: ArrayList<Catalog>) : DefaultHandler() {
    var chapterEntities: ArrayList<Catalog> = ArrayList()
    var isNavMap = false
    var isText = false
    //    boolean isNavPoint = false;
    //    boolean isNavLabel = false;
    var tagName = ""
    var curChapterPathName = ""
    var curData = ""

    init {
        this.chapterEntities = chapterEntities
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
        for (chapter in chapterEntities) {
            if (keyString == chapter.urlShort) {
                chapter.name = data
                break
            }
        }
    }
}