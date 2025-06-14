// In file: app/src/main/java/org/com/hcmurs/di/RepositoriesModule.kt
package org.com.hcmurs.di

// --- CÁC IMPORT CŨ CỦA BẠN ---
import org.com.hcmurs.repositories.Api
import org.com.hcmurs.repositories.ApiImpl
import org.com.hcmurs.repositories.MainLog
import org.com.hcmurs.repositories.MainLogImpl
import org.com.hcmurs.repositories.Store
import org.com.hcmurs.repositories.StoreImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// --- THÊM CÁC IMPORT MỚI ---
import org.com.hcmurs.repositories.station.StationRepository
import org.com.hcmurs.repositories.station.StationRepositoryImpl


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {
    @Binds
    @Singleton
    abstract fun bindMainLog(
        log: MainLogImpl
    ): MainLog

    @Binds
    @Singleton
    abstract fun bindApi(
        api: ApiImpl
    ): Api

    @Binds
    @Singleton
    abstract fun bindStore(store: StoreImpl): Store

    @Binds
    @Singleton
    abstract fun bindStationRepository(
        stationRepositoryImpl: StationRepositoryImpl
    ): StationRepository
}