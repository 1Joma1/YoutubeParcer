package com.joma.youtubeparcer.ui.detail_video

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joma.youtubeparcer.model.DetailPlaylist
import com.joma.youtubeparcer.model.DetailVideo
import com.joma.youtubeparcer.repository.MainRepository
import com.joma.youtubeparcer.utils.App
import kotlinx.coroutines.launch

class DetailVideoViewModel : ViewModel() {

    val db = App().getInstance().getDatabase()

    fun getVideoData(id: String) : LiveData<DetailVideo>? {
        return MainRepository.fetchVideoData(id)
    }

    suspend fun getDetailVideoData(): List<DetailVideo>? {
        return db.ytVideoDao().getDetailVideo()
    }

    fun insertDetailVideoData(model: DetailVideo) {
        viewModelScope.launch {
            db.ytVideoDao().insertDetailVideo(model)
        }
    }

}