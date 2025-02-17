package com.example.inventory

import android.app.Application
import android.content.Context
import com.example.inventory.data.AppDataContainer

class InventoryApplication : Application() {

    // Lazily initialize the container. This is better for startup performance.
    lateinit var container: AppDataContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        instance = this //set up the companion object
    }
    companion object{
        private lateinit var instance: InventoryApplication
        fun getContext(): Context = instance.applicationContext
    }
}