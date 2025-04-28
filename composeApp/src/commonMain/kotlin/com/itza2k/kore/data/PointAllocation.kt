package com.itza2k.kore.data

/**
 * Represents a weekly or monthly allocation of reward points to habits and tasks
 */
data class PointAllocation(
    val id: String,
    val name: String,
    val description: String = "",
    val totalPoints: Int,
    val period: AllocationPeriod = AllocationPeriod.WEEKLY,
    val allocations: Map<String, Int> = mapOf(), // Map of habitId to allocated points
    val startDate: Long = 0,
    val endDate: Long = 0,
    val isActive: Boolean = true
)

/**
 * Represents the period for a point allocation
 */
enum class AllocationPeriod {
    WEEKLY,
    MONTHLY
}