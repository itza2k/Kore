package com.itza2k.kore

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itza2k.kore.navigation.Screen
import com.itza2k.kore.navigation.rememberNavigationState
import com.itza2k.kore.theme.KoreTheme
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

    CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
        val viewModel: KoreViewModel = viewModel()

        KoreTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                if (navigationState.currentScreen.value == Screen.ONBOARDING) {
                    OnboardingScreen(
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
                Triple(Screen.INSIGHTS, "Insights", Icons.Default.Info)
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
                Screen.ONBOARDING -> {} // Handled outside this when statement
            }
        }
    }
}
