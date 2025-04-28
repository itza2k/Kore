package com.itza2k.kore.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable

enum class Screen {
    DASHBOARD,
    HABITS,
    REWARDS,
    INSIGHTS,
    ONBOARDING,
    AI_ASSISTANT
}

class NavigationState(
    val currentScreen: MutableState<Screen>
) {
    fun navigateTo(screen: Screen) {
        currentScreen.value = screen
    }
}

@Composable
fun rememberNavigationState(initialScreen: Screen = Screen.DASHBOARD): NavigationState {
    val currentScreen = rememberSaveable { mutableStateOf(initialScreen) }
    return remember { NavigationState(currentScreen) }
}
