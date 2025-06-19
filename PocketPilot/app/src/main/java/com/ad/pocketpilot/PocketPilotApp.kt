package com.ad.pocketpilot

import android.app.Application
import com.ad.pocketpilot.data.local.PocketPilotDatabase

class PocketPilotApp : Application() {
    private val database: PocketPilotDatabase by lazy {
        PocketPilotDatabase.getDatabase(this)
    }

    fun getDatabaseInstance() : PocketPilotDatabase {
        return database;
    }
}