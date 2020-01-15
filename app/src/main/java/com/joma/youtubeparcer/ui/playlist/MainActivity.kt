package com.joma.youtubeparcer.ui.playlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.joma.youtubeparcer.R
import com.joma.youtubeparcer.adapter.PlaylistAdapter
import com.joma.youtubeparcer.model.ItemsItem
import com.joma.youtubeparcer.model.Playlist
import com.joma.youtubeparcer.ui.detail_playlist.DetailPlaylistActivity
import com.joma.youtubeparcer.utils.InternetHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var viewModel: MainViewModel? = null
    private var adapter: PlaylistAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        initView()
        initAdapter()
        getDataFromDatabase()
    }

    private fun initView() {
        internet_try_again_btn.setOnClickListener {
            if (InternetHelper().checkInternetConnection(this)) {
                fetchPlaylist()
                Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initAdapter() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        adapter = PlaylistAdapter { item: ItemsItem -> clickItem(item) }
        recycler_view.adapter = adapter

    }

    private fun clickItem(item: ItemsItem) {
        val intent = Intent(this, DetailPlaylistActivity::class.java)
        intent.putExtra("id", item.id)
        intent.putExtra("title", item.snippet.title)
        intent.putExtra("channelTitle", item.snippet.channelId)
        intent.putExtra("etag", item.etag)
        intent.putExtra("description", item.snippet.description)
        startActivity(intent)
    }

    private fun fetchPlaylist() {
        if (InternetHelper().checkInternetConnection(this)) {
            no_internet_view.visibility = View.GONE
            recycler_view.visibility = View.VISIBLE
            val data = viewModel?.getPlaylistData()
            data?.observe(this, Observer<Playlist> {
                val model: Playlist? = data.value
                when {
                    model != null -> {
                        updateDatabasePlaylist(model)
                        updateAdapterData(model)
                    }
                }
            })
        } else {
            showEmptyState()
        }
    }

    private fun showEmptyState() {
        no_internet_view.visibility = View.VISIBLE
        recycler_view.visibility = View.GONE
    }

    private fun updateAdapterData(model: Playlist?) {
        val data = model?.items
        adapter?.update(data)
    }

    private fun updateDatabasePlaylist(model: Playlist?) {
        model?.let { viewModel?.insertPlaylistData(it) }
    }

    private fun getDataFromDatabase() {
        CoroutineScope(Dispatchers.Main).launch {
            val model = viewModel?.getDataFromDB()
            if (model != null && !model.items.isNullOrEmpty()) {
                updateAdapterData(model)
                fetchNewPlaylistData()
            } else {
                fetchPlaylist()
            }
        }
    }

    private fun fetchNewPlaylistData() {
        val data = viewModel?.getPlaylistData()
        data?.observe(this, Observer<Playlist> {
            val model: Playlist? = data.value
            when {
                model != null -> {
                    updateDatabasePlaylist(model)
                    updateAdapterData(model)
                }
            }
        })
    }
}
