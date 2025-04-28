package com.itza2k.kore.ui.screens.rewards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.itza2k.kore.data.Reward
import com.itza2k.kore.viewmodel.KoreViewModel
import java.util.UUID

@Composable
fun RewardsScreen(viewModel: KoreViewModel) {
    var showAddRewardDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }
    var showSuggestedRewards by remember { mutableStateOf(false) }
    
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
                text = "My Rewards",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Button(
                onClick = { showAddRewardDialog = true }
            ) {
                Text("Add Reward")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Points display
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
                    text = "Available Points",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = "${viewModel.totalPoints}",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Category filter
        val categories = listOf("All") + viewModel.rewards
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
        
        // Rewards grid
        if (viewModel.rewards.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No Rewards Yet",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Add some rewards to motivate yourself.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    
                    Button(
                        onClick = { showSuggestedRewards = true },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("See Suggested Rewards")
                    }
                }
            }
        } else {
            val filteredRewards = if (selectedCategory == "All") {
                viewModel.rewards
            } else {
                viewModel.rewards.filter { it.category == selectedCategory }
            }
            
            if (filteredRewards.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No rewards in the '$selectedCategory' category",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 250.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredRewards) { reward ->
                        RewardCard(
                            reward = reward,
                            canAfford = viewModel.totalPoints >= reward.pointsCost,
                            onRedeem = { 
                                val success = viewModel.redeemReward(reward)
                                if (success) {
                                    // Show a snackbar or some other confirmation
                                }
                            }
                        )
                    }
                }
            }
        }
        
        // Suggested rewards section
        if (showSuggestedRewards) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Suggested Rewards",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // List of suggested rewards
            val suggestedRewards = listOf(
                Triple("Movie Night", "Treat yourself to a movie of your choice", 100),
                Triple("Book Purchase", "Buy that book you've been wanting to read", 150),
                Triple("Dessert Treat", "Enjoy your favorite dessert", 75),
                Triple("Nature Walk", "Take time for a relaxing walk in nature", 50),
                Triple("Eco-Friendly Purchase", "Buy something sustainable", 200)
            )
            
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 250.dp),
                modifier = Modifier.height(200.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestedRewards) { (name, desc, cost) ->
                    SuggestedRewardCard(
                        name = name,
                        description = desc,
                        pointsCost = cost,
                        onAdd = {
                            val reward = Reward(
                                id = UUID.randomUUID().toString(),
                                name = name,
                                description = desc,
                                pointsCost = cost,
                                isEcoFriendly = name.contains("Eco", ignoreCase = true)
                            )
                            viewModel.addReward(reward)
                            showSuggestedRewards = false
                        }
                    )
                }
            }
        } else if (viewModel.rewards.isEmpty()) {
            Button(
                onClick = { showSuggestedRewards = true },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("See Suggested Rewards")
            }
        }
    }
    
    // Add reward dialog
    if (showAddRewardDialog) {
        AddRewardDialog(
            viewModel = viewModel,
            onDismiss = { showAddRewardDialog = false }
        )
    }
}

@Composable
fun AddRewardDialog(viewModel: KoreViewModel, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var pointsCost by remember { mutableStateOf("50") }
    var category by remember { mutableStateOf("") }
    var isEcoFriendly by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Reward") },
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
                    value = pointsCost,
                    onValueChange = { pointsCost = it.filter { char -> char.isDigit() } },
                    label = { Text("Points Cost") },
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
                    
                    Text("Eco-friendly (costs fewer points)")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && pointsCost.isNotBlank()) {
                        val reward = Reward(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            description = description,
                            pointsCost = pointsCost.toIntOrNull() ?: 50,
                            category = category,
                            isEcoFriendly = isEcoFriendly
                        )
                        viewModel.addReward(reward)
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
fun RewardCard(
    reward: Reward,
    canAfford: Boolean,
    onRedeem: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (reward.isEcoFriendly) 
                MaterialTheme.colorScheme.secondaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reward.name,
                    style = MaterialTheme.typography.titleMedium
                )
                
                if (reward.isEcoFriendly) {
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
                text = reward.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "${reward.pointsCost} points",
                style = MaterialTheme.typography.titleSmall
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onRedeem,
                enabled = canAfford,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Redeem")
            }
            
            if (!canAfford) {
                Text(
                    text = "Not enough points",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun SuggestedRewardCard(
    name: String,
    description: String,
    pointsCost: Int,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "$pointsCost points",
                style = MaterialTheme.typography.titleSmall
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add to My Rewards")
            }
        }
    }
}