package com.zyra.music.zyra.exoplayer

import android.util.Log
import androidx.core.bundle.Bundle
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.zyra.music.zyra.domain.model.TrackFullOne

private const val TAG = "MusicQueueManagerNew"
private const val KEY_INSTANCE_ID = "com.zyra.music.INSTANCE_ID"

class NewMusicQueueManager {

    private fun TrackFullOne.toMediaItem(): MediaItem {

        val metadata = MediaMetadata.Builder()
            .setTitle(this.title)
            .setArtist(this.artistName)
            .setArtworkUri(this.thumbnail.toUri())
            .setAlbumTitle(this.artistName)
            .build()
        val extras = Bundle().apply {
            putString(KEY_INSTANCE_ID,this@toMediaItem.queueInstanceId)
        }

        return MediaItem.Builder()
            .setMediaId(this.videoId)
            .setMediaMetadata(metadata)
            .setRequestMetadata(MediaItem.RequestMetadata.Builder().setExtras(extras).build())
            .build()

    }

    fun setQueueAndPlay(controller: MediaController?, tracks: List<TrackFullOne>, startIndex: Int,shuffle : Boolean = false) {
        if (tracks.isEmpty() || startIndex !in tracks.indices) return

        val trackList = if (shuffle) tracks.shuffled() else tracks
        val finalIndex = if (shuffle) 0 else startIndex

        val mediaItems = trackList.map { it.toMediaItem() }
        controller?.setMediaItems(mediaItems, finalIndex, C.TIME_UNSET)
        controller?.prepare()
        controller?.play()

        Log.d(TAG, "Set new queue with ${tracks.size} songs, starting at index $startIndex")

    }

    fun addSongToQueue(controller: MediaController?, track: TrackFullOne) {
        controller?.addMediaItem(track.toMediaItem())
        Log.d(TAG, "Sent command to add song to queue: ${track.title}")
    }


    fun addSongToPlayNext(controller: MediaController?, track: TrackFullOne) {
        val nextIndex =
            if (controller?.playbackState == Player.STATE_IDLE) 0 else (controller?.currentMediaItemIndex
                ?: -1) + 1
        controller?.addMediaItem(nextIndex, track.toMediaItem())
        Log.d(TAG, "Sent command to add ${track.title} to play next at index $nextIndex")
    }

    fun replaceUpComingQueue(controller: MediaController?, newTrack: List<TrackFullOne>) {

        val mediaItemCount = controller?.mediaItemCount ?: 0
        val mediaItems = newTrack.map { it.toMediaItem() }
        val currentIndex = controller?.currentMediaItemIndex ?: -1

        if (mediaItemCount > currentIndex + 1) {
            controller?.removeMediaItems(currentIndex + 2, mediaItemCount)
        }
        controller?.addMediaItems(mediaItems)
        Log.d(TAG, "Sent command to replace upcoming queue with ${newTrack.size} new songs")
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

    fun moveSongInQueue(controller: MediaController?, fromIndex : Int, toIndex : Int){
        val count = controller?.mediaItemCount ?: 0
        if(fromIndex in 0 until count && toIndex in 0 until count && fromIndex != toIndex){
            controller?.moveMediaItem(fromIndex,toIndex)
            Log.d(TAG,"${controller?.mediaMetadata?.title} move from $fromIndex to $toIndex")
        }
    }

    fun removeSongFromQueue(controller: MediaController?, index: Int) {
        if (index >= 0 && index < (controller?.mediaItemCount ?: 0)) {
            controller?.removeMediaItem(index)
            Log.d(TAG, "Sent command to remove song at index $index from queue")

        }
    }
}