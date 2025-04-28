package com.itza2k.kore.ui.screens.insights

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
fun InsightsScreen(viewModel: KoreViewModel) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Insights",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Overview section
        InsightSection(
            title = "Overview",
            content = {
                OverviewInsights(viewModel)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Habits section
        InsightSection(
            title = "Habits",
            content = {
                HabitInsights(viewModel)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Goals section
        InsightSection(
            title = "Goals",
            content = {
                GoalInsights(viewModel)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Activities section
        InsightSection(
            title = "Activities",
            content = {
                ActivityInsights(viewModel)
            }
        )
    }
}

@Composable
fun InsightSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            content()
        }
    }
}

@Composable
fun OverviewInsights(viewModel: KoreViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Total points
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total Points",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "${viewModel.totalPoints}",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Stats grid
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
                title = "Activities",
                value = viewModel.activities.size.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Eco-friendly stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Eco-Friendly Habits",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "${viewModel.habits.count { it.isEcoFriendly }}",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Eco-friendly activities count
        val ecoFriendlyActivities = viewModel.activities.count { activity ->
            viewModel.habits.find { it.id == activity.habitId }?.isEcoFriendly == true
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Eco-Friendly Activities Completed",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "$ecoFriendlyActivities",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun HabitInsights(viewModel: KoreViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Most consistent habits (highest streaks)
        Text(
            text = "Most Consistent Habits",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (viewModel.habits.isEmpty()) {
            Text(
                text = "No habits yet",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        } else {
            val topHabits = viewModel.habits
                .sortedByDescending { it.streak }
                .take(3)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                topHabits.forEach { habit ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = habit.name,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "${habit.streak} days",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Habit categories
        Text(
            text = "Habit Categories",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (viewModel.habits.isEmpty()) {
            Text(
                text = "No habits yet",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        } else {
            val categories = viewModel.habits
                .map { it.category }
                .filter { it.isNotEmpty() }
                .groupBy { it }
                .mapValues { it.value.size }
                .toList()
                .sortedByDescending { it.second }

            if (categories.isEmpty()) {
                Text(
                    text = "No categories defined",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { (category, count) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = "$count habits",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoalInsights(viewModel: KoreViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Goals progress
        Text(
            text = "Goals Progress",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (viewModel.goals.isEmpty()) {
            Text(
                text = "No goals yet",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        } else {
            val completedGoals = viewModel.goals.count { it.isCompleted }
            val totalGoals = viewModel.goals.size
            val progressPercentage = if (totalGoals > 0) {
                (completedGoals.toFloat() / totalGoals) * 100
            } else {
                0f
            }

            Text(
                text = "$completedGoals of $totalGoals goals completed (${progressPercentage.toInt()}%)",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LinearProgressIndicator(
                progress = { if (totalGoals > 0) completedGoals.toFloat() / totalGoals else 0f },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            // Goals list
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.goals.forEach { goal ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = { goal.progress },
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = goal.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ActivityInsights(viewModel: KoreViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Activity over time
        Text(
            text = "Activity Over Time",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (viewModel.activities.isEmpty()) {
            Text(
                text = "No activities yet",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        } else {
            // Group activities by day
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

            val activitiesByDay = viewModel.activities.groupBy { activity ->
                calendar.timeInMillis = activity.timestamp
                dateFormat.format(calendar.time)
            }

            // Calculate points earned per day
            val pointsByDay = activitiesByDay.mapValues { (_, activities) ->
                activities.sumOf { it.pointsEarned + it.bonusPoints }
            }

            // Display points by day
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pointsByDay.entries.sortedByDescending { it.key }.forEach { (day, points) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = day,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "$points points",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Recent activities
        Text(
            text = "Recent Activities",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (viewModel.activities.isEmpty()) {
            Text(
                text = "No activities yet",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.activities
                    .sortedByDescending { it.timestamp }
                    .take(5)
                    .forEach { activity ->
                        val habit = viewModel.habits.find { it.id == activity.habitId }
                        if (habit != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = habit.name,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = "${activity.pointsEarned + activity.bonusPoints} points",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
            }
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
