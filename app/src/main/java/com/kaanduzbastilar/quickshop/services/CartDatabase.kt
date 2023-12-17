package com.kaanduzbastilar.quickshop.services

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kaanduzbastilar.quickshop.model.CartItemModel

@Database(entities = [CartItemModel::class], version = 2)
abstract class CartDatabase : RoomDatabase() {

    abstract fun cartDao() : CartDao

    companion object{

       @Volatile private var instance : CartDatabase? = null

        private val lock = Any()

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE CartItemModel ADD COLUMN updateListener TEXT")
            }
        }

        operator fun invoke(context: Context) = instance ?: synchronized(lock){
            instance ?: makeDatabase(context).also {
                instance = it
            }
        }

        private fun makeDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,CartDatabase::class.java,"cartdatabase"
        ).addMigrations(MIGRATION_1_2)
            .build()

    }

}