package com.example.yaoyu.demokotlin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  private val data = mutableListOf<Show>()
  private val show = mutableListOf<ShowOut>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    recyclerView.run {
      layoutManager = LinearLayoutManager(context)
      adapter = MainAdapter(show) {
        changeChild(it)
      }
    }

    initData()
  }


  private fun initData() {
    (0..3).forEach {
      data.add(Show(it.toLong(), "position $it", level = 0))
    }
    (4..5).forEach {
      data.add(Show(it.toLong(), "position $it", level = 1))
    }
    (6..9).forEach {
      data.add(Show(it.toLong(), "position $it", level = 0))
    }
    (10..22).forEach {
      data.add(Show(it.toLong(), "position $it", level = 1))
    }
    data.add(Show(23, "position 13", level = 0))
    data.add(Show(24, "position 14", level = 0))
    (25..60).forEach {
      data.add(Show(it.toLong(), "position $it", level = 1))
    }
    data.add(Show(61, "position 61", level = 0))
    (62..70).forEach {
      data.add(Show(it.toLong(), "position $it", level = 1))
    }
    (71..80).forEach {
      data.add(Show(it.toLong(), "position $it", level = 2))
    }
    refreshShowList()
  }


  private fun changeChild(click: ShowOut) {
    val index = data.find { it.id == click.id }
    val position = data.indexOf(index)
    log("changeChild $position")
    val item = data[position]
    if (item.showChild) {
      log("$position 隐藏")
      hideChild(position)
    } else {
      log("$position 展开")
      expandChild(position)
    }
  }

  private fun expandChild(position: Int) {
    if (position == data.lastIndex) return
    // 向下搜索
    // 确认搜索范围
    val subList = data.subList(position + 1, data.size)
    log("subList=$subList")
    val current = data[position]
    current.showChild = true
    val find = subList.find { current.level >= it.level }
    log("find=$find")
    val hideRange = if (find == null) {
      // 后面全部是小于他的
      (position + 1) until data.size
    } else {
      val findPosition = data.indexOf(find)
      (position + 1) until findPosition
    }
    log("hideRange=$hideRange")
    hideRange.forEach {
      data[it].showSelf = true
    }

    //刷新 show 列表
    refreshShowList()
    recyclerView.adapter?.notifyDataSetChanged()
  }

  private fun hideChild(position: Int) {
    if (position == data.lastIndex) return
    // 向下搜索
    // 确认搜索范围
    val subList = data.subList(position + 1, data.size)
    log("subList=$subList")
    val current = data[position]
    current.showChild = false
    val find = subList.find { current.level >= it.level }
    log("find=$find")
    val hideRange = if (find == null) {
      // 后面全部是小于他的
      (position + 1) until data.size
    } else {
      val findPosition = data.indexOf(find)
      (position + 1) until findPosition
    }
    log("hideRange=$hideRange")
    hideRange.forEach {
      data[it].showSelf = false
    }

    //刷新 show 列表
    refreshShowList()
    recyclerView.adapter?.notifyDataSetChanged()
  }

  private fun refreshShowList() {
    show.clear()
    data.filter {
      it.showSelf
    }.forEach {
      show.add(ShowOut(it.id, it.info, it.level, it.showChild))
    }
  }

}

data class Show(
  val id: Long,
  val info: String,
  var level: Int = 0,
  var showSelf: Boolean = true,
  var showChild: Boolean = true
)

data class ShowOut(
  val id: Long,
  val info: String,
  var level: Int = 0,
  var showChild: Boolean = true
)

class MainAdapter(
  private val data: MutableList<ShowOut>
  , val changeChildClick: (ShowOut) -> Unit
) :
  RecyclerView.Adapter<MainAdapter.ViewHolder>() {

  init {
    hasStableIds()
  }

  override fun getItemId(position: Int): Long {
    return data[position].id
  }
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.list_item_show, parent, false)
    )
  }

  override fun getItemCount(): Int = data.size

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = data[position]
    holder.run {
      name.text = item.info
      showLevel(this, item)
      changeChild.setOnClickListener {
        changeChildClick.invoke(item)
      }
      changeChild.text = if (item.showChild) {
        "隐藏"
      } else {
        "展开"
      }
    }
  }


  private fun showLevel(holder: ViewHolder, item: ShowOut) {
    when (item.level) {
      0 -> {
        holder.head1.visibility = View.GONE
        holder.head2.visibility = View.GONE
      }
      1 -> {
        holder.head1.visibility = View.VISIBLE
        holder.head2.visibility = View.GONE
      }
      2 -> {
        holder.head1.visibility = View.VISIBLE
        holder.head2.visibility = View.VISIBLE
      }
    }
  }

  class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val name = view.findViewById<TextView>(R.id.name)
    val head1 = view.findViewById<View>(R.id.head1)
    val head2 = view.findViewById<View>(R.id.head2)
    val changeChild = view.findViewById<Button>(R.id.changeChild)
    val rootView = view
  }
}

const val TAG = "temp"
fun log(msg: String) {
  Log.i(TAG, msg)
}


