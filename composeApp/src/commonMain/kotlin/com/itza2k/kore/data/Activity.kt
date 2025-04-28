package com.itza2k.kore.data

data class Activity(
    val id: String,
    val habitId: String,
    val timestamp: Long,
    val pointsEarned: Int,
    val bonusPoints: Int = 0,
    val bonusReason: String = ""
)