package com.example.blockfile.core.data.di

import com.example.blockfile.core.data.network.BlockFileApi
import com.example.blockfile.core.data.repository.AuthRepository
import com.example.blockfile.core.data.repository.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(api: BlockFileApi): AuthRepository =
        AuthRepositoryImpl(api)
}
