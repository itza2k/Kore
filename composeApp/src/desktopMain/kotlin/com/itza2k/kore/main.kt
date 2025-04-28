package com.itza2k.kore

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.useResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.Desktop
import java.net.URI
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import androidx.compose.ui.graphics.toComposeImageBitmap

fun main() {
    try {
        application {
            // Create a custom icon programmatically
            val iconBitmap = createKoreIcon()

            Window(
                onCloseRequest = ::exitApplication,
                title = "Kore",
                icon = BitmapPainter(iconBitmap)
            ) {
                // Simply call App() without try-catch
                // Error handling should be done inside the App component
                App()
            }
        }
    } catch (e: Exception) {
        // Handle any exceptions that occur during application startup
        println("Fatal error during application startup: ${e.message}")
        e.printStackTrace()

        // Show a dialog with the error message
        showErrorDialog("Kore Application Error", e.message ?: "Unknown error occurred")
    }
}

// Function to show an error dialog
fun showErrorDialog(title: String, message: String) {
    val dialog = java.awt.Dialog(null as java.awt.Frame?, title, true)
    dialog.layout = java.awt.BorderLayout()

    val panel = java.awt.Panel()
    panel.layout = java.awt.BorderLayout()

    val messageLabel = java.awt.Label(message)
    messageLabel.alignment = java.awt.Label.CENTER
    panel.add(messageLabel, java.awt.BorderLayout.CENTER)

    val buttonPanel = java.awt.Panel()
    val okButton = java.awt.Button("OK")
    okButton.addActionListener {
        dialog.dispose()
    }
    buttonPanel.add(okButton)
    panel.add(buttonPanel, java.awt.BorderLayout.SOUTH)

    dialog.add(panel)
    dialog.setSize(400, 200)
    dialog.setLocationRelativeTo(null)
    dialog.isVisible = true
}

@Composable
fun ErrorScreen(errorMessage: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "An error occurred",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* You could add a retry action here */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Close Application")
            }
        }
    }
}

// Function to create a simple K icon programmatically
fun createKoreIcon(): androidx.compose.ui.graphics.ImageBitmap {
    val size = 48
    val image = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
    val g = image.createGraphics()

    // Set background to transparent
    g.background = java.awt.Color(0, 0, 0, 0)
    g.clearRect(0, 0, size, size)

    // Draw a green circle
    val green = java.awt.Color(76, 175, 80) // #4CAF50
    g.color = green
    g.drawOval(4, 4, size - 8, size - 8)

    // Draw a K letter
    g.fillRect(16, 14, 4, 20) // vertical line
    g.drawLine(20, 24, 28, 14) // upper diagonal
    g.drawLine(20, 24, 28, 34) // lower diagonal
    g.fillRect(27, 14, 4, 4) // top right end
    g.fillRect(27, 30, 4, 4) // bottom right end

    g.dispose()

    // Convert to Compose ImageBitmap
    val baos = ByteArrayOutputStream()
    ImageIO.write(image, "png", baos)
    val bais = ByteArrayInputStream(baos.toByteArray())
    return org.jetbrains.skia.Image.makeFromEncoded(bais.readAllBytes()).toComposeImageBitmap()
}

// Function to open URLs (used for GitHub profile link)
fun openUrl(url: String) {
    try {
        val desktop = Desktop.getDesktop()
        if (desktop.isSupported(Desktop.Action.BROWSE)) {
            desktop.browse(URI(url))
        }
    } catch (e: Exception) {
        println("Error opening URL: ${e.message}")
    }
}
