package com.app.ecomapp.di

import android.content.Context
import com.app.ecomapp.data.local.WishlistDao
import com.app.ecomapp.data.database.AppDatabase
import com.app.ecomapp.data.local.NotificationDao
import com.app.ecomapp.domain.repository.NotificationRepository
import com.app.ecomapp.domain.repository.WishlistRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WishlistModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideWishlistDao(database: AppDatabase): WishlistDao {
        return database.wishlistDao()
    }

    @Provides
    @Singleton
    fun provideWishlistRepository(dao: WishlistDao): WishlistRepository {
        return WishlistRepository(dao)
    }
    @Provides
    fun provideNotificationDao(database: AppDatabase): NotificationDao {
        return database.notificationDao()
    }

    @Provides
    fun provideNotificationRepository(notificationDao: NotificationDao): NotificationRepository {
        return NotificationRepository(notificationDao)
    }

}
