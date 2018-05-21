package com.unual.ibook

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.unual.tools.Book
import com.unual.tools.EpubLoader
import com.unual.tools.TagContent
import kotlinx.android.synthetic.main.activity_epublayout.*

/**
 * Created by Administrator on 2018/5/17.
 */
class EpubActivity : AppCompatActivity() {
    var tags = ArrayList<TagContent>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_epublayout)
        var epubLoader = EpubLoader()
        var filePath = intent.getStringExtra("filePath")
        var saveFilePath = intent.getStringExtra("saveFilePath")
        recycler.adapter = MyAdapter(this, tags)
        recycler.layoutManager = LinearLayoutManager(this)
        epubLoader.loadBook(filePath, { book: Book ->
            Log.e("TAG", "$filePath open ${book.bookName} at ${Thread.currentThread().name}")
            epubLoader.parseChapter(book, {
                tags.clear()
                book.catalogs.forEach {
                    tags.addAll(it.chapter?.tags ?: ArrayList())
                }
                recycler.adapter.notifyDataSetChanged()
            })
        })

    }

    class MyAdapter(context: Context, tags: ArrayList<TagContent>) : RecyclerView.Adapter<MyViewHolder>() {
        private var ctx = context
        var data = tags
        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            with(data[position]) {
                if (tagName == "img") {
                    holder.textView.visibility = View.GONE
                    holder.imageView.visibility = View.VISIBLE
                    GlideApp.with(ctx).load(content).into(holder.imageView)
                } else {
                    holder.textView.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    holder.textView.text = content
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            var view = LayoutInflater.from(ctx).inflate(R.layout.item_recycler, parent, false)
            return MyViewHolder(view)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.image)
        var textView: TextView = itemView.findViewById(R.id.text)
    }
}
