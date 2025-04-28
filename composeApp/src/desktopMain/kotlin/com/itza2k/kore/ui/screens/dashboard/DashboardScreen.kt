package com.itza2k.kore.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.itza2k.kore.data.Activity
import com.itza2k.kore.data.Goal
import com.itza2k.kore.data.Habit
import com.itza2k.kore.viewmodel.KoreViewModel
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun DashboardScreen(viewModel: KoreViewModel) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        DashboardHeader(viewModel)
        
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Today's Habits",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (viewModel.habits.isEmpty()) {
            EmptyStateCard(
                title = "No Habits Yet",
                message = "Add some habits to get started on your journey.",
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            val todaysHabits = viewModel.habits.filter { !it.completedToday }
            if (todaysHabits.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "All Done for Today!",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "You've completed all your habits for today. Great job!",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    todaysHabits.forEach { habit ->
                        HabitCard(
                            habit = habit,
                            onComplete = { viewModel.completeHabit(habit) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Recent Activities",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (viewModel.activities.isEmpty()) {
            EmptyStateCard(
                title = "No Activities Yet",
                message = "Complete some habits to see your activities here.",
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.activities.sortedByDescending { it.timestamp }.take(5).forEach { activity ->
                    val habit = viewModel.habits.find { it.id == activity.habitId }
                    if (habit != null) {
                        ActivityCard(
                            activity = activity,
                            habit = habit,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Goals in Progress",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (viewModel.goals.isEmpty()) {
            EmptyStateCard(
                title = "No Goals Yet",
                message = "Add some goals to track your progress.",
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.goals.filter { !it.isCompleted }.forEach { goal ->
                    GoalProgressCard(
                        goal = goal,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardHeader(viewModel: KoreViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to Kore",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "You have ${viewModel.totalPoints} points",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    title = "Habits",
                    value = viewModel.habits.size.toString(),
                    modifier = Modifier.weight(1f)
                )
                
                StatCard(
                    title = "Goals",
                    value = viewModel.goals.size.toString(),
                    modifier = Modifier.weight(1f)
                )
                
                StatCard(
                    title = "Rewards",
                    value = viewModel.rewards.size.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun HabitCard(habit: Habit, onComplete: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (habit.isEcoFriendly) 
                MaterialTheme.colorScheme.secondaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = habit.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    text = "${habit.currentPoints} points",
                    style = MaterialTheme.typography.bodySmall
                )
                
                if (habit.streak > 0) {
                    Text(
                        text = "Streak: ${habit.streak} days",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Button(
                onClick = onComplete,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Complete")
            }
        }
    }
}

@Composable
fun ActivityCard(activity: Activity, habit: Habit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = habit.name,
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = "Completed on ${formatTimestamp(activity.timestamp)}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Base: ${activity.pointsEarned} points",
                    style = MaterialTheme.typography.bodySmall
                )
                
                if (activity.bonusPoints > 0) {
                    Text(
                        text = "Bonus: +${activity.bonusPoints} points",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            if (activity.bonusReason.isNotEmpty()) {
                Text(
                    text = activity.bonusReason,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun GoalProgressCard(goal: Goal, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = goal.name,
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = goal.description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (goal.targetDate > 0) {
                Text(
                    text = "Target: ${formatTimestamp(goal.targetDate)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { goal.progress },
                modifier = Modifier.fillMaxWidth()
            )
            
            Text(
                text = "${(goal.progress * 100).toInt()}% complete",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun EmptyStateCard(title: String, message: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}


fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return format.format(date)
}