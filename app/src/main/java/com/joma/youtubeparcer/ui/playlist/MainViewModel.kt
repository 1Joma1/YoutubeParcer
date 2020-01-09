package com.joma.youtubeparcer.ui.playlist

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.joma.youtubeparcer.model.Playlist
import com.joma.youtubeparcer.repository.MainRepository

class MainViewModel : ViewModel() {

    fun getPlaylistData() : LiveData<Playlist> {
        return MainRepository.fetchYoutubePlaylistData()
    }
}