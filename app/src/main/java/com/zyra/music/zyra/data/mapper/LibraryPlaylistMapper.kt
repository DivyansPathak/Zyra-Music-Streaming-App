package com.zyra.music.zyra.data.mapper

import com.zyra.music.zyra.data.local.entity.LibraryPlaylistEntity
import com.zyra.music.zyra.data.remote.dto.favoriteDto.LibraryPlaylistDto
import com.zyra.music.zyra.data.remote.dto.userPlaylist.UserPlaylistDto
import com.zyra.music.zyra.data.remote.dto.userPlaylist.UserPlaylistSongDto
import com.zyra.music.zyra.domain.model.LibraryPlaylist
import com.zyra.music.zyra.domain.model.playlistData.UserPlaylist
import com.zyra.music.zyra.domain.model.playlistData.UserPlaylistSong
import com.zyra.music.zyra.navigation.PlayListType

fun LibraryPlaylistDto.toLibraryPlaylists(
    finalImageUrl : String,
    userName : String?  = "You"
) : LibraryPlaylist {
    val creatorSubtitle = when(this.playlistType){
        "USER_CREATED" -> "Playlist ● $userName"
        "LIKED_SONGS" -> this.subtitle ?: "Auto playlist"
        else -> "Playlist"
    }

    val typeEnum = when(this.playlistType){
        "USER_CREATED" -> PlayListType.USER_CREATED
        "LIKED_SONGS" -> PlayListType.FAVORITES
        else -> PlayListType.USER_CREATED
    }

    return LibraryPlaylist(
        id = this.id,
        name = this.title,
        creator = creatorSubtitle,
        imageUrl = finalImageUrl,
        trackCount = this.trackCount,
        playlistType = typeEnum
    )

}

fun UserPlaylistDto.toUserPlaylist() = UserPlaylist(
    id = this.id,
    userId = this.userId,
    name = this.name,
    description = this.description
)
fun UserPlaylist.toUserPlaylistDto() = UserPlaylistDto(
    id = this.id,
    userId = this.userId,
    name = this.name,
    description = this.description
)
fun UserPlaylistSongDto.toUserPlaylistSong() = UserPlaylistSong(
    playlistId = this.playlistId,
    songId = this.songId
)
fun UserPlaylistSong.toUserPlaylistSongDto() = UserPlaylistSongDto(
    playlistId = this.playlistId,
    songId = this.songId
)

/**
 * Converts the remote DTO to a database-ready Entity.
 * This is where you transform the data.
 */
fun LibraryPlaylistDto.toEntity(
    finalImageUrl: String?,
    userName: String? = "You"
): LibraryPlaylistEntity {
    val creatorSubtitle = when (this.playlistType) {
        "USER_CREATED" -> "Playlist ● $userName"
        "LIKED_SONGS" -> this.subtitle ?: "Auto playlist"
        else -> "Playlist"
    }

    val typeEnum = when (this.playlistType) {
        "USER_CREATED" -> PlayListType.USER_CREATED
        "LIKED_SONGS" -> PlayListType.FAVORITES
        else -> PlayListType.USER_CREATED // Default case
    }

    return LibraryPlaylistEntity(
        id = this.id,
        name = this.title,
        creator = creatorSubtitle,
        imageUrl = finalImageUrl,
        trackCount = this.trackCount,
        playlistType = typeEnum
    )
}

/**
 * Converts a database Entity to a UI-ready Domain model.
 * This should be a simple 1-to-1 mapping.
 */
fun LibraryPlaylistEntity.toLibraryPlaylist(): LibraryPlaylist {
    return LibraryPlaylist(
        id = this.id,
        name = this.name,
        creator = this.creator,
        imageUrl = this.imageUrl ?: "",
        trackCount = this.trackCount,
        playlistType = this.playlistType
    )
}