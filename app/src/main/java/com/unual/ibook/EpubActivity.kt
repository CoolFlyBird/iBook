package com.unual.ibook

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.unual.tools.Book
import com.unual.tools.EpubLoader
import kotlinx.android.synthetic.main.activity_epublayout.*
import java.io.File

/**
 * Created by Administrator on 2018/5/17.
 */
class EpubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_epublayout)
        var epubLoader = EpubLoader()
        var filePath = intent.getStringExtra("filePath")
        var saveFilePath = intent.getStringExtra("saveFilePath")
        epubLoader.loadBook(filePath, { book: Book ->
            Log.e("TAG", "$filePath open ${book.bookName} at ${Thread.currentThread().name}")
            epubLoader.parseChapter(book, {

            })
        })
    }
}