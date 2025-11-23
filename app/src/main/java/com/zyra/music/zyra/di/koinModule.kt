package com.zyra.music.zyra.di

import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.zyra.music.zyra.data.local.AppDatabase
import com.zyra.music.zyra.data.local.DatabaseFactory
import com.zyra.music.zyra.data.remote.HttpClientFactory
import com.zyra.music.zyra.data.remote.RemoteSongDataSource
import com.zyra.music.zyra.data.remote.RemoteSongDataSourceImpl
import com.zyra.music.zyra.data.repository.LibraryRepositoryImplNew
import com.zyra.music.zyra.data.repository.PlaylistRepositoryImpl
import com.zyra.music.zyra.data.repository.SongRepositoryImpl
import com.zyra.music.zyra.domain.repository.LibraryRepositoryNew
import com.zyra.music.zyra.domain.repository.PlaylistRepository
import com.zyra.music.zyra.domain.repository.SongRepository
import com.zyra.music.zyra.exoplayer.NewMusicQueueManager
import com.zyra.music.zyra.presentation.addPlaylist.AddPlaylistViewModel
import com.zyra.music.zyra.presentation.home.HomeViewModel
import com.zyra.music.zyra.presentation.libraryScreen.LibraryViewModelNew
import com.zyra.music.zyra.presentation.newPlayer.MainMusicViewModel
import com.zyra.music.zyra.presentation.playlistScreen.PlaylistViewModel
import com.zyra.music.zyra.presentation.profileScreen.ProfileViewModel
import com.zyra.music.zyra.presentation.searchScreen.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val koinModule = module {

    single { HttpClientFactory.create() }
    single { DatabaseFactory.create(get()) }

    single { ExoPlayer.Builder(get()).build() } bind Player::class

    single{get<AppDatabase>().playlistDao()}
    single{get<AppDatabase>().libraryPlaylistDao()}

    singleOf(::NewMusicQueueManager)
    singleOf(::RemoteSongDataSourceImpl).bind<RemoteSongDataSource>()
    singleOf(::SongRepositoryImpl).bind<SongRepository>()
    singleOf(::PlaylistRepositoryImpl).bind<PlaylistRepository>()
    singleOf(::LibraryRepositoryImplNew).bind<LibraryRepositoryNew>()


    @UnstableApi
    viewModel{
        MainMusicViewModel(get(), get(), androidContext())
    }
    viewModelOf(::SearchViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::LibraryViewModelNew)
    viewModelOf(::ProfileViewModel)


    viewModel{
        AddPlaylistViewModel(get())
    }
   viewModel {param ->

       PlaylistViewModel(get(),
           get(),
           get(),
           playlistId = param.get(),
           playlistType = param.get()
       )
   }

}