package com.joma.youtubeparcer.ui.detail_video

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.joma.youtubeparcer.model.DetailVideo
import com.joma.youtubeparcer.repository.MainRepository

class DetailVideoViewModel : ViewModel() {

    fun getVideoData(id: String) : LiveData<DetailVideo>? {
        return MainRepository.fetchVideoData(id)
    }

}