package com.joma.youtubeparcer.ui.detail_playlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.joma.youtubeparcer.R
import com.joma.youtubeparcer.adapter.DetailPlaylistAdapter
import com.joma.youtubeparcer.model.DetailPlaylist
import com.joma.youtubeparcer.model.ItemsItem
import com.joma.youtubeparcer.ui.detail_video.DetailVideoActivity
import kotlinx.android.synthetic.main.activity_detail_playlist.*

class DetailPlaylistActivity : AppCompatActivity() {

    private lateinit var viewModel: DetailPlaylistViewModel
    private lateinit var adapter: DetailPlaylistAdapter

    private var id: String? = null
    private var title: String? = null
    private var description: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_playlist)

        viewModel = ViewModelProviders.of(this).get(DetailPlaylistViewModel::class.java)

        initAdapter()
        getIntentData()
        subscribeToViewModel()
        initActionBar()
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

        val data = id?.let { viewModel.fetchDetailPlaylistData(it) }
        data?.observe(this, Observer<DetailPlaylist> {
            if (data.value != null) {
                updateViews(data.value!!)
            }
        })
    }

    private fun updateViews(it: DetailPlaylist) {
        adapter.updateData(it.items)
    }
}
