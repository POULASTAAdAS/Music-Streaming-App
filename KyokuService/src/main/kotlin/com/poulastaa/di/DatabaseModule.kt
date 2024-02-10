package com.poulastaa.di

import com.poulastaa.data.repository.JWTRepositoryImpl
import com.poulastaa.data.repository.SongRepositoryImpl
import com.poulastaa.data.repository.UserServiceRepositoryImpl
import com.poulastaa.data.repository.playlist.PlaylistRepositoryImpl
import com.poulastaa.domain.repository.UserServiceRepository
import com.poulastaa.domain.repository.jwt.JWTRepository
import com.poulastaa.domain.repository.playlist.PlaylistRepository
import com.poulastaa.domain.repository.song.SongRepository
import io.ktor.server.application.*
import org.koin.dsl.module

fun provideJWTRepo(call: Application) = module {
    single<JWTRepository> {
        JWTRepositoryImpl(call)
    }
}

fun provideDatabaseRepo() = module {
    single<SongRepository> {
        SongRepositoryImpl()
    }
    single<PlaylistRepository> {
        PlaylistRepositoryImpl()
    }
}

fun provideService() = module {
    single<UserServiceRepository> {
        UserServiceRepositoryImpl(
            songRepository = get(),
            playlist = get()
        )
    }
}