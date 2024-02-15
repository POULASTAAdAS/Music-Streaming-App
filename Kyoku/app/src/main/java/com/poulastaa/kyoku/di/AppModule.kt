package com.poulastaa.kyoku.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.poulastaa.kyoku.connectivity.NetworkObserver
import com.poulastaa.kyoku.connectivity.NetworkObserverImpl
import com.poulastaa.kyoku.data.remote.ServiceApi
import com.poulastaa.kyoku.data.repository.AuthHeaderInterceptor
import com.poulastaa.kyoku.data.repository.ServiceRepositoryImpl
import com.poulastaa.kyoku.domain.repository.DataStoreOperation
import com.poulastaa.kyoku.domain.repository.ServiceRepository
import com.poulastaa.kyoku.utils.Constants.SERVICE_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.JavaNetCookieJar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.net.CookieManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideCookieManager(): CookieManager = CookieManager()

    @Provides
    @Singleton
    fun provideHttpClient(
        cookieManager: CookieManager,
        ds: DataStoreOperation,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(AuthHeaderInterceptor(ds))
            .cookieJar(JavaNetCookieJar(cookieManager))
            .build()


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(SERVICE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivityObserver(@ApplicationContext context: Context): NetworkObserver =
        NetworkObserverImpl(context = context)


    @Provides
    @Singleton
    fun provideServiceApi(retrofit: Retrofit): ServiceApi = retrofit.create(ServiceApi::class.java)

    @Provides
    @Singleton
    fun provideServiceRepository(serviceApi: ServiceApi): ServiceRepository =
        ServiceRepositoryImpl(serviceApi)
}