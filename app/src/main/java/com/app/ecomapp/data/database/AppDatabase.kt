package com.app.ecomapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.ecomapp.data.local.NotificationDao
import com.app.ecomapp.data.local.WishlistDao
import com.app.ecomapp.data.models.entity.NotificationEntity
import com.app.ecomapp.data.models.entity.WishlistProduct

@Database(entities = [WishlistProduct::class, NotificationEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wishlistDao(): WishlistDao
    abstract fun notificationDao(): NotificationDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "JetShopDatabase"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
