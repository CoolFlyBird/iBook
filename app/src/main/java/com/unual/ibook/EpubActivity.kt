package com.unual.ibook

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.unual.tools.EpubLoader

/**
 * Created by Administrator on 2018/5/17.
 */
class EpubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var epubLoader = EpubLoader()

        var filePath = intent.getStringExtra("filePath")
        var saveFilePath = intent.getStringExtra("saveFilePath")

        epubLoader.loadBook(filePath)
    }
}