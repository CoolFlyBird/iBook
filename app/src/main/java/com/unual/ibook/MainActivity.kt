package com.unual.ibook

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import com.unual.tools.EpubLoader
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.ArrayList

/**
 * Created by Administrator on 2018/5/15.
 */
class MainActivity : AppCompatActivity() {
    internal var infos: MutableList<BookPathInfo> = ArrayList()
    internal lateinit var adapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        swipeRefreshLayout.setOnRefreshListener { getAllBook() }
        var loader = EpubLoader()

        adapter = BookAdapter()
        allbooklist.adapter = adapter

        allbooklist.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val epubFile = infos[position].path
            val intent = Intent(this@MainActivity, EpubActivity::class.java)
            intent.putExtra("filePath", epubFile)
            intent.putExtra("saveFilePath", epubFile?.substring(0, epubFile.lastIndexOf(".")))
            startActivity(intent)
        }

    }

    private fun getAllBook() {
        Thread(Runnable {
            loop(File("/storage/emulated/0/z/小说/全金属狂潮"))

        }).start()
    }

    private fun loop(file: File) {
        val files = file.listFiles() ?: return
        for (f in files) {
            if (f.isDirectory) {
                loop(f)
            } else if (f.isFile) {
                val fileName = f.absolutePath
                if (fileName.endsWith(".epub")) {
                    val name = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length)
                    val info = BookPathInfo()
                    info.name = name
                    info.path = fileName
                    infos.add(info)
                }
            }
        }
        runOnUiThread {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!isDestroyed && !isFinishing) {
                    swipeRefreshLayout.isRefreshing = false
                    adapter.notifyDataSetChanged()
                }
            } else {
                swipeRefreshLayout.isRefreshing = false
                adapter.notifyDataSetChanged()
            }
        }
    }


    inner class BookAdapter : BaseAdapter() {

        override fun notifyDataSetChanged() {
            super.notifyDataSetChanged()
            if (infos.isEmpty()) {
                empty.visibility = View.VISIBLE
            } else {
                empty.visibility = View.GONE
            }
        }

        override fun getCount(): Int {
            return infos.size
        }

        override fun getItem(position: Int): Any {
            return infos.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                convertView = TextView(this@MainActivity)
            }
            val textView = convertView as TextView?
            convertView.setPadding(10, 10, 10, 10)
            convertView.textSize = 25f
            val info = infos.get(position)
            textView!!.setText(info.name)
            return convertView
        }
    }

    class BookPathInfo {
        internal var name: String? = null
        internal var path: String? = null
    }
}