package com.example.inventory.ui.home

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

private val Context.datastore by preferencesDataStore("settings")

