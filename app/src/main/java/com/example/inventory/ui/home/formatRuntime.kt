package com.example.inventory.ui.home

fun formatRuntime(minutes: Int?): String {
    val runtime = minutes ?: 0 // Use 0 as the default value if minutes is null
    val hours = runtime / 60
    val remainingMinutes = runtime % 60
    return when {
        hours > 0 && remainingMinutes > 0 -> "${hours}h ${remainingMinutes}m"
        hours > 0 -> "${hours}h"
        else -> "${remainingMinutes}m"
    }
}