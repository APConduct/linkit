package org.linkit.gui
@file:Suppress("UNRESOLVED_REFERENCE")
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import org.linkit.AngleMode
import org.linkit.Calc
import org.linkit.Parser
import org.linkit.PreciseDisplay

fun main() = application {
    var isOpen by remember { mutableStateOf(true) }

    if (isOpen) {
        Window(
                onCloseRequest = { isOpen = false },
                title = "LinkIt Calculator",
                state = WindowState(width = 400.dp, height = 600.dp)
        ) { CalculatorApp() }
    }
}

@Composable
fun CalculatorApp() {
    val calc = remember { Calc() }
    val parser = remember { Parser() }
    var displayText by remember { mutableStateOf("0") }
    var currentInput by remember { mutableStateOf("") }
    var angleMode by remember { mutableStateOf(AngleMode.RADIANS) }

    // Update calculator angle mode when state changes
    LaunchedEffect(angleMode) { calc.setAngleMode(angleMode) }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header with title and angle mode
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                            text = "🧮 LinkIt Calculator",
                            style = MaterialTheme.typography.headlineSmall
                    )

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
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                                    containerColor = MaterialTheme.colorScheme.error
                            )
            ) { Text("Clear") }

            Button(
                    onClick = onBackspace,
                    modifier = Modifier.weight(1f),
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                            )
            ) { Text("⌫") }

            Button(
                    onClick = onEquals,
                    modifier = Modifier.weight(2f),
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
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
            modifier = modifier
    ) { Text(text = text, style = MaterialTheme.typography.bodyMedium) }
}

@Preview
@Composable
fun CalculatorAppPreview() {
    CalculatorApp()
}
