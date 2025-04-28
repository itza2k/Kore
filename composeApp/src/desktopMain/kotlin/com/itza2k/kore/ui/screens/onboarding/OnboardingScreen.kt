package com.itza2k.kore.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itza2k.kore.viewmodel.KoreViewModel


@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    var currentStep by remember { mutableStateOf(0) }
    val viewModel: KoreViewModel = viewModel()

    val steps = listOf(
        "Welcome" to @Composable { WelcomeStep { currentStep++ } },
        "Goals" to @Composable { GoalsStep(viewModel) { currentStep++ } },
        "Habits" to @Composable { HabitsStep(viewModel) { currentStep++ } },
        "Rewards" to @Composable { RewardsStep(viewModel) { currentStep++ } },
        "Complete" to @Composable { CompleteStep { onComplete() } }
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress indicator
        LinearProgressIndicator(
            progress = { (currentStep + 1).toFloat() / steps.size },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // Step title
        Text(
            text = steps[currentStep].first,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Step content
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            steps[currentStep].second()
        }

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentStep > 0) {
                Button(
                    onClick = { currentStep-- },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.weight(1f).padding(end = 8.dp))
            }

            if (currentStep < steps.size - 1) {
                Button(
                    onClick = { currentStep++ },
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text("Next")
                }
            } else {
                Button(
                    onClick = { onComplete() },
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text("Get Started")
                }
            }
        }
    }
}


@Composable
fun WelcomeStep(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Kore",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Your personal productivity app for sustainable living",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text(
            text = "Kore helps you align your daily activities with long-term goals through a personal economy system while promoting sustainable living practices.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
        )

        Button(
            onClick = onNext,
            modifier = Modifier.padding(top = 32.dp)
        ) {
            Text("Let's Get Started")
        }
    }
}

@Composable
fun GoalsStep(viewModel: KoreViewModel, onNext: () -> Unit) {
    // Implementation will be added later
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Set Your Goals",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Goals help you track your progress and stay motivated.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onNext,
            modifier = Modifier.padding(top = 32.dp)
        ) {
            Text("Continue")
        }
    }
}


@Composable
fun HabitsStep(viewModel: KoreViewModel, onNext: () -> Unit) {
    // Implementation will be added later
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Your Habits",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Habits are the daily activities that help you achieve your goals.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onNext,
            modifier = Modifier.padding(top = 32.dp)
        ) {
            Text("Continue")
        }
    }
}

@Composable
fun RewardsStep(viewModel: KoreViewModel, onNext: () -> Unit) {
    // Implementation will be added later
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Define Your Rewards",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Rewards are what you earn by completing habits and achieving goals.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onNext,
            modifier = Modifier.padding(top = 32.dp)
        ) {
            Text("Continue")
        }
    }
}


@Composable
fun CompleteStep(onComplete: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "You're All Set!",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "You're ready to start using Kore to improve your productivity and live more sustainably.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onComplete,
            modifier = Modifier.padding(top = 32.dp)
        ) {
            Text("Get Started")
        }
    }
}
