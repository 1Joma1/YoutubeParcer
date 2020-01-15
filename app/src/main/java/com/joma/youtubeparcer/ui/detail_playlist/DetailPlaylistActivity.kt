package com.joma.youtubeparcer.ui.detail_playlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.joma.youtubeparcer.R
import com.joma.youtubeparcer.adapter.DetailPlaylistAdapter
import com.joma.youtubeparcer.model.DetailPlaylist
import com.joma.youtubeparcer.model.ItemsItem
import com.joma.youtubeparcer.ui.detail_video.DetailVideoActivity
import com.joma.youtubeparcer.utils.InternetHelper
import kotlinx.android.synthetic.main.activity_detail_playlist.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailPlaylistActivity : AppCompatActivity() {

    private lateinit var viewModel: DetailPlaylistViewModel
    private lateinit var adapter: DetailPlaylistAdapter

    private var id: String? = null
    private var title: String? = null
    private var description: String? = null
    private var list: DetailPlaylist? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_playlist)

        viewModel = ViewModelProviders.of(this).get(DetailPlaylistViewModel::class.java)

        initAdapter()
        initView()
        getIntentData()
        getDetailPlaylistData()
        initActionBar()
    }

    private fun initView() {
        play_video.setOnClickListener {
            val intent = Intent(this, DetailVideoActivity::class.java)
            intent.putExtra("playlistId", id)
            intent.putExtra("videoId", list?.items?.get(0)?.snippet?.resourceId?.videoId)
            startActivity(intent)
        }
    }

    private fun getDetailPlaylistData() {
        CoroutineScope(Dispatchers.Main).launch {
            val model = viewModel.getDetailPlaylistData()
            if (model != null && !model.isNullOrEmpty()) {
                getExtraDetailPlaylistData(model)
                list = model[0]
            } else {
                if (InternetHelper().checkInternetConnection(this@DetailPlaylistActivity)) {
                    subscribeToViewModel()
                } else {
                    Toast.makeText(
                        this@DetailPlaylistActivity,
                        "No internet connection",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }
    }

    private fun getExtraDetailPlaylistData(model: List<DetailPlaylist>) {
        var detailPlaylist: DetailPlaylist? = null
        for (i in model.indices) {
            for (element in model[i].items) {
                if (element.snippet.playlistId == id) {
                    detailPlaylist = model[i]
                }
            }
        }

        if (detailPlaylist != null) updateViews(detailPlaylist)
        else {
            if (InternetHelper().checkInternetConnection(this@DetailPlaylistActivity)) {
                subscribeToViewModel()
            } else {
                if (InternetHelper().checkInternetConnection(this@DetailPlaylistActivity)) {
                    subscribeToViewModel()
                } else {
                    Toast.makeText(
                        this@DetailPlaylistActivity,
                        "No internet connection",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }
    }

    private fun initActionBar() {
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getIntentData() {
        id = intent?.getStringExtra("id")
        title = intent?.getStringExtra("title")
        description = intent?.getStringExtra("description")
        tv_title.text = title
        tv_description.text = description
    }

    private fun initAdapter() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        adapter = DetailPlaylistAdapter { item: ItemsItem -> click(item) }
        recycler_view.adapter = adapter
    }

    private fun click(item: ItemsItem) {
        val intent = Intent(this, DetailVideoActivity::class.java)
        intent.putExtra("playlistId", id)
        intent.putExtra("videoId", item.snippet.resourceId.videoId)
        startActivity(intent)
    }


    private fun subscribeToViewModel() {
        val data = id?.let {
            viewModel.fetchDetailPlaylistData(it)
        }
        data?.observe(this, Observer<DetailPlaylist> {
            if (data.value != null) {
                list = data.value
                updateViews(data.value!!)
                updateDatabasePlaylistData(data.value!!)
            }
        })
    }

    private fun updateDatabasePlaylistData(value: DetailPlaylist) {
        viewModel.insertDetailPlaylistData(value)
    }

    private fun updateViews(it: DetailPlaylist) {
        adapter.updateData(it.items)
    }
}
