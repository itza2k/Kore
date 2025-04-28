package com.itza2k.kore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itza2k.kore.db.DatabaseFactory
import com.itza2k.kore.db.DatabaseHelper
import com.itza2k.kore.navigation.Screen
import com.itza2k.kore.navigation.rememberNavigationState
import com.itza2k.kore.theme.KoreTheme
import com.itza2k.kore.ui.screens.ai.AiScreen
import com.itza2k.kore.ui.screens.dashboard.DashboardScreen
import com.itza2k.kore.ui.screens.habits.HabitsScreen
import com.itza2k.kore.ui.screens.insights.InsightsScreen
import com.itza2k.kore.ui.screens.onboarding.OnboardingScreen
import com.itza2k.kore.ui.screens.rewards.RewardsScreen
import com.itza2k.kore.viewmodel.KoreViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    var hasCompletedOnboarding by remember { mutableStateOf(false) }

    val viewModelStoreOwner = remember {
        object : ViewModelStoreOwner {
            private val store = ViewModelStore()
            override val viewModelStore: ViewModelStore
                get() = store
        }
    }

    val navigationState = rememberNavigationState(
        initialScreen = if (hasCompletedOnboarding) Screen.DASHBOARD else Screen.ONBOARDING
    )

    // Initialize database
    val database = remember { DatabaseFactory.createDatabase() }
    val databaseHelper = remember { DatabaseHelper(database) }

    CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
        // Create ViewModel with DatabaseHelper
        val viewModel = remember { KoreViewModel(databaseHelper) }

        KoreTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                if (navigationState.currentScreen.value == Screen.ONBOARDING) {
                    OnboardingScreen(
                        viewModel = viewModel,
                        onComplete = {
                            hasCompletedOnboarding = true
                            navigationState.navigateTo(Screen.DASHBOARD)
                        }
                    )
                } else {
                    MainScreen(viewModel, navigationState)
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: KoreViewModel, navigationState: com.itza2k.kore.navigation.NavigationState) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Navigation Rail
        NavigationRail(
            modifier = Modifier.fillMaxHeight(),
            header = {
                IconButton(
                    onClick = { /* Menu button action */ },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu"
                    )
                }
            }
        ) {
            val navItems = listOf(
                Triple(Screen.DASHBOARD, "Dashboard", Icons.Default.Home),
                Triple(Screen.HABITS, "Habits", Icons.Default.CheckCircle),
                Triple(Screen.REWARDS, "Rewards", Icons.Default.Star),
                Triple(Screen.INSIGHTS, "Insights", Icons.Default.Info),
                Triple(Screen.AI_ASSISTANT, "AI Assistant", Icons.Default.Menu)
            )

            Spacer(modifier = Modifier.height(16.dp))

            navItems.forEach { (screen, title, icon) ->
                NavigationRailItem(
                    selected = navigationState.currentScreen.value == screen,
                    onClick = { navigationState.navigateTo(screen) },
                    icon = { Icon(imageVector = icon, contentDescription = title) },
                    label = { Text(title) }
                )
            }

            // Add spacer to push attribution to bottom
            Spacer(modifier = Modifier.weight(1f))

            // Subtle attribution
            Text(
                text = "itza2k",
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .clickable { 
                        openUrl("https://itza2k.github.io/") 
                    }
            )
        }

        // Main content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (navigationState.currentScreen.value) {
                Screen.DASHBOARD -> DashboardScreen(viewModel)
                Screen.HABITS -> HabitsScreen(viewModel)
                Screen.REWARDS -> RewardsScreen(viewModel)
                Screen.INSIGHTS -> InsightsScreen(viewModel)
                Screen.AI_ASSISTANT -> AiScreen(viewModel)
                Screen.ONBOARDING -> {} // Handled outside this when statement
            }
        }
    }
}
