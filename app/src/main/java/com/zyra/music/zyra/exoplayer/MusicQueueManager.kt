package com.zyra.music.zyra.exoplayer


import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.zyra.music.zyra.domain.model.SingleTrack


private const val TAG = "MusicQueueManager"

class MusicQueueManager{

    private fun SingleTrack.toMediaItem() : MediaItem{
        val metadata = MediaMetadata.Builder()
            .setTitle(this.title)
            .setArtist(this.artistName)
            .setArtworkUri(this.thumbnail.toUri())
            .build()

        return MediaItem.Builder()
            .setMediaId(this.url)
            .setMediaMetadata(metadata)
            .build()
    }
    fun setQueueAndPlay(controller: MediaController?, tracks: List<SingleTrack>, startIndex: Int){

        if (tracks.isEmpty() || startIndex !in tracks.indices) return

        val mediaItems = tracks.map { it.toMediaItem() }
        controller?.setMediaItems(mediaItems, startIndex, C.TIME_UNSET) // Correct way to set items and start index
        controller?.prepare()
        controller?.play()

        Log.d(TAG,"Set new queue with ${tracks.size} songs, starting at index $startIndex")
    }
    fun addSongToQueue(controller: MediaController?, track: SingleTrack){
        controller?.addMediaItem(track.toMediaItem())
        Log.d(TAG, "Sent command to add song to queue: ${track.title}")
    }
    fun addSongToPlayNext(controller: MediaController?, track: SingleTrack){
        val nextIndex = if (controller?.playbackState == Player.STATE_IDLE) 0 else (controller?.currentMediaItemIndex ?: -1) + 1
        controller?.addMediaItem(nextIndex, track.toMediaItem())
        Log.d(TAG, "Sent command to add ${track.title} to play next at index $nextIndex")
    }

    fun replaceUpcomingQueue(controller: MediaController?, newTracks: List<SingleTrack>) {
//        val mediaItemCount = controller?.mediaItemCount ?: 0
//        if (mediaItemCount == 0) return
//
//        val mediaItems = newTracks.map { it.toMediaItem() }
//        val currentIndex = controller?.currentMediaItemIndex ?: -1
//
//        if (mediaItemCount > currentIndex + 1) {
//            controller?.removeMediaItems(currentIndex + 1, mediaItemCount)
//        }
//        controller?.addMediaItems(mediaItems)
//        Log.d(TAG, "Sent command to replace upcoming queue with ${newTracks.size} new songs")


        // The rest of the function is correct.
        val mediaItemCount = controller?.mediaItemCount ?: 0
        val mediaItems = newTracks.map { it.toMediaItem() }
        val currentIndex = controller?.currentMediaItemIndex ?: -1

        if (mediaItemCount > currentIndex + 1) {
            controller?.removeMediaItems(currentIndex + 1, mediaItemCount)
        }
        controller?.addMediaItems(mediaItems)
        Log.d(TAG, "Sent command to replace upcoming queue with ${newTracks.size} new songs")
    }

    fun playSongAtIndex(controller: MediaController?, index: Int) {
        if (index >= 0 && index < (controller?.mediaItemCount ?: 0)) {
            controller?.seekToDefaultPosition(index)
            if (controller?.isPlaying == false) {
                controller.play()
            }
            Log.d(TAG, "Sent command to play song at index $index")
        }
    }

    fun removeSongFromQueue(controller: MediaController?, index: Int) {
        if (index >= 0 && index < (controller?.mediaItemCount ?: 0)) {
            controller?.removeMediaItem(index)
            Log.d(TAG, "Sent command to remove song at index $index from queue")
        }
    }
}