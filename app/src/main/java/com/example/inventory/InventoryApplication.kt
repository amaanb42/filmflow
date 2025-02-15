package com.example.inventory

import android.app.Application
import android.content.Context
import com.example.inventory.data.AppDataContainer
import com.example.inventory.data.AppDatabase

class InventoryApplication : Application() {

    // Lazily initialize the container. This is better for startup performance.
    lateinit var container: AppDataContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        // Consider moving database initialization to a background thread
        // (though, as mentioned, Room often handles this well enough).
        //  initializeDatabase()
        instance = this //set up the companion object
    }
    companion object{
        private lateinit var instance: InventoryApplication
        fun getContext(): Context = instance.applicationContext
    }
    /*
    // OPTIONAL: Example of background database initialization (using a coroutine):
    private fun initializeDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(this@InventoryApplication).openHelper.writableDatabase
        }
    }
    */
}