package com.ad.pocketpilot.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TransactionEntity::class], version = 1, exportSchema = false)
abstract class PocketPilotDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: PocketPilotDatabase? = null

        fun getDatabase(context: Context): PocketPilotDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    PocketPilotDatabase::class.java,
                    "pocket_pilot_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}