package com.joma.youtubeparcer.ui.detail_playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.joma.youtubeparcer.model.DetailPlaylist
import com.joma.youtubeparcer.repository.MainRepository

class DetailPlaylistViewModel : ViewModel() {

    fun fetchDetailPlaylistData(id: String): LiveData<DetailPlaylist>? {
        return MainRepository.fetchYoutubeDetailPlaylistData(id)
    }
}
