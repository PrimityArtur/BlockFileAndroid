package com.example.blockfile.core.data.di

import com.example.blockfile.core.data.network.BlockFileApi
import com.example.blockfile.core.data.repository.AuthRepository
import com.example.blockfile.core.data.repository.AuthRepositoryImpl
import com.example.blockfile.core.data.repository.CatalogRepository
import com.example.blockfile.core.data.repository.CatalogRepositoryImpl
import com.example.blockfile.core.data.repository.RankingRepository
import com.example.blockfile.core.data.repository.RankingRepositoryImpl
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

    @Provides
    @Singleton
    fun provideCatalogRepository(api: BlockFileApi): CatalogRepository =
        CatalogRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideRankingRepository(api: BlockFileApi): RankingRepository =
        RankingRepositoryImpl(api)

}
