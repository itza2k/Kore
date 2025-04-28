package com.itza2k.kore.ui.screens.habits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.itza2k.kore.data.Habit
import com.itza2k.kore.viewmodel.KoreViewModel
import java.util.UUID


@Composable
fun HabitsScreen(viewModel: KoreViewModel) {
    var showAddHabitDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Habits",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Button(
                onClick = { showAddHabitDialog = true }
            ) {
                Text("Add Habit")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Category filter
        val categories = listOf("All") + viewModel.habits
            .map { it.category }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()
        
        if (categories.size > 1) {
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory),
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEachIndexed { index, category ->
                    Tab(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        text = { Text(category) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Habits list
        if (viewModel.habits.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No Habits Yet",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Add some habits to get started on your journey.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    
                    Button(
                        onClick = { showAddHabitDialog = true },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Add Your First Habit")
                    }
                }
            }
        } else {
            val filteredHabits = if (selectedCategory == "All") {
                viewModel.habits
            } else {
                viewModel.habits.filter { it.category == selectedCategory }
            }
            
            if (filteredHabits.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No habits in the '$selectedCategory' category",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredHabits) { habit ->
                        HabitDetailCard(
                            habit = habit,
                            onComplete = { viewModel.completeHabit(habit) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
    
    // Add habit dialog
    if (showAddHabitDialog) {
        AddHabitDialog(
            viewModel = viewModel,
            onDismiss = { showAddHabitDialog = false }
        )
    }
}

@Composable
fun AddHabitDialog(viewModel: KoreViewModel, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var points by remember { mutableStateOf("10") }
    var category by remember { mutableStateOf("") }
    var isEcoFriendly by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Habit") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = points,
                    onValueChange = { points = it.filter { char -> char.isDigit() } },
                    label = { Text("Points") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isEcoFriendly,
                        onCheckedChange = { isEcoFriendly = it }
                    )
                    
                    Text("Eco-friendly (earns bonus points)")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && points.isNotBlank()) {
                        val habit = Habit(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            description = description,
                            basePoints = points.toIntOrNull() ?: 10,
                            category = category,
                            isEcoFriendly = isEcoFriendly
                        )
                        viewModel.addHabit(habit)
                        onDismiss()
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun HabitDetailCard(habit: Habit, onComplete: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (habit.isEcoFriendly) 
                MaterialTheme.colorScheme.secondaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    if (habit.category.isNotEmpty()) {
                        Text(
                            text = habit.category,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                if (habit.isEcoFriendly) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "Eco",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = habit.description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Points: ${habit.currentPoints}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    
                    if (habit.streak > 0) {
                        Text(
                            text = "Streak: ${habit.streak} days",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    if (habit.progressLevel > 1) {
                        Text(
                            text = "Level: ${habit.progressLevel}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                Button(
                    onClick = onComplete,
                    enabled = !habit.completedToday
                ) {
                    Text(if (habit.completedToday) "Completed" else "Complete")
                }
            }
        }
    }
}