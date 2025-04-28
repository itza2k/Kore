package com.itza2k.kore.ui.screens.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.itza2k.kore.api.GeminiApiService
import com.itza2k.kore.data.Activity
import com.itza2k.kore.data.Goal
import com.itza2k.kore.data.Habit
import com.itza2k.kore.viewmodel.KoreViewModel
import kotlinx.coroutines.launch
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

        // AI Insights Section
        AiInsightsSection(viewModel)

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

@Composable
fun AiInsightsSection(viewModel: KoreViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var aiInsight by remember { mutableStateOf("") }
    var completionPredictions by remember { mutableStateOf<Map<String, Float>>(emptyMap()) }

    // Generate insights when the component is first displayed
    LaunchedEffect(Unit) {
        if (viewModel.habits.isNotEmpty() && aiInsight.isEmpty()) {
            isLoading = true
            generateInsights(viewModel) { insight, predictions ->
                aiInsight = insight
                completionPredictions = predictions
                isLoading = false
            }
        }
    }

    Text(
        text = "AI Insights",
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "AI Insights",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Text(
                    text = "Personalized Insights",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (aiInsight.isEmpty()) {
                Text(
                    text = "Complete more habits to get personalized insights.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        isLoading = true
                        coroutineScope.launch {
                            generateInsights(viewModel) { insight, predictions ->
                                aiInsight = insight
                                completionPredictions = predictions
                                isLoading = false
                            }
                        }
                    }
                ) {
                    Text("Generate Insights")
                }
            } else {
                Text(
                    text = aiInsight,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Only show predictions if we have some
                if (completionPredictions.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Predictions",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        Text(
                            text = "Habit Completion Predictions",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    // Visualization of predictions
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(vertical = 8.dp)
                    ) {
                        CompletionPredictionChart(completionPredictions)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        isLoading = true
                        coroutineScope.launch {
                            generateInsights(viewModel) { insight, predictions ->
                                aiInsight = insight
                                completionPredictions = predictions
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Refresh Insights")
                }
            }
        }
    }
}

@Composable
fun CompletionPredictionChart(predictions: Map<String, Float>) {
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.primaryContainer
    )

    // Use a Box to position the chart elements
    Box(modifier = Modifier.fillMaxSize()) {
        // Draw the bars using Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val barWidth = canvasWidth / (predictions.size * 2)

            // Draw bars
            predictions.entries.forEachIndexed { index, (_, prediction) ->
                val barHeight = canvasHeight * prediction
                val color = colors[index % colors.size]

                // Draw bar
                drawRect(
                    color = color,
                    topLeft = Offset(index * (barWidth * 2) + barWidth/2, canvasHeight - barHeight),
                    size = Size(barWidth, barHeight)
                )
            }

            // Draw axes
            drawLine(
                color = Color.Gray,
                start = Offset(0f, canvasHeight),
                end = Offset(canvasWidth, canvasHeight),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )

            drawLine(
                color = Color.Gray,
                start = Offset(0f, 0f),
                end = Offset(0f, canvasHeight),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
        }

        // Draw the labels and percentages using Compose Text
        predictions.entries.forEachIndexed { index, (habitName, prediction) ->
            val shortName = habitName.take(10) + if (habitName.length > 10) "..." else ""

            // Position the labels using Box with alignment
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    predictions.entries.forEachIndexed { i, _ ->
                        if (i == index) {
                            Text(
                                text = shortName,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(80.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.width(80.dp))
                        }
                    }
                }
            }

            // Position the percentages
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    predictions.entries.forEachIndexed { i, (_, pred) ->
                        if (i == index) {
                            Text(
                                text = "${(pred * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(80.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.width(80.dp))
                        }
                    }
                }
            }
        }
    }
}

private suspend fun generateInsights(
    viewModel: KoreViewModel,
    onResult: (String, Map<String, Float>) -> Unit
) {
    // If there are no habits, return a default message
    if (viewModel.habits.isEmpty()) {
        onResult("Add some habits to get personalized insights.", emptyMap())
        return
    }

    // Generate some simple insights based on the user's data
    val completedHabits = viewModel.habits.filter { it.completedToday }
    val incompleteHabits = viewModel.habits.filter { !it.completedToday }
    val completionRate = if (viewModel.habits.isNotEmpty()) {
        completedHabits.size.toFloat() / viewModel.habits.size
    } else {
        0f
    }

    // Generate predictions for habit completion
    val predictions = mutableMapOf<String, Float>()
    viewModel.habits.forEach { habit ->
        // Simple prediction based on streak and completion rate
        val streakFactor = minOf(habit.streak.toFloat() / 10f, 0.5f)
        val randomFactor = (0.5f + Math.random().toFloat() * 0.3f)
        val prediction = if (habit.completedToday) {
            1.0f
        } else {
            minOf(0.3f + streakFactor + randomFactor, 1.0f)
        }
        predictions[habit.name] = prediction
    }

    // Generate insights text
    val insight = buildString {
        append("Based on your habits and activities, ")

        if (completedHabits.isNotEmpty()) {
            append("you've completed ${completedHabits.size} out of ${viewModel.habits.size} habits today (${(completionRate * 100).toInt()}%). ")

            if (completionRate > 0.7f) {
                append("Great job! You're making excellent progress. ")
            } else if (completionRate > 0.3f) {
                append("You're making good progress. Keep it up! ")
            } else {
                append("You still have some habits to complete today. ")
            }
        } else if (viewModel.habits.isNotEmpty()) {
            append("you haven't completed any habits yet today. ")
        }

        // Add streak information
        val habitsWithStreaks = viewModel.habits.filter { it.streak > 0 }
        if (habitsWithStreaks.isNotEmpty()) {
            val topStreak = habitsWithStreaks.maxByOrNull { it.streak }
            if (topStreak != null && topStreak.streak > 1) {
                append("Your longest streak is ${topStreak.streak} days for '${topStreak.name}'. ")
            }
        }

        // Add eco-friendly information
        val ecoHabits = viewModel.habits.filter { it.isEcoFriendly }
        if (ecoHabits.isNotEmpty()) {
            val completedEcoHabits = ecoHabits.filter { it.completedToday }
            append("You have ${ecoHabits.size} eco-friendly habits")
            if (completedEcoHabits.isNotEmpty()) {
                append(" and you've completed ${completedEcoHabits.size} of them today. ")
            } else {
                append(". ")
            }
        }

        // Add recommendation
        if (incompleteHabits.isNotEmpty()) {
            val recommendedHabit = incompleteHabits.maxByOrNull { 
                it.basePoints * (if (it.isEcoFriendly) 1.5f else 1.0f)
            }
            if (recommendedHabit != null) {
                append("I recommend focusing on '${recommendedHabit.name}' next, as it will earn you ${recommendedHabit.currentPoints} points.")
            }
        }
    }

    onResult(insight, predictions)
}
