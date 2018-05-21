package com.unual.tools

/**
 * Created by unual on 2018/5/15.
 *
 */
class Book {
    var bookName: String = ""//书名
    var author: String = ""//作者
    var ISBN: String = ""//编号
    var language: String = ""//语言
    var date: String = ""//发布时间
    var publisher: String = ""//出版社
    var cover: String = ""//封面
    var ncxPath: String = ""//路径
    var catalogs: ArrayList<Catalog> = ArrayList()//章节
}

class Catalog {
    var index: Int = 0//序号
    var name: String = ""//目录名
    var page: String = ""//对应页码
    var path: String = ""//地址前缀
    var url: String = ""//对应地址
    var urlShort: String = ""//短地址
    var chapter: Chapter? = null
}

class Chapter {
    var title: String = ""//标题
    var tags: ArrayList<TagContent> = ArrayList()
    var content: String = ""//内容
    var totalPage: Int = 0//总页数
    var current: Int = 0//当前页数
    var currentContent: String = ""//当前内容
}

class TagContent {
    var tagName: String = ""
    var attName: String = ""
    var attValue: String = ""
    var content: String = "    "
}