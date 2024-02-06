package com.poulastaa.kyoku.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.poulastaa.kyoku.connectivity.NetworkObserver
import com.poulastaa.kyoku.connectivity.NetworkObserverImpl
import com.poulastaa.kyoku.data.remote.AuthApi
import com.poulastaa.kyoku.data.repository.AuthRepositoryImpl
import com.poulastaa.kyoku.domain.repository.AuthRepository
import com.poulastaa.kyoku.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.serialization.json.Json
import okhttp3.JavaNetCookieJar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.net.CookieManager
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Qualifier

@Module
@InstallIn(ViewModelComponent::class)
object AuthModule {
    @Provides
    @ViewModelScoped
    @Named("AuthCookie")
    fun provideCookieManager(): CookieManager = CookieManager()

    @Provides
    @ViewModelScoped
    @Named("AuthHttpClient")
    fun provideHttpClient(@Named("AuthCookie") cookieManager: CookieManager): OkHttpClient =
        OkHttpClient
            .Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .cookieJar(JavaNetCookieJar(cookieManager))
            .build()

    @Provides
    @ViewModelScoped
    @Named("AuthRetrofit")
    fun provideRetrofit(@Named("AuthHttpClient") okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @ViewModelScoped
    fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager =
        CredentialManager.create(context)

    @Provides
    @ViewModelScoped
    @Named("AuthNetworkObserver")
    fun provideNetworkConnectivityObserver(@ApplicationContext context: Context): NetworkObserver =
        NetworkObserverImpl(context = context)

    @Provides
    @ViewModelScoped
    @Named("AuthApi")
    fun provideAuthApi(@Named("AuthRetrofit") retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @ViewModelScoped
    @Named("AuthApiImpl")
    fun provideAuthRepo(@Named("AuthApi") authApi: AuthApi): AuthRepository =
        AuthRepositoryImpl(authApi)
}