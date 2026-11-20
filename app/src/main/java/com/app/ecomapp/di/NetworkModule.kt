package com.app.ecomapp.di

import com.app.ecomapp.data.remote.ApiService
import com.app.ecomapp.data.repository.auth.AuthRepository
import com.app.ecomapp.data.repository.auth.AuthRepositoryImpl
import com.app.ecomapp.data.repository.cart.CartRepository
import com.app.ecomapp.data.repository.cart.CartRepositoryImpl
import com.app.ecomapp.data.repository.home.HomeRepository
import com.app.ecomapp.data.repository.home.HomeRepositoryImpl
import com.app.ecomapp.data.repository.order.OrderRepository
import com.app.ecomapp.data.repository.order.OrderRepositoryImpl
import com.app.ecomapp.data.repository.payment.PaymentRepository
import com.app.ecomapp.data.repository.payment.PaymentRepositoryImpl
import com.app.ecomapp.data.repository.profile.ProfileRepository
import com.app.ecomapp.data.repository.profile.ProfileRepositoryImpl
import com.app.ecomapp.data.repository.refer.ReferRepository
import com.app.ecomapp.data.repository.refer.ReferRepositoryImpl
import com.app.ecomapp.utils.Constants.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun providesBaseUrl(): String {
        return BASE_URL
    }

    @Provides
    fun providesLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val okHttpClient = OkHttpClient().newBuilder()

        okHttpClient.callTimeout(40, TimeUnit.SECONDS)
        okHttpClient.connectTimeout(40, TimeUnit.SECONDS)
        okHttpClient.readTimeout(40, TimeUnit.SECONDS)
        okHttpClient.writeTimeout(40, TimeUnit.SECONDS)
        okHttpClient.addInterceptor(loggingInterceptor)
        okHttpClient.build()
        return okHttpClient.build()
    }

    @Provides
    fun provideConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create()
    }

    @Provides
    fun provideRetrofitClient(okHttpClient: OkHttpClient, baseUrl: String, converterFactory: Converter.Factory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideAuthRepository(authApi: ApiService): AuthRepository =
        AuthRepositoryImpl(authApi)

    @Provides
    @Singleton
    fun provideHomeRepository(authApi: ApiService): HomeRepository =
        HomeRepositoryImpl(authApi)

    @Provides
    @Singleton
    fun provideCartRepository(authApi: ApiService): CartRepository =
        CartRepositoryImpl(authApi)

    @Provides
    @Singleton
    fun provideProfileRepository(authApi: ApiService): ProfileRepository =
        ProfileRepositoryImpl(authApi)

    @Provides
    @Singleton
    fun provideReferRepository(authApi: ApiService): ReferRepository =
        ReferRepositoryImpl(authApi)

    @Provides
    @Singleton
    fun provideOrderRepository(authApi: ApiService): OrderRepository =
        OrderRepositoryImpl(authApi)

    @Provides
    @Singleton
    fun providePaymentRepository(authApi: ApiService): PaymentRepository =
        PaymentRepositoryImpl(authApi)
}
