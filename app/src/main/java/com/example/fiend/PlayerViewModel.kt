package com.example.fiend

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val client = InnerTubeClient()
    val player = ExoPlayer.Builder(application).build()

    private val _recommendations = MutableStateFlow<List<MusicItem>>(emptyList())
    val recommendations: StateFlow<List<MusicItem>> = _recommendations.asStateFlow()

    private val _currentSong = MutableStateFlow<MusicItem?>(null)
    val currentSong: StateFlow<MusicItem?> = _currentSong.asStateFlow()

    init {
        fetchHome()
    }

    private fun fetchHome() {
        viewModelScope.launch(Dispatchers.IO) {
            val recs = client.fetchRecommendations()
            _recommendations.value = recs
        }
    }

    fun playSong(item: MusicItem) {
        _currentSong.value = item
        viewModelScope.launch(Dispatchers.IO) {
            val streamUrl = client.getStreamUrl(item.videoId)
            if (streamUrl != null) {
                // Ensure player operates on main thread
                viewModelScope.launch(Dispatchers.Main) {
                    val mediaItem = MediaItem.fromUri(streamUrl)
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
