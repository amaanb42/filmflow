package com.example.inventory

import android.app.Application
import com.example.inventory.data.AppDataContainer
import com.example.inventory.data.AppDatabase

class InventoryApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     * Example usage if wanting to get all Lists: InventoryApplication().container.getAllListsStream().first()
     */
    val container: AppDataContainer = AppDataContainer(this)

    override fun onCreate() {
        super.onCreate()
        /** force db to be opened/created on app start */
        AppDatabase.getDatabase(this).openHelper.writableDatabase
    }
}
