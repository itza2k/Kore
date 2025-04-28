package com.itza2k.kore.data

data class Goal(
    val id: String,
    val name: String,
    val description: String,
    val targetDate: Long = 0,
    val progress: Float = 0f,
    val relatedHabitIds: List<String> = emptyList(),
    val isCompleted: Boolean = false
)