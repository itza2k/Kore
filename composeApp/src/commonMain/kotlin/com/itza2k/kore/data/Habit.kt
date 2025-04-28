package com.itza2k.kore.data

data class Habit(
    val id: String,
    val name: String,
    val description: String,
    val basePoints: Int,
    val currentPoints: Int = basePoints,
    val isEcoFriendly: Boolean = false,
    val completedToday: Boolean = false,
    val streak: Int = 0,
    val lastCompletedDate: Long = 0,
    val category: String = "",
    val progressLevel: Int = 1,
    val goalProgress: Float = 0f // For goal gradient system (0.0 to 1.0)
)