package com.unual.ibook

import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.unual.tools.Book
import com.unual.tools.EpubLoader
import kotlinx.android.synthetic.main.activity_epublayout.*
import java.io.File
import java.util.HashMap

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
            if (book.chapterEntities.size > 0) {
                val webSettings = webview.settings
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }
                // 设置与Js交互的权限
                webSettings.javaScriptEnabled = true
                // 设置允许JS弹窗
                webSettings.javaScriptCanOpenWindowsAutomatically = true
                webSettings.builtInZoomControls = false
                webSettings.loadsImagesAutomatically = true
                webSettings.domStorageEnabled = true
                webview.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        // 如下方案可在非微信内部WebView的H5页面中调出微信支付
                        view.loadUrl(url)
                        return true
                    }
                }
                var file = File(book.chapterEntities[1].url)
                var url = file.toURI().toString()
                webview.loadUrl(url)
                Log.e("TAG", "load url -> $url")
            }
        })
    }

    override fun onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack()
        } else {
            super.onBackPressed()
        }
    }
}