package com.itza2k.kore.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.itza2k.kore.data.Activity
import com.itza2k.kore.data.AllocationPeriod
import com.itza2k.kore.data.Goal
import com.itza2k.kore.data.Habit
import com.itza2k.kore.data.PointAllocation
import com.itza2k.kore.data.Reward
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID


class DatabaseHelper(private val database: KoreDatabase) {

    // Activity operations
    fun getAllActivities(): Flow<List<Activity>> {
        return database.activityQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { activityList ->
                activityList.map { activity ->
                    Activity(
                        id = activity.id,
                        habitId = activity.habitId,
                        timestamp = activity.timestamp,
                        pointsEarned = activity.pointsEarned.toInt(),
                        bonusPoints = activity.bonusPoints.toInt(),
                        bonusReason = activity.bonusReason
                    )
                }
            }
    }

    fun getActivitiesByHabitId(habitId: String): Flow<List<Activity>> {
        return database.activityQueries.selectByHabitId(habitId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { activityList ->
                activityList.map { activity ->
                    Activity(
                        id = activity.id,
                        habitId = activity.habitId,
                        timestamp = activity.timestamp,
                        pointsEarned = activity.pointsEarned.toInt(),
                        bonusPoints = activity.bonusPoints.toInt(),
                        bonusReason = activity.bonusReason
                    )
                }
            }
    }

    suspend fun insertActivity(activity: Activity) {
        database.activityQueries.insert(
            id = activity.id,
            habitId = activity.habitId,
            timestamp = activity.timestamp,
            pointsEarned = activity.pointsEarned.toLong(),
            bonusPoints = activity.bonusPoints.toLong(),
            bonusReason = activity.bonusReason
        )
    }

    suspend fun deleteActivity(id: String) {
        database.activityQueries.delete(id)
    }

    // Habit operations
    fun getAllHabits(): Flow<List<Habit>> {
        return database.habitQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { habitList ->
                habitList.map { habit ->
                    Habit(
                        id = habit.id,
                        name = habit.name,
                        description = habit.description,
                        basePoints = habit.basePoints.toInt(),
                        currentPoints = habit.currentPoints.toInt(),
                        isEcoFriendly = habit.isEcoFriendly == 1L,
                        completedToday = habit.completedToday == 1L,
                        streak = habit.streak.toInt(),
                        lastCompletedDate = habit.lastCompletedDate,
                        category = habit.category,
                        progressLevel = habit.progressLevel.toInt(),
                        goalProgress = habit.goalProgress.toFloat()
                    )
                }
            }
    }

    fun getHabitById(id: String): Flow<Habit?> {
        return database.habitQueries.selectById(id)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { habitList ->
                habitList.firstOrNull()?.let { habit ->
                    Habit(
                        id = habit.id,
                        name = habit.name,
                        description = habit.description,
                        basePoints = habit.basePoints.toInt(),
                        currentPoints = habit.currentPoints.toInt(),
                        isEcoFriendly = habit.isEcoFriendly == 1L,
                        completedToday = habit.completedToday == 1L,
                        streak = habit.streak.toInt(),
                        lastCompletedDate = habit.lastCompletedDate,
                        category = habit.category,
                        progressLevel = habit.progressLevel.toInt(),
                        goalProgress = habit.goalProgress.toFloat()
                    )
                }
            }
    }

    suspend fun insertHabit(habit: Habit) {
        database.habitQueries.insert(
            id = habit.id,
            name = habit.name,
            description = habit.description,
            basePoints = habit.basePoints.toLong(),
            currentPoints = habit.currentPoints.toLong(),
            isEcoFriendly = if (habit.isEcoFriendly) 1L else 0L,
            completedToday = if (habit.completedToday) 1L else 0L,
            streak = habit.streak.toLong(),
            lastCompletedDate = habit.lastCompletedDate,
            category = habit.category,
            progressLevel = habit.progressLevel.toLong(),
            goalProgress = habit.goalProgress.toDouble()
        )
    }

    suspend fun updateHabit(habit: Habit) {
        database.habitQueries.update(
            name = habit.name,
            description = habit.description,
            basePoints = habit.basePoints.toLong(),
            currentPoints = habit.currentPoints.toLong(),
            isEcoFriendly = if (habit.isEcoFriendly) 1L else 0L,
            completedToday = if (habit.completedToday) 1L else 0L,
            streak = habit.streak.toLong(),
            lastCompletedDate = habit.lastCompletedDate,
            category = habit.category,
            progressLevel = habit.progressLevel.toLong(),
            goalProgress = habit.goalProgress.toDouble(),
            id = habit.id
        )
    }

    suspend fun markHabitCompleted(id: String, timestamp: Long, newPoints: Int) {
        database.habitQueries.markCompleted(
            lastCompletedDate = timestamp,
            currentPoints = newPoints.toLong(),
            id = id
        )
    }

    suspend fun resetCompletedToday() {
        database.habitQueries.resetCompletedToday()
    }

    suspend fun deleteHabit(id: String) {
        database.habitQueries.delete(id)
    }

    // Goal operations
    fun getAllGoals(): Flow<List<Goal>> {
        return database.goalQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { goalList ->
                goalList.map { goal ->
                    Goal(
                        id = goal.id,
                        name = goal.name,
                        description = goal.description,
                        targetDate = goal.targetDate,
                        progress = goal.progress.toFloat(),
                        isCompleted = goal.isCompleted == 1L,
                        relatedHabitIds = getHabitIdsForGoal(goal.id)
                    )
                }
            }
    }

    private suspend fun getHabitIdsForGoal(goalId: String): List<String> {
        return database.goalQueries.selectHabitsForGoal(goalId)
            .executeAsList()
            .map { it.id }
    }

    suspend fun insertGoal(goal: Goal) {
        database.goalQueries.insertGoal(
            id = goal.id,
            name = goal.name,
            description = goal.description,
            targetDate = goal.targetDate,
            progress = goal.progress.toDouble(),
            isCompleted = if (goal.isCompleted) 1L else 0L
        )

        // Add habit relationships
        goal.relatedHabitIds.forEach { habitId ->
            database.goalQueries.addHabitToGoal(goal.id, habitId)
        }
    }

    suspend fun updateGoal(goal: Goal) {
        database.goalQueries.updateGoal(
            name = goal.name,
            description = goal.description,
            targetDate = goal.targetDate,
            progress = goal.progress.toDouble(),
            isCompleted = if (goal.isCompleted) 1L else 0L,
            id = goal.id
        )
    }

    suspend fun updateGoalProgress(id: String, progress: Float) {
        database.goalQueries.updateProgress(
            progress = progress.toDouble(),
            id = id
        )
    }

    suspend fun deleteGoal(id: String) {
        database.goalQueries.deleteGoal(id)
    }

    // Reward operations
    fun getAllRewards(): Flow<List<Reward>> {
        return database.rewardQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rewardList ->
                rewardList.map { reward ->
                    Reward(
                        id = reward.id,
                        name = reward.name,
                        description = reward.description,
                        pointsCost = reward.pointsCost.toInt(),
                        category = reward.category,
                        isEcoFriendly = reward.isEcoFriendly == 1L
                    )
                }
            }
    }

    suspend fun insertReward(reward: Reward) {
        database.rewardQueries.insertReward(
            id = reward.id,
            name = reward.name,
            description = reward.description,
            pointsCost = reward.pointsCost.toLong(),
            category = reward.category,
            isEcoFriendly = if (reward.isEcoFriendly) 1L else 0L
        )
    }

    suspend fun updateReward(reward: Reward) {
        database.rewardQueries.updateReward(
            name = reward.name,
            description = reward.description,
            pointsCost = reward.pointsCost.toLong(),
            category = reward.category,
            isEcoFriendly = if (reward.isEcoFriendly) 1L else 0L,
            id = reward.id
        )
    }

    suspend fun redeemReward(rewardId: String, pointsSpent: Int) {
        database.rewardQueries.redeemReward(
            id = UUID.randomUUID().toString(),
            rewardId = rewardId,
            timestamp = System.currentTimeMillis(),
            pointsSpent = pointsSpent.toLong()
        )
    }

    suspend fun deleteReward(id: String) {
        database.rewardQueries.deleteReward(id)
    }

    // PointAllocation operations
    fun getAllPointAllocations(): Flow<List<PointAllocation>> {
        return database.pointAllocationQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { allocationList ->
                allocationList.map { allocation ->
                    PointAllocation(
                        id = allocation.id,
                        name = allocation.name,
                        description = allocation.description,
                        totalPoints = allocation.totalPoints.toInt(),
                        period = AllocationPeriod.valueOf(allocation.period),
                        startDate = allocation.startDate,
                        endDate = allocation.endDate,
                        isActive = allocation.isActive == 1L,
                        allocations = getAllocationItems(allocation.id)
                    )
                }
            }
    }

    fun getActivePointAllocation(): Flow<PointAllocation?> {
        return database.pointAllocationQueries.selectActive()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { allocationList ->
                allocationList.firstOrNull()?.let { allocation ->
                    PointAllocation(
                        id = allocation.id,
                        name = allocation.name,
                        description = allocation.description,
                        totalPoints = allocation.totalPoints.toInt(),
                        period = AllocationPeriod.valueOf(allocation.period),
                        startDate = allocation.startDate,
                        endDate = allocation.endDate,
                        isActive = allocation.isActive == 1L,
                        allocations = getAllocationItems(allocation.id)
                    )
                }
            }
    }

    private suspend fun getAllocationItems(allocationId: String): Map<String, Int> {
        val items = database.pointAllocationQueries.selectItemsForAllocation(allocationId)
            .executeAsList()

        return items.associate { item ->
            item.habitId to item.points.toInt()
        }
    }

    suspend fun insertPointAllocation(pointAllocation: PointAllocation) {
        // First deactivate all allocations if this one is active
        if (pointAllocation.isActive) {
            database.pointAllocationQueries.deactivateAllAllocations()
        }

        // Insert the allocation
        database.pointAllocationQueries.insertAllocation(
            id = pointAllocation.id,
            name = pointAllocation.name,
            description = pointAllocation.description,
            totalPoints = pointAllocation.totalPoints.toLong(),
            period = pointAllocation.period.name,
            startDate = pointAllocation.startDate,
            endDate = pointAllocation.endDate,
            isActive = if (pointAllocation.isActive) 1L else 0L
        )

        // Insert all allocation items
        pointAllocation.allocations.forEach { (habitId, points) ->
            database.pointAllocationQueries.insertAllocationItem(
                id = UUID.randomUUID().toString(),
                allocationId = pointAllocation.id,
                habitId = habitId,
                points = points.toLong()
            )
        }
    }

    suspend fun updatePointAllocation(pointAllocation: PointAllocation) {
        // First deactivate all allocations if this one is active
        if (pointAllocation.isActive) {
            database.pointAllocationQueries.deactivateAllAllocations()
        }

        // Update the allocation
        database.pointAllocationQueries.updateAllocation(
            name = pointAllocation.name,
            description = pointAllocation.description,
            totalPoints = pointAllocation.totalPoints.toLong(),
            period = pointAllocation.period.name,
            startDate = pointAllocation.startDate,
            endDate = pointAllocation.endDate,
            isActive = if (pointAllocation.isActive) 1L else 0L,
            id = pointAllocation.id
        )

        // Delete all existing items and insert new ones
        database.pointAllocationQueries.deleteAllItemsForAllocation(pointAllocation.id)

        // Insert all allocation items
        pointAllocation.allocations.forEach { (habitId, points) ->
            database.pointAllocationQueries.insertAllocationItem(
                id = UUID.randomUUID().toString(),
                allocationId = pointAllocation.id,
                habitId = habitId,
                points = points.toLong()
            )
        }
    }

    suspend fun activatePointAllocation(id: String) {
        // First deactivate all allocations
        database.pointAllocationQueries.deactivateAllAllocations()

        // Then activate the specified allocation
        database.pointAllocationQueries.activateAllocation(id)
    }

    suspend fun deletePointAllocation(id: String) {
        database.pointAllocationQueries.deleteAllocation(id)
    }
}
