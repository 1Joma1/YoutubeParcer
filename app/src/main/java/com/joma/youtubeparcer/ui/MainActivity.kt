package com.joma.youtubeparcer.ui

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
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var viewModel: MainViewModel? = null
    private var adapter: PlaylistAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        initView()
        initAdapter()
        fetchPlaylist()
    }

    private fun initView() {
        internet_try_again_btn.setOnClickListener {
            fetchPlaylist()
            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()}
    }

    private fun initAdapter() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        adapter = PlaylistAdapter { item: ItemsItem -> clickItem(item) }
        recycler_view.adapter = adapter

    }

    private fun clickItem(item: ItemsItem) {

    }

    private fun fetchPlaylist() {
        val data = viewModel?.getPlaylistData()
        data?.observe(this, Observer<Playlist> {
            val model: Playlist? = data.value
            if (data.value != null) {
                updateAdapterData(model)
                no_internet_view.visibility = View.GONE
            } else {
                no_internet_view.visibility = View.VISIBLE
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateAdapterData(list: Playlist?) {
        val data = list?.items
        adapter?.update(data)
    }
}
