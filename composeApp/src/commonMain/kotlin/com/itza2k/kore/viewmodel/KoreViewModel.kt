package com.itza2k.kore.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.itza2k.kore.data.Activity
import com.itza2k.kore.data.Goal
import com.itza2k.kore.data.Habit
import com.itza2k.kore.data.Reward
import com.itza2k.kore.util.UUID
import kotlin.math.min

class KoreViewModel : ViewModel() {
    private val _habits = mutableStateListOf<Habit>()
    val habits: List<Habit> get() = _habits

    private val _rewards = mutableStateListOf<Reward>()
    val rewards: List<Reward> get() = _rewards

    private val _goals = mutableStateListOf<Goal>()
    val goals: List<Goal> get() = _goals

    private val _activities = mutableStateListOf<Activity>()
    val activities: List<Activity> get() = _activities

    private val _totalPoints = mutableStateOf(0)
    val totalPoints: Int get() = _totalPoints.value

    fun addHabit(habit: Habit) {
        _habits.add(habit)
    }
    fun addReward(reward: Reward) {
        _rewards.add(reward)
    }
    fun addGoal(goal: Goal) {
        _goals.add(goal)
    }
    fun completeHabit(habit: Habit) {
        val index = _habits.indexOfFirst { it.id == habit.id }
        if (index == -1) return

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
        _activities.add(activity)
        val updatedHabit = habit.copy(
            completedToday = true,
            streak = newStreak,
            lastCompletedDate = now,
            progressLevel = if (newStreak % 5 == 0) habit.progressLevel + 1 else habit.progressLevel
        )
        _habits[index] = updatedHabit
        updateGoalsForHabit(habit.id)
    }

    private fun updateGoalsForHabit(habitId: String) {
        _goals.forEachIndexed { index, goal ->
            if (goal.relatedHabitIds.contains(habitId)) {
                val totalHabits = goal.relatedHabitIds.size
                val completedHabits = goal.relatedHabitIds.count { habitId ->
                    _habits.find { it.id == habitId }?.completedToday == true
                }
                val newProgress = completedHabits.toFloat() / totalHabits
                val isCompleted = newProgress >= 1.0f
                _goals[index] = goal.copy(
                    progress = newProgress,
                    isCompleted = isCompleted
                )
            }
        }
    }

    fun redeemReward(reward: Reward): Boolean {
        if (_totalPoints.value < reward.pointsCost) return false

        _totalPoints.value -= reward.pointsCost
        return true
    }

    fun resetDailyHabits() {
        _habits.forEachIndexed { index, habit ->
            if (habit.completedToday) {
                _habits[index] = habit.copy(completedToday = false)
            }
        }
    }

    private fun getCurrentTimeMillis(): Long = System.currentTimeMillis()
}