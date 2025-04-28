package com.itza2k.kore.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itza2k.kore.data.Activity
import com.itza2k.kore.data.AllocationPeriod
import com.itza2k.kore.data.Goal
import com.itza2k.kore.data.Habit
import com.itza2k.kore.data.PointAllocation
import com.itza2k.kore.data.Reward
import com.itza2k.kore.db.DatabaseHelper
import com.itza2k.kore.util.UUID
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.min

class KoreViewModel(private val databaseHelper: DatabaseHelper) : ViewModel() {
    private val _habits = mutableStateListOf<Habit>()
    val habits: List<Habit> get() = _habits

    private val _rewards = mutableStateListOf<Reward>()
    val rewards: List<Reward> get() = _rewards

    private val _goals = mutableStateListOf<Goal>()
    val goals: List<Goal> get() = _goals

    private val _activities = mutableStateListOf<Activity>()
    val activities: List<Activity> get() = _activities

    private val _pointAllocations = mutableStateListOf<PointAllocation>()
    val pointAllocations: List<PointAllocation> get() = _pointAllocations

    private val _totalPoints = mutableStateOf(0)
    val totalPoints: Int get() = _totalPoints.value

    private val _weeklyAllocationPoints = mutableStateOf(500) // Default weekly allocation
    val weeklyAllocationPoints: Int get() = _weeklyAllocationPoints.value

    init {
        // Load data from database
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Load habits
            databaseHelper.getAllHabits().collectLatest { habitList ->
                _habits.clear()
                _habits.addAll(habitList)
            }
        }

        viewModelScope.launch {
            // Load rewards
            databaseHelper.getAllRewards().collectLatest { rewardList ->
                _rewards.clear()
                _rewards.addAll(rewardList)
            }
        }

        viewModelScope.launch {
            // Load goals
            databaseHelper.getAllGoals().collectLatest { goalList ->
                _goals.clear()
                _goals.addAll(goalList)
            }
        }

        viewModelScope.launch {
            // Load activities
            databaseHelper.getAllActivities().collectLatest { activityList ->
                _activities.clear()
                _activities.addAll(activityList)

                // Calculate total points from activities
                _totalPoints.value = activityList.sumOf { it.pointsEarned + it.bonusPoints }
            }
        }

        viewModelScope.launch {
            // Load point allocations
            databaseHelper.getAllPointAllocations().collectLatest { allocationList ->
                _pointAllocations.clear()
                _pointAllocations.addAll(allocationList)

                // Set weekly allocation points from active allocation
                val activeWeeklyAllocation = allocationList.firstOrNull { 
                    it.isActive && it.period == AllocationPeriod.WEEKLY 
                }
                if (activeWeeklyAllocation != null) {
                    _weeklyAllocationPoints.value = activeWeeklyAllocation.totalPoints
                }
            }
        }
    }

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            // Persist to database
            databaseHelper.insertHabit(habit)

            // Update in-memory collection (will be updated by flow collection)
            _habits.add(habit)
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            // Persist to database
            databaseHelper.updateHabit(habit)

            // Update in-memory collection (will be updated by flow collection)
            val index = _habits.indexOfFirst { it.id == habit.id }
            if (index != -1) {
                _habits[index] = habit
            }
        }
    }

    fun deleteHabit(id: String) {
        viewModelScope.launch {
            // Persist to database
            databaseHelper.deleteHabit(id)

            // Update in-memory collection (will be updated by flow collection)
            val index = _habits.indexOfFirst { it.id == id }
            if (index != -1) {
                _habits.removeAt(index)
            }
        }
    }

    fun addReward(reward: Reward) {
        viewModelScope.launch {
            // Persist to database
            databaseHelper.insertReward(reward)

            // Update in-memory collection (will be updated by flow collection)
            _rewards.add(reward)
        }
    }

    fun addGoal(goal: Goal) {
        viewModelScope.launch {
            // Persist to database
            databaseHelper.insertGoal(goal)

            // Update in-memory collection (will be updated by flow collection)
            _goals.add(goal)
        }
    }

    fun addPointAllocation(pointAllocation: PointAllocation) {
        viewModelScope.launch {
            // Persist to database
            databaseHelper.insertPointAllocation(pointAllocation)

            // Update in-memory collection (will be updated by flow collection)
            if (pointAllocation.isActive) {
                deactivateAllAllocations()
            }
            _pointAllocations.add(pointAllocation)

            // Save the weekly allocation points
            if (pointAllocation.period == AllocationPeriod.WEEKLY) {
                _weeklyAllocationPoints.value = pointAllocation.totalPoints
            }
        }
    }

    fun updatePointAllocation(pointAllocation: PointAllocation) {
        viewModelScope.launch {
            // Persist to database
            databaseHelper.updatePointAllocation(pointAllocation)

            // Update in-memory collection (will be updated by flow collection)
            val index = _pointAllocations.indexOfFirst { it.id == pointAllocation.id }
            if (index != -1) {
                if (pointAllocation.isActive) {
                    deactivateAllAllocations()
                }
                _pointAllocations[index] = pointAllocation

                // Save the weekly allocation points
                if (pointAllocation.period == AllocationPeriod.WEEKLY) {
                    _weeklyAllocationPoints.value = pointAllocation.totalPoints
                }
            }
        }
    }

    fun deletePointAllocation(id: String) {
        viewModelScope.launch {
            // Persist to database
            databaseHelper.deletePointAllocation(id)

            // Update in-memory collection (will be updated by flow collection)
            val index = _pointAllocations.indexOfFirst { it.id == id }
            if (index != -1) {
                _pointAllocations.removeAt(index)
            }
        }
    }

    fun activatePointAllocation(id: String) {
        viewModelScope.launch {
            // Persist to database
            databaseHelper.activatePointAllocation(id)

            // Update in-memory collection (will be updated by flow collection)
            deactivateAllAllocations()
            val index = _pointAllocations.indexOfFirst { it.id == id }
            if (index != -1) {
                val allocation = _pointAllocations[index]
                _pointAllocations[index] = allocation.copy(isActive = true)

                // Save the weekly allocation points
                if (allocation.period == AllocationPeriod.WEEKLY) {
                    _weeklyAllocationPoints.value = allocation.totalPoints
                }
            }
        }
    }

    private fun deactivateAllAllocations() {
        _pointAllocations.forEachIndexed { index, allocation ->
            if (allocation.isActive) {
                _pointAllocations[index] = allocation.copy(isActive = false)
            }
        }
    }

    fun setWeeklyAllocationPoints(points: Int) {
        _weeklyAllocationPoints.value = points

        // Update any active weekly allocation
        val activeWeeklyAllocation = _pointAllocations.firstOrNull { 
            it.isActive && it.period == AllocationPeriod.WEEKLY 
        }
        if (activeWeeklyAllocation != null) {
            updatePointAllocation(activeWeeklyAllocation.copy(totalPoints = points))
        } else {
            // Create a new weekly allocation if none exists
            viewModelScope.launch {
                val newAllocation = PointAllocation(
                    id = UUID.randomUUID(),
                    name = "Weekly Allocation",
                    description = "Default weekly point allocation",
                    totalPoints = points,
                    period = AllocationPeriod.WEEKLY,
                    startDate = System.currentTimeMillis(),
                    endDate = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000), // 7 days
                    isActive = true,
                    allocations = emptyMap()
                )
                addPointAllocation(newAllocation)
            }
        }
    }
    fun completeHabit(habit: Habit) {
        viewModelScope.launch {
            val index = _habits.indexOfFirst { it.id == habit.id }
            if (index == -1) return@launch

            val now = getCurrentTimeMillis()
            val oneDayInMillis = 24 * 60 * 60 * 1000

            val lastCompletedDate = habit.lastCompletedDate
            val isNewStreak = lastCompletedDate == 0L || now - lastCompletedDate > oneDayInMillis
            val newStreak = if (isNewStreak) habit.streak + 1 else habit.streak

            var bonusPoints = 0
            var bonusReason = ""

            if (newStreak > 1) {
                val streakBonus = min(newStreak * 5, 50) // Cap at 50 bonus points
                bonusPoints += streakBonus
                bonusReason += "Streak bonus: +$streakBonus points\n"
            }
            if (habit.isEcoFriendly) {
                bonusPoints += 20
                bonusReason += "Eco-friendly bonus: +20 points\n"
            }
            val basePoints = habit.basePoints * habit.progressLevel
            _totalPoints.value += basePoints + bonusPoints

            val activity = Activity(
                id = UUID.randomUUID(),
                habitId = habit.id,
                timestamp = now,
                pointsEarned = basePoints,
                bonusPoints = bonusPoints,
                bonusReason = bonusReason.trim()
            )

            // Persist activity to database
            databaseHelper.insertActivity(activity)

            // Update in-memory collection (will be updated by flow collection)
            _activities.add(activity)

            val updatedHabit = habit.copy(
                completedToday = true,
                streak = newStreak,
                lastCompletedDate = now,
                progressLevel = if (newStreak % 5 == 0) habit.progressLevel + 1 else habit.progressLevel
            )

            // Persist updated habit to database
            databaseHelper.updateHabit(updatedHabit)

            // Update in-memory collection (will be updated by flow collection)
            _habits[index] = updatedHabit

            updateGoalsForHabit(habit.id)
        }
    }

    private fun updateGoalsForHabit(habitId: String) {
        viewModelScope.launch {
            _goals.forEachIndexed { index, goal ->
                if (goal.relatedHabitIds.contains(habitId)) {
                    val totalHabits = goal.relatedHabitIds.size
                    val completedHabits = goal.relatedHabitIds.count { habitId ->
                        _habits.find { it.id == habitId }?.completedToday == true
                    }
                    val newProgress = completedHabits.toFloat() / totalHabits
                    val isCompleted = newProgress >= 1.0f
                    val updatedGoal = goal.copy(
                        progress = newProgress,
                        isCompleted = isCompleted
                    )

                    // Persist updated goal to database
                    databaseHelper.updateGoal(updatedGoal)

                    // Update in-memory collection (will be updated by flow collection)
                    _goals[index] = updatedGoal
                }
            }
        }
    }

    fun redeemReward(reward: Reward): Boolean {
        if (_totalPoints.value < reward.pointsCost) return false

        viewModelScope.launch {
            // Persist to database
            databaseHelper.redeemReward(reward.id, reward.pointsCost)

            // Update total points
            _totalPoints.value -= reward.pointsCost
        }

        return true
    }

    fun resetDailyHabits() {
        viewModelScope.launch {
            // Persist to database
            databaseHelper.resetCompletedToday()

            // Update in-memory collection (will be updated by flow collection)
            _habits.forEachIndexed { index, habit ->
                if (habit.completedToday) {
                    _habits[index] = habit.copy(completedToday = false)
                }
            }
        }
    }

    private fun getCurrentTimeMillis(): Long = System.currentTimeMillis()
}
