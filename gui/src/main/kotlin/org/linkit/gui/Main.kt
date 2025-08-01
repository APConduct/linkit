package org.linkit.gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import org.linkit.AngleMode
import org.linkit.Calc
import org.linkit.Parser
import org.linkit.PreciseDisplay

// Custom calculator dark theme
private val CalculatorDarkColorScheme =
        darkColorScheme(
                primary = Color(0xFF4FC3F7), // Light blue for primary actions
                secondary = Color(0xFFFFB74D), // Orange for secondary actions
                tertiary = Color(0xFF81C784), // Green for tertiary actions
                background = Color(0xFF121212), // Deep black background
                surface = Color(0xFF1E1E1E), // Dark gray for cards
                surfaceVariant = Color(0xFF2D2D2D), // Lighter gray for display
                error = Color(0xFFEF5350), // Red for clear/error
                onPrimary = Color(0xFF000000), // Black text on primary
                onSecondary = Color(0xFF000000), // Black text on secondary
                onTertiary = Color(0xFF000000), // Black text on tertiary
                onBackground = Color(0xFFE0E0E0), // Light gray text on background
                onSurface = Color(0xFFE0E0E0), // Light gray text on surface
                onSurfaceVariant = Color(0xFFBDBDBD), // Medium gray text
                onError = Color(0xFFFFFFFF), // White text on error
        )

// Custom calculator light theme
private val CalculatorLightColorScheme =
        lightColorScheme(
                primary = Color(0xFF1976D2), // Blue for primary actions
                secondary = Color(0xFFFF9800), // Orange for secondary actions
                tertiary = Color(0xFF4CAF50), // Green for tertiary actions
                background = Color(0xFFFAFAFA), // Light gray background
                surface = Color(0xFFFFFFFF), // White for cards
                surfaceVariant = Color(0xFFF5F5F5), // Very light gray for display
                error = Color(0xFFD32F2F), // Red for clear/error
                onPrimary = Color(0xFFFFFFFF), // White text on primary
                onSecondary = Color(0xFFFFFFFF), // White text on secondary
                onTertiary = Color(0xFFFFFFFF), // White text on tertiary
                onBackground = Color(0xFF212121), // Dark gray text on background
                onSurface = Color(0xFF212121), // Dark gray text on surface
                onSurfaceVariant = Color(0xFF424242), // Medium gray text
                onError = Color(0xFFFFFFFF), // White text on error
        )

fun main() = application {
    var isOpen by remember { mutableStateOf(true) }
    var isDarkMode by remember { mutableStateOf(true) }

    // Apply macOS dark mode styling
    LaunchedEffect(isDarkMode) {
        try {
            System.setProperty(
                    "apple.awt.application.appearance",
                    if (isDarkMode) "NSAppearanceNameDarkAqua" else "NSAppearanceNameAqua"
            )
            System.setProperty("apple.laf.useScreenMenuBar", "true")
        } catch (e: Exception) {
            // Ignore if not on macOS
        }
    }

    if (isOpen) {
        Window(
                onCloseRequest = { isOpen = false },
                title = "🧮 LinkIt Calculator",
                state = WindowState(width = 400.dp, height = 600.dp),
                resizable = true
        ) { CalculatorApp(isDarkModeExternal = isDarkMode, onDarkModeChange = { isDarkMode = it }) }
    }
}

@Composable
fun CalculatorApp(isDarkModeExternal: Boolean = true, onDarkModeChange: (Boolean) -> Unit = {}) {
    val calc = remember { Calc() }
    val parser = remember { Parser() }
    var displayText by remember { mutableStateOf("0") }
    var currentInput by remember { mutableStateOf("") }
    var angleMode by remember { mutableStateOf(AngleMode.RADIANS) }
    var isDarkMode by remember { mutableStateOf(isDarkModeExternal) }

    // Sync with external state
    LaunchedEffect(isDarkModeExternal) { isDarkMode = isDarkModeExternal }

    // Update calculator angle mode when state changes
    LaunchedEffect(angleMode) { calc.setAngleMode(angleMode) }

    MaterialTheme(
            colorScheme = if (isDarkMode) CalculatorDarkColorScheme else CalculatorLightColorScheme
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header with title and controls
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                            text = "🧮 LinkIt Calculator",
                            style = MaterialTheme.typography.headlineSmall
                    )

                    Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Dark mode toggle
                        IconButton(
                                onClick = {
                                    isDarkMode = !isDarkMode
                                    onDarkModeChange(isDarkMode)
                                }
                        ) {
                            Text(
                                    text = if (isDarkMode) "🌙" else "☀️",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Angle mode toggle
                        TextButton(
                                onClick = {
                                    angleMode =
                                            if (angleMode == AngleMode.DEGREES) {
                                                AngleMode.RADIANS
                                            } else {
                                                AngleMode.DEGREES
                                            }
                                }
                        ) {
                            Text(
                                    text =
                                            when (angleMode) {
                                                AngleMode.DEGREES -> "DEG"
                                                AngleMode.RADIANS -> "RAD"
                                            },
                                    color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                HorizontalDivider()

                // Display
                CalculatorDisplay(
                        displayText = displayText,
                        currentInput = currentInput,
                        modifier = Modifier.height(120.dp)
                )

                HorizontalDivider()

                // Button grid
                CalculatorButtonGrid(
                        onInput = { input -> currentInput += input },
                        onEquals = {
                            try {
                                if (currentInput.isNotEmpty()) {
                                    val expr = parser.parse(currentInput)
                                    val result = calc.eval(expr)
                                    displayText = PreciseDisplay.formatValue(result, angleMode)
                                    currentInput = ""
                                }
                            } catch (e: Exception) {
                                displayText = "Error: ${e.message}"
                                currentInput = ""
                            }
                        },
                        onClear = {
                            currentInput = ""
                            displayText = "0"
                        },
                        onBackspace = {
                            if (currentInput.isNotEmpty()) {
                                currentInput = currentInput.dropLast(1)
                            }
                        },
                        modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun CalculatorDisplay(displayText: String, currentInput: String, modifier: Modifier = Modifier) {
    Card(
            modifier = modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
    ) {
        Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Current input
            Text(
                    text = if (currentInput.isEmpty()) "Enter expression..." else currentInput,
                    style = MaterialTheme.typography.bodyLarge,
                    color =
                            if (currentInput.isEmpty()) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
            )

            // Result display
            Text(
                    text = displayText,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun CalculatorButtonGrid(
        onInput: (String) -> Unit,
        onEquals: () -> Unit,
        onClear: () -> Unit,
        onBackspace: () -> Unit,
        modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Row 1: Functions
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton("sin", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("cos", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("tan", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("sqrt", onInput, modifier = Modifier.weight(1f))
        }

        // Row 2: More functions
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton("ln", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("log10", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("exp", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("fact", onInput, modifier = Modifier.weight(1f))
        }

        // Row 3: Numbers and operators
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton("7", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("8", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("9", onInput, modifier = Modifier.weight(1f))
            CalculatorButton(" / ", onInput, modifier = Modifier.weight(1f))
        }

        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton("4", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("5", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("6", onInput, modifier = Modifier.weight(1f))
            CalculatorButton(" * ", onInput, modifier = Modifier.weight(1f))
        }

        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton("1", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("2", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("3", onInput, modifier = Modifier.weight(1f))
            CalculatorButton(" - ", onInput, modifier = Modifier.weight(1f))
        }

        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton("0", onInput, modifier = Modifier.weight(1f))
            CalculatorButton(".", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("PI", onInput, modifier = Modifier.weight(1f))
            CalculatorButton(" + ", onInput, modifier = Modifier.weight(1f))
        }

        // Row 4: Special functions
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton("(", onInput, modifier = Modifier.weight(1f))
            CalculatorButton(")", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("^", onInput, modifier = Modifier.weight(1f))
            CalculatorButton(" % ", onInput, modifier = Modifier.weight(1f))
        }

        // Row 5: Control buttons
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                    onClick = onClear,
                    modifier = Modifier.weight(1f),
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                            )
            ) { Text("Clear") }

            Button(
                    onClick = onBackspace,
                    modifier = Modifier.weight(1f),
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                            )
            ) { Text("⌫") }

            Button(
                    onClick = onEquals,
                    modifier = Modifier.weight(2f),
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                            )
            ) { Text("=", style = MaterialTheme.typography.titleLarge) }
        }
    }
}

@Composable
fun CalculatorButton(text: String, onInput: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
            onClick = {
                val input =
                        if (text in
                                        listOf(
                                                "sin",
                                                "cos",
                                                "tan",
                                                "sqrt",
                                                "ln",
                                                "log10",
                                                "exp",
                                                "fact"
                                        )
                        ) {
                            "$text("
                        } else {
                            text
                        }
                onInput(input)
            },
            modifier = modifier,
            colors =
                    ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            containerColor = MaterialTheme.colorScheme.surface
                    )
    ) { Text(text = text, style = MaterialTheme.typography.bodyMedium) }
}

@Preview
@Composable
fun CalculatorAppPreview() {
    CalculatorApp()
}
